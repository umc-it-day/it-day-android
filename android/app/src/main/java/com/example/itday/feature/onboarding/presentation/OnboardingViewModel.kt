package com.example.itday.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun setAgreement(
        type: AgreementType,
        checked: Boolean,
    ) {
        _uiState.update { state ->
            when (type) {
                AgreementType.Location -> state.copy(locationAgreed = checked)
                AgreementType.Privacy -> state.copy(privacyAgreed = checked)
                AgreementType.Notification -> state.copy(notificationAgreed = checked)
            }
        }
    }
}
