package com.example.penjadwalan_sidang.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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

// ✅ KONSTANTA TRANSISI GLOBAL
private const val TRANSITION_DURATION = 300

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraphDosen(navController: NavHostController, onLogout: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "dashboard_dosen"
    ) {
        // ✅ DASHBOARD DOSEN
        composable(
            "dashboard_dosen",
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) {
            DashboardDosenScreen(
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateToProfil = { navController.navigate("profil_dosen") },
                onNavigateToTerjadwal = { navController.navigate("terjadwal_list") },
                onLogout = onLogout
            )
        }

        // ✅ PENGAJUAN LIST
        composable(
            "pengajuan_list",
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) {
            PengajuanListDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateToProfile = { navController.navigate("profil_dosen") },
                onNavigateToDetail = { pengajuanId -> navController.navigate("pengajuan_detail/$pengajuanId") },
            )
        }

        // ✅ KALENDER DOSEN
        composable(
            "kalender_dosen",
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) {
            KalenderDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToProfile = { navController.navigate("profil_dosen") },
                onNavigateToTerjadwal = { navController.navigate("terjadwal_list") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✅ PROFIL DOSEN
        composable(
            "profil_dosen",
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) {
            ProfileDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✅ TERJADWAL LIST
        composable(
            "terjadwal_list",
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) {
            TerjadwalListDosenScreen(
                onNavigateToDashboard = { navController.navigate("dashboard_dosen") },
                onNavigateToPengajuan = { navController.navigate("pengajuan_list") },
                onNavigateToKalender = { navController.navigate("kalender_dosen") },
                onNavigateToProfile = { navController.navigate("profil_dosen") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✅ DETAIL PENGAJUAN
        composable(
            route = "pengajuan_detail/{pengajuanId}",
            arguments = listOf(
                navArgument("pengajuanId") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) { backStackEntry ->
            val pengajuanId = backStackEntry.arguments?.getString("pengajuanId") ?: ""

            PengajuanDetailScreen(
                id = pengajuanId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJadwal = { mahasiswaId -> navController.navigate("jadwal_sidang/$mahasiswaId") }
            )
        }

        // ✅ JADWAL SIDANG DOSEN
        composable(
            route = "jadwal_sidang/{mahasiswaId}",
            arguments = listOf(
                navArgument("mahasiswaId") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            exitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_DURATION)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_DURATION)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(TRANSITION_DURATION)
                )
            }
        ) { backStackEntry ->
            val mahasiswaId = backStackEntry.arguments?.getString("mahasiswaId") ?: ""

            JadwalSidangDosenScreen(
                mahasiswaId = mahasiswaId,
                onNavigateBack = { navController.popBackStack() },
                onJadwalConfirmed = { _, _, _ ->
                    navController.navigate("dashboard_dosen") {
                        popUpTo("dashboard_dosen") { inclusive = true }
                    }
                }
            )
        }
    }
}