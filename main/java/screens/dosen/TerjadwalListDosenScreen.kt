package com.example.penjadwalan_sidang.screens.dosen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.model.User
import com.example.penjadwalan_sidang.data.repository.DosenRepository
import com.example.penjadwalan_sidang.data.repository.ProfileRepository
import kotlin.math.ceil

private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerjadwalListDosenScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToPengajuan: () -> Unit,
    onNavigateToKalender: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { DosenRepository(context) }
    val profileRepository = remember { ProfileRepository(context) }

    // State untuk profile
    var userProfile by remember { mutableStateOf<User?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    // State untuk thesis list
    var allJadwal by remember { mutableStateOf<List<Thesis>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load profile
    LaunchedEffect(Unit) {
        profileRepository.getMyProfile(
            onSuccess = { user ->
                userProfile = user
                isLoadingProfile = false
                Log.d("TERJADWAL_LIST", "Profile loaded: ${user.name}")
            },
            onError = { error ->
                Log.e("TERJADWAL_LIST", "Failed to load profile: $error")
                isLoadingProfile = false
            }
        )
    }

    // Load all thesis yang sudah dijadwalkan
    LaunchedEffect(Unit) {
        repository.getAllThesis(
            onSuccess = { list ->
                // Filter hanya yang sudah scheduled dan approved
                allJadwal = list.filter {
                    it.scheduledAt != null && it.status == "APPROVED"
                }
                isLoading = false
                Log.d("TERJADWAL_LIST", "Loaded ${allJadwal.size} scheduled thesis")
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
                Log.e("TERJADWAL_LIST", "Error loading thesis: $error")
            }
        )
    }

    // Pagination
    val itemsPerPage = 15
    val totalPages = if (allJadwal.isEmpty()) 1 else ceil(allJadwal.size.toDouble() / itemsPerPage).toInt()
    var currentPage by remember { mutableStateOf(1) }

    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, allJadwal.size)
    val currentPageData = if (allJadwal.isNotEmpty()) allJadwal.subList(startIndex, endIndex) else emptyList()

    var selectedTab by remember { mutableIntStateOf(2) }

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToPengajuan()
                    2 -> {} // Stay here
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(padding)
        ) {
            // ✅ HEADER KONSISTEN
            UnifiedDosenHeader(
                userProfile = userProfile,
                isLoading = isLoadingProfile
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = "⚠️ $errorMessage",
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = "Mahasiswa yang sudah dijadwalkan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            TerjadwalTableHeader()
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    if (currentPageData.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Belum ada jadwal", color = Color.Gray)
                            }
                        }
                    } else {
                        items(currentPageData, key = { it.id }) { thesis ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                                exit = fadeOut(tween(300))
                            ) {
                                TerjadwalListItem(thesis = thesis)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                PaginationBar(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPageChange = { newPage -> currentPage = newPage }
                )
            }
        }
    }
}

@Composable
fun TerjadwalTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Nama Mahasiswa", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f))
        Text("Judul Tugas Akhir", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.5f))
        Text("Tanggal sidang", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text("Jam sidang", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TerjadwalListItem(thesis: Thesis) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nama Mahasiswa
        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                thesis.student?.name ?: "Unknown",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                thesis.student?.id?.take(10) ?: "-",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Judul TA
        Text(
            thesis.title,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Tanggal Sidang
        Text(
            thesis.scheduledAt?.substring(0, 10) ?: "-",
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Jam Sidang
        Text(
            thesis.scheduledAt?.substring(11, 16) ?: "09:00",
            fontSize = 12.sp,
            color = PrimaryColor,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}