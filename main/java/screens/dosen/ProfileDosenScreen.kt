package com.example.penjadwalan_sidang.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.screens.dosen.BottomBarDosen // Asumsi BottomBarDosen diimpor dari DashboardDosenScreen.kt

// --- Konstanta Warna ---
private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)

// Mock Data untuk Dropdown
private val mockProgramStudi = listOf("Manajemen Informatika", "Sistem Informasi", "Teknik Komputer")
private val mockJurusan = listOf("Teknologi Informasi", "Manajemen Bisnis", "Akuntansi")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDosenScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToPengajuan: () -> Unit,
    onNavigateToKalender: () -> Unit,
    onNavigateBack: () -> Unit,
    // Note: Karena ini adalah halaman Profil, tombol Profil akan tetap di halaman ini.
) {
    // 🔑 State untuk Form
    var nama by remember { mutableStateOf("Andrean Ramadani") }
    var nip by remember { mutableStateOf("210401010") }
    var email by remember { mutableStateOf("emaiDosen@gmail.com") }
    var noTelepon by remember { mutableStateOf("08123456789") }

    // 🔑 State untuk Dropdown
    var selectedProdi by remember { mutableStateOf(mockProgramStudi[0]) }
    var selectedJurusan by remember { mutableStateOf(mockJurusan[0]) }

    // 🔑 State untuk Bottom Bar
    var selectedTab by remember { mutableIntStateOf(3) } // ⬅️ Index 3 untuk Profil

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToPengajuan()
                    2 -> onNavigateToKalender()
                    3 -> {/* Stay on Profile */}
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ➡️ Header Profile (Menggunakan komponen yang disederhanakan dari KalenderDosenScreen)
            HeaderProfile()

            // Konten Form Utama
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profil",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Nama
                    FormInputField(label = "Nama", value = nama, onValueChange = { nama = it })
                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. NIP
                    FormInputField(label = "NIP", value = nip, onValueChange = { nip = it }, readOnly = true)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Program Studi (Dropdown)
                    DropdownInputField(
                        label = "Program Studi",
                        selectedValue = selectedProdi,
                        options = mockProgramStudi,
                        onOptionSelected = { selectedProdi = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Jurusan (Dropdown)
                    DropdownInputField(
                        label = "Jurusan",
                        selectedValue = selectedJurusan,
                        options = mockJurusan,
                        onOptionSelected = { selectedJurusan = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Email
                    FormInputField(label = "Email", value = email, onValueChange = { email = it })
                    Spacer(modifier = Modifier.height(16.dp))

                    // 6. No Telepon
                    FormInputField(label = "No Telepon", value = noTelepon, onValueChange = { noTelepon = it })
                    Spacer(modifier = Modifier.height(32.dp))

                    // Tombol SAVE
                    Button(
                        onClick = {
                            // TODO: Implementasi logika penyimpanan data profil
                            println("Data Saved: $nama, $nip, $selectedProdi, $selectedJurusan, $email, $noTelepon")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SAVE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// ===================================
// KOMPONEN PENDUKUNG FORM
// ===================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.LightGray,
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInputField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {}, // Tidak diubah manual
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }, // Klik membuka dropdown
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.LightGray,
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f) // Menyesuaikan lebar
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

