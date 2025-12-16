package com.example.penjadwalan_sidang.screens.dosen

/**
 * Optional helper data classes.
 * Sekarang kita sudah memakai data dari API (Thesis model).
 * File ini bisa dihapus atau digunakan untuk helper functions.
 */

// Data class ini TIDAK DIGUNAKAN lagi karena sudah diganti dengan Thesis dari API
// Tapi tetap dipertahankan untuk backward compatibility jika ada reference

@Deprecated("Use Thesis model from API instead")
data class JadwalSidang(
    val namaMahasiswa: String,
    val nim: String,
    val judulTA: String,
    val tanggalSidang: String, // format "DD-MM-YYYY"
    val jamSidang: String,
    val ruangan: String // DEPRECATED - tidak ada di database
)