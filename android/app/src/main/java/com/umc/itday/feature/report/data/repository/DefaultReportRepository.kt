package com.umc.itday.feature.report.data.repository

import android.util.Log
import com.umc.itday.BuildConfig
import com.umc.itday.core.auth.AuthTokenStorage
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.data.result.AuthErrorReason
import com.umc.itday.core.network.safeApiCall
import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.report.data.mapper.toDomain
import com.umc.itday.feature.report.data.mapper.toDto
import com.umc.itday.feature.report.data.model.AttendanceDto
import com.umc.itday.feature.report.data.model.ReportDetailDto
import com.umc.itday.feature.report.data.model.ReportSummaryDto
import com.umc.itday.feature.report.data.model.VisitChallengeDto
import com.umc.itday.feature.report.data.remote.ReportApi
import com.umc.itday.feature.report.domain.model.Attendance
import com.umc.itday.feature.report.domain.model.CreateReportInput
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.model.ReportSummary
import com.umc.itday.feature.report.domain.model.VisitChallenge
import com.umc.itday.feature.report.domain.repository.ReportRepository

class DefaultReportRepository(
    private val api: ReportApi,
    private val tokenStorage: AuthTokenStorage,
) : ReportRepository {
    override suspend fun checkAttendance(): ApiResult<Attendance> {
        Log.d(API_LOG_TAG, "POST /api/attendance 요청 시작")
        return withUserId { userId -> api.checkAttendance(userId).attendanceResult() }
            .also(::logAttendanceResult)
    }

    override suspend fun getVisitChallenge(): ApiResult<VisitChallenge> {
        Log.d(API_LOG_TAG, "GET /api/challenges/visits 요청 시작")
        return withUserId { userId -> api.getVisitChallenge(userId).visitChallengeResult() }
            .also { logChallengeResult("GET", it) }
    }

    override suspend fun recordVisit(): ApiResult<VisitChallenge> {
        Log.d(API_LOG_TAG, "POST /api/challenges/visits 요청 시작")
        return withUserId { userId -> api.recordVisit(userId).visitChallengeResult() }
            .also { logChallengeResult("POST", it) }
    }

    override suspend fun getReports(): ApiResult<List<ReportSummary>> =
        withUserId { userId -> api.getReports(userId).summaryResult() }

    override suspend fun createReport(input: CreateReportInput): ApiResult<ReportDetail> =
        withUserId { userId -> api.createReport(userId, input.toDto()).detailResult() }

    override suspend fun getReport(reportId: Long): ApiResult<ReportDetail> =
        withUserId { userId -> api.getReport(userId, reportId).detailResult() }

    override suspend fun getReportsByPeriod(periodType: ReportPeriodType): ApiResult<List<ReportSummary>> =
        withUserId { userId -> api.getReportsByPeriod(userId, periodType.name).summaryResult() }

    private suspend fun <T> withUserId(block: suspend (Long) -> ApiResult<T>): ApiResult<T> =
        safeApiCall {
            val userId = tokenStorage.getTokens()?.userId
                ?: return@safeApiCall ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            block(userId)
        }

    private fun ApiResponseDto<List<ReportSummaryDto>>.summaryResult(): ApiResult<List<ReportSummary>> =
        if (success && data != null) ApiResult.Success(data.map(ReportSummaryDto::toDomain)) else failure(message)

    private fun ApiResponseDto<ReportDetailDto>.detailResult(): ApiResult<ReportDetail> =
        if (success && data != null) ApiResult.Success(data.toDomain()) else failure(message)

    private fun ApiResponseDto<AttendanceDto>.attendanceResult(): ApiResult<Attendance> =
        if (success && data != null) ApiResult.Success(data.toDomain()) else failure(message)

    private fun ApiResponseDto<VisitChallengeDto>.visitChallengeResult(): ApiResult<VisitChallenge> =
        if (success && data != null) ApiResult.Success(data.toDomain()) else failure(message)

    private fun failure(message: String): ApiResult.Failure {
        if (BuildConfig.DEBUG) Log.e(API_LOG_TAG, "API 응답 실패: $message")
        return ApiResult.Failure(AppError.Server(statusCode = 200, message = message))
    }

    private fun logAttendanceResult(result: ApiResult<Attendance>) {
        if (result is ApiResult.Success) {
            Log.i(API_LOG_TAG, "POST /api/attendance 성공: ${result.data.earnedPoint}P 획득")
        }
    }

    private fun logChallengeResult(method: String, result: ApiResult<VisitChallenge>) {
        if (result is ApiResult.Success) {
            Log.i(API_LOG_TAG, "$method /api/challenges/visits 성공: ${result.data.visitCount}회")
        }
    }

    private companion object {
        const val API_LOG_TAG = "ChallengeApi"
    }
}
