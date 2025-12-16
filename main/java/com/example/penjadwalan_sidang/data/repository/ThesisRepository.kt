package com.example.penjadwalan_sidang.data.repository

import android.content.Context
import com.example.penjadwalan_sidang.data.model.CreateThesisRequest
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository untuk handle Thesis API (Mahasiswa)
 *
 * Fungsi:
 * - getMyThesis() → GET /api/thesis/me/all
 * - createThesis() → POST /api/thesis
 */
class ThesisRepository(private val context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    /**
     * Get semua thesis milik mahasiswa login
     */
    fun getMyThesis(
        onSuccess: (List<Thesis>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getMyThesis().enqueue(object : Callback<List<Thesis>> {
            override fun onResponse(call: Call<List<Thesis>>, response: Response<List<Thesis>>) {
                if (response.isSuccessful) {
                    val thesisList = response.body() ?: emptyList()
                    onSuccess(thesisList)
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Sesi login telah berakhir"
                        403 -> "Anda tidak memiliki akses"
                        404 -> "Data tidak ditemukan"
                        500 -> "Server sedang bermasalah"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<Thesis>>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Create thesis baru (Upload TA)
     */
    fun createThesis(
        title: String,
        docUrl: String,
        onSuccess: (Thesis) -> Unit,
        onError: (String) -> Unit
    ) {
        // Validasi input
        if (title.length !in 10..200) {
            onError("Judul TA harus 10-200 karakter")
            return
        }

        if (!docUrl.matches(Regex("^https?://.+"))) {
            onError("URL dokumen tidak valid")
            return
        }

        val request = CreateThesisRequest(title = title, docUrl = docUrl)

        api.createThesis(request).enqueue(object : Callback<Thesis> {
            override fun onResponse(call: Call<Thesis>, response: Response<Thesis>) {
                if (response.isSuccessful) {
                    val newThesis = response.body()
                    if (newThesis != null) {
                        onSuccess(newThesis)
                    } else {
                        onError("Response kosong dari server")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Data tidak valid"
                        401 -> "Sesi login telah berakhir"
                        403 -> "Hanya mahasiswa yang dapat mengajukan TA"
                        500 -> "Server sedang bermasalah"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<Thesis>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }
}