package com.example.itday.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ItDayNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH.route,
    ) {
        composable(AppRoute.SPLASH.route) {
            SplashPlaceholderScreen(
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
            LoginPlaceholderScreen(
                onLoginClick = {
                    navController.navigate(AppRoute.ONBOARDING.route)
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
