package com.example.itday.feature.report.presentation

import androidx.compose.runtime.Composable
import com.example.itday.R
import com.example.itday.feature.report.presentation.component.ReportCategoryItem
import com.example.itday.feature.report.presentation.content.GuestReportContent
import com.example.itday.feature.report.presentation.content.FreeReportContent

@Composable
fun ReportScreen(
    uiState: ReportUiState =
        ReportUiState(
            access = ReportAccess.Free,
            categories =
                listOf(
                    ReportCategoryItem("카페", 1, R.drawable.report_category_cafe),
                    ReportCategoryItem("편의점", 2, R.drawable.report_category_convenience),
                    ReportCategoryItem("식당", 2, R.drawable.report_category_restaurant),
                    ReportCategoryItem("서점", 1, R.drawable.report_category_bookstore),
                ),
        ),
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
