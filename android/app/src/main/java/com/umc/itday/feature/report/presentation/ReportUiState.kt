package com.umc.itday.feature.report.presentation

import com.umc.itday.feature.report.presentation.component.ReportCategoryItem
import com.umc.itday.feature.report.presentation.component.ReportTopStoreItem
import com.umc.itday.feature.report.domain.model.Attendance
import com.umc.itday.feature.report.domain.model.ReportDetail
import com.umc.itday.feature.report.domain.model.VisitChallenge

data class ReportUiState(
    val access: ReportAccess = ReportAccess.Guest,
    val categories: List<ReportCategoryItem> = emptyList(),
    val topStores: List<ReportTopStoreItem> = emptyList(),
    val report: ReportDetail? = null,
    val attendance: Attendance? = null,
    val visitChallenge: VisitChallenge? = null,
    val isAttendanceSubmitting: Boolean = false,
    val isTodayAttended: Boolean = false,
    val attendanceSuccessMessage: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {

    val pointBalance: Int
        get() = report?.totalEarnedPoint ?: attendance?.monthlyEarnedPoint ?: 0

    val consecutiveDays: Int
        get() = attendance?.consecutiveDays ?: report?.maxConsecutiveDays ?: 0

    val monthlyAttendanceCount: Int
        get() = attendance?.monthlyAttendanceCount ?: report?.attendanceCount ?: 0

    val monthlyEarnedPoint: Int
        get() = attendance?.monthlyEarnedPoint ?: report?.attendancePoint ?: 0


    val sevenDayBonus: Boolean
        get() = attendance?.sevenDayBonusReceived ?: (consecutiveDays >= 7)

    val fifteenDayBonus: Boolean
        get() = attendance?.fifteenDayBonusReceived ?: (consecutiveDays >= 15)

    val thirtyDayBonus: Boolean
        get() = attendance?.thirtyDayBonusReceived ?: (consecutiveDays >= 30)

    val isAttendanceCompleted: Boolean
        get() = isTodayAttended || attendance != null

    val completedAttendanceDays: Set<Int>
        get() {
            val dayFromAttendance =
                attendance
                    ?.attendanceDate
                    ?.substringAfterLast("-", missingDelimiterValue = "")
                    ?.toIntOrNull()

            val today =
                java.util.Calendar
                    .getInstance()
                    .get(java.util.Calendar.DAY_OF_MONTH)

            return when {
                dayFromAttendance != null -> setOf(dayFromAttendance)
                isTodayAttended -> setOf(today)
                else -> emptySet()
            }
        }

    val latestAttendanceEarnedPoint: Int
        get() = attendance?.earnedPoint ?: 0


    val pointHistories: List<com.umc.itday.feature.report.presentation.content.PointHistoryUiModel>
        get() {
            val list = mutableListOf<com.umc.itday.feature.report.presentation.content.PointHistoryUiModel>()
            val todayDate = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.KOREA).format(java.util.Date())

            attendance?.let { att ->
                val dateStr = if (att.attendanceDate.isNotBlank()) att.attendanceDate.replace("-", ".") else todayDate
                list.add(
                    com.umc.itday.feature.report.presentation.content.PointHistoryUiModel(
                        date = dateStr,
                        title = "출석체크 완료 보상",
                        detail = "연속 ${att.consecutiveDays}일차 출석",
                        points = att.earnedPoint,
                        type = com.umc.itday.feature.report.presentation.content.PointHistoryType.Earned,
                    )
                )
                if (att.bonusPoint > 0) {
                    list.add(
                        com.umc.itday.feature.report.presentation.content.PointHistoryUiModel(
                            date = dateStr,
                            title = "연속 출석 보너스 달성",
                            detail = "스탬프 보너스 추가 적립",
                            points = att.bonusPoint,
                            type = com.umc.itday.feature.report.presentation.content.PointHistoryType.Earned,
                        )
                    )
                }
            }

            visitChallenge?.let { vc ->
                if (vc.accumulatedRewardPoint > 0) {
                    list.add(
                        com.umc.itday.feature.report.presentation.content.PointHistoryUiModel(
                            date = todayDate,
                            title = "매장 방문 챌린지 (${vc.currentStage}단계) 달성",
                            detail = "누적 방문 ${vc.visitCount}회 달성 보상",
                            points = vc.accumulatedRewardPoint,
                            type = com.umc.itday.feature.report.presentation.content.PointHistoryType.Earned,
                        )
                    )
                }
            }

            report?.let { rep ->
                if (rep.totalEarnedPoint > 0 && attendance == null) {
                    val dateStr = if (rep.startDate.isNotBlank()) rep.startDate.replace("-", ".") else todayDate
                    list.add(
                        com.umc.itday.feature.report.presentation.content.PointHistoryUiModel(
                            date = dateStr,
                            title = "월간 소비 리포트 분석 보상",
                            detail = "출석 ${rep.attendancePoint}P + 방문 ${rep.visitRewardPoint}P",
                            points = rep.totalEarnedPoint,
                            type = com.umc.itday.feature.report.presentation.content.PointHistoryType.Earned,
                        )
                    )
                }
            }

            return list
        }
}




enum class ReportAccess { Guest, Free, Pro }

sealed interface ReportUiEvent {
    data class ShowMessage(val message: String) : ReportUiEvent
}
