package com.example.penjadwalan_sidang.screens.mahasiswa

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
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
import com.example.penjadwalan_sidang.data.repository.ThesisRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPengajuanScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateLogout: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ThesisRepository(context) }

    var judulTA by remember { mutableStateOf("") }
    var linkTA by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab = 1) { tab ->
                when (tab) {
                    0 -> onNavigateToDashboard()
                    2 -> onNavigateToProfile()
                    3 -> onNavigateLogout()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fatih Gantenk",
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
                        text = "FG",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Pengajuan Tugas Akhir",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Judul Tugas Akhir Field
                        Column {
                            Text(
                                text = "Judul Tugas Akhir",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = judulTA,
                                onValueChange = { judulTA = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "Masukkan judul tugas akhir (10-200 karakter)",
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4A90E2),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                minLines = 3,
                                maxLines = 5,
                                enabled = !isLoading
                            )
                            // Character counter
                            Text(
                                text = "${judulTA.length}/200",
                                fontSize = 12.sp,
                                color = if (judulTA.length in 10..200) Color.Gray else Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, end = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }

                        // Link Tugas Akhir Field
                        Column {
                            Text(
                                text = "Link Tugas Akhir",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = linkTA,
                                onValueChange = { linkTA = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = "https://drive.google.com/...",
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4A90E2),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                enabled = !isLoading
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        // Validasi input
                        when {
                            judulTA.isEmpty() -> {
                                Toast.makeText(context, "Judul Tugas Akhir tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            }
                            judulTA.length !in 10..200 -> {
                                Toast.makeText(context, "Judul TA harus 10-200 karakter", Toast.LENGTH_SHORT).show()
                            }
                            linkTA.isEmpty() -> {
                                Toast.makeText(context, "Link Tugas Akhir tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            }
                            !linkTA.matches(Regex("^https?://.+")) -> {
                                Toast.makeText(context, "URL harus dimulai dengan http:// atau https://", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // Submit ke API
                                isLoading = true

                                repository.createThesis(
                                    title = judulTA,
                                    docUrl = linkTA,
                                    onSuccess = { newThesis ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "✅ Pengajuan berhasil disubmit!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Reset form
                                        judulTA = ""
                                        linkTA = ""

                                        // Kembali ke Dashboard
                                        onNavigateToDashboard()
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
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(48.dp)
                        .widthIn(min = 140.dp),
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
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Submit",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}