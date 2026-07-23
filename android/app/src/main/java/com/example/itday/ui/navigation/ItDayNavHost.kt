package com.example.itday.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itday.ui.start.LoginScreen
import com.example.itday.ui.start.SplashScreen

@Composable
fun ItDayNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH.route,
    ) {
        composable(AppRoute.SPLASH.route) {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(AppRoute.LOGIN.route) {
                        popUpTo(AppRoute.SPLASH.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(AppRoute.LOGIN.route) {
            LoginScreen(
                onKakaoLoginClick = {
                    navController.navigate(AppRoute.ONBOARDING.route)
                },
                onGuestClick = {
                    navController.navigate(AppRoute.MAIN.route) {
                        popUpTo(AppRoute.LOGIN.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppRoute.ONBOARDING.route) {
            OnboardingPlaceholderScreen(
                onStartClick = {
                    navController.navigate(AppRoute.MAIN.route) {
                        popUpTo(AppRoute.LOGIN.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(AppRoute.MAIN.route) {
            MainTabScaffold()
        }
        composable(AppRoute.BARCODE.route) {
            BarcodePlaceholderScreen()
        }
    }
}
