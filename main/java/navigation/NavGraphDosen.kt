package com.example.penjadwalan_sidang.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.penjadwalan_sidang.screens.dosen.DashboardDosenScreen
import com.example.penjadwalan_sidang.screens.dosen.PengajuanListDosenScreen
import com.example.penjadwalan_sidang.screens.dosen.KalenderDosenScreen
import com.example.penjadwalan_sidang.screens.dosen.ProfileDosenScreen
import com.example.penjadwalan_sidang.screens.dosen.TerjadwalListDosenScreen
import com.example.penjadwalan_sidang.screens.dosen.PengajuanDetailScreen
import com.example.penjadwalan_sidang.screens.dosen.JadwalSidangDosenScreen

@Composable
fun NavGraphDosen(navController: NavHostController, onLogout: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "dashboard_dosen"
    ) {

        // =====================================
        // RUTE 1: DASHBOARD
        // =====================================
        composable("dashboard_dosen") {
            DashboardDosenScreen(
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateToProfil = { navController.navigate("profil_dosen") },
                onNavigateToTerjadwal = { navController.navigate("terjadwal_list") },
                onLogout = onLogout
            )
        }

        // =====================================
        // RUTE 2: PENGAJUAN LIST
        // =====================================
        composable("pengajuan_list") {
            PengajuanListDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateToProfile = { navController.navigate("profil_dosen") },
                onNavigateToDetail = { pengajuanId -> navController.navigate("pengajuan_detail/$pengajuanId") },
            )
        }

        // =====================================
        // RUTE 3: KALENDER DOSEN
        // =====================================
        composable("kalender_dosen") {
            KalenderDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToProfile = { navController.navigate("profil_dosen") },
                onNavigateToTerjadwal = { navController.navigate("terjadwal_list") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // =====================================
        // RUTE 4: PROFIL DOSEN
        // =====================================
        composable("profil_dosen") {
            ProfileDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // =====================================
        // RUTE 5: TERJADWAL LIST
        // =====================================
        composable("terjadwal_list") {
            TerjadwalListDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateToProfile = { navController.navigate("profil_dosen") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // =====================================
        // RUTE 6: DETAIL PENGAJUAN
        // =====================================
        composable(
            route = "pengajuan_detail/{pengajuanId}",
            arguments = listOf(
                navArgument("pengajuanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pengajuanId = backStackEntry.arguments?.getString("pengajuanId") ?: ""

            PengajuanDetailScreen(
                id = pengajuanId,
                onNavigateBack = { navController.popBackStack() },
                // Meneruskan navigasi ke halaman jadwal dengan ID
                onNavigateToJadwal = { mahasiswaId -> navController.navigate("jadwal_sidang/$mahasiswaId") }
            )
        }

        // =====================================
        // RUTE 7: JADWAL SIDANG DOSEN
        // =====================================
        composable(
            route = "jadwal_sidang/{mahasiswaId}",
            arguments = listOf(
                navArgument("mahasiswaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mahasiswaId = backStackEntry.arguments?.getString("mahasiswaId") ?: ""

            JadwalSidangDosenScreen(
                mahasiswaId = mahasiswaId,
                onNavigateBack = { navController.popBackStack() },
                // Menerima 3 argumen (Tanggal, Jam, Ruangan) dan menavigasi kembali ke Dashboard
                onJadwalConfirmed = { _, _, _ ->
                    navController.navigate("dashboard_dosen") {
                        popUpTo("dashboard_dosen") { inclusive = true }
                    }
                }
            )
        }
    }
}