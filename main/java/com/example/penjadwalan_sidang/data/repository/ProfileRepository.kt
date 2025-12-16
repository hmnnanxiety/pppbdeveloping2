package com.example.penjadwalan_sidang.data.repository

import android.content.Context
import com.example.penjadwalan_sidang.data.model.UpdateProfileRequest
import com.example.penjadwalan_sidang.data.model.User
import com.example.penjadwalan_sidang.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository untuk handle Profile API (Mahasiswa & Dosen)
 *
 * Fungsi:
 * - getMyProfile() → GET /api/profile/me
 * - updateProfile() → POST /api/profile
 */
class ProfileRepository(private val context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    /**
     * Get profile user yang sedang login
     */
    fun getMyProfile(
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getMyProfile().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onError("Data profil kosong")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Sesi login telah berakhir"
                        403 -> "Anda tidak memiliki akses"
                        404 -> "Profil tidak ditemukan"
                        500 -> "Server sedang bermasalah"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Update profile user (nama & prodi)
     */
    fun updateProfile(
        name: String,
        prodi: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        // Validasi input
        if (name.trim().length !in 2..100) {
            onError("Nama harus 2-100 karakter")
            return
        }

        if (prodi.trim().length !in 2..100) {
            onError("Program studi harus 2-100 karakter")
            return
        }

        val request = UpdateProfileRequest(name = name.trim(), prodi = prodi.trim())

        api.updateProfile(request).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    if (updatedUser != null) {
                        onSuccess(updatedUser)
                    } else {
                        onError("Response kosong dari server")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Data tidak valid"
                        401 -> "Sesi login telah berakhir"
                        403 -> "Anda tidak memiliki akses"
                        500 -> "Server sedang bermasalah"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }
}