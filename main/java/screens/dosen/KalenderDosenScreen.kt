package com.example.penjadwalan_sidang.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.math.ceil

// --- Konstanta Warna ---
private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)
private val LightPurple = Color(0xFFC0B3E8)
private val LightYellow = Color(0xFFFFE066)

// NOTE: data class JadwalSidang DIHAPUS dan diasumsikan dapat diakses
// dari package yang sama atau DataClass.kt.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalenderDosenScreen(
    // 🔔 Parameter navigasi Bottom Bar
    onNavigateToDashboard: () -> Unit,
    onNavigateToPengajuan: () -> Unit,
    onNavigateToTerjadwal: () -> Unit, // Terjadwal ganti Kalender di Bottom Bar
    onNavigateToProfile: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // 🔑 State untuk Kalender
    // Kita set default ke September 2025 agar sesuai dengan screenshot
    var currentMonth by remember { mutableStateOf(YearMonth.of(2025, 9)) }
    var selectedDate by remember { mutableStateOf(LocalDate.of(2025, 9, 11)) } // Default ke tanggal 11
    var selectedTab by remember { mutableIntStateOf(2) } // ⬅️ INDEX 2 UNTUK KALENDER

    // 🔑 Data Jadwal yang difilter
    val allSchedules = getMockJadwalSidang()
    val filteredSchedules = allSchedules.filter {
        it.tanggalSidang == selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToPengajuan()
                    2 -> {/* Stay on Kalender */ } // Sudah di halaman Kalender
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

            // HEADER PROFIL
            HeaderProfile()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                // KALENDER VIEW
                DosenCalendarView(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    allSchedules = allSchedules,
                    onMonthChange = { currentMonth = it; selectedDate = it.atDay(1) },
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // AGENDA HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agenda",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DAFTAR JADWAL SIDANG (Agenda)
                if (filteredSchedules.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada agenda pada tanggal ini.", color = Color.Gray)
                    }
                } else {
                    filteredSchedules.forEach { jadwal ->
                        AgendaItem(jadwal)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// ===================================
// KOMPONEN HEADER PROFIL (Disalin dari design baru)
// ===================================

@Composable
fun HeaderProfile() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PA",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Pak Afif",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Dosen",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

// ===================================
// KOMPONEN KALENDER (Disalin dari design baru)
// ===================================

@Composable
fun DosenCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    allSchedules: List<JadwalSidang>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header bulan dan tahun
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.ChevronLeft, "Previous month", tint = Color.Black)
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} ${currentMonth.year}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.ChevronRight, "Next month", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama hari
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
            }

            // Tanggal
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7

            val totalCells = ceil((daysInMonth + firstDayOfMonth) / 7.0).toInt() * 7

            Column {
                for (week in 0 until totalCells / 7) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (day in 0 until 7) {
                            val cellIndex = week * 7 + day
                            val dayOfMonth = cellIndex - firstDayOfMonth + 1

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(horizontal = 2.dp, vertical = 4.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (dayOfMonth in 1..daysInMonth || dayOfMonth > daysInMonth) {
                                    val date =
                                        if (dayOfMonth in 1..daysInMonth) currentMonth.atDay(dayOfMonth)
                                        else currentMonth.atEndOfMonth().plusDays(dayOfMonth - daysInMonth.toLong())

                                    val isCurrentMonth = dayOfMonth in 1..daysInMonth
                                    val isSelected = isCurrentMonth && date == selectedDate
                                    val isScheduled = isCurrentMonth && allSchedules.any {
                                        it.tanggalSidang == date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable(enabled = isCurrentMonth) {
                                                onDateSelected(date)
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top
                                    ) {
                                        // 1. Kotak Angka Tanggal
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp, bottom = 2.dp)
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) PrimaryColor else if (isScheduled) Color.Transparent else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isCurrentMonth) dayOfMonth.toString() else date.dayOfMonth.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = if (isCurrentMonth) FontWeight.SemiBold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> Color.White
                                                    !isCurrentMonth -> Color.LightGray
                                                    else -> Color.Black
                                                }
                                            )
                                        }

                                        // 2. Indikator Jadwal ('Ujian TA')
                                        if (isScheduled) {
                                            val bgColor = if (date.dayOfMonth % 2 == 0) LightYellow else LightPurple

                                            Text(
                                                text = "Ujian TA",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(bgColor)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ===================================
// KOMPONEN ITEM AGENDA (Disalin dari design baru)
// ===================================

@Composable
fun AgendaItem(jadwal: JadwalSidang) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Detail Agenda
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ujian Tugas Akhir",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = "Ruangan ${jadwal.ruangan}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // Jam Sidang
            Text(
                text = jadwal.jamSidang.replace("–", "-"), // Ganti dash panjang ke pendek
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PrimaryColor
            )
        }
    }
}


// ⚠️ MOCK DATA (Disalin dari design baru)
// Diperlukan di sini agar DosenCalendarView dan filterSchedules dapat berjalan.
fun getMockJadwalSidang(): List<JadwalSidang> {
    // Asumsi: data class JadwalSidang sudah didefinisikan secara eksternal.
    // Jika masih ada error, pastikan Anda telah membuat DataClass.kt atau
    // menempatkan definisi JadwalSidang di tempat yang benar.

    // Anggap JadwalSidang memiliki 6 parameter: namaMahasiswa, nim, judulTA, tanggalSidang, jamSidang, ruangan
    return listOf(
        JadwalSidang(
            "Wati Nurhaliza", "20/190002", "Sistem Informasi Akademik",
            "11-09-2025", "09:00–11:00", "R.301"
        ),

        // Jadwal di kalender
        JadwalSidang(
            "Budi Santoso", "20/190001", "Analisis Jaringan IDS",
            "12-09-2025", "13:00–15:00", "R.302"
        ),
        JadwalSidang(
            "Rahmat Fajar", "20/190003", "Aplikasi Mobile E-commerce",
            "13-09-2025", "10:00–12:00", "R.301"
        ),
        JadwalSidang(
            "Dina Kurniawan", "20/190004", "Machine Learning",
            "23-09-2025", "08:00–10:00", "Lab 1"
        ),
        JadwalSidang(
            "Eko Prasetyo", "20/190005", "Pengembangan Website",
            "26-09-2025", "14:00–16:00", "R.303"
        ),
        JadwalSidang(
            "Fara Anjani", "20/190006", "Data Mining",
            "27-09-2025", "09:00–11:00", "R.301"
        ),
        JadwalSidang(
            "Gatot Subroto", "20/190007", "Keamanan Jaringan",
            "29-09-2025", "13:00–15:00", "R.302"
        ),
    )
}