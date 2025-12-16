package com.example.penjadwalan_sidang.screens.mahasiswa

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.model.User
import com.example.penjadwalan_sidang.data.repository.ProfileRepository
import com.example.penjadwalan_sidang.data.repository.ThesisRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.*

private val PrimaryColor = Color(0xFF4A90E2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToForm: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val thesisRepository = remember { ThesisRepository(context) }
    val profileRepository = remember { ProfileRepository(context) }
    val selectedTab = 0

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // State untuk thesis list
    var listPengajuan by remember { mutableStateOf<List<Thesis>>(emptyList()) }
    var isLoadingThesis by remember { mutableStateOf(true) }
    var thesisErrorMessage by remember { mutableStateOf<String?>(null) }

    // State untuk profile
    var userProfile by remember { mutableStateOf<User?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    // Load Profile
    LaunchedEffect(Unit) {
        profileRepository.getMyProfile(
            onSuccess = { user ->
                userProfile = user
                isLoadingProfile = false
                Log.d("DASHBOARD", "Profile loaded: ${user.name}, ${user.email}")
            },
            onError = { error ->
                Log.e("DASHBOARD", "Failed to load profile: $error")
                isLoadingProfile = false
            }
        )
    }

    // Load Thesis List
    LaunchedEffect(Unit) {
        isLoadingThesis = true
        thesisErrorMessage = null

        thesisRepository.getMyThesis(
            onSuccess = { list ->
                listPengajuan = list
                isLoadingThesis = false
                Log.d("DASHBOARD", "Berhasil load ${list.size} thesis")
            },
            onError = { error ->
                thesisErrorMessage = error
                isLoadingThesis = false
                Log.e("DASHBOARD", "Gagal load thesis: $error")
            }
        )
    }

    // Filter thesis yang sudah dijadwalkan untuk hari yang dipilih
    val scheduledThesisForDate = remember(listPengajuan, selectedDate) {
        listPengajuan.filter { thesis ->
            thesis.scheduledAt != null &&
                    thesis.status == "APPROVED" &&
                    thesis.scheduledAt.startsWith(selectedDate.toString())
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Ya", color = PrimaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab = selectedTab) { tab ->
                when (tab) {
                    1 -> onNavigateToForm()
                    2 -> onNavigateToProfile()
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // ✅ HEADER KONSISTEN
            item {
                UnifiedHeader(
                    userProfile = userProfile,
                    isLoading = isLoadingProfile,
                    role = "Mahasiswa",
                    onLogoutClick = { showLogoutDialog = true }
                )
            }

            // ✅ KALENDER + AGENDA JADI SATU SECTION
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Kalender
                        CalendarView(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            scheduledDates = listPengajuan
                                .filter { it.scheduledAt != null && it.status == "APPROVED" }
                                .mapNotNull {
                                    try {
                                        LocalDate.parse(it.scheduledAt!!.substring(0, 10))
                                    } catch (e: Exception) {
                                        null
                                    }
                                },
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
                        if (scheduledThesisForDate.isEmpty()) {
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
                            scheduledThesisForDate.forEach { thesis ->
                                AgendaItem(thesis = thesis)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // Status Pengajuan Header
            item {
                Text(
                    text = "Status Pengajuan Saya",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // Loading State
            if (isLoadingThesis) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                }
            }
            // Error State
            else if (thesisErrorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "⚠️ Gagal memuat data",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = thesisErrorMessage ?: "Unknown error",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            // Empty State
            else if (listPengajuan.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada pengajuan",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            // Data dari API
            else {
                items(listPengajuan, key = { it.id }) { thesis ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = thesis.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Status Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (thesis.status) {
                                                    "APPROVED" -> Color(0xFF4CAF50)
                                                    "REJECTED" -> Color(0xFFF44336)
                                                    else -> Color(0xFFFFC107)
                                                }
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = thesis.status,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Tanggal
                                    Text(
                                        text = thesis.createdAt.substring(0, 10),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Button Ajukan Sidang
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onNavigateToForm() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Ajukan Sidang",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ✅ HEADER KONSISTEN UNTUK SEMUA SCREEN
@Composable
fun UnifiedHeader(
    userProfile: User?,
    isLoading: Boolean,
    role: String,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            if (isLoading) {
                Text(
                    text = "Loading...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = role,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else if (userProfile != null) {
                Text(
                    text = userProfile.name ?: "User",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = role,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "User",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = role,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryColor)
                .clickable { onLogoutClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (userProfile != null) {
                    val name = userProfile.name ?: "U"
                    val parts = name.split(" ")
                    if (parts.size >= 2) {
                        "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                    } else {
                        name.take(2).uppercase()
                    }
                } else {
                    "U"
                },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
                    text = thesis.title,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            // Jam Sidang
            Text(
                text = thesis.scheduledAt?.substring(11, 16) ?: "09:00",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryColor
            )
        }
    }
}

@Composable
fun CalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    scheduledDates: List<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ChevronLeft, "Previous month", tint = Color.Black)
            }
            Text(
                text = "${currentMonth.month.getDisplayName(JavaTextStyle.SHORT, Locale("id"))} ${currentMonth.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
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
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
        val totalCells = ((daysInMonth + firstDayOfMonth + 6) / 7) * 7

        Column {
            for (week in 0 until (totalCells / 7)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (day in 0 until 7) {
                        val cellIndex = week * 7 + day
                        val dayOfMonth = cellIndex - firstDayOfMonth + 1

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayOfMonth in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayOfMonth)
                                val isSelected = date == selectedDate
                                val isScheduled = scheduledDates.contains(date)
                                val isToday = date == LocalDate.now()

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> PrimaryColor
                                                isScheduled -> Color(0xFFFFE066)
                                                isToday -> Color(0xFFE0E7FF)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayOfMonth.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected || isScheduled) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color.White
                                            else -> Color.Black
                                        }
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

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        CustomNavigationBarItemMahasiswa(
            index = 0,
            selectedTab = selectedTab,
            icon = Icons.Default.Dashboard,
            label = "Dashboard",
            onTabSelected = onTabSelected
        )

        CustomNavigationBarItemMahasiswa(
            index = 1,
            selectedTab = selectedTab,
            icon = Icons.Default.Description,
            label = "Form Pengajuan",
            onTabSelected = onTabSelected
        )

        CustomNavigationBarItemMahasiswa(
            index = 2,
            selectedTab = selectedTab,
            icon = Icons.Default.Person,
            label = "Profil",
            onTabSelected = onTabSelected
        )
    }
}

@Composable
fun RowScope.CustomNavigationBarItemMahasiswa(
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