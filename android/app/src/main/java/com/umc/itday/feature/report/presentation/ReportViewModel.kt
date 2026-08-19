package com.umc.itday.feature.report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.core.local.LocalPreferencesDataSource
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.repository.ReportRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportViewModel(
    private val repository: ReportRepository,
    private val localPreferencesDataSource: LocalPreferencesDataSource? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _events = Channel<ReportUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeAttendancePersistence()
    }

    private fun getTodayDateString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())

    private fun observeAttendancePersistence() {
        val local = localPreferencesDataSource ?: return
        val today = getTodayDateString()
        viewModelScope.launch {
            local.lastAttendanceDate.collect { savedDate ->
                _uiState.update { it.copy(isTodayAttended = savedDate == today) }
            }
        }
    }

    fun loadInitialData() {
        loadLatestMonthlyReport()
        loadVisitChallenge()
    }

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

    fun checkAttendance(onSuccess: (Int) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAttendanceSubmitting = true) }
            when (val result = repository.checkAttendance()) {
                is ApiResult.Success -> {
                    val attendance = result.data
                    val today = getTodayDateString()
                    localPreferencesDataSource?.setLastAttendanceDate(today)
                    _uiState.update {
                        it.copy(
                            isAttendanceSubmitting = false,
                            isTodayAttended = true,
                            attendance = attendance,
                            attendanceSuccessMessage = "출석 완료! +${attendance.earnedPoint}P 획득!",
                        )
                    }
                    onSuccess(attendance.earnedPoint)
                }
                is ApiResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isAttendanceSubmitting = false,
                            errorMessage = result.error.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    fun loadVisitChallenge() {
        viewModelScope.launch {
            when (val result = repository.getVisitChallenge()) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(visitChallenge = result.data) }
                }
                is ApiResult.Failure -> {
                    // 방문 챌린지 조회 실패는 리포트 전체를 막지 않음
                }
            }
        }
    }

    fun recordVisit(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            when (val result = repository.recordVisit()) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(visitChallenge = result.data) }
                    onSuccess()
                }
                is ApiResult.Failure -> {
                    _uiState.update { it.copy(errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun showStorePreparingMessage() {
        viewModelScope.launch {
            _events.send(ReportUiEvent.ShowMessage("상점 기능은 준비중이에요! 곧 오픈될 예정입니다."))
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
        private val localPreferencesDataSource: LocalPreferencesDataSource? = null,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReportViewModel(repository, localPreferencesDataSource) as T
    }
}
