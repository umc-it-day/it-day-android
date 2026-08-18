package com.umc.itday.feature.report.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umc.itday.feature.report.presentation.content.GuestReportContent
import com.umc.itday.feature.report.presentation.content.FreeReportContent
import com.umc.itday.feature.report.presentation.content.AttendanceContent
import com.umc.itday.feature.report.presentation.content.PointHistoryContent
import com.umc.itday.feature.report.presentation.content.ProReportContent
import com.umc.itday.feature.report.presentation.content.StoreContent
import com.umc.itday.core.di.appContainer

private enum class ReportPage(val route: String) {
    Main("report/main"),
    Store("report/store"),
    PointHistory("report/points"),
    Attendance("report/attendance"),
}

@Composable
fun ReportRoute(
    isGuestMode: Boolean,
    isProMember: Boolean = false,
    onHomeClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: ReportViewModel =
        viewModel(
            factory = ReportViewModel.Factory(
                repository = context.appContainer.reportRepository,
            ),
        )
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(isGuestMode) {
        if (!isGuestMode && uiState.report == null) viewModel.loadLatestMonthlyReport()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ReportScreen(
            uiState = uiState,
            isGuestMode = isGuestMode,
            isProMember = isProMember,
            onHomeClick = onHomeClick,
        )
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage != null && uiState.report == null) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(errorMessage)
                Button(onClick = viewModel::loadLatestMonthlyReport) { Text("다시 시도") }
            }
        }
    }
}

@Composable
fun ReportScreen(
    uiState: ReportUiState = ReportUiState(access = ReportAccess.Free),
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
                        floor = uiState.report?.unlockedFloor ?: 1,
                        pointBalance = uiState.report?.totalEarnedPoint ?: 0,
                    )
                ReportAccess.Pro ->
                    ProReportContent(
                        onAttendanceClick = { openPage(ReportPage.Attendance) },
                        attendanceCount = uiState.report?.attendanceCount ?: 0,
                        monthlyPoint = uiState.report?.totalEarnedPoint ?: 0,
                        visitCount = uiState.report?.visitCount ?: 0,
                        discountUseCount = uiState.report?.visitCount ?: 0,
                    )
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
