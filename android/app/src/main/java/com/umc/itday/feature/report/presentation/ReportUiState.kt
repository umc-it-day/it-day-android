package com.umc.itday.feature.report.presentation

import com.umc.itday.feature.report.presentation.component.ReportCategoryItem
import com.umc.itday.feature.report.presentation.component.ReportTopStoreItem
import com.umc.itday.feature.report.domain.model.ReportDetail

data class ReportUiState(
    val access: ReportAccess = ReportAccess.Guest,
    val categories: List<ReportCategoryItem> = emptyList(),
    val topStores: List<ReportTopStoreItem> = emptyList(),
    val report: ReportDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class ReportAccess { Guest, Free, Pro }
