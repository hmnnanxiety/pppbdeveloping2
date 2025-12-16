package com.example.penjadwalan_sidang.data.remote

import android.content.Context
import com.example.penjadwalan_sidang.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Auth Interceptor untuk:
 * 1. Auto inject token ke setiap request
 * 2. Handle 401/403 (auto logout)
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    private val sessionManager by lazy { SessionManager(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. Ambil token dari SessionManager
        val token = sessionManager.getToken()

        // 2. Inject token ke header (jika ada)
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        // 3. Proceed request
        val response = chain.proceed(newRequest)

        // 4. Handle 401/403 (Token invalid/expired)
        if (response.code == 401 || response.code == 403) {
            // Auto logout (hapus session)
            sessionManager.logout()

            // Note: Redirect ke LoginScreen akan di-handle oleh MainActivity
            // karena MainActivity cek sessionManager.isLogin() di startDestination
        }

        return response
    }
}