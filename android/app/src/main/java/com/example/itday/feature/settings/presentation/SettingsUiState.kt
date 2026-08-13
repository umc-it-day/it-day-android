package com.example.itday.feature.settings.presentation

enum class SettingsScreenType {
    Main,
    PrivacySecurity,
    CustomerService,
}

data class UserProfile(
    val userName: String = "김예진",
    val userEmail: String = "asdfg1234@it-day.example.edu",
)

data class MembershipInfo(
    val carrier: String = "SKT",
    val grade: String = "VIP",
    val isPro: Boolean = true,
    val barcodeNumber: String = "1234 5678 9012 3456",
)

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String = "잇데이 고객센터 문의 답변 준비 중입니다.",
    val isExpanded: Boolean = false,
)

data class SettingsUiState(
    val currentScreen: SettingsScreenType = SettingsScreenType.Main,
    val profile: UserProfile = UserProfile(),
    val membership: MembershipInfo = MembershipInfo(),
    val promotionNotification: Boolean = true,
    val characterNotification: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isWithdrawing: Boolean = false,
    val errorMessage: String? = null,
    val appVersion: String = "V 0.00.0",
    val faqList: List<FaqItem> =
        listOf(
            FaqItem(1, "바코드가 자동으로 뜨지 않아요"),
            FaqItem(2, "멤버십 등록은 어떻게 하나요?"),
            FaqItem(3, "위치 권한을 허용했는데도 안돼요"),
        ),
)
