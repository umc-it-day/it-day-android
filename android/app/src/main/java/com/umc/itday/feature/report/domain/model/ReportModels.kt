package com.umc.itday.feature.report.domain.model

enum class ReportPeriodType { WEEKLY, MONTHLY }

data class ReportSummary(
    val reportId: Long,
    val periodType: ReportPeriodType,
    val startDate: String,
    val endDate: String,
    val attendanceCount: Int,
    val visitCount: Int,
    val totalEarnedPoint: Int,
    val summary: String,
)

data class ReportDetail(
    val reportId: Long,
    val periodType: ReportPeriodType,
    val startDate: String,
    val endDate: String,
    val attendanceCount: Int,
    val maxConsecutiveDays: Int,
    val attendancePoint: Int,
    val visitCount: Int,
    val visitStage: Int,
    val visitRewardPoint: Int,
    val totalEarnedPoint: Int,
    val unlockedFloor: Int,
    val summary: String,
)

data class CreateReportInput(
    val periodType: ReportPeriodType,
    val startDate: String,
    val endDate: String,
    val benefitUsageCount: Int,
    val totalSavingAmount: Int,
    val visitedStoreCount: Int,
    val favoriteCategory: String,
)
