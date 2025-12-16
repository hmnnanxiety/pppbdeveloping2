package com.example.penjadwalan_sidang.screens.dosen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.model.User
import com.example.penjadwalan_sidang.data.repository.DosenRepository
import com.example.penjadwalan_sidang.data.repository.ProfileRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.math.ceil

private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)
private val LightPurple = Color(0xFFC0B3E8)
private val LightYellow = Color(0xFFFFE066)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalenderDosenScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToPengajuan: () -> Unit,
    onNavigateToTerjadwal: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DosenRepository(context) }
    val profileRepository = remember { ProfileRepository(context) }

    // State untuk profile
    var userProfile by remember { mutableStateOf<User?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    // State untuk thesis list
    var allThesis by remember { mutableStateOf<List<Thesis>>(emptyList()) }
    var isLoadingThesis by remember { mutableStateOf(true) }

    // Kalender state
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTab by remember { mutableIntStateOf(2) }

    // Load profile
    LaunchedEffect(Unit) {
        profileRepository.getMyProfile(
            onSuccess = { user ->
                userProfile = user
                isLoadingProfile = false
                Log.d("KALENDER_DOSEN", "Profile loaded: ${user.name}")
            },
            onError = { error ->
                Log.e("KALENDER_DOSEN", "Failed to load profile: $error")
                isLoadingProfile = false
            }
        )
    }

    // Load all thesis
    LaunchedEffect(Unit) {
        repository.getAllThesis(
            onSuccess = { list ->
                allThesis = list.filter { it.scheduledAt != null && it.status == "APPROVED" }
                isLoadingThesis = false
                Log.d("KALENDER_DOSEN", "Loaded ${allThesis.size} scheduled thesis")
            },
            onError = { error ->
                Log.e("KALENDER_DOSEN", "Error loading thesis: $error")
                isLoadingThesis = false
            }
        )
    }

    // Filter thesis untuk tanggal yang dipilih
    val filteredSchedules = remember(allThesis, selectedDate) {
        allThesis.filter { thesis ->
            try {
                val thesisDate = LocalDate.parse(thesis.scheduledAt!!.substring(0, 10))
                thesisDate == selectedDate
            } catch (e: Exception) {
                false
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToPengajuan()
                    2 -> {} // Stay on Kalender
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // ✅ KALENDER + AGENDA DALAM SATU CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Kalender
                        DosenCalendarView(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            allSchedules = allThesis,
                            onMonthChange = { currentMonth = it },
                            onDateSelected = { selectedDate = it }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Divider
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Agenda Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Agenda",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id"))),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Agenda Items
                        if (isLoadingThesis) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryColor)
                            }
                        } else if (filteredSchedules.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak ada agenda pada tanggal ini",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            filteredSchedules.forEach { thesis ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                                    exit = fadeOut(tween(300))
                                ) {
                                    AgendaItem(thesis = thesis)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// ✅ HEADER KONSISTEN UNTUK DOSEN
@Composable
fun UnifiedDosenHeader(
    userProfile: User?,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                text = if (userProfile != null) {
                    val name = userProfile.name ?: "D"
                    val parts = name.split(" ")
                    if (parts.size >= 2) {
                        "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                    } else {
                        name.take(2).uppercase()
                    }
                } else {
                    "D"
                },
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                Text("Loading...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Dosen", fontSize = 14.sp, color = Color.Gray)
            } else {
                Text(
                    text = userProfile?.name ?: "Dosen",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Dosen", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DosenCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    allSchedules: List<Thesis>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }, modifier = Modifier.size(30.dp)) {
                Icon(Icons.Default.ChevronLeft, "Previous month", tint = Color.Black)
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.SHORT, Locale("id"))} ${currentMonth.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }, modifier = Modifier.size(30.dp)) {
                Icon(Icons.Default.ChevronRight, "Next month", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
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
                            if (dayOfMonth in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayOfMonth)
                                val isSelected = date == selectedDate
                                val isScheduled = allSchedules.any {
                                    try {
                                        LocalDate.parse(it.scheduledAt!!.substring(0, 10)) == date
                                    } catch (e: Exception) {
                                        false
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onDateSelected(date) },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp, bottom = 2.dp)
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) PrimaryColor
                                                else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayOfMonth.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else Color.Black
                                        )
                                    }

                                    if (isScheduled) {
                                        Text(
                                            text = "Ujian TA",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(LightYellow)
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

// ✅ AGENDA ITEM (TANPA RUANGAN)
@Composable
fun AgendaItem(thesis: Thesis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ujian Tugas Akhir",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = thesis.student?.name ?: "Unknown",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = thesis.scheduledAt?.substring(11, 16) ?: "09:00",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryColor
            )
        }
    }
}