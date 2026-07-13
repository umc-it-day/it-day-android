package com.example.itday.core.data.mock

import com.example.itday.core.model.ItDayBarcodeData
import com.example.itday.core.model.ItDayHomeData
import com.example.itday.core.model.ItDayMapPlace
import com.example.itday.core.model.ItDayMonthlySummary
import com.example.itday.core.model.ItDayNotice
import com.example.itday.core.model.ItDayOnboardingPage
import com.example.itday.core.model.ItDayPaymentPlan
import com.example.itday.core.model.ItDayQuickAction
import com.example.itday.core.model.ItDayReportData
import com.example.itday.core.model.ItDayReportHighlight
import com.example.itday.core.model.ItDaySchedule
import com.example.itday.core.model.ItDaySettingItem

object ItDayMockData {
    val homeData =
        ItDayHomeData(
            userName = "김잇데이",
            todayText = "7월 13일 월요일",
            attendanceRate = 82,
            currentStatus = "오늘 2개의 일정이 남아 있어요",
            nextSchedule =
                ItDaySchedule(
                    id = "schedule-001",
                    title = "오전 스터디 출석",
                    placeName = "강남 ITDAY 라운지",
                    startAtText = "10:00",
                    status = "입장 가능",
                ),
            quickActions =
                listOf(
                    ItDayQuickAction(
                        id = "barcode",
                        title = "바코드",
                        description = "멤버십 바코드로 빠르게 체크인",
                    ),
                    ItDayQuickAction(
                        id = "map",
                        title = "지도",
                        description = "가까운 제휴 공간 찾기",
                    ),
                    ItDayQuickAction(
                        id = "report",
                        title = "리포트",
                        description = "이번 달 이용 현황 확인",
                    ),
                ),
            notices =
                listOf(
                    ItDayNotice(
                        id = "notice-001",
                        title = "주말 운영 시간 안내",
                        description = "일부 지점은 주말 단축 운영됩니다.",
                    ),
                    ItDayNotice(
                        id = "notice-002",
                        title = "신규 제휴 공간 오픈",
                        description = "성수 지점에서 멤버십 혜택을 사용할 수 있어요.",
                    ),
                ),
        )

    val mapPlaces =
        listOf(
            ItDayMapPlace(
                id = "place-001",
                name = "강남 ITDAY 라운지",
                category = "스터디룸",
                distanceText = "320m",
                address = "서울 강남구 테헤란로 123",
                latitude = 37.5009,
                longitude = 127.0364,
                isOpen = true,
                benefitSummary = "체크인 시 음료 1잔 제공",
            ),
            ItDayMapPlace(
                id = "place-002",
                name = "역삼 코워킹 스팟",
                category = "코워킹",
                distanceText = "780m",
                address = "서울 강남구 논현로 456",
                latitude = 37.5031,
                longitude = 127.0412,
                isOpen = true,
                benefitSummary = "회의실 30분 무료",
            ),
            ItDayMapPlace(
                id = "place-003",
                name = "성수 네트워킹 허브",
                category = "라운지",
                distanceText = "6.4km",
                address = "서울 성동구 아차산로 17",
                latitude = 37.5446,
                longitude = 127.0558,
                isOpen = false,
                benefitSummary = "멤버 전용 좌석 예약 가능",
            ),
        )

    val barcodeData =
        ItDayBarcodeData(
            userName = "김잇데이",
            membershipName = "ITDAY Basic",
            barcodeValue = "8801234567890",
            expiresAtText = "2026.12.31까지",
            availableBenefits =
                listOf(
                    "제휴 공간 체크인",
                    "멤버십 할인 적용",
                    "이용 내역 자동 기록",
                ),
        )

    val reportData =
        ItDayReportData(
            periodText = "2026년 7월",
            attendanceRate = 82,
            totalVisitCount = 14,
            savedAmountText = "34,000원",
            monthlySummaries =
                listOf(
                    ItDayMonthlySummary("5월", 8, "18,000원"),
                    ItDayMonthlySummary("6월", 11, "27,000원"),
                    ItDayMonthlySummary("7월", 14, "34,000원"),
                ),
            highlights =
                listOf(
                    ItDayReportHighlight(
                        id = "highlight-001",
                        title = "가장 자주 방문한 공간",
                        value = "강남 ITDAY 라운지",
                        description = "이번 달 7회 방문",
                    ),
                    ItDayReportHighlight(
                        id = "highlight-002",
                        title = "누적 절약 금액",
                        value = "79,000원",
                        description = "최근 3개월 기준",
                    ),
                ),
        )

    val onboardingPages =
        listOf(
            ItDayOnboardingPage(
                id = "onboarding-001",
                title = "ITDAY 멤버십을 한 곳에서",
                description = "제휴 공간과 멤버십 혜택을 앱에서 확인하세요.",
                imageKey = "membership",
            ),
            ItDayOnboardingPage(
                id = "onboarding-002",
                title = "가까운 공간을 빠르게 찾기",
                description = "현재 위치 기준으로 이용 가능한 공간을 보여줍니다.",
                imageKey = "map",
            ),
            ItDayOnboardingPage(
                id = "onboarding-003",
                title = "이용 기록을 리포트로 확인",
                description = "출석, 방문, 혜택 사용 현황을 모아볼 수 있어요.",
                imageKey = "report",
            ),
        )

    val paymentPlans =
        listOf(
            ItDayPaymentPlan(
                id = "plan-basic",
                name = "Basic",
                priceText = "월 9,900원",
                billingCycle = "매월 결제",
                benefits = listOf("월 10회 체크인", "기본 제휴 혜택"),
                isRecommended = false,
            ),
            ItDayPaymentPlan(
                id = "plan-pro",
                name = "Pro",
                priceText = "월 19,900원",
                billingCycle = "매월 결제",
                benefits = listOf("무제한 체크인", "프리미엄 제휴 혜택", "리포트 확장"),
                isRecommended = true,
            ),
        )

    val settingItems =
        listOf(
            ItDaySettingItem(
                id = "profile",
                title = "프로필 관리",
                description = "이름과 멤버십 정보를 확인합니다.",
            ),
            ItDaySettingItem(
                id = "notification",
                title = "알림 설정",
                description = "일정과 혜택 알림을 관리합니다.",
            ),
            ItDaySettingItem(
                id = "support",
                title = "고객센터",
                description = "문의와 공지사항을 확인합니다.",
            ),
        )
}
