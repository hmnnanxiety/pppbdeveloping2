package com.example.penjadwalan_sidang.screens.dosen

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.data.model.User
import com.example.penjadwalan_sidang.data.repository.ProfileRepository

private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)

private val mockProgramStudi = listOf("Manajemen Informatika", "Sistem Informasi", "Teknik Komputer")
private val mockJurusan = listOf("Teknologi Informasi", "Manajemen Bisnis", "Akuntansi")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDosenScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToPengajuan: () -> Unit,
    onNavigateToKalender: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val profileRepo = remember { ProfileRepository(context) }

    var profile by remember { mutableStateOf<User?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }

    var nama by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var noTelepon by remember { mutableStateOf("") }
    var selectedProdi by remember { mutableStateOf(mockProgramStudi[0]) }
    var selectedJurusan by remember { mutableStateOf(mockJurusan[0]) }
    var selectedTab by remember { mutableIntStateOf(3) }

    // Load profile
    LaunchedEffect(Unit) {
        profileRepo.getMyProfile(
            onSuccess = { user ->
                profile = user
                nama = user.name ?: ""
                nip = user.id
                email = user.email
                selectedProdi = user.prodi ?: mockProgramStudi[0]
                isLoadingProfile = false
                Log.d("PROFILE_DOSEN", "Profile loaded: ${user.name}")
            },
            onError = { error ->
                Toast.makeText(context, "⚠️ Gagal memuat profil: $error", Toast.LENGTH_SHORT).show()
                isLoadingProfile = false
                Log.e("PROFILE_DOSEN", "Failed to load: $error")
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomBarDosen(selectedTab) { selected ->
                selectedTab = selected
                when (selected) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToPengajuan()
                    2 -> onNavigateToKalender()
                    3 -> {}
                }
            }
        }
    ) { padding ->
        if (isLoadingProfile) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderProfile()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
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

                        FormInputField(
                            label = "Nama",
                            value = nama,
                            onValueChange = { nama = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(
                            label = "NIP",
                            value = nip,
                            onValueChange = {},
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        DropdownInputField(
                            label = "Program Studi",
                            selectedValue = selectedProdi,
                            options = mockProgramStudi,
                            onOptionSelected = { selectedProdi = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        DropdownInputField(
                            label = "Jurusan",
                            selectedValue = selectedJurusan,
                            options = mockJurusan,
                            onOptionSelected = { selectedJurusan = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(
                            label = "Email",
                            value = email,
                            onValueChange = {},
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(
                            label = "No Telepon",
                            value = noTelepon,
                            onValueChange = { noTelepon = it }
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (nama.trim().length !in 2..100) {
                                    Toast.makeText(
                                        context,
                                        "Nama harus 2-100 karakter",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                isSubmitting = true

                                profileRepo.updateProfile(
                                    name = nama,
                                    prodi = selectedProdi,
                                    onSuccess = { updatedUser ->
                                        Toast.makeText(
                                            context,
                                            "✅ Profil berhasil disimpan!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        profile = updatedUser
                                        nama = updatedUser.name ?: ""
                                        isSubmitting = false
                                        Log.d("PROFILE_DOSEN", "Profile updated successfully")
                                    },
                                    onError = { error ->
                                        Toast.makeText(
                                            context,
                                            "❌ $error",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        isSubmitting = false
                                        Log.e("PROFILE_DOSEN", "Failed to update: $error")
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(PrimaryColor),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SAVE",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = if (readOnly) Color.Gray.copy(alpha = 0.1f) else Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.1f)
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
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}