package com.example.penjadwalan_sidang.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.penjadwalan_sidang.screens.login.LoginScreen
import com.example.penjadwalan_sidang.screens.mahasiswa.DashboardScreen
import com.example.penjadwalan_sidang.screens.mahasiswa.FormPengajuanScreen
import com.example.penjadwalan_sidang.screens.mahasiswa.ProfileScreen

@Composable
fun NavGraphMahasiswa(navController: NavHostController, onLogout: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") {
            DashboardScreen(
                onNavigateToForm = {
                    navController.navigate("form_pengajuan")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onLogout = onLogout
            )
        }
        composable("form_pengajuan") {
            FormPengajuanScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateLogout = {
                    onLogout()
                }
            )
        }


        composable("profile") {
            ProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToForm = {
                    navController.navigate("form_pengajuan")
                },
                onLogout = onLogout
            )
        }
    }
}