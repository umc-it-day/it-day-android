package com.umc.itday.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

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
            OnboardingUiState.TERMS_STEP -> {
                if (_uiState.value.notificationAgreed) {
                    _events.trySend(OnboardingUiEvent.RequestNotificationPermission)
                }
                _uiState.update { it.copy(step = OnboardingUiState.LOCATION_PERM_STEP) }
            }
            OnboardingUiState.LOCATION_PERM_STEP -> {
                _events.trySend(OnboardingUiEvent.RequestLocationPermission)
            }
            OnboardingUiState.BRAND_STEP -> _events.trySend(OnboardingUiEvent.Complete)
            else -> _uiState.update { it.copy(step = it.step + 1) }
        }
    }

    fun back() =
        _uiState.update {
            it.copy(
                step = (it.step - 1).coerceAtLeast(OnboardingUiState.TERMS_STEP),
                locationError = false,
            )
        }

    fun onLocationResult(granted: Boolean) {
        if (granted) {
            _uiState.update { it.copy(step = OnboardingUiState.CARRIER_STEP, locationError = false) }
        } else {
            _uiState.update { it.copy(locationError = true) }
        }
    }

    fun selectCarrier(carrier: CarrierType) = _uiState.update { it.copy(selectedCarrier = carrier) }

    fun selectMembershipGrade(grade: MembershipGradeType) = _uiState.update { it.copy(selectedMembershipGrade = grade) }

    fun toggleBrand(value: String) =
        _uiState.update { state ->
            val brands =
                if (value in state.preferredBrands) {
                    state.preferredBrands - value
                } else {
                    state.preferredBrands + value
                }
            state.copy(preferredBrands = brands)
        }

    fun showTerms(type: AgreementType) = _uiState.update { it.copy(showingTerms = type) }

    fun hideTerms() = _uiState.update { it.copy(showingTerms = null) }
}
