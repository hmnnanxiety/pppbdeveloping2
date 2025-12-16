package com.example.penjadwalan_sidang.screens.dosen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.model.User
import com.example.penjadwalan_sidang.data.remote.RetrofitClient
import com.example.penjadwalan_sidang.data.repository.DosenRepository
import com.example.penjadwalan_sidang.data.repository.ProfileRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val PrimaryColor = Color(0xFF4A90E2)

@Composable
fun DashboardDosenScreen(
    onLogout: () -> Unit = {},
    onNavigateToPengajuan: () -> Unit = {},
    onNavigateToTerjadwal: () -> Unit = {},
    onNavigateToKalender: () -> Unit = {},
    onNavigateToProfil: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Repository
    val dosenRepo = remember { DosenRepository(context) }
    val profileRepo = remember { ProfileRepository(context) }

    // State untuk profile dosen
    var dosenProfile by remember { mutableStateOf<User?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    // State untuk pending thesis
    var pendingThesisList by remember { mutableStateOf<List<Thesis>>(emptyList()) }
    var isLoadingPending by remember { mutableStateOf(true) }
    var errorPending by remember { mutableStateOf<String?>(null) }

    // State untuk terjadwal (approved dengan scheduledAt)
    var scheduledThesisList by remember { mutableStateOf<List<Thesis>>(emptyList()) }
    var isLoadingScheduled by remember { mutableStateOf(true) }
    var errorScheduled by remember { mutableStateOf<String?>(null) }

    // Load profile dosen
    LaunchedEffect(Unit) {
        profileRepo.getMyProfile(
            onSuccess = { user ->
                dosenProfile = user
                isLoadingProfile = false
                Log.d("DASHBOARD_DOSEN", "Profile loaded: ${user.name}")
            },
            onError = { error ->
                Log.e("DASHBOARD_DOSEN", "Failed to load profile: $error")
                isLoadingProfile = false
            }
        )
    }

    // Load pending thesis (5 terbaru)
    LaunchedEffect(Unit) {
        dosenRepo.getPendingThesis(
            onSuccess = { thesisList ->
                pendingThesisList = thesisList.take(5)
                isLoadingPending = false
                Log.d("DASHBOARD_DOSEN", "Loaded ${thesisList.size} pending thesis")
            },
            onError = { error ->
                errorPending = error
                isLoadingPending = false
                Log.e("DASHBOARD_DOSEN", "Failed to load pending: $error")
            }
        )
    }

    // Load scheduled thesis (APPROVED dengan scheduledAt)
    LaunchedEffect(Unit) {
        dosenRepo.getAllThesis(
            onSuccess = { allThesis ->
                scheduledThesisList = allThesis
                    .filter { it.status == "APPROVED" && it.scheduledAt != null }
                    .take(5)
                isLoadingScheduled = false
                Log.d("DASHBOARD_DOSEN", "Loaded ${scheduledThesisList.size} scheduled thesis")
            },
            onError = { error ->
                errorScheduled = error
                isLoadingScheduled = false
                Log.e("DASHBOARD_DOSEN", "Failed to load scheduled: $error")
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> {} // Stay on Dashboard
                    1 -> onNavigateToPengajuan()
                    2 -> onNavigateToKalender()
                    3 -> onNavigateToProfil()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .background(Color(0xFFFFF5F5))
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLoadingProfile) "..." else {
                            dosenProfile?.name?.let {
                                it.split(" ").take(2).joinToString("") { word -> word.first().toString() }
                            } ?: "D"
                        },
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLoadingProfile) "Loading..." else (dosenProfile?.name ?: "Dosen"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dosen",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PENGAJUAN TERBARU
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pengajuan Terbaru Tugas Akhir Mahasiswa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Loading state
                    if (isLoadingPending) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                        }
                    }
                    // Error state
                    else if (errorPending != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚠️ $errorPending",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                    // Empty state
                    else if (pendingThesisList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada pengajuan pending",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    // Data loaded
                    else {
                        pendingThesisList.forEach { thesis ->
                            DosenListItem(
                                nama = thesis.student?.name ?: "Mahasiswa",
                                nim = thesis.studentId.take(8),
                                judul = thesis.title,
                                tanggal = thesis.createdAt.substring(0, 10),
                                status = thesis.status
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToPengajuan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Lihat Semua",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TERJADWAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Terjadwal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Loading state
                    if (isLoadingScheduled) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                        }
                    }
                    // Error state
                    else if (errorScheduled != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚠️ $errorScheduled",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                    // Empty state
                    else if (scheduledThesisList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada sidang terjadwal",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    // Data loaded
                    else {
                        scheduledThesisList.forEach { thesis ->
                            val scheduledDate = thesis.scheduledAt?.substring(0, 10) ?: "TBA"
                            val scheduledTime = thesis.scheduledAt?.substring(11, 16) ?: "00:00"

                            DosenListItem(
                                nama = thesis.student?.name ?: "Mahasiswa",
                                nim = thesis.studentId.take(8),
                                judul = thesis.title,
                                tanggal = scheduledDate,
                                jam = "$scheduledTime-${(scheduledTime.split(":")[0].toInt() + 2).toString().padStart(2, '0')}:${scheduledTime.split(":")[1]}",
                                isScheduled = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToTerjadwal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Lihat Semua",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun DosenListItem(
    nama: String,
    nim: String,
    judul: String,
    tanggal: String,
    jam: String = "",
    status: String = "",
    isScheduled: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF7ED957)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nama.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = nama, fontWeight = FontWeight.Bold)
            Text(text = nim, fontSize = 12.sp, color = Color.Gray)
            Text(text = judul, fontSize = 12.sp, maxLines = 1)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(text = tanggal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

            if (isScheduled) {
                Text(text = jam, fontSize = 12.sp, color = Color.Gray)
            } else {
                StatusBadge(status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "APPROVED" -> Color(0xFF4CAF50) to Color.White
        "REJECTED" -> Color(0xFFF44336) to Color.White
        else -> Color(0xFFFFC542) to Color.White
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = when (status) {
                "PENDING" -> "Menunggu"
                "APPROVED" -> "Disetujui"
                "REJECTED" -> "Ditolak"
                else -> status
            },
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BottomBarDosen(selectedTab: Int, onSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        CustomNavigationBarItemDosen(0, selectedTab, Icons.Default.Home, "Dashboard", onSelected)
        CustomNavigationBarItemDosen(1, selectedTab, Icons.Default.Description, "Pengajuan", onSelected)
        CustomNavigationBarItemDosen(2, selectedTab, Icons.Default.CalendarMonth, "Kalender", onSelected)
        CustomNavigationBarItemDosen(3, selectedTab, Icons.Default.Person, "Profil", onSelected)
    }
}

@Composable
fun RowScope.CustomNavigationBarItemDosen(
    index: Int,
    selectedTab: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onTabSelected: (Int) -> Unit
) {
    val isSelected = selectedTab == index
    val tintColor = if (isSelected) PrimaryColor else Color.Gray

    NavigationBarItem(
        selected = isSelected,
        onClick = { onTabSelected(index) },
        icon = { Icon(icon, contentDescription = label, tint = tintColor) },
        label = { Text(label, color = tintColor) },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = PrimaryColor.copy(alpha = 0.1f)
        )
    )
}