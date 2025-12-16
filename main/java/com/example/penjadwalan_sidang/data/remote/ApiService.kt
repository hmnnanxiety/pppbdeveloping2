package com.example.penjadwalan_sidang.data.remote

import com.example.penjadwalan_sidang.data.model.*
import retrofit2.Call
import retrofit2.http.*

/**
 * API Service Interface
 *
 * CHANGES:
 * ✅ Ganti model PengajuanMahasiswa → Thesis
 * ✅ Tambah endpoint profile
 * ✅ Tambah endpoint dosen (review, schedule, detail)
 * ✅ Remove @Header("Authorization") karena sudah di-handle AuthInterceptor
 */
interface ApiService {

    // ==========================================
    // HEALTH CHECK
    // ==========================================

    @GET("health")
    fun healthCheck(): Call<Map<String, Any>>

    // ==========================================
    // PROFILE (MAHASISWA & DOSEN)
    // ==========================================

    @GET("api/profile/me")
    fun getMyProfile(): Call<User>

    @POST("api/profile")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<User>

    // ==========================================
    // THESIS - MAHASISWA
    // ==========================================

    @GET("api/thesis/me/all")
    fun getMyThesis(): Call<List<Thesis>>

    @POST("api/thesis")
    fun createThesis(@Body request: CreateThesisRequest): Call<Thesis>

    // ==========================================
    // THESIS - DOSEN
    // ==========================================

    @GET("api/dosen/pending")
    fun getPendingThesis(): Call<List<Thesis>>

    @GET("api/dosen/all")
    fun getAllThesis(): Call<List<Thesis>>

    @GET("api/dosen/thesis/{id}")
    fun getThesisDetail(@Path("id") id: String): Call<Thesis>

    @PUT("api/dosen/review/{id}")
    fun reviewThesis(
        @Path("id") id: String,
        @Body request: ReviewThesisRequest
    ): Call<Thesis>

    @PUT("api/dosen/schedule/{id}")
    fun scheduleThesis(
        @Path("id") id: String,
        @Body request: ScheduleThesisRequest
    ): Call<Thesis>
}