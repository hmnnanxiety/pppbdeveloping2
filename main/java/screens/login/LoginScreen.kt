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

    // Client ID Google OAuth
    val webClientId = "191478654932-3k8448tve2cpp7o9dh7ed9vmg2ti6sbc.apps.googleusercontent.com"

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
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

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("LOGIN_DEBUG", "Result OK. Mencoba ambil data akun...")

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                Log.d("LOGIN_DEBUG", "Token didapat: $idToken")

                if (idToken != null) {
                    val email = account.email ?: ""
                    val role = if (email.contains("dosen") || email.contains("afif")) "DOSEN" else "MAHASISWA"

                    Log.d("LOGIN_DEBUG", "Email: $email, Role: $role")

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
                Log.e("LOGIN_DEBUG", "Google Error Code: ${e.statusCode}")
                Toast.makeText(context, "Google Error: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("LOGIN_DEBUG", "Login Dibatalkan / Result Canceled")
            Toast.makeText(context, "Login Dibatalkan (Result Code: ${result.resultCode})", Toast.LENGTH_SHORT).show()
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
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Text("Login dengan Google UGM")
                }
            }
        }
    }
}