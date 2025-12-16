package com.example.penjadwalan_sidang.screens.dosen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.repository.DosenRepository

private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)
private val DangerColor = Color(0xFFF44336)
private val SuccessColor = Color(0xFF03A9F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengajuanDetailScreen(
    id: String,
    onNavigateBack: () -> Unit,
    onNavigateToJadwal: (mahasiswaId: String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DosenRepository(context) }

    var thesis by remember { mutableStateOf<Thesis?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRejectionDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Load thesis detail
    LaunchedEffect(id) {
        repository.getThesisDetail(
            id = id,
            onSuccess = { data ->
                thesis = data
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pengajuan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️ Error", fontWeight = FontWeight.Bold, color = DangerColor)
                        Text(errorMessage ?: "Unknown error", color = Color.Gray)
                    }
                }
            }
        } else if (thesis != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                Text(
                    text = "Pengajuan Mahasiswa",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DataField(
                                label = "Nama Mahasiswa",
                                value = thesis!!.student?.name ?: "Unknown",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            DataField(
                                label = "NIM/ ID Mahasiswa",
                                value = thesis!!.student?.id ?: "-",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            DataField(
                                label = "Jurusan",
                                value = thesis!!.student?.prodi ?: "-",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        DataField(label = "Judul Tugas Akhir", value = thesis!!.title)
                        Spacer(modifier = Modifier.height(16.dp))

                        DataField(label = "Tautan Tugas Akhir", value = thesis!!.docUrl, isLink = true)
                        Spacer(modifier = Modifier.height(16.dp))

                        DataField(label = "Status", value = thesis!!.status)

                        Spacer(modifier = Modifier.height(32.dp))

                        // Tombol Aksi (hanya jika status PENDING)
                        if (thesis!!.status == "PENDING") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        isSubmitting = true
                                        repository.reviewThesis(
                                            id = id,
                                            decision = "APPROVED",
                                            onSuccess = { updatedThesis ->
                                                isSubmitting = false
                                                Toast.makeText(context, "✅ TA Disetujui!", Toast.LENGTH_SHORT).show()
                                                // Navigate ke jadwal
                                                onNavigateToJadwal(id)
                                            },
                                            onError = { error ->
                                                isSubmitting = false
                                                Toast.makeText(context, "❌ $error", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isSubmitting
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Verifikasi", modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Verifikasi", fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = { showRejectionDialog = true },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isSubmitting
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Tolak", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tolak", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        } else {
                            // Status sudah direview
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (thesis!!.status) {
                                        "APPROVED" -> Color(0xFFE8F5E9)
                                        "REJECTED" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFFFF9C4)
                                    }
                                )
                            ) {
                                Text(
                                    text = "Status: ${thesis!!.status}",
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Rejection
    if (showRejectionDialog) {
        var rejectionComment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRejectionDialog = false },
            title = {
                Text(
                    "Tolak Pengajuan Tugas Akhir",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DangerColor
                )
            },
            text = {
                Column {
                    Text(
                        "Apakah Anda yakin ingin menolak pengajuan ini?",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = rejectionComment,
                        onValueChange = { rejectionComment = it },
                        label = { Text("Komentar (Opsional)") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        singleLine = false,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectionDialog = false
                        isSubmitting = true
                        repository.reviewThesis(
                            id = id,
                            decision = "REJECTED",
                            onSuccess = {
                                isSubmitting = false
                                Toast.makeText(context, "❌ TA Ditolak", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = { error ->
                                isSubmitting = false
                                Toast.makeText(context, "❌ $error", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerColor)
                ) {
                    Text("Tolak")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectionDialog = false }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun DataField(label: String, value: String, modifier: Modifier = Modifier, isLink: Boolean = false) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = if (isLink) PrimaryColor else Color.Black
        )
    }
}