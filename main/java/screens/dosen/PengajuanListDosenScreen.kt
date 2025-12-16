package com.example.penjadwalan_sidang.screens.dosen
import com.example.penjadwalan_sidang.FakeData
import com.example.penjadwalan_sidang.PengajuanMahasiswa
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import kotlin.math.ceil



private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)
private val ActionColor = Color(0xFF4CAF50) // Mengubah AccentColor menjadi ActionColor (lebih jelas)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengajuanListDosenScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToKalender: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(1) } // Default tab ke Pengajuan

    // Ambil data langsung dari FakeData. Filter hanya yang statusnya "Menunggu" jika mau
    val allPengajuan = FakeData.pengajuanList

    val itemsPerPage = 15
    val totalPages = ceil(allPengajuan.size.toDouble() / itemsPerPage).toInt()
    var currentPage by remember { mutableStateOf(1) }

    val startIndex = (currentPage - 1) * itemsPerPage
    val endIndex = minOf(startIndex + itemsPerPage, allPengajuan.size)
    val currentPageData = allPengajuan.subList(startIndex, endIndex)

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

            DosenHeader(namaDosen = "Pak Afif", role = "Dosen")

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp) // 🔥 Padding yang lebih rapi
                    ) {
                        Text(
                            text = "Pengajuan Terbaru Tugas Akhir Mahasiswa",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        // Header dipisahkan dari List Item agar tidak scroll
                        PengajuanTableHeader()
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // 🛠️ Perbaiki component calling dan hubungkan ke navigasi detail
                items(currentPageData, key = { it.id }) { pengajuan ->
                    PengajuanListItem(
                        pengajuan = pengajuan,
                        onClick = { onNavigateToDetail(pengajuan.id) } // 🔥 Hubungkan ke navigasi detail
                    )
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

// ===================================
// KOMPONEN UI LIST PENGAJUAN
// ===================================

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
        // 🔥 Avatar di kiri agar konsisten dengan dashboard
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = namaDosen.first().toString() +
                        (namaDosen.split(" ").getOrNull(1)?.firstOrNull()?.toString() ?: ""),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp)) // 🔥 Spacer untuk memberi jarak

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 🔥 Penyesuaian weight agar kolom lebih rapi
                Text("Nama Mahasiswa", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                Text("Judul Tugas Akhir", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.7f))
                Text("Tanggal", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                Text("Aksi", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
            }
        }
        // 🔥 Tidak perlu Divider di dalam Card ini
    }
}

// 🛠️ Component diganti namanya dari PengajuanItemWithAction menjadi PengajuanListItem
@Composable
fun PengajuanListItem(
    pengajuan: PengajuanMahasiswa,
    onClick: () -> Unit // Action saat tombol Aksi diklik
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // KOLOM NAMA + NIM
            Row(
                modifier = Modifier.weight(1.3f), // 🔥 Weight disesuaikan dengan header
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatarnya dikecilkan sedikit biar balance
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        pengajuan.namaMahasiswa.first().toString(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        pengajuan.namaMahasiswa,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        pengajuan.nim,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            // KOLOM JUDUL TA
            Text(
                pengajuan.judulTA,
                fontSize = 12.sp,
                modifier = Modifier
                    .weight(1.7f) // 🔥 Weight disesuaikan dengan header
                    .padding(end = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // KOLOM TANGGAL
            Text(
                pengajuan.tanggalPengajuan,
                fontSize = 12.sp,
                modifier = Modifier.weight(0.9f) // 🔥 Weight disesuaikan dengan header
            )

            // KOLOM AKSI (Tombol yang mengarah ke Detail)
            Button(
                onClick = onClick, // 🔥 Action navigasi ke detail
                modifier = Modifier
                    .weight(0.8f) // 🔥 Weight disesuaikan dengan header
                    .height(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ActionColor),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Aksi", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ... (Kode PaginationBar dan CustomDosenBottomBar tetap sama)
// ...

@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
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

        CustomNavigationBarItem(
            index = 0,
            selectedTab = selectedTab,
            icon = Icons.Default.Dashboard,
            label = "Dashboard",
            onClick = onNavigateToDashboard,
            onTabSelected = onTabSelected
        )

        CustomNavigationBarItem(
            index = 1,
            selectedTab = selectedTab,
            icon = Icons.Default.Description,
            label = "Pengajuan",
            onClick = {},
            onTabSelected = onTabSelected
        )

        CustomNavigationBarItem(
            index = 2,
            selectedTab = selectedTab,
            icon = Icons.Default.CalendarToday,
            label = "Kalender",
            onClick = onNavigateToKalender,
            onTabSelected = onTabSelected
        )

        CustomNavigationBarItem(
            index = 3,
            selectedTab = selectedTab,
            icon = Icons.Default.Person,
            label = "Profil",
            onClick = onNavigateToProfile,
            onTabSelected = onTabSelected
        )
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
