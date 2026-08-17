package com.umc.itday.feature.report.data.remote

import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.report.data.model.CreateReportRequestDto
import com.umc.itday.feature.report.data.model.AttendanceDto
import com.umc.itday.feature.report.data.model.ReportDetailDto
import com.umc.itday.feature.report.data.model.ReportSummaryDto
import com.umc.itday.feature.report.data.model.VisitChallengeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportApi {
    @POST("api/attendance")
    suspend fun checkAttendance(
        @Header("X-USER-ID") userId: Long,
    ): ApiResponseDto<AttendanceDto>

    @GET("api/challenges/visits")
    suspend fun getVisitChallenge(
        @Header("X-USER-ID") userId: Long,
    ): ApiResponseDto<VisitChallengeDto>

    @POST("api/challenges/visits")
    suspend fun recordVisit(
        @Header("X-USER-ID") userId: Long,
    ): ApiResponseDto<VisitChallengeDto>

    @GET("api/reports")
    suspend fun getReports(@Header("X-USER-ID") userId: Long): ApiResponseDto<List<ReportSummaryDto>>

    @POST("api/reports")
    suspend fun createReport(
        @Header("X-USER-ID") userId: Long,
        @Body request: CreateReportRequestDto,
    ): ApiResponseDto<ReportDetailDto>

    @GET("api/reports/{reportId}")
    suspend fun getReport(
        @Header("X-USER-ID") userId: Long,
        @Path("reportId") reportId: Long,
    ): ApiResponseDto<ReportDetailDto>

    @GET("api/reports/period")
    suspend fun getReportsByPeriod(
        @Header("X-USER-ID") userId: Long,
        @Query("periodType") periodType: String,
    ): ApiResponseDto<List<ReportSummaryDto>>
}
