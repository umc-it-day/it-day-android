package com.example.itday.feature.report.presentation

import androidx.compose.runtime.Composable
import com.example.itday.feature.report.presentation.content.GuestReportContent

@Composable
fun ReportScreen(
    uiState: ReportUiState = ReportUiState(),
    onSignUpClick: () -> Unit = {},
) {
    when (uiState.access) {
        ReportAccess.Guest -> GuestReportContent(onSignUpClick = onSignUpClick)
    }
}
