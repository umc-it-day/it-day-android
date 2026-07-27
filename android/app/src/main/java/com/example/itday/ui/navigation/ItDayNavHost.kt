package com.example.itday.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itday.core.di.appContainer
import com.example.itday.feature.onboarding.presentation.OnboardingScreen
import com.example.itday.feature.onboarding.presentation.OnboardingViewModel
import com.example.itday.ui.start.LoginEvent
import com.example.itday.ui.start.LoginScreen
import com.example.itday.ui.start.LoginViewModel
import com.example.itday.ui.start.SessionDestination
import com.example.itday.ui.start.SessionViewModel
import com.example.itday.ui.start.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun ItDayNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH.route,
    ) {
        composable(AppRoute.SPLASH.route) {
            SplashDestination(navController = navController)
        }
        composable(AppRoute.LOGIN.route) {
            LoginDestination(navController = navController)
        }
        composable(AppRoute.ONBOARDING.route) {
            OnboardingDestination(navController = navController)
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
private fun SplashDestination(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionViewModel: SessionViewModel =
        viewModel(
            factory = SessionViewModel.Factory(context.appContainer.localPreferencesDataSource),
        )

    SplashScreen(
        onNavigateNext = {
            coroutineScope.launch {
                val route =
                    when (sessionViewModel.resolveDestination()) {
                        SessionDestination.Login -> AppRoute.LOGIN
                        SessionDestination.Onboarding -> AppRoute.ONBOARDING
                        SessionDestination.Main -> AppRoute.MAIN
                    }
                navController.navigate(route.route) {
                    popUpTo(AppRoute.SPLASH.route) { inclusive = true }
                }
            }
        },
    )
}

@Composable
private fun LoginDestination(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val localPreferencesDataSource = context.appContainer.localPreferencesDataSource
    val loginViewModel: LoginViewModel =
        viewModel(
            factory =
                LoginViewModel.Factory(
                    context.appContainer.kakaoLoginClient,
                    localPreferencesDataSource,
                ),
        )
    val sessionViewModel: SessionViewModel =
        viewModel(factory = SessionViewModel.Factory(localPreferencesDataSource))
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
            coroutineScope.launch {
                sessionViewModel.enterGuestMode()
                navController.navigate(AppRoute.MAIN.route) {
                    popUpTo(AppRoute.LOGIN.route) { inclusive = true }
                }
            }
        },
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
    )
}

@Composable
private fun OnboardingDestination(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionViewModel: SessionViewModel =
        viewModel(
            factory = SessionViewModel.Factory(context.appContainer.localPreferencesDataSource),
        )
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val uiState by onboardingViewModel.uiState.collectAsState()

    OnboardingScreen(
        state = uiState,
        events = onboardingViewModel.events,
        onAgreementChange = onboardingViewModel::setAgreement,
        onClose = { navController.popBackStack() },
        onBack = onboardingViewModel::back,
        onNext = onboardingViewModel::next,
        onLocationResult = onboardingViewModel::onLocationResult,
        onCarrierSelect = onboardingViewModel::selectCarrier,
        onMembershipSelect = onboardingViewModel::selectMembership,
        onBrandToggle = onboardingViewModel::toggleBrand,
        onComplete = {
            coroutineScope.launch {
                sessionViewModel.completeOnboarding()
                navController.navigate(AppRoute.MAIN.route) {
                    popUpTo(AppRoute.ONBOARDING.route) { inclusive = true }
                }
            }
        },
    )
}
