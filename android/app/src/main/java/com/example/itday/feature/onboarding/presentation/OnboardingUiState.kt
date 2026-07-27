package com.example.itday.feature.onboarding.presentation

enum class AgreementType {
    Location,
    Privacy,
    Notification,
}

data class OnboardingUiState(
    val step: Int = 0,
    val locationAgreed: Boolean = false,
    val privacyAgreed: Boolean = false,
    val notificationAgreed: Boolean = false,
    val locationError: Boolean = false,
    val carrier: String? = null,
    val membership: String? = null,
    val preferredBrands: Set<String> = emptySet(),
) {
    val canContinue: Boolean
        get() =
            when (step) {
                0 -> locationAgreed && privacyAgreed
                CARRIER_STEP -> carrier != null
                MEMBERSHIP_STEP -> membership != null
                BRAND_STEP -> preferredBrands.size == 3
                else -> true
            }
}
