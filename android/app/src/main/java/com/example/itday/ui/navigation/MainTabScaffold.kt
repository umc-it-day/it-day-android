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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainTabScaffold() {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    BackHandler(enabled = currentRoute != null && currentRoute != AppRoute.HOME.route) {
        tabNavController.navigate(AppRoute.HOME.route) {
            popUpTo(AppRoute.HOME.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomTabItems.forEach { item ->
                    NavigationBarItem(
                        selected =
                            currentDestination
                                ?.hierarchy
                                ?.any { destination -> destination.route == item.route.route } == true,
                        onClick = {
                            tabNavController.navigate(item.route.route) {
                                popUpTo(AppRoute.HOME.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(text = item.iconKey)
                        },
                        label = {
                            Text(text = item.label)
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = AppRoute.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppRoute.HOME.route) {
                HomePlaceholderScreen()
            }
            composable(AppRoute.MAP.route) {
                MapPlaceholderScreen()
            }
            composable(AppRoute.REPORT.route) {
                ReportPlaceholderScreen()
            }
            composable(AppRoute.SETTINGS.route) {
                SettingsPlaceholderScreen()
            }
        }
    }
}
