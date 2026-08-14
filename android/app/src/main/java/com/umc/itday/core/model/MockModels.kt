package com.umc.itday.core.model

data class ItDayHomeData(
    val userName: String,
    val todayText: String,
    val attendanceRate: Int,
    val currentStatus: String,
    val nextSchedule: ItDaySchedule,
    val quickActions: List<ItDayQuickAction>,
    val notices: List<ItDayNotice>,
)

data class ItDaySchedule(
    val id: String,
    val title: String,
    val placeName: String,
    val startAtText: String,
    val status: String,
)

data class ItDayQuickAction(
    val id: String,
    val title: String,
    val description: String,
)

data class ItDayNotice(
    val id: String,
    val title: String,
    val description: String,
)

data class ItDayMapPlace(
    val id: String,
    val name: String,
    val category: String,
    val distanceText: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen: Boolean,
    val benefitSummary: String,
)

data class ItDayBarcodeData(
    val userName: String,
    val membershipName: String,
    val barcodeValue: String,
    val expiresAtText: String,
    val availableBenefits: List<String>,
)

data class ItDayReportData(
    val periodText: String,
    val attendanceRate: Int,
    val totalVisitCount: Int,
    val savedAmountText: String,
    val monthlySummaries: List<ItDayMonthlySummary>,
    val highlights: List<ItDayReportHighlight>,
)

data class ItDayMonthlySummary(
    val label: String,
    val visitCount: Int,
    val savedAmountText: String,
)

data class ItDayReportHighlight(
    val id: String,
    val title: String,
    val value: String,
    val description: String,
)

data class ItDayOnboardingPage(
    val id: String,
    val title: String,
    val description: String,
    val imageKey: String,
)

data class ItDayPaymentPlan(
    val id: String,
    val name: String,
    val priceText: String,
    val billingCycle: String,
    val benefits: List<String>,
    val isRecommended: Boolean,
)

data class ItDaySettingItem(
    val id: String,
    val title: String,
    val description: String,
)
