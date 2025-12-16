package com.example.penjadwalan_sidang.screens.dosen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.FakeData

// 🎨 DEFINISI WARNA PRIMARY (Biru: 0xFF4A90E2)
private val PrimaryColor = Color(0xFF4A90E2)

@Composable
fun DashboardDosenScreen(
    onLogout: () -> Unit = {},
    onNavigateToPengajuan: () -> Unit = {},
    onNavigateToTerjadwal: () -> Unit = {},
    // ⬅️ Pastikan parameter ini ada dan digunakan untuk Bottom Bar Index 2
    onNavigateToKalender: () -> Unit = {},
    onNavigateToProfil: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                // 1. Update state untuk pewarnaan
                selectedTab = selected

                // 2. Lakukan navigasi
                when (selected) {
                    0 -> {} // Tetap di Dashboard
                    1 -> onNavigateToPengajuan()
                    // ✅ KOREKSI: Panggil onNavigateToKalender untuk index 2
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
                        .background(PrimaryColor), // Menggunakan PrimaryColor
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PA",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pak Afif",
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

            // =====================
            // PENGAJUAN TERBARU
            // =====================
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

                    // LIST 5 DATA STATIC

                    // Ambil 3 data teratas dari FakeData untuk ditampilkan di Dashboard
                    FakeData.pengajuanList.take(3).forEach { item ->
                        DosenListItem(
                            nama = item.namaMahasiswa,
                            nim = item.nim,
                            judul = item.judulTA,
                            tanggal = item.tanggalPengajuan,
                            status = item.status
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToPengajuan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryColor), // Menggunakan PrimaryColor
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

            // =====================
            // TERJADWAL
            // =====================
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

                    repeat(5) {
                        DosenListItem(
                            nama = "Budi Santoso",
                            nim = "20/190001",
                            judul = "Analisis Jaringan Menggunakan Metode IDS",
                            tanggal = "20-10-2025",
                            jam = "09:00–11:00",
                            isScheduled = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToTerjadwal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryColor), // Menggunakan PrimaryColor
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

// ... (DosenListItem dan StatusBadge tetap sama) ...

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
            Text(text = judul, fontSize = 12.sp)
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFC542))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// 🎯 KOMPONEN UTAMA BOTTOM BAR DOSEN
@Composable
fun BottomBarDosen(selectedTab: Int, onSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White) {

        // Index 0: Dashboard
        CustomNavigationBarItemDosen(
            index = 0,
            selectedTab = selectedTab,
            icon = Icons.Default.Home,
            label = "Dashboard",
            onTabSelected = onSelected
        )

        // Index 1: Pengajuan
        CustomNavigationBarItemDosen(
            index = 1,
            selectedTab = selectedTab,
            icon = Icons.Default.Description,
            label = "Pengajuan",
            onTabSelected = onSelected
        )

        // Index 2: Kalender (Dosen menggunakan CalendarMonth)
        CustomNavigationBarItemDosen(
            index = 2,
            selectedTab = selectedTab,
            icon = Icons.Default.CalendarMonth,
            label = "Kalender",
            onTabSelected = onSelected
        )

        // Index 3: Profil
        CustomNavigationBarItemDosen(
            index = 3,
            selectedTab = selectedTab,
            icon = Icons.Default.Person,
            label = "Profil",
            onTabSelected = onSelected
        )
    }
}

// 🎯 KOMPONEN PEMBANTU UNTUK PEWARNAAN AKTIF
@Composable
fun RowScope.CustomNavigationBarItemDosen(
    index: Int,
    selectedTab: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onTabSelected: (Int) -> Unit
) {
    val isSelected = selectedTab == index
    val tintColor = if (isSelected) PrimaryColor else Color.Gray // ⬅️ Logika Pewarnaan Aktif

    NavigationBarItem(
        selected = isSelected,
        onClick = { onTabSelected(index) },
        icon = { Icon(icon, contentDescription = label, tint = tintColor) },
        label = { Text(label, color = tintColor) }, // ⬅️ Pewarnaan pada Label
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = PrimaryColor.copy(alpha = 0.1f) // Indikator latar belakang
        )
    )
}