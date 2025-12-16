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
import com.example.penjadwalan_sidang.utils.SessionManager
import com.example.penjadwalan_sidang.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

// GLOBAL VARIABLE UNTUK TOKEN (Tetap pakai ini karena diperlukan beberapa tempat)
object UserSession {
    var token: String? = null
    var role: String? = null
}

@Composable
fun LoginScreen(
    onLoginMahasiswa: () -> Unit,
    onLoginDosen: () -> Unit
) {
    val context = LocalContext.current

    // Sign out dulu untuk memastikan fresh login
    LaunchedEffect(Unit) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
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
        Log.d("LOGIN_DEBUG", "Result Code: ${result.resultCode}")
        Log.d("LOGIN_DEBUG", "Activity.RESULT_OK: ${Activity.RESULT_OK}")
        Log.d("LOGIN_DEBUG", "Activity.RESULT_CANCELED: ${Activity.RESULT_CANCELED}")

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                Log.d("LOGIN_DEBUG", "Result OK. Mencoba ambil data akun...")
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken

                    Log.d("LOGIN_DEBUG", "Token didapat: ${idToken?.take(20)}...")
                    Log.d("LOGIN_DEBUG", "Email: ${account.email}")

                    if (idToken != null) {
                        val email = account.email ?: ""
                        // Sesuaikan dengan backend logic: @mail.ugm.ac.id = DOSEN
                        val role = if (email.endsWith("@mail.ugm.ac.id")) {
                            "DOSEN"
                        } else {
                            "MAHASISWA"
                        }

                        Log.d("LOGIN_DEBUG", "Role ditentukan: $role")

                        // Simpan ke SessionManager
                        val sessionManager = SessionManager(context)
                        sessionManager.saveSession(idToken, role)

                        // Simpan ke UserSession (untuk backward compatibility)
                        UserSession.token = idToken
                        UserSession.role = role

                        // Navigate
                        if (role == "DOSEN") {
                            Toast.makeText(context, "Login Dosen Berhasil!", Toast.LENGTH_SHORT).show()
                            onLoginDosen()
                        } else {
                            Toast.makeText(context, "Login Mahasiswa Berhasil!", Toast.LENGTH_SHORT).show()
                            onLoginMahasiswa()
                        }
                    } else {
                        Log.e("LOGIN_DEBUG", "Token KOSONG (Null)")
                        Toast.makeText(context, "Gagal: Token Google Kosong", Toast.LENGTH_LONG).show()
                    }
                } catch (e: ApiException) {
                    Log.e("LOGIN_DEBUG", "Google Sign-In Error")
                    Log.e("LOGIN_DEBUG", "Status Code: ${e.statusCode}")
                    Log.e("LOGIN_DEBUG", "Status Message: ${e.statusMessage}")
                    Log.e("LOGIN_DEBUG", "Error: ${e.message}")

                    val errorMsg = when (e.statusCode) {
                        10 -> "Developer Error: Periksa SHA-1 fingerprint dan Client ID di Google Console"
                        12501 -> "Login dibatalkan oleh user"
                        12500 -> "Konfigurasi Google Sign-In salah"
                        else -> "Error Code: ${e.statusCode}"
                    }

                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            Activity.RESULT_CANCELED -> {
                Log.e("LOGIN_DEBUG", "Login Dibatalkan oleh User")
                Toast.makeText(context, "Login Dibatalkan", Toast.LENGTH_SHORT).show()
            }

            0 -> {
                Log.e("LOGIN_DEBUG", "Result Code 0 - Kemungkinan masalah konfigurasi Google Sign-In")
                Log.e("LOGIN_DEBUG", "Periksa: 1) SHA-1 Fingerprint, 2) Client ID, 3) Package Name")
                Toast.makeText(
                    context,
                    "Konfigurasi Error: Periksa SHA-1 & Client ID di Google Console",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Log.e("LOGIN_DEBUG", "Unknown Result Code: ${result.resultCode}")
                Toast.makeText(context, "Error tidak dikenal: ${result.resultCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFECDBD4)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
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
                        Log.d("LOGIN_DEBUG", "Tombol Login diklik")
                        Log.d("LOGIN_DEBUG", "Client ID: ${Constants.GOOGLE_CLIENT_ID}")

                        try {
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        } catch (e: Exception) {
                            Log.e("LOGIN_DEBUG", "Error saat launch intent: ${e.message}")
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Text("Login dengan Google UGM")
                }

                // Debug Info (hapus setelah berhasil)
                Text(
                    text = "Debug: Package = com.example.penjadwalan_sidang",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}