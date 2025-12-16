package com.example.penjadwalan_sidang.screens.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.penjadwalan_sidang.utils.Constants
import com.example.penjadwalan_sidang.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onLoginMahasiswa: () -> Unit,
    onLoginDosen: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // 🔥 ENHANCED LOGGING
    LaunchedEffect(Unit) {
        Log.d("LOGIN_INIT", "=== LOGIN SCREEN INITIALIZED ===")
        Log.d("LOGIN_INIT", "Google Client ID: ${Constants.GOOGLE_CLIENT_ID}")
        Log.d("LOGIN_INIT", "Base URL: ${Constants.BASE_URL}")
    }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("LOGIN_RESULT", "=== ACTIVITY RESULT RECEIVED ===")
        Log.d("LOGIN_RESULT", "Result Code: ${result.resultCode}")
        Log.d("LOGIN_RESULT", "RESULT_OK: ${Activity.RESULT_OK}")
        Log.d("LOGIN_RESULT", "RESULT_CANCELED: ${Activity.RESULT_CANCELED}")

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                Log.d("LOGIN_SUCCESS", "✅ User selected account")

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    val email = account.email
                    val displayName = account.displayName

                    Log.d("LOGIN_SUCCESS", "Account email: $email")
                    Log.d("LOGIN_SUCCESS", "Display name: $displayName")
                    Log.d("LOGIN_SUCCESS", "Token exists: ${idToken != null}")
                    Log.d("LOGIN_SUCCESS", "Token preview: ${idToken?.take(30)}...")

                    if (idToken != null) {
                        // Determine role dari email
                        val role = if (email?.contains("dosen", ignoreCase = true) == true ||
                            email?.contains("afif", ignoreCase = true) == true) {
                            "DOSEN"
                        } else {
                            "MAHASISWA"
                        }

                        Log.d("LOGIN_SUCCESS", "Determined role: $role")

                        // Save session
                        sessionManager.saveSession(idToken, role)

                        Toast.makeText(
                            context,
                            "✅ Login berhasil sebagai $role",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate
                        if (role == "DOSEN") {
                            onLoginDosen()
                        } else {
                            onLoginMahasiswa()
                        }
                    } else {
                        Log.e("LOGIN_ERROR", "❌ ID Token is NULL")
                        Toast.makeText(
                            context,
                            "❌ Gagal mendapatkan token. Coba lagi.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: ApiException) {
                    Log.e("LOGIN_ERROR", "❌ ApiException: ${e.statusCode}")
                    Log.e("LOGIN_ERROR", "Message: ${e.message}")
                    Log.e("LOGIN_ERROR", "Stack trace:", e)

                    val errorMessage = when (e.statusCode) {
                        10 -> "Developer error: Check google-services.json"
                        12501 -> "Login dibatalkan oleh user"
                        12500 -> "Sign-in failed: Check SHA-1 fingerprint"
                        else -> "Error ${e.statusCode}: ${e.message}"
                    }

                    Toast.makeText(
                        context,
                        "❌ $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            Activity.RESULT_CANCELED -> {
                Log.w("LOGIN_CANCELED", "⚠️ User cancelled sign-in")
                Toast.makeText(
                    context,
                    "Login dibatalkan",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Log.e("LOGIN_ERROR", "❌ Unknown result code: ${result.resultCode}")
                Toast.makeText(
                    context,
                    "❌ Login gagal (code: ${result.resultCode})",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECDBD4)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Selamat Datang\nPenjadwalan Sidang",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        Log.d("LOGIN_CLICK", "🔘 Login button clicked")

                        // Sign out dulu untuk clear cache
                        googleSignInClient.signOut().addOnCompleteListener {
                            Log.d("LOGIN_CLICK", "Previous account signed out")

                            // Launch sign-in
                            val signInIntent = googleSignInClient.signInIntent
                            Log.d("LOGIN_CLICK", "Launching Google Sign-In Intent")
                            launcher.launch(signInIntent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4)
                    )
                ) {
                    Text("Login dengan Google UGM", fontSize = 16.sp)
                }

                // Debug info (remove in production)
                if (Constants.BASE_URL.contains("localhost")) {
                    Text(
                        text = "⚠️ DEV MODE",
                        fontSize = 10.sp,
                        color = Color.Red
                    )
                }
            }
        }
    }
}