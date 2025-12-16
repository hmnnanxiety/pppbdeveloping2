package com.example.penjadwalan_sidang.data.remote

import android.content.Context
import android.util.Log
import com.example.penjadwalan_sidang.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Auth Interceptor untuk:
 * 1. Auto inject token ke setiap request
 * 2. Handle 401/403 (auto logout)
 * 3. Logging untuk debugging
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    private val sessionManager by lazy { SessionManager(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Log original request
        Log.d("AUTH_INTERCEPTOR", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d("AUTH_INTERCEPTOR", "📤 REQUEST START")
        Log.d("AUTH_INTERCEPTOR", "URL: $url")
        Log.d("AUTH_INTERCEPTOR", "Method: ${originalRequest.method}")

        // 1. Ambil token dari SessionManager
        val token = sessionManager.getToken()

        if (token != null) {
            Log.d("AUTH_INTERCEPTOR", "✅ Token ditemukan (${token.length} chars)")
            Log.d("AUTH_INTERCEPTOR", "Token preview: ${token.take(30)}...")
        } else {
            Log.w("AUTH_INTERCEPTOR", "⚠️ Token TIDAK ditemukan!")
        }

        // 2. Inject token ke header (jika ada)
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        // Log headers yang dikirim
        Log.d("AUTH_INTERCEPTOR", "📋 Request Headers:")
        newRequest.headers.forEach { (name, value) ->
            if (name == "Authorization") {
                Log.d("AUTH_INTERCEPTOR", "  • $name: Bearer ${value.removePrefix("Bearer ").take(30)}...")
            } else {
                Log.d("AUTH_INTERCEPTOR", "  • $name: $value")
            }
        }

        // 3. Proceed request
        val startTime = System.currentTimeMillis()
        val response = chain.proceed(newRequest)
        val duration = System.currentTimeMillis() - startTime

        // Log response
        Log.d("AUTH_INTERCEPTOR", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d("AUTH_INTERCEPTOR", "📥 RESPONSE")
        Log.d("AUTH_INTERCEPTOR", "Status: ${response.code} ${response.message}")
        Log.d("AUTH_INTERCEPTOR", "Duration: ${duration}ms")

        // 4. Handle 401/403 (Token invalid/expired)
        when (response.code) {
            401 -> {
                Log.e("AUTH_INTERCEPTOR", "❌ 401 UNAUTHORIZED")
                Log.e("AUTH_INTERCEPTOR", "Token mungkin invalid atau expired")

                // Peek response body untuk debugging (tanpa consume stream)
                val responseBody = response.peekBody(Long.MAX_VALUE).string()
                Log.e("AUTH_INTERCEPTOR", "Response body: $responseBody")

                // Auto logout
                sessionManager.logout()
                Log.w("AUTH_INTERCEPTOR", "⚠️ Session cleared (auto logout)")
            }
            403 -> {
                Log.e("AUTH_INTERCEPTOR", "❌ 403 FORBIDDEN")
                Log.e("AUTH_INTERCEPTOR", "User tidak punya akses ke resource ini")

                val responseBody = response.peekBody(Long.MAX_VALUE).string()
                Log.e("AUTH_INTERCEPTOR", "Response body: $responseBody")

                // JANGAN auto logout pada 403, karena bisa jadi memang role tidak sesuai
            }
            in 200..299 -> {
                Log.d("AUTH_INTERCEPTOR", "✅ Request SUCCESS!")
            }
            in 400..499 -> {
                Log.w("AUTH_INTERCEPTOR", "⚠️ Client Error: ${response.code}")
            }
            in 500..599 -> {
                Log.e("AUTH_INTERCEPTOR", "❌ Server Error: ${response.code}")
            }
            else -> {
                Log.w("AUTH_INTERCEPTOR", "⚠️ Unexpected response code: ${response.code}")
            }
        }

        Log.d("AUTH_INTERCEPTOR", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

        return response
    }
}