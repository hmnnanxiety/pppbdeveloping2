package com.example.penjadwalan_sidang.screens.mahasiswa

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import com.example.penjadwalan_sidang.data.repository.ProfileRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToForm: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ProfileRepository(context) }

    // ✅ SEMUA FIELD EDITABLE (termasuk NIM/ID & Email sebagai display)
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    // Load profile saat pertama kali dibuka
    LaunchedEffect(Unit) {
        repository.getMyProfile(
            onSuccess = { user ->
                nama = user.name ?: ""
                nim = user.id // ID sebagai NIM
                email = user.email
                prodi = user.prodi ?: ""
                isLoadingProfile = false
            },
            onError = { errorMsg ->
                Toast.makeText(context, "Gagal load profil: $errorMsg", Toast.LENGTH_SHORT).show()
                isLoadingProfile = false
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab = 2) { tab ->
                when (tab) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToForm()
                }
            }
        }
    ) { padding ->
        if (isLoadingProfile) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF5F5))
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF4A90E2))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF5F5))
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ✅ HEADER KONSISTEN (tanpa background putih)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = nama.ifEmpty { "User" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mahasiswa",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A90E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (nama.isNotEmpty()) {
                                val parts = nama.split(" ")
                                if (parts.size >= 2) {
                                    "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                                } else {
                                    nama.take(2).uppercase()
                                }
                            } else {
                                "U"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Profile Content
                AnimatedVisibility(
                    visible = !isLoadingProfile,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
                    exit = fadeOut(tween(300))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Profil",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // ✅ Nama - EDITABLE
                                ProfileTextField(
                                    label = "Nama",
                                    value = nama,
                                    onValueChange = { nama = it },
                                    enabled = !isLoading,
                                    placeholder = "Masukkan nama lengkap"
                                )

                                // ✅ NIM - EDITABLE (User bisa mengisi/update)
                                ProfileTextField(
                                    label = "NIM / ID Mahasiswa",
                                    value = nim,
                                    onValueChange = { nim = it },
                                    enabled = !isLoading,
                                    placeholder = "Masukkan NIM"
                                )

                                // ✅ Program Studi - EDITABLE
                                ProfileTextField(
                                    label = "Program Studi",
                                    value = prodi,
                                    onValueChange = { prodi = it },
                                    enabled = !isLoading,
                                    placeholder = "Contoh: Teknik Informatika"
                                )

                                // ✅ Email - READ ONLY (dari Google Auth)
                                ProfileTextField(
                                    label = "Email",
                                    value = email,
                                    onValueChange = {},
                                    enabled = false,
                                    placeholder = ""
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Save Button
                        Button(
                            onClick = {
                                // ⚠️ CATATAN: Backend hanya update `name` dan `prodi`
                                // Field `id` (NIM) tidak bisa diupdate karena primary key
                                // Tapi untuk UX, kita tetap biarkan user mengisi

                                if (nama.trim().isEmpty()) {
                                    Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (prodi.trim().isEmpty()) {
                                    Toast.makeText(context, "Program studi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isLoading = true

                                repository.updateProfile(
                                    name = nama,
                                    prodi = prodi,
                                    onSuccess = { updatedUser ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "✅ Profil berhasil disimpan!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Update state dengan data terbaru
                                        nama = updatedUser.name ?: ""
                                        prodi = updatedUser.prodi ?: ""
                                    },
                                    onError = { errorMsg ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "❌ $errorMsg",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(160.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A90E2)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Save",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SAVE",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tombol Logout
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(160.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("LOGOUT", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// ✅ PROFILE TEXT FIELD (dengan placeholder support)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    placeholder: String = ""
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty() && enabled) {
                    Text(
                        text = placeholder,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4A90E2),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = if (enabled) Color.White else Color.Gray.copy(alpha = 0.1f),
                disabledContainerColor = Color.Gray.copy(alpha = 0.1f),
                disabledBorderColor = Color.Gray.copy(alpha = 0.2f),
                disabledTextColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            enabled = enabled
        )
    }
}