package com.example.itday.ui.navigation

data class BottomTabItem(
    val route: AppRoute,
    val label: String,
    val iconKey: String,
)

val bottomTabItems =
    listOf(
        BottomTabItem(
            route = AppRoute.HOME,
            label = "홈",
            iconKey = "home",
        ),
        BottomTabItem(
            route = AppRoute.MAP,
            label = "지도",
            iconKey = "map",
        ),
        BottomTabItem(
            route = AppRoute.REPORT,
            label = "리포트",
            iconKey = "report",
        ),
        BottomTabItem(
            route = AppRoute.SETTINGS,
            label = "설정",
            iconKey = "settings",
        ),
    )
