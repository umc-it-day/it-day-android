package com.example.itday.feature.report.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.itday.R
import com.example.itday.feature.report.presentation.component.ReportCategoryItem
import com.example.itday.feature.report.presentation.content.GuestReportContent
import com.example.itday.feature.report.presentation.content.FreeReportContent
import com.example.itday.feature.report.presentation.content.AttendanceContent
import com.example.itday.feature.report.presentation.content.PointHistoryContent
import com.example.itday.feature.report.presentation.content.ProReportContent
import com.example.itday.feature.report.presentation.content.StoreContent

private enum class ReportPage(val route: String) {
    Main("report/main"),
    Store("report/store"),
    PointHistory("report/points"),
    Attendance("report/attendance"),
}

@Composable
fun ReportScreen(
    uiState: ReportUiState =
        ReportUiState(
            access = ReportAccess.Free,
            categories =
                listOf(
                    ReportCategoryItem("카페", 3, R.drawable.report_category_cafe),
                    ReportCategoryItem("편의점", 6, R.drawable.report_category_convenience),
                    ReportCategoryItem("식당", 10, R.drawable.report_category_restaurant),
                    ReportCategoryItem("서점", 2, R.drawable.report_category_bookstore),
                ),
        ),
    onSignUpClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onPointHistoryClick: () -> Unit = {},
    onSubscribeClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    isGuestMode: Boolean = false,
    isProMember: Boolean = false,
) {
    val access = when {
        isGuestMode -> ReportAccess.Guest
        isProMember -> ReportAccess.Pro
        else -> ReportAccess.Free
    }
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val openPage: (ReportPage) -> Unit = { navController.navigate(it.route) { launchSingleTop = true } }
    BackHandler(enabled = currentRoute != null && currentRoute != ReportPage.Main.route) {
        navController.popBackStack()
    }

    NavHost(navController = navController, startDestination = ReportPage.Main.route) {
        composable(ReportPage.Main.route) {
            when (access) {
                ReportAccess.Guest ->
                    GuestReportContent(
                        onSignUpClick = onSignUpClick,
                        onAttendanceClick = { openPage(ReportPage.Attendance) },
                    )
                ReportAccess.Free ->
                    FreeReportContent(
                        categories = uiState.categories,
                        topStores = uiState.topStores,
                        onShopClick = {
                            openPage(ReportPage.Store)
                            onShopClick()
                        },
                        onPointHistoryClick = {
                            openPage(ReportPage.PointHistory)
                            onPointHistoryClick()
                        },
                        onAttendanceClick = { openPage(ReportPage.Attendance) },
                    )
                ReportAccess.Pro ->
                    ProReportContent(onAttendanceClick = { openPage(ReportPage.Attendance) })
            }
        }
        composable(ReportPage.Store.route) {
            StoreContent(
                onBackClick = { navController.popBackStack() },
                onPointClick = { openPage(ReportPage.PointHistory) },
                isProMember = isProMember,
                onMissionClick = { openPage(ReportPage.Attendance) },
                onSubscribeClick = onSubscribeClick,
            )
        }
        composable(ReportPage.PointHistory.route) {
            PointHistoryContent(
                onBackClick = { navController.popBackStack() },
                onAttendanceClick = { openPage(ReportPage.Attendance) },
            )
        }
        composable(ReportPage.Attendance.route) {
            AttendanceContent(
                onBackClick = { navController.popBackStack() },
                onPointClick = { openPage(ReportPage.PointHistory) },
                onHomeClick = onHomeClick,
            )
        }
    }
}
