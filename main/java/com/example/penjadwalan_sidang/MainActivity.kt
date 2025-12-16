package com.example.penjadwalan_sidang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.penjadwalan_sidang.navigation.NavGraphDosen
import com.example.penjadwalan_sidang.navigation.NavGraphMahasiswa
import com.example.penjadwalan_sidang.screens.login.LoginScreen
import com.example.penjadwalan_sidang.screens.login.UserSession
import com.example.penjadwalan_sidang.ui.theme.PengajuanTheme
import com.example.penjadwalan_sidang.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PengajuanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val navController = rememberNavController()
    val sessionManager = remember { SessionManager(context) }

    // Tentukan startDestination secara dinamis
    val startDest = if (sessionManager.isLogin()) {
        if (sessionManager.getRole() == "DOSEN") "dosen_route" else "mahasiswa_route"
    } else {
        "login_route"
    }

    // Isi UserSession global biar API tetap jalan
    if (sessionManager.isLogin()) {
        UserSession.token = sessionManager.getToken()
        UserSession.role = sessionManager.getRole()
    }

    NavHost(navController = navController, startDestination = startDest) {

        // 1. Rute Login
        composable("login_route") {
            LoginScreen(
                onLoginMahasiswa = { navController.navigate("mahasiswa_route") },
                onLoginDosen = { navController.navigate("dosen_route") }
            )
        }

        // 2. Rute Mahasiswa
        composable("mahasiswa_route") {
            NavGraphMahasiswa(
                navController = rememberNavController(),
                onLogout = {
                    sessionManager.logout()
                    navController.navigate("login_route") {
                        popUpTo(0)
                    }
                }
            )
        }

        // 3. Rute Dosen
        composable("dosen_route") {
            NavGraphDosen(
                navController = rememberNavController(),
                onLogout = {
                    sessionManager.logout()
                    navController.navigate("login_route") {
                        popUpTo("dosen_route") { inclusive = true }
                    }
                }
            )
        }
    }
}