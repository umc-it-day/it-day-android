package com.umc.itday.feature.report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportViewModel(
    private val repository: ReportRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun loadLatestMonthlyReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val summaries = repository.getReportsByPeriod(ReportPeriodType.MONTHLY)) {
                is ApiResult.Failure -> finishWithError(summaries.error.toUserMessage())
                is ApiResult.Success -> {
                    val latest = summaries.data.maxByOrNull { it.endDate }
                    if (latest == null) {
                        _uiState.update { it.copy(isLoading = false) }
                    } else {
                        loadDetail(latest.reportId)
                    }
                }
            }
        }
    }

    private suspend fun loadDetail(reportId: Long) {
        when (val detail = repository.getReport(reportId)) {
            is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, report = detail.data) }
            is ApiResult.Failure -> finishWithError(detail.error.toUserMessage())
        }
    }

    private fun finishWithError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    class Factory(
        private val repository: ReportRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReportViewModel(repository) as T
    }
}
