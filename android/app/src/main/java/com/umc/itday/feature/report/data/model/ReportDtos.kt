package com.umc.itday.feature.report.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ReportSummaryDto(
    val reportId: Long,
    val periodType: String,
    val startDate: String,
    val endDate: String,
    val attendanceCount: Int,
    val visitCount: Int,
    val totalEarnedPoint: Int,
    val summary: String,
)

@Serializable
data class ReportDetailDto(
    val reportId: Long,
    val periodType: String,
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

@Serializable
data class CreateReportRequestDto(
    val periodType: String,
    val startDate: String,
    val endDate: String,
    val benefitUsageCount: Int,
    val totalSavingAmount: Int,
    val visitedStoreCount: Int,
    val favoriteCategory: String,
)
