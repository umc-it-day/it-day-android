package com.example.itday.feature.onboarding.presentation

sealed interface OnboardingUiEvent {
    data object RequestLocationPermission : OnboardingUiEvent
    data object Complete : OnboardingUiEvent
}
