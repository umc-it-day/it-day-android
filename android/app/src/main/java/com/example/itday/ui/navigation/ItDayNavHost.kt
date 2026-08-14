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

import com.example.itday.feature.barcode.presentation.BarcodeRegistrationRoute

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
            MainDestination(navController = navController)
        }
        composable(AppRoute.BARCODE.route) {
            BarcodeRegistrationRoute(
                onBackClick = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate(AppRoute.MAIN.route) {
                        popUpTo(AppRoute.MAIN.route) { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
private fun SplashDestination(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionViewModel: SessionViewModel =
        viewModel(
            factory =
                SessionViewModel.Factory(
                    context.appContainer.localPreferencesDataSource,
                    context.appContainer.authRepository,
                    context.appContainer.authTokenStorage,
                ),
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
                    context.appContainer.authRepository,
                    localPreferencesDataSource,
                ),
        )
    val sessionViewModel: SessionViewModel =
        viewModel(
            factory =
                SessionViewModel.Factory(
                    localPreferencesDataSource,
                    context.appContainer.authRepository,
                    context.appContainer.authTokenStorage,
                ),
        )
    val uiState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(loginViewModel) {
        loginViewModel.events.collect { event ->
            when (event) {
                is LoginEvent.Authenticated -> {
                    val destination =
                        if (event.isNewUser) AppRoute.ONBOARDING else AppRoute.MAIN
                    navController.navigate(destination.route) {
                        popUpTo(AppRoute.LOGIN.route) { inclusive = true }
                    }
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
private fun MainDestination(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionViewModel: SessionViewModel =
        viewModel(
            factory =
                SessionViewModel.Factory(
                    context.appContainer.localPreferencesDataSource,
                    context.appContainer.authRepository,
                    context.appContainer.authTokenStorage,
                ),
        )

    MainTabScaffold(
        onLogout = {
            coroutineScope.launch {
                sessionViewModel.logout()
                navController.navigate(AppRoute.LOGIN.route) {
                    popUpTo(AppRoute.MAIN.route) { inclusive = true }
                }
            }
        },
        onLogin = {
            coroutineScope.launch {
                sessionViewModel.exitGuestMode()
                navController.navigate(AppRoute.LOGIN.route) {
                    popUpTo(AppRoute.MAIN.route) { inclusive = true }
                }
            }
        },
    )
}

@Composable
private fun OnboardingDestination(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionViewModel: SessionViewModel =
        viewModel(
            factory =
                SessionViewModel.Factory(
                    context.appContainer.localPreferencesDataSource,
                    context.appContainer.authRepository,
                    context.appContainer.authTokenStorage,
                ),
        )
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val uiState by onboardingViewModel.uiState.collectAsState()

    OnboardingScreen(
        state = uiState,
        events = onboardingViewModel.events,
        onAgreementChange = onboardingViewModel::setAgreement,
        onClose = {
            navController.navigate(AppRoute.LOGIN.route) {
                popUpTo(AppRoute.ONBOARDING.route) { inclusive = true }
            }
        },
        onBack = onboardingViewModel::back,
        onNext = onboardingViewModel::next,
        onShowTerms = onboardingViewModel::showTerms,
        onHideTerms = onboardingViewModel::hideTerms,
        onLocationResult = onboardingViewModel::onLocationResult,
        onCarrierSelect = onboardingViewModel::selectCarrier,
        onMembershipGradeSelect = onboardingViewModel::selectMembershipGrade,
        onBrandToggle = onboardingViewModel::toggleBrand,
        permissionManager = context.appContainer.permissionManager,
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
