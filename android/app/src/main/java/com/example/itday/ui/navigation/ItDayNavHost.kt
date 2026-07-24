package com.example.itday.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itday.core.di.appContainer
import com.example.itday.ui.start.LoginEvent
import com.example.itday.ui.start.LoginScreen
import com.example.itday.ui.start.LoginViewModel
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
            LoginDestination(navController = navController)
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

@Composable
private fun LoginDestination(navController: NavHostController) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel =
        viewModel(
            factory = LoginViewModel.Factory(context.appContainer.kakaoLoginClient),
        )
    val uiState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(loginViewModel) {
        loginViewModel.events.collect { event ->
            when (event) {
                LoginEvent.Authenticated ->
                    navController.navigate(AppRoute.ONBOARDING.route) {
                        popUpTo(AppRoute.LOGIN.route) { inclusive = true }
                    }
            }
        }
    }

    LoginScreen(
        onKakaoLoginClick = { loginViewModel.loginWithKakao(context) },
        onGuestClick = {
            navController.navigate(AppRoute.MAIN.route) {
                popUpTo(AppRoute.LOGIN.route) { inclusive = true }
            }
        },
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
    )
}
