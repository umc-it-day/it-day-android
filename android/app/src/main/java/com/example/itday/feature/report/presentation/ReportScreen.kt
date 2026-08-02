package com.example.itday.feature.report.presentation

import androidx.compose.runtime.Composable
import com.example.itday.feature.report.presentation.content.GuestReportContent
import com.example.itday.feature.report.presentation.content.FreeReportContent

@Composable
fun ReportScreen(
    uiState: ReportUiState = ReportUiState(),
    onSignUpClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onPointHistoryClick: () -> Unit = {},
) {
    when (uiState.access) {
        ReportAccess.Guest -> GuestReportContent(onSignUpClick = onSignUpClick)
        ReportAccess.Free ->
            FreeReportContent(
                categories = uiState.categories,
                topStores = uiState.topStores,
                onShopClick = onShopClick,
                onPointHistoryClick = onPointHistoryClick,
            )
    }
}
