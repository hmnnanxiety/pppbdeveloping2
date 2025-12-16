package com.example.penjadwalan_sidang.data.remote

import android.content.Context
import com.example.penjadwalan_sidang.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client Singleton
 *
 * CHANGES:
 * ✅ Pakai AuthInterceptor untuk auto inject token
 * ✅ Tambah timeout configuration
 * ✅ Better logging interceptor
 */
object RetrofitClient {

    @Volatile
    private var apiService: ApiService? = null

    fun getInstance(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: buildApiService(context).also { apiService = it }
        }
    }

    private fun buildApiService(context: Context): ApiService {
        // 1. Logging Interceptor (untuk debug)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. Auth Interceptor (auto inject token)
        val authInterceptor = AuthInterceptor(context.applicationContext)

        // 3. OkHttpClient dengan interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)      // Inject token
            .addInterceptor(loggingInterceptor)   // Logging
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout koneksi
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout baca
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout tulis
            .build()

        // 4. Retrofit Instance
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Reset instance (untuk logout atau testing)
     */
    fun reset() {
        apiService = null
    }
}