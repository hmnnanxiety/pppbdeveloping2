
package com.example.penjadwalan_sidang.screens.dosen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil

// --- Data Class ---


// --- Konstanta Warna ---
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
    // Data dummy
    val allJadwal = remember {
        List(100) { index ->
            JadwalSidang(
                namaMahasiswa = "Budi Santoso",
                nim = "201901001",
                judulTA = "Analisis Jaringan Menggunakan Metode IDS",
                tanggalSidang = "20-10-2025",
                jamSidang = "09:00–11:00",
                ruangan = "R. Sidang 1"
            )
        }
    }

    // Logika Pagination
    val itemsPerPage = 15
    val totalPages = ceil(allJadwal.size.toDouble() / itemsPerPage).toInt()
    var currentPage by remember { mutableStateOf(1) }

    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, allJadwal.size)
    val currentPageData = allJadwal.subList(startIndex, endIndex)

    // selectedTab: 2 adalah indeks untuk Kalender/Terjadwal
    var selectedTab by remember { mutableIntStateOf(2) }


    Scaffold(
        bottomBar = {
            // Memanggil BottomBarDosen yang didefinisikan di file lain
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToPengajuan()
                    2 -> {}
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

            // Header Dosen (Mirip Dashboard)
            DosenHeaderList(namaDosen = "Pak Afif", role = "Dosen")

            // Konten Utama dengan LazyColumn
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp)
            ) {

                // Judul Halaman
                item {
                    Text(
                        text = "Mahasiswa yang sudah dijadwalkan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Header Tabel
                item {
                    TerjadwalTableHeader()
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Daftar Item
                items(currentPageData) { jadwal ->
                    TerjadwalListItem(jadwal = jadwal)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Pagination Bar
            PaginationBar(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageChange = { newPage ->
                    currentPage = newPage
                }
            )
        }
    }
}

// ===================================
// KOMPONEN UI KHUSUS LAYAR INI
// ===================================

@Composable
fun DosenHeaderList(namaDosen: String, role: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo atau Avatar (mirip dashboard)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = namaDosen.split(" ").first().take(1) + namaDosen.split(" ").last().take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = namaDosen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = role, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun TerjadwalTableHeader() {
    // Header tabel yang statis di atas daftar
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
fun TerjadwalListItem(jadwal: JadwalSidang) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ❌ BAGIAN AVATAR DIHAPUS UNTUK MEMBERIKAN RUANG KOSONG

        // Nama Mahasiswa
        Column(modifier = Modifier.weight(1.2f)) {
            Text(jadwal.namaMahasiswa, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(jadwal.nim, fontSize = 11.sp, color = Color.Gray)
        }

        // ➡️ Spacer tambahan untuk memisahkan Nama dan Judul
        Spacer(modifier = Modifier.width(12.dp))

        // Judul TA
        Text(
            jadwal.judulTA,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // ➡️ Spacer tambahan untuk memisahkan Judul dan Tanggal
        Spacer(modifier = Modifier.width(12.dp))

        // Tanggal Sidang
        Text(
            jadwal.tanggalSidang,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Jam Sidang
        Text(
            jadwal.jamSidang,
            fontSize = 12.sp,
            color = PrimaryColor, // Warna biru untuk jam sidang
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
