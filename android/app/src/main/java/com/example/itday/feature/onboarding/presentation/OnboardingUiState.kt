package com.example.itday.feature.onboarding.presentation

enum class AgreementType {
    Location,
    Privacy,
    Notification,
}

data class OnboardingUiState(
    val locationAgreed: Boolean = false,
    val privacyAgreed: Boolean = false,
    val notificationAgreed: Boolean = false,
) {
    val canContinue: Boolean
        get() = locationAgreed && privacyAgreed
}
