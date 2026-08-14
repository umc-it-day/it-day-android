package com.umc.itday.ui.navigation

import androidx.annotation.DrawableRes
import com.umc.itday.R

data class BottomTabItem(
    val route: AppRoute,
    val label: String,
    @DrawableRes val iconRes: Int,
)

val bottomTabItems =
    listOf(
        BottomTabItem(
            route = AppRoute.HOME,
            label = "홈",
            iconRes = R.drawable.ic_nav_home,
        ),
        BottomTabItem(
            route = AppRoute.MAP,
            label = "지도",
            iconRes = R.drawable.ic_nav_map,
        ),
        BottomTabItem(
            route = AppRoute.REPORT,
            label = "리포트",
            iconRes = R.drawable.ic_nav_report,
        ),
        BottomTabItem(
            route = AppRoute.SETTINGS,
            label = "설정",
            iconRes = R.drawable.ic_nav_settings,
        ),
    )
