package com.example.penjadwalan_sidang.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_ROLE = "role" // DOSEN atau MAHASISWA
        const val KEY_IS_LOGIN = "is_login"
    }

    // 1. Simpan Sesi (Dipanggil saat Login Berhasil)
    fun saveSession(token: String, role: String) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_ROLE, role)
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.apply()
    }

    // 2. Ambil Token (Dipanggil saat mau Request API)
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // 3. Ambil Role (Dipanggil saat buka aplikasi pertama kali)
    fun getRole(): String? {
        return prefs.getString(KEY_ROLE, null)
    }

    // 4. Cek apakah sudah login?
    fun isLogin(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGIN, false)
    }

    // 5. Logout (Hapus semua data)
    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}