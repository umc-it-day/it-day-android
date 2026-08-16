package com.umc.itday.feature.report.data.repository

import com.umc.itday.core.auth.AuthTokenStorage
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.data.result.AuthErrorReason
import com.umc.itday.core.network.safeApiCall
import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.report.data.mapper.toDomain
import com.umc.itday.feature.report.data.mapper.toDto
import com.umc.itday.feature.report.data.model.ReportDetailDto
import com.umc.itday.feature.report.data.model.ReportSummaryDto
import com.umc.itday.feature.report.data.remote.ReportApi
import com.umc.itday.feature.report.domain.model.CreateReportInput
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.model.ReportSummary
import com.umc.itday.feature.report.domain.repository.ReportRepository

class DefaultReportRepository(
    private val api: ReportApi,
    private val tokenStorage: AuthTokenStorage,
) : ReportRepository {
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
        if (success && data != null) {
            ApiResult.Success(data.map(ReportSummaryDto::toDomain))
        } else {
            failure(message)
        }

    private fun ApiResponseDto<ReportDetailDto>.detailResult(): ApiResult<ReportDetail> =
        if (success && data != null) ApiResult.Success(data.toDomain()) else failure(message)

    private fun failure(message: String): ApiResult.Failure =
        ApiResult.Failure(AppError.Server(statusCode = 200, message = message))
}
