package com.example.penjadwalan_sidang.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.penjadwalan_sidang.screens.mahasiswa.DashboardScreen
import com.example.penjadwalan_sidang.screens.mahasiswa.FormPengajuanScreen
import com.example.penjadwalan_sidang.screens.mahasiswa.ProfileScreen

// ✅ KONSTANTA TRANSISI GLOBAL
private const val TRANSITION_DURATION = 300

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraphMahasiswa(navController: NavHostController, onLogout: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        // ✅ DASHBOARD dengan transisi fade + slide
        composable(
            "dashboard",
            enterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            }
        ) {
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

        // ✅ FORM PENGAJUAN dengan transisi fade + slide
        composable(
            "form_pengajuan",
            enterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            }
        ) {
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

        // ✅ PROFILE dengan transisi fade + slide
        composable(
            "profile",
            enterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(TRANSITION_DURATION)
                        )
            }
        ) {
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