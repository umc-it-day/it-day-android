package com.example.itday.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    private val _events = Channel<OnboardingUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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

    fun next() {
        if (_uiState.value.step == TERMS_STEP) {
            _uiState.update { it.copy(step = LOCATION_STEP) }
        } else {
            _events.trySend(OnboardingUiEvent.RequestLocationPermission)
        }
    }

    fun back() = _uiState.update { it.copy(step = TERMS_STEP, locationError = false) }

    fun onLocationResult(granted: Boolean) {
        if (granted) {
            _events.trySend(OnboardingUiEvent.Complete)
        } else {
            _uiState.update { it.copy(locationError = true) }
        }
    }

    private companion object {
        const val TERMS_STEP = 0
        const val LOCATION_STEP = 1
    }
}
