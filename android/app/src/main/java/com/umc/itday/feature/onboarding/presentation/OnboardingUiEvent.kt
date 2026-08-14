package com.umc.itday.feature.onboarding.presentation

sealed interface OnboardingUiEvent {
    data object RequestLocationPermission : OnboardingUiEvent
    data object RequestNotificationPermission : OnboardingUiEvent
    data object Complete : OnboardingUiEvent
}
