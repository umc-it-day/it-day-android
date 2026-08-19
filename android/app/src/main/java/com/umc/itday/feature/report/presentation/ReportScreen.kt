package com.umc.itday.feature.report.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umc.itday.core.di.appContainer
import com.umc.itday.feature.report.presentation.content.AttendanceContent
import com.umc.itday.feature.report.presentation.content.FreeReportContent
import com.umc.itday.feature.report.presentation.content.GuestReportContent
import com.umc.itday.feature.report.presentation.content.PointHistoryContent
import com.umc.itday.feature.report.presentation.content.ProReportContent
import com.umc.itday.feature.report.presentation.content.StoreContent

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
                localPreferencesDataSource = context.appContainer.localPreferencesDataSource,
            ),
        )

    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(isGuestMode) {
        if (!isGuestMode) {
            viewModel.loadInitialData()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ReportScreen(
            uiState = uiState,
            isGuestMode = isGuestMode,
            isProMember = isProMember,
            onHomeClick = onHomeClick,
            onAttendanceSubmit = { viewModel.checkAttendance() },
        )
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage != null && uiState.report == null && uiState.attendance == null) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(errorMessage)
                Button(onClick = viewModel::loadInitialData) { Text("다시 시도") }
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
    onAttendanceSubmit: () -> Unit = {},
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
                        pointBalance = uiState.pointBalance,
                    )
                ReportAccess.Pro ->
                    ProReportContent(
                        onAttendanceClick = { openPage(ReportPage.Attendance) },
                        attendanceCount = uiState.report?.attendanceCount ?: 0,
                        monthlyPoint = uiState.pointBalance,
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
                pointBalance = uiState.pointBalance,
                histories = uiState.pointHistories,
                onBackClick = { navController.popBackStack() },
                onAttendanceClick = { openPage(ReportPage.Attendance) },
            )
        }

        composable(ReportPage.Attendance.route) {
            AttendanceContent(
                monthlyPoints = uiState.monthlyEarnedPoint,
                consecutiveDays = uiState.consecutiveDays,
                sevenDaysBonus = uiState.sevenDayBonus,
                fifteenDaysBonus = uiState.fifteenDayBonus,
                thirtyDaysBonus = uiState.thirtyDayBonus,
                isAttendanceSubmitting = uiState.isAttendanceSubmitting,
                isAttendanceCompleted = uiState.isAttendanceCompleted,
                attendanceSuccessMessage = uiState.attendanceSuccessMessage,
                completedAttendanceDays = uiState.completedAttendanceDays,
                latestEarnedPoint = uiState.latestAttendanceEarnedPoint,
                onAttendanceClick = onAttendanceSubmit,
                onBackClick = { navController.popBackStack() },
                onPointClick = { openPage(ReportPage.PointHistory) },
                onHomeClick = onHomeClick,
            )

        }
    }
}
