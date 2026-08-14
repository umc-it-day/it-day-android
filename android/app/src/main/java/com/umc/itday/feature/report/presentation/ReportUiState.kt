package com.umc.itday.feature.report.presentation

import com.umc.itday.feature.report.presentation.component.ReportCategoryItem
import com.umc.itday.feature.report.presentation.component.ReportTopStoreItem

data class ReportUiState(
    val access: ReportAccess = ReportAccess.Guest,
    val categories: List<ReportCategoryItem> = emptyList(),
    val topStores: List<ReportTopStoreItem> = emptyList(),
)

enum class ReportAccess { Guest, Free, Pro }
