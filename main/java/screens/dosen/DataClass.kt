package com.example.penjadwalan_sidang.screens.dosen

// File: DataClass.kt

// --- DATA CLASS UNTUK JADWAL SIDANG (DIGUNAKAN BERSAMA) ---
data class JadwalSidang(
    val namaMahasiswa: String,
    val nim: String,
    val judulTA: String,
    val tanggalSidang: String, // format "DD-MM-YYYY"
    val jamSidang: String,
    val ruangan: String
)

// Anda bisa menambahkan data class lain di sini di masa mendatang.