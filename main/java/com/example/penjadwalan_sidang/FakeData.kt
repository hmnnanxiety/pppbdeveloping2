//package com.example.penjadwalan_sidang
//
//import androidx.compose.runtime.mutableStateListOf
//import com.google.gson.annotations.SerializedName
//
//// 1. Model Data (Dipindah ke sini agar bisa dipakai semua layar)
//data class PengajuanMahasiswa(
//    @SerializedName("id")
//    val id: String,
//
//    @SerializedName("title") // Server bilang "title"
//    val judulTA: String,     // UI kamu taunya "judulTA" (Jadi otomatis diterjemahkan)
//
//    @SerializedName("status")
//    val status: String,      // PENDING, APPROVED, REJECTED
//
//    @SerializedName("createdAt") // Server bilang "createdAt"
//    val tanggalPengajuan: String, // UI kamu taunya "tanggalPengajuan"
//
//    // Field Tambahan (Boleh null karena kadang server gak ngirim ini di list tertentu)
//    @SerializedName("docUrl")
//    val linkDoc: String? = null,
//
//    // Nama & NIM biasanya ada di object "student", tapi untuk Dashboard Mhs
//    // kita bisa pakai default user login dulu atau abaikan sementara.
//    val namaMahasiswa: String = "Saya",
//    val nim: String = "-"
//)
//
//// 2. Repository Pusat (Jembatan Data)
//object FakeData {
//    // List ini "hidup" selama aplikasi nyala. Menggunakan mutableStateListOf agar UI otomatis update.
//    val pengajuanList = mutableStateListOf<PengajuanMahasiswa>(
//        // Data Awal (Dummy biar dosen gak kaget kosong)
//        PengajuanMahasiswa("1", "Budi Santoso", "210401", "Analisis Jaringan IDS", "20-10-2025", "Menunggu"),
//        PengajuanMahasiswa("2", "Siti Aminah", "210402", "E-Commerce Batik", "21-10-2025", "Menunggu"),
//        PengajuanMahasiswa("3", "Ahmad Dhani", "210403", "Sistem Pakar Musik", "22-10-2025", "Ditolak")
//    )
//
//    // Fungsi untuk Mahasiswa menambah data
//    fun tambahPengajuan(judul: String, link: String) {
//        val newId = (pengajuanList.size + 1).toString()
//        val newItem = PengajuanMahasiswa(
//            id = newId,
//            namaMahasiswa = "Fatih Gantenk", // Hardcode sesuai profil mahasiswa saat ini
//            nim = "210401010",
//            judulTA = judul,
//            tanggalPengajuan = "Hari Ini", // Simulasi tanggal
//            status = "Menunggu"
//        )
//        // Tambahkan ke urutan paling atas (index 0)
//        pengajuanList.add(0, newItem)
//    }
//}