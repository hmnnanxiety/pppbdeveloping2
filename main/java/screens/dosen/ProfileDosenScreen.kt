package com.example.penjadwalan_sidang.screens.dosen

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
import com.example.penjadwalan_sidang.data.repository.ProfileRepository

private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDosenScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToPengajuan: () -> Unit,
    onNavigateToKalender: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { ProfileRepository(context) }

    var nama by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingProfile by remember { mutableStateOf(true) }

    var selectedTab by remember { mutableIntStateOf(3) }

    // Load profile
    LaunchedEffect(Unit) {
        repository.getMyProfile(
            onSuccess = { user ->
                nama = user.name ?: ""
                nip = user.id
                email = user.email
                prodi = user.prodi ?: ""
                isLoadingProfile = false
            },
            onError = { error ->
                Toast.makeText(context, "Gagal load profil: $error", Toast.LENGTH_SHORT).show()
                isLoadingProfile = false
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

                        FormInputField(label = "Nama", value = nama, onValueChange = { nama = it })
                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(label = "NIP", value = nip, onValueChange = {}, readOnly = true)
                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(label = "Program Studi", value = prodi, onValueChange = { prodi = it })
                        Spacer(modifier = Modifier.height(16.dp))

                        FormInputField(label = "Email", value = email, onValueChange = {}, readOnly = true)
                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                repository.updateProfile(
                                    name = nama,
                                    prodi = prodi,
                                    onSuccess = { updatedUser ->
                                        isLoading = false
                                        Toast.makeText(context, "✅ Profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                        nama = updatedUser.name ?: ""
                                        prodi = updatedUser.prodi ?: ""
                                    },
                                    onError = { error ->
                                        isLoading = false
                                        Toast.makeText(context, "❌ $error", Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(PrimaryColor),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "SAVE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color.LightGray,
                containerColor = if (readOnly) Color.Gray.copy(alpha = 0.1f) else Color.White,
                disabledBorderColor = Color.LightGray,
//                disabledContainerColor = Color.Gray.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}