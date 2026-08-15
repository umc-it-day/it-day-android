package com.umc.itday.feature.report.data.mapper

import com.umc.itday.feature.report.data.model.CreateReportRequestDto
import com.umc.itday.feature.report.data.model.ReportDetailDto
import com.umc.itday.feature.report.data.model.ReportSummaryDto
import com.umc.itday.feature.report.domain.model.CreateReportInput
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.model.ReportSummary

fun ReportSummaryDto.toDomain() =
    ReportSummary(
        reportId = reportId,
        periodType = periodType.toReportPeriodType(),
        startDate = startDate,
        endDate = endDate,
        attendanceCount = attendanceCount,
        visitCount = visitCount,
        totalEarnedPoint = totalEarnedPoint,
        summary = summary,
    )

fun ReportDetailDto.toDomain() =
    ReportDetail(
        reportId = reportId,
        periodType = periodType.toReportPeriodType(),
        startDate = startDate,
        endDate = endDate,
        attendanceCount = attendanceCount,
        maxConsecutiveDays = maxConsecutiveDays,
        attendancePoint = attendancePoint,
        visitCount = visitCount,
        visitStage = visitStage,
        visitRewardPoint = visitRewardPoint,
        totalEarnedPoint = totalEarnedPoint,
        unlockedFloor = unlockedFloor,
        summary = summary,
    )

fun CreateReportInput.toDto() =
    CreateReportRequestDto(
        periodType = periodType.name,
        startDate = startDate,
        endDate = endDate,
        benefitUsageCount = benefitUsageCount,
        totalSavingAmount = totalSavingAmount,
        visitedStoreCount = visitedStoreCount,
        favoriteCategory = favoriteCategory,
    )

private fun String.toReportPeriodType(): ReportPeriodType =
    runCatching { ReportPeriodType.valueOf(uppercase()) }.getOrDefault(ReportPeriodType.MONTHLY)
