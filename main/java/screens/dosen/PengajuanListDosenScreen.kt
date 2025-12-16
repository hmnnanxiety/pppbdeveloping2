package com.example.penjadwalan_sidang.screens.dosen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val ActionColor = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengajuanListDosenScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToKalender: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(1) }

    val dosenRepo = remember { DosenRepository(context) }
    val profileRepo = remember { ProfileRepository(context) }

    var dosenProfile by remember { mutableStateOf<User?>(null) }
    var allThesis by remember { mutableStateOf<List<Thesis>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val itemsPerPage = 15
    var currentPage by remember { mutableIntStateOf(1) }

    val totalPages = remember(allThesis.size) {
        ceil(allThesis.size.toDouble() / itemsPerPage).toInt().coerceAtLeast(1)
    }

    val currentPageData = remember(allThesis, currentPage) {
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, allThesis.size)
        if (startIndex < allThesis.size) allThesis.subList(startIndex, endIndex) else emptyList()
    }

    // Load profile
    LaunchedEffect(Unit) {
        profileRepo.getMyProfile(
            onSuccess = { dosenProfile = it },
            onError = { Log.e("PENGAJUAN_LIST", "Profile error: $it") }
        )
    }

    // Load all thesis
    LaunchedEffect(Unit) {
        dosenRepo.getAllThesis(
            onSuccess = { thesisList ->
                allThesis = thesisList
                isLoading = false
                Log.d("PENGAJUAN_LIST", "Loaded ${thesisList.size} thesis")
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
                Log.e("PENGAJUAN_LIST", "Failed to load: $error")
            }
        )
    }

    Scaffold(
        bottomBar = {
            CustomDosenBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { index -> selectedTab = index },
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToKalender = onNavigateToKalender,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(padding)
        ) {
            DosenHeader(
                namaDosen = dosenProfile?.name ?: "Dosen",
                role = "Dosen"
            )

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
                            text = "Pengajuan Terbaru Tugas Akhir Mahasiswa",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        PengajuanTableHeader()
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Loading state
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                        }
                    }
                }
                // Error state
                else if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(Color(0xFFFFEBEE))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "⚠️ Gagal memuat data",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Text(
                                    text = errorMessage ?: "Unknown error",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                // Empty state
                else if (allThesis.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum ada pengajuan",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                // Data loaded
                else {
                    items(currentPageData, key = { it.id }) { thesis ->
                        PengajuanListItem(
                            thesis = thesis,
                            onClick = { onNavigateToDetail(thesis.id) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            if (!isLoading && allThesis.isNotEmpty()) {
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
fun DosenHeader(namaDosen: String, role: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = namaDosen.split(" ").take(2).joinToString("") { it.take(1) },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(namaDosen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(role, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PengajuanTableHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nama Mahasiswa", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                Text("Judul Tugas Akhir", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.7f))
                Text("Tanggal", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                Text("Aksi", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
            }
        }
    }
}

@Composable
fun PengajuanListItem(
    thesis: Thesis,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1.3f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (thesis.student?.name ?: "M").take(1),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        thesis.student?.name ?: "Mahasiswa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        thesis.studentId.take(8),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            Text(
                thesis.title,
                fontSize = 12.sp,
                modifier = Modifier
                    .weight(1.7f)
                    .padding(end = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                thesis.createdAt.substring(0, 10),
                fontSize = 12.sp,
                modifier = Modifier.weight(0.9f)
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .weight(0.8f)
                    .height(30.dp),
                colors = ButtonDefaults.buttonColors(ActionColor),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Aksi", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PaginationBar(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) { Icon(Icons.Default.ChevronLeft, contentDescription = "Prev") }

        Text("$currentPage / $totalPages", fontWeight = FontWeight.Bold)

        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) { Icon(Icons.Default.ChevronRight, contentDescription = "Next") }
    }
}

@Composable
fun CustomDosenBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToKalender: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        CustomNavigationBarItem(0, selectedTab, Icons.Default.Dashboard, "Dashboard", onNavigateToDashboard, onTabSelected)
        CustomNavigationBarItem(1, selectedTab, Icons.Default.Description, "Pengajuan", {}, onTabSelected)
        CustomNavigationBarItem(2, selectedTab, Icons.Default.CalendarToday, "Kalender", onNavigateToKalender, onTabSelected)
        CustomNavigationBarItem(3, selectedTab, Icons.Default.Person, "Profil", onNavigateToProfile, onTabSelected)
    }
}

@Composable
fun RowScope.CustomNavigationBarItem(
    index: Int,
    selectedTab: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    onTabSelected: (Int) -> Unit
) {
    val isSelected = selectedTab == index
    val tintColor = if (isSelected) PrimaryColor else Color.Gray

    NavigationBarItem(
        selected = isSelected,
        onClick = {
            onTabSelected(index)
            if (index != 1) onClick()
        },
        icon = { Icon(icon, contentDescription = label, tint = tintColor) },
        label = { Text(label, color = tintColor) }
    )
}