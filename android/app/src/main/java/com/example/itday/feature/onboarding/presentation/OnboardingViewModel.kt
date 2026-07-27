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
        when (_uiState.value.step) {
            TERMS_STEP -> _uiState.update { it.copy(step = LOCATION_STEP) }
            LOCATION_STEP -> _events.trySend(OnboardingUiEvent.RequestLocationPermission)
            BRAND_STEP -> _events.trySend(OnboardingUiEvent.Complete)
            else -> _uiState.update { it.copy(step = it.step + 1) }
        }
    }

    fun back() = _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(TERMS_STEP), locationError = false) }

    fun onLocationResult(granted: Boolean) {
        if (granted) {
            _uiState.update { it.copy(step = CARRIER_STEP, locationError = false) }
        } else {
            _uiState.update { it.copy(locationError = true) }
        }
    }

    fun selectCarrier(value: String) = _uiState.update { it.copy(carrier = value, membership = null) }
    fun selectMembership(value: String) = _uiState.update { it.copy(membership = value) }
    fun toggleBrand(value: String) =
        _uiState.update {
            val brands =
                if (value in it.preferredBrands) it.preferredBrands - value
                else if (it.preferredBrands.size < 3) it.preferredBrands + value
                else it.preferredBrands
            it.copy(preferredBrands = brands)
        }

    private companion object {
        const val TERMS_STEP = 0
        const val LOCATION_STEP = 1
    }
}
