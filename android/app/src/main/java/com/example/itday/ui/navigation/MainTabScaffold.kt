package com.example.itday.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.itday.BuildConfig
import com.example.itday.feature.payment.presentation.DemoPaymentGateway
import com.example.itday.feature.payment.presentation.PaymentFlowScreen
import com.example.itday.feature.payment.presentation.PaymentViewModel
import com.example.itday.feature.report.presentation.ReportScreen

@Composable
fun MainTabScaffold() {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    BackHandler(
        enabled =
            currentRoute != null &&
                currentRoute != AppRoute.HOME.route &&
                currentRoute != AppRoute.PAYMENT.route,
    ) {
        tabNavController.navigateToHome()
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != AppRoute.PAYMENT.route) {
                MainBottomNavigationBar(
                    currentDestination = currentDestination,
                    onTabClick = { route -> tabNavController.navigateToTopLevelRoute(route) },
                )
            }
        },
    ) { innerPadding ->
        MainTabNavHost(
            navController = tabNavController,
            modifier =
                if (currentRoute == AppRoute.PAYMENT.route) {
                    Modifier
                } else {
                    Modifier.padding(innerPadding)
                },
        )
    }
}

@Composable
private fun MainBottomNavigationBar(
    currentDestination: NavDestination?,
    onTabClick: (AppRoute) -> Unit,
) {
    NavigationBar {
        bottomTabItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination.isRouteInHierarchy(item.route),
                onClick = { onTabClick(item.route) },
                icon = {
                    Text(text = item.iconKey)
                },
                label = {
                    Text(text = item.label)
                },
            )
        }
    }
}

@Composable
private fun MainTabNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.HOME.route,
        modifier = modifier,
    ) {
        composable(AppRoute.HOME.route) {
            HomePlaceholderScreen()
        }
        composable(AppRoute.MAP.route) {
            MapPlaceholderScreen()
        }
        composable(AppRoute.REPORT.route) {
            ReportScreen()
        }
        composable(AppRoute.SETTINGS.route) {
            SettingsPlaceholderScreen(
                onPaymentClick =
                    if (BuildConfig.DEBUG) {
                        {
                            navController.navigate(AppRoute.PAYMENT.route) {
                                launchSingleTop = true
                            }
                        }
                    } else {
                        null
                    },
            )
        }
        if (BuildConfig.DEBUG) {
            composable(AppRoute.PAYMENT.route) {
                val paymentViewModel: PaymentViewModel =
                    viewModel(
                        factory = PaymentViewModel.factory(DemoPaymentGateway()),
                    )
                PaymentFlowScreen(
                    viewModel = paymentViewModel,
                    onExit = { navController.popBackStack() },
                    onHome = { navController.navigateToHome() },
                    onChallenges = {},
                )
            }
        }
    }
}

private fun NavHostController.navigateToHome() {
    navigateToTopLevelRoute(AppRoute.HOME)
}

private fun NavHostController.navigateToTopLevelRoute(route: AppRoute) {
    navigate(route.route) {
        popUpTo(AppRoute.HOME.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavDestination?.isRouteInHierarchy(route: AppRoute): Boolean =
    this
        ?.hierarchy
        ?.any { destination -> destination.route == route.route } == true
