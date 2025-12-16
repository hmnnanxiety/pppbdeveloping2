package com.example.penjadwalan_sidang.screens.dosen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.penjadwalan_sidang.PengajuanMahasiswa

// --- Konstanta Warna ---
private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFFFF5F5)
private val DangerColor = Color(0xFFF44336)
private val SuccessColor = Color(0xFF03A9F4) // Menggunakan biru muda untuk verifikasi/sukses

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengajuanDetailScreen(
    id: String? = "123",
    onNavigateBack: () -> Unit,
    // ➡️ TAMBAHKAN FUNGSI NAVIGASI KE JADWAL
    onNavigateToJadwal: (mahasiswaId: String) -> Unit,
    pengajuan: PengajuanMahasiswa? = null,
) {
    // 🎯 STATE UNTUK MENGONTROL DIALOG PENOLAKAN
    var showRejectionDialog by remember { mutableStateOf(false) }

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

                    // --- Bagian Data Mahasiswa ---
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DataField(label = "Nama Mahasiswa", value = "Andi Pratama", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        DataField(label = "NIM/ ID Mahasiswa", value = "013829573918", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        DataField(label = "Jurusan", value = "Teknik Informatika", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Bagian Judul TA ---
                    DataField(label = "Judul Tugas Akhir", value = "Sistem Informasi Akademik Berbasis Web")
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Bagian Tautan TA ---
                    DataField(label = "Tautan Tugas Akhir", value = "https://tugasakhir.saya.co", isLink = true)

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Tombol Aksi ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Verifikasi
                        Button(
                            // ➡️ UBAH: Verifikasi mengarahkan ke Jadwal Sidang
                            onClick = {
                                // Asumsi ID yang diteruskan ke screen adalah ID Mahasiswa/Pengajuan
                                onNavigateToJadwal(id ?: "")
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verifikasi", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifikasi", fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // TOMBOL TOLAK
                        Button(
                            onClick = { showRejectionDialog = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = "Tolak", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tolak", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    // PANGGIL DIALOG JIKA showRejectionDialog bernilai true
    if (showRejectionDialog) {
        RejectionCommentDialog(
            onDismiss = { showRejectionDialog = false },
            onConfirmRejection = { comment ->
                // TODO: Lakukan aksi penolakan ke backend dengan komentar
                println("Pengajuan Ditolak dengan Komentar: $comment")
                showRejectionDialog = false // Tutup dialog setelah aksi
            }
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

// ===================================
// KOMPONEN DIALOG PENOLAKAN
// ===================================

@Composable
fun RejectionCommentDialog(
    onDismiss: () -> Unit,
    onConfirmRejection: (comment: String) -> Unit
) {
    var rejectionComment by remember { mutableStateOf("") }

    // Menggunakan AlertDialog dari Material3
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
                // Kolom input komentar
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
            // Tombol untuk menolak (mengirim komentar)
            Button(
                onClick = { onConfirmRejection(rejectionComment) },
                enabled = rejectionComment.isNotBlank(), // Aktif hanya jika komentar diisi
                colors = ButtonDefaults.buttonColors(containerColor = DangerColor)
            ) {
                Text("Tolak dan Kirim")
            }
        },
        dismissButton = {
            // Tombol untuk membatalkan
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}