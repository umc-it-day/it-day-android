package com.umc.itday.feature.report.domain.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.feature.report.domain.model.CreateReportInput
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.model.ReportSummary

interface ReportRepository {
    suspend fun getReports(): ApiResult<List<ReportSummary>>
    suspend fun createReport(input: CreateReportInput): ApiResult<ReportDetail>
    suspend fun getReport(reportId: Long): ApiResult<ReportDetail>
    suspend fun getReportsByPeriod(periodType: ReportPeriodType): ApiResult<List<ReportSummary>>
}
