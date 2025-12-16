package com.example.penjadwalan_sidang.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import kotlin.math.ceil
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.draw.clip

// --- Konstanta Warna ---
private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalSidangDosenScreen(
    mahasiswaId: String, // ID Mahasiswa yang disetujui
    onNavigateBack: () -> Unit,
    onJadwalConfirmed: (LocalDate, String, String) -> Unit // Tanggal, Jam, Ruangan
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedHour by remember { mutableStateOf(10) } // Default jam 10
    var selectedMinute by remember { mutableStateOf(0) } // Default menit 00

    // Mock Ruangan
    var selectedRuangan by remember { mutableStateOf("R.301") }
    val ruanganOptions = listOf("R.301", "R.302", "Lab 1", "Lab 2")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Sidang") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(padding)
                .padding(16.dp)
        ) {

            // --- Pilih Tanggal Sidang ---
            Text(
                text = "Pilih Tanggal Sidang",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ScheduleCalendarView(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onMonthChange = { currentMonth = it },
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Pilih Jam Sidang dan Ruangan ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pilih Jam Sidang
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pilih Jam Sidang",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TimePickerView(
                        selectedHour = selectedHour,
                        onHourChange = { selectedHour = it },
                        selectedMinute = selectedMinute,
                        onMinuteChange = { selectedMinute = it }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Pilih Ruangan
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pilih Ruangan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    RuanganPicker(
                        selectedRuangan = selectedRuangan,
                        options = ruanganOptions,
                        onRuanganSelected = { selectedRuangan = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Konfirmasi
            Button(
                onClick = {
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    onJadwalConfirmed(selectedDate, formattedTime, selectedRuangan)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(150.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Jadwalkan", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Jadwalkan")
            }
        }
    }
}

// ===================================
// KOMPONEN KALENDER SIDANG (DIBUAT ULANG KHUSUS PAGE INI)
// ===================================

@Composable
fun ScheduleCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header bulan dan tahun
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                    Icon(Icons.Default.ArrowBack, "Previous month")
                }
                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.SHORT, Locale("id", "ID"))} ${currentMonth.year}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                // Perbaikan
                IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next month")
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama hari (SUN, MON, ...)
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
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
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayOfMonth in 1..daysInMonth) {
                                    val date = currentMonth.atDay(dayOfMonth)
                                    val isSelected = date == selectedDate

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryColor.copy(alpha = 0.2f) else Color.Transparent)
                                            .clickable { onDateSelected(date) }
                                            .padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = dayOfMonth.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PrimaryColor else Color.Black
                                        )
                                        // Mock indikator jadwal (sesuai desain)
                                        if (date.dayOfMonth % 5 == 0) {
                                            Text(
                                                text = "Ujian TA",
                                                fontSize = 8.sp,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF8B5CF6))
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
// KOMPONEN TIME PICKER (Sesuai Desain "24 Hours")
// ===================================
@Composable
fun TimePickerView(
    selectedHour: Int,
    onHourChange: (Int) -> Unit,
    selectedMinute: Int,
    onMinuteChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "24 Hours",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour Picker (Simple Text/Simulasi Picker)
                Text(
                    text = String.format("%02d", selectedHour),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    modifier = Modifier.clickable {
                        // Simulasi logic picker (bisa diganti dengan komponen picker sungguhan)
                        onHourChange((selectedHour + 1) % 24)
                    }
                )

                Text(
                    text = ":",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Minute Picker (Simple Text/Simulasi Picker)
                Text(
                    text = String.format("%02d", selectedMinute),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    modifier = Modifier.clickable {
                        // Simulasi logic picker
                        onMinuteChange((selectedMinute + 5) % 60)
                    }
                )
            }
            Text(
                text = "Tap untuk mengubah",
                fontSize = 10.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ===================================
// KOMPONEN RUANGAN PICKER
// ===================================
@Composable
fun RuanganPicker(
    selectedRuangan: String,
    options: List<String>,
    onRuanganSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            options.forEach { ruangan ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onRuanganSelected(ruangan) }
                        .background(if (selectedRuangan == ruangan) PrimaryColor.copy(alpha = 0.1f) else Color.Transparent)
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = ruangan,
                        tint = if (selectedRuangan == ruangan) PrimaryColor else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ruangan,
                        fontWeight = if (selectedRuangan == ruangan) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedRuangan == ruangan) PrimaryColor else Color.Black
                    )
                }
            }
        }
    }
}