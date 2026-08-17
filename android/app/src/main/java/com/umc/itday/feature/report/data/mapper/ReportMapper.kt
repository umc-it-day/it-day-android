package com.umc.itday.feature.report.data.mapper

import com.umc.itday.feature.report.data.model.CreateReportRequestDto
import com.umc.itday.feature.report.data.model.AttendanceDto
import com.umc.itday.feature.report.data.model.ReportDetailDto
import com.umc.itday.feature.report.data.model.ReportSummaryDto
import com.umc.itday.feature.report.data.model.VisitChallengeDto
import com.umc.itday.feature.report.domain.model.Attendance
import com.umc.itday.feature.report.domain.model.CreateReportInput
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.ReportPeriodType
import com.umc.itday.feature.report.domain.model.ReportSummary
import com.umc.itday.feature.report.domain.model.VisitChallenge

fun AttendanceDto.toDomain() =
    Attendance(
        attendanceId = attendanceId,
        attendanceDate = attendanceDate,
        consecutiveDays = consecutiveDays,
        dailyPoint = dailyPoint,
        bonusPoint = bonusPoint,
        earnedPoint = earnedPoint,
        monthlyAttendanceCount = monthlyAttendanceCount,
        monthlyEarnedPoint = monthlyEarnedPoint,
        sevenDayBonusReceived = sevenDayBonusReceived,
        fifteenDayBonusReceived = fifteenDayBonusReceived,
        thirtyDayBonusReceived = thirtyDayBonusReceived,
    )

fun VisitChallengeDto.toDomain() =
    VisitChallenge(
        visitCount = visitCount,
        currentStage = currentStage,
        nextTargetVisitCount = nextTargetVisitCount,
        currentStageRewardPoint = currentStageRewardPoint,
        accumulatedRewardPoint = accumulatedRewardPoint,
        newlyEarnedPoint = newlyEarnedPoint,
        progressRate = progressRate,
        completed = completed,
        characterVisual = characterVisual,
    )

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
