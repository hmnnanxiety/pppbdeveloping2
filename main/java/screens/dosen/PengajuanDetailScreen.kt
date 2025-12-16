package com.example.penjadwalan_sidang.screens.dosen

import android.util.Log
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
    val dosenRepo = remember { DosenRepository(context) }

    var thesis by remember { mutableStateOf<Thesis?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRejectionDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Load thesis detail
    LaunchedEffect(id) {
        dosenRepo.getThesisDetail(
            id = id,
            onSuccess = { loadedThesis ->
                thesis = loadedThesis
                isLoading = false
                Log.d("DETAIL_SCREEN", "Thesis loaded: ${loadedThesis.title}")
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
                Log.e("DETAIL_SCREEN", "Failed to load: $error")
            }
        )
    }

    if (showRejectionDialog) {
        RejectionCommentDialog(
            onDismiss = { showRejectionDialog = false },
            onConfirmRejection = { comment ->
                isSubmitting = true
                dosenRepo.reviewThesis(
                    id = id,
                    decision = "REJECTED",
                    onSuccess = {
                        Toast.makeText(context, "✅ Pengajuan ditolak", Toast.LENGTH_SHORT).show()
                        showRejectionDialog = false
                        isSubmitting = false
                        onNavigateBack()
                    },
                    onError = { error ->
                        Toast.makeText(context, "❌ $error", Toast.LENGTH_LONG).show()
                        isSubmitting = false
                    }
                )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(padding)
        ) {
            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else if (errorMessage != null) {
                // Error state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "⚠️ Gagal memuat data",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "Unknown error",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(PrimaryColor)
                    ) {
                        Text("Kembali")
                    }
                }
            } else if (thesis != null) {
                // Data loaded
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                DataField(
                                    label = "Nama Mahasiswa",
                                    value = thesis!!.student?.name ?: "Loading...",
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                DataField(
                                    label = "NIM/ ID Mahasiswa",
                                    value = thesis!!.studentId,
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

                            DataField(
                                label = "Judul Tugas Akhir",
                                value = thesis!!.title
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            DataField(
                                label = "Tautan Tugas Akhir",
                                value = thesis!!.docUrl,
                                isLink = true
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Action buttons (only show if PENDING)
                            if (thesis!!.status == "PENDING") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            isSubmitting = true
                                            dosenRepo.reviewThesis(
                                                id = id,
                                                decision = "APPROVED",
                                                onSuccess = {
                                                    Toast.makeText(
                                                        context,
                                                        "✅ Pengajuan disetujui",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    isSubmitting = false
                                                    onNavigateToJadwal(thesis!!.studentId)
                                                },
                                                onError = { error ->
                                                    Toast.makeText(
                                                        context,
                                                        "❌ $error",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    isSubmitting = false
                                                }
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(SuccessColor),
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
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Verifikasi",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Verifikasi", fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Button(
                                        onClick = { showRejectionDialog = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(DangerColor),
                                        shape = RoundedCornerShape(8.dp),
                                        enabled = !isSubmitting
                                    ) {
                                        Icon(
                                            Icons.Default.Cancel,
                                            contentDescription = "Tolak",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Tolak", fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            } else {
                                // Show status badge if already processed
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            when (thesis!!.status) {
                                                "APPROVED" -> Color(0xFFE8F5E9)
                                                "REJECTED" -> Color(0xFFFFEBEE)
                                                else -> Color(0xFFFFF9C4)
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = "Status: ${thesis!!.status}",
                                            modifier = Modifier.padding(16.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = when (thesis!!.status) {
                                                "APPROVED" -> Color(0xFF4CAF50)
                                                "REJECTED" -> Color(0xFFF44336)
                                                else -> Color(0xFFFFC107)
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
}

@Composable
fun DataField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLink: Boolean = false
) {
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

@Composable
fun RejectionCommentDialog(
    onDismiss: () -> Unit,
    onConfirmRejection: (comment: String) -> Unit
) {
    var rejectionComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                    "Mohon berikan komentar atau alasan penolakan:",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = rejectionComment,
                    onValueChange = { rejectionComment = it },
                    label = { Text("Komentar Penolakan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    singleLine = false,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRejection(rejectionComment) },
                enabled = rejectionComment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(DangerColor)
            ) {
                Text("Tolak dan Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}