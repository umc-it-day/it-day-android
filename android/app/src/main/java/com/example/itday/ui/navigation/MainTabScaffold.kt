package com.example.itday.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.itday.core.di.appContainer
import com.example.itday.feature.report.presentation.ReportScreen
import com.example.itday.ui.home.HomeRoute
import com.example.itday.ui.theme.HomeNavigationMuted
import com.example.itday.ui.theme.HomeNavigationSelected
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun MainTabScaffold() {
    val context = LocalContext.current
    val isGuestMode by
        context.appContainer.localPreferencesDataSource.isGuestMode
            .collectAsState(initial = false)
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    BackHandler(enabled = currentRoute != null && currentRoute != AppRoute.HOME.route) {
        tabNavController.navigateToHome()
    }

    Scaffold(
        containerColor = ItDayWhite,
        bottomBar = {
            MainBottomNavigationBar(
                currentDestination = currentDestination,
                onTabClick = { route -> tabNavController.navigateToTopLevelRoute(route) },
            )
        },
    ) { innerPadding ->
        MainTabNavHost(
            navController = tabNavController,
            isGuestMode = isGuestMode,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun MainBottomNavigationBar(
    currentDestination: NavDestination?,
    onTabClick: (AppRoute) -> Unit,
) {
    NavigationBar(containerColor = ItDayWhite) {
        bottomTabItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination.isRouteInHierarchy(item.route),
                onClick = { onTabClick(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp),
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = HomeNavigationSelected,
                        selectedTextColor = HomeNavigationSelected,
                        unselectedIconColor = HomeNavigationMuted,
                        unselectedTextColor = HomeNavigationMuted,
                        indicatorColor = Color.Transparent,
                    ),
            )
        }
    }
}

@Composable
private fun MainTabNavHost(
    navController: NavHostController,
    isGuestMode: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.HOME.route,
        modifier = modifier,
    ) {
        composable(AppRoute.HOME.route) {
            HomeRoute(isGuestMode = isGuestMode)
        }
        composable(AppRoute.MAP.route) {
            MapPlaceholderScreen()
        }
        composable(AppRoute.REPORT.route) {
            ReportScreen()
        }
        composable(AppRoute.SETTINGS.route) {
            SettingsPlaceholderScreen()
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
