package com.umc.itday.feature.report.presentation

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.feature.report.domain.model.Attendance
import com.umc.itday.feature.report.domain.model.CreateReportInput
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.model.ReportSummary
import com.umc.itday.feature.report.domain.model.VisitChallenge
import com.umc.itday.feature.report.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkAttendance 호출 시 출석 정보가 업데이트되고 포인트가 반영된다`() = runTest {
        val fakeRepo = FakeReportRepository()
        val fakeLocal = FakeLocalPreferencesDataSource()
        val viewModel = ReportViewModel(fakeRepo, fakeLocal)

        viewModel.checkAttendance()

        val state = viewModel.uiState.value
        assertNotNull(state.attendance)
        assertEquals(7, state.consecutiveDays)
        assertEquals(400, state.monthlyEarnedPoint)
        assertTrue(state.sevenDayBonus)
        assertTrue(state.isAttendanceCompleted)
        assertNotNull(state.attendanceSuccessMessage)
        assertTrue(state.pointHistories.isNotEmpty())
        assertEquals("출석체크 완료 보상", state.pointHistories.first().title)
        assertEquals("2026-08-19", fakeLocal.savedDate)
    }

    @Test
    fun `오늘 이미 출석한 기록이 있으면 isTodayAttended 및 isAttendanceCompleted 가 true 로 설정된다`() = runTest {
        val fakeRepo = FakeReportRepository()
        val fakeLocal = FakeLocalPreferencesDataSource()
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA).format(java.util.Date())
        fakeLocal.setDate(today)

        val viewModel = ReportViewModel(fakeRepo, fakeLocal)

        val state = viewModel.uiState.value
        assertTrue(state.isTodayAttended)
        assertTrue(state.isAttendanceCompleted)
    }



    @Test
    fun `loadInitialData 호출 시 최신 리포트 및 방문 챌린지 데이터가 로드된다`() = runTest {
        val fakeRepo = FakeReportRepository()
        val viewModel = ReportViewModel(fakeRepo)

        viewModel.loadInitialData()

        val state = viewModel.uiState.value
        assertNotNull(state.report)
        assertEquals(1500, state.pointBalance)
        assertNotNull(state.visitChallenge)
        assertEquals(3, state.visitChallenge?.visitCount)
    }

    private class FakeReportRepository : ReportRepository {
        var attendanceResult: ApiResult<Attendance> =
            ApiResult.Success(
                Attendance(
                    attendanceId = 1L,
                    attendanceDate = "2026-08-19",
                    consecutiveDays = 7,
                    dailyPoint = 10,
                    bonusPoint = 30,
                    earnedPoint = 40,
                    monthlyAttendanceCount = 7,
                    monthlyEarnedPoint = 400,
                    sevenDayBonusReceived = true,
                    fifteenDayBonusReceived = false,
                    thirtyDayBonusReceived = false,
                )
            )

        var visitChallengeResult: ApiResult<VisitChallenge> =
            ApiResult.Success(
                VisitChallenge(
                    visitCount = 3,
                    currentStage = 1,
                    nextTargetVisitCount = 5,
                    currentStageRewardPoint = 100,
                    accumulatedRewardPoint = 300,
                    newlyEarnedPoint = 0,
                    progressRate = 0.6,
                    completed = false,
                    characterVisual = "capybara_stage_1",
                )
            )

        override suspend fun checkAttendance(): ApiResult<Attendance> = attendanceResult
        override suspend fun getVisitChallenge(): ApiResult<VisitChallenge> = visitChallengeResult
        override suspend fun recordVisit(): ApiResult<VisitChallenge> = visitChallengeResult
        override suspend fun getReports(): ApiResult<List<ReportSummary>> = ApiResult.Success(emptyList())
        override suspend fun createReport(input: CreateReportInput): ApiResult<ReportDetail> =
            ApiResult.Success(
                ReportDetail(
                    reportId = 1L,
                    periodType = input.periodType,
                    startDate = input.startDate,
                    endDate = "2026-08-31",
                    attendanceCount = 5,
                    maxConsecutiveDays = 5,
                    attendancePoint = 200,
                    visitCount = 3,
                    visitStage = 1,
                    visitRewardPoint = 300,
                    totalEarnedPoint = 1500,
                    unlockedFloor = 2,
                    summary = "8월 요약",
                )
            )

        override suspend fun getReport(reportId: Long): ApiResult<ReportDetail> =
            ApiResult.Success(
                ReportDetail(
                    reportId = reportId,
                    periodType = ReportPeriodType.MONTHLY,
                    startDate = "2026-08-01",
                    endDate = "2026-08-31",
                    attendanceCount = 7,
                    maxConsecutiveDays = 7,
                    attendancePoint = 400,
                    visitCount = 3,
                    visitStage = 1,
                    visitRewardPoint = 300,
                    totalEarnedPoint = 1500,
                    unlockedFloor = 3,
                    summary = "8월 요약",
                )
            )

        override suspend fun getReportsByPeriod(periodType: ReportPeriodType): ApiResult<List<ReportSummary>> =
            ApiResult.Success(
                listOf(
                    ReportSummary(
                        reportId = 10L,
                        periodType = periodType,
                        startDate = "2026-08-01",
                        endDate = "2026-08-31",
                        attendanceCount = 7,
                        visitCount = 3,
                        totalEarnedPoint = 1500,
                        summary = "요약",
                    )
                )
            )
    }

    private class FakeLocalPreferencesDataSource : com.umc.itday.core.local.LocalPreferencesDataSource {
        var savedDate: String = ""
        private val _dateFlow = kotlinx.coroutines.flow.MutableStateFlow("")
        override val lastAttendanceDate: kotlinx.coroutines.flow.Flow<String> = _dateFlow

        override val isLoggedIn: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
        override val isOnboardingCompleted: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
        override val isGuestMode: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
        override val preferredBrandNames: kotlinx.coroutines.flow.Flow<Set<String>> = kotlinx.coroutines.flow.flowOf(emptySet())

        fun setDate(date: String) {
            savedDate = date
            _dateFlow.value = date
        }

        override suspend fun setLastAttendanceDate(date: String) {
            savedDate = date
            _dateFlow.value = date
        }

        override suspend fun setLoggedIn(loggedIn: Boolean) {}
        override suspend fun setOnboardingCompleted(completed: Boolean) {}
        override suspend fun setGuestMode(enabled: Boolean) {}
        override suspend fun setPreferredBrandNames(brands: Set<String>) {}
        override suspend fun addPreferredBrandName(brandName: String) {}
        override suspend fun clearUserSessionPreferences() {}
    }
}
