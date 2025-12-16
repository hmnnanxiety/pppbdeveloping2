package com.example.penjadwalan_sidang.data.repository

import android.content.Context
import com.example.penjadwalan_sidang.data.model.ReviewThesisRequest
import com.example.penjadwalan_sidang.data.model.ScheduleThesisRequest
import com.example.penjadwalan_sidang.data.model.Thesis
import com.example.penjadwalan_sidang.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository untuk handle Dosen API
 *
 * Fungsi:
 * - getPendingThesis() → GET /api/dosen/pending
 * - getAllThesis() → GET /api/dosen/all
 * - getThesisDetail() → GET /api/dosen/thesis/:id
 * - reviewThesis() → PUT /api/dosen/review/:id
 * - scheduleThesis() → PUT /api/dosen/schedule/:id
 */
class DosenRepository(private val context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    /**
     * Get 5 TA terbaru yang statusnya PENDING
     */
    fun getPendingThesis(
        onSuccess: (List<Thesis>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getPendingThesis().enqueue(object : Callback<List<Thesis>> {
            override fun onResponse(call: Call<List<Thesis>>, response: Response<List<Thesis>>) {
                if (response.isSuccessful) {
                    val thesisList = response.body() ?: emptyList()
                    onSuccess(thesisList)
                } else {
                    handleErrorResponse(response.code(), onError)
                }
            }

            override fun onFailure(call: Call<List<Thesis>>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Get semua TA (untuk list pengajuan)
     */
    fun getAllThesis(
        onSuccess: (List<Thesis>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getAllThesis().enqueue(object : Callback<List<Thesis>> {
            override fun onResponse(call: Call<List<Thesis>>, response: Response<List<Thesis>>) {
                if (response.isSuccessful) {
                    val thesisList = response.body() ?: emptyList()
                    onSuccess(thesisList)
                } else {
                    handleErrorResponse(response.code(), onError)
                }
            }

            override fun onFailure(call: Call<List<Thesis>>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Get detail TA tertentu
     */
    fun getThesisDetail(
        id: String,
        onSuccess: (Thesis) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getThesisDetail(id).enqueue(object : Callback<Thesis> {
            override fun onResponse(call: Call<Thesis>, response: Response<Thesis>) {
                if (response.isSuccessful) {
                    val thesis = response.body()
                    if (thesis != null) {
                        onSuccess(thesis)
                    } else {
                        onError("Data TA tidak ditemukan")
                    }
                } else {
                    handleErrorResponse(response.code(), onError)
                }
            }

            override fun onFailure(call: Call<Thesis>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Review TA (APPROVED/REJECTED/PENDING)
     */
    fun reviewThesis(
        id: String,
        decision: String,
        onSuccess: (Thesis) -> Unit,
        onError: (String) -> Unit
    ) {
        // Validasi decision
        if (decision !in listOf("APPROVED", "REJECTED", "PENDING")) {
            onError("Decision harus APPROVED, REJECTED, atau PENDING")
            return
        }

        val request = ReviewThesisRequest(decision = decision)

        api.reviewThesis(id, request).enqueue(object : Callback<Thesis> {
            override fun onResponse(call: Call<Thesis>, response: Response<Thesis>) {
                if (response.isSuccessful) {
                    val thesis = response.body()
                    if (thesis != null) {
                        onSuccess(thesis)
                    } else {
                        onError("Response kosong dari server")
                    }
                } else {
                    when (response.code()) {
                        404 -> onError("TA tidak ditemukan")
                        else -> handleErrorResponse(response.code(), onError)
                    }
                }
            }

            override fun onFailure(call: Call<Thesis>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Schedule TA (set tanggal sidang)
     */
    fun scheduleThesis(
        id: String,
        date: String, // ISO format: "2025-12-20T10:00:00Z"
        onSuccess: (Thesis) -> Unit,
        onError: (String) -> Unit
    ) {
        // Validasi tanggal (harus masa depan)
        // Note: Validasi detail dilakukan di backend

        val request = ScheduleThesisRequest(date = date)

        api.scheduleThesis(id, request).enqueue(object : Callback<Thesis> {
            override fun onResponse(call: Call<Thesis>, response: Response<Thesis>) {
                if (response.isSuccessful) {
                    val thesis = response.body()
                    if (thesis != null) {
                        onSuccess(thesis)
                    } else {
                        onError("Response kosong dari server")
                    }
                } else {
                    when (response.code()) {
                        400 -> onError("Tanggal tidak valid atau sudah lewat")
                        404 -> onError("TA tidak ditemukan")
                        else -> handleErrorResponse(response.code(), onError)
                    }
                }
            }

            override fun onFailure(call: Call<Thesis>, t: Throwable) {
                onError("Koneksi gagal: ${t.message ?: "Unknown error"}")
            }
        })
    }

    /**
     * Helper function untuk handle error response
     */
    private fun handleErrorResponse(code: Int, onError: (String) -> Unit) {
        val errorMsg = when (code) {
            401 -> "Sesi login telah berakhir"
            403 -> "Anda tidak memiliki akses (hanya dosen)"
            404 -> "Data tidak ditemukan"
            500 -> "Server sedang bermasalah"
            else -> "Error $code"
        }
        onError(errorMsg)
    }
}