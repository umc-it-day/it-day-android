package com.umc.itday.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.R
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.feature.onboarding.domain.model.OnboardingSubmission
import com.umc.itday.feature.onboarding.domain.model.OnboardingTerm
import com.umc.itday.feature.onboarding.domain.model.TermAgreement
import com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: OnboardingRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    private val _events = Channel<OnboardingUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val terms = async { repository.getTerms() }
            val telecoms = async { repository.getTelecoms() }
            val termsResult = terms.await()
            val telecomsResult = telecoms.await()

            _uiState.update { state ->
                state.copy(
                    terms = (termsResult as? ApiResult.Success)?.data.orEmpty().toAgreementMap(),
                    availableCarriers =
                        (telecomsResult as? ApiResult.Success)?.data.orEmpty()
                            .mapNotNull { carrierTypeFromApiCode(it.code) },
                    isLoading = false,
                    errorMessage =
                        listOf(termsResult, telecomsResult)
                            .filterIsInstance<ApiResult.Failure>()
                            .firstOrNull()?.error?.toUserMessage(),
                )
            }
        }
    }

    fun setAgreement(type: AgreementType, checked: Boolean) {
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
            OnboardingUiState.LOCATION_PERM_STEP -> _events.trySend(OnboardingUiEvent.RequestLocationPermission)
            OnboardingUiState.CARRIER_STEP -> _uiState.update { it.copy(step = OnboardingUiState.MEMBERSHIP_STEP) }
            OnboardingUiState.MEMBERSHIP_STEP -> {
                _uiState.update { it.copy(step = OnboardingUiState.BRAND_STEP) }
                loadBrands()
            }
            OnboardingUiState.BRAND_STEP -> submitOnboarding()
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

    fun selectCarrier(carrier: CarrierType) {
        _uiState.update {
            it.copy(
                selectedCarrier = carrier,
                selectedMembershipGrade = null,
                selectedMembershipId = null,
                availableGrades = emptyList(),
            )
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getGrades(carrier.apiCode)) {
                is ApiResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            availableGrades =
                                result.data.mapNotNull { grade ->
                                    membershipGradeFromApiValue(grade.grade)?.let {
                                        MembershipGradeInfo(it, it.iconResId, grade.membershipId)
                                    }
                                },
                            isLoading = false,
                        )
                    }
                }
                is ApiResult.Failure -> updateFailure(result)
            }
        }
    }

    fun selectMembershipGrade(grade: MembershipGradeType) =
        _uiState.update { state ->
            state.copy(
                selectedMembershipGrade = grade,
                selectedMembershipId = state.availableGrades.firstOrNull { it.type == grade }?.membershipId,
            )
        }

    fun toggleBrand(value: String) =
        _uiState.update { state ->
            val brands =
                if (value in state.preferredBrands) state.preferredBrands - value
                else state.preferredBrands + value
            state.copy(preferredBrands = brands)
        }

    fun showTerms(type: AgreementType) = _uiState.update { it.copy(showingTerms = type) }
    fun hideTerms() = _uiState.update { it.copy(showingTerms = null) }

    private fun loadBrands() {
        if (_uiState.value.availableBrands.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getBrands()) {
                is ApiResult.Success -> _uiState.update { it.copy(availableBrands = result.data, isLoading = false) }
                is ApiResult.Failure -> updateFailure(result)
            }
        }
    }

    private fun submitOnboarding() {
        val state = _uiState.value
        val membershipId = state.selectedMembershipId ?: return
        if (state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val submission =
                OnboardingSubmission(
                    termAgreements =
                        state.terms.map { (type, term) ->
                            TermAgreement(term.id, state.isAgreed(type))
                        },
                    membershipId = membershipId,
                    preferredBrandIds =
                        state.availableBrands
                            .filter { it.name in state.preferredBrands }
                            .map { it.id },
                )
            when (val result = repository.submitOnboarding(submission)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(OnboardingUiEvent.Complete)
                }
                is ApiResult.Failure -> updateFailure(result)
            }
        }
    }

    private fun updateFailure(failure: ApiResult.Failure) {
        val message = failure.error.toUserMessage()
        _uiState.update { it.copy(isLoading = false, isSubmitting = false, errorMessage = message) }
        _events.trySend(OnboardingUiEvent.ShowMessage(message))
    }

    class Factory(private val repository: OnboardingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = OnboardingViewModel(repository) as T
    }
}

private fun List<OnboardingTerm>.toAgreementMap(): Map<AgreementType, OnboardingTerm> =
    mapIndexedNotNull { index, term ->
        val type =
            when {
                "위치" in term.title -> AgreementType.Location
                "개인" in term.title -> AgreementType.Privacy
                "알림" in term.title -> AgreementType.Notification
                else -> AgreementType.entries.getOrNull(index)
            }
        type?.let { it to term }
    }.toMap()

private fun OnboardingUiState.isAgreed(type: AgreementType): Boolean =
    when (type) {
        AgreementType.Location -> locationAgreed
        AgreementType.Privacy -> privacyAgreed
        AgreementType.Notification -> notificationAgreed
    }

private val CarrierType.apiCode: String
    get() = if (this == CarrierType.LGU_PLUS) "LGU" else name

private fun carrierTypeFromApiCode(value: String): CarrierType? =
    when (value.uppercase()) {
        "SKT" -> CarrierType.SKT
        "KT" -> CarrierType.KT
        "LGU", "LGU+", "LGU_PLUS" -> CarrierType.LGU_PLUS
        else -> null
    }

private fun membershipGradeFromApiValue(value: String): MembershipGradeType? =
    MembershipGradeType.entries.firstOrNull {
        it.name.equals(value, ignoreCase = true) || it.displayName == value
    }

private val MembershipGradeType.iconResId: Int
    get() =
        when (this) {
            MembershipGradeType.VVIP -> R.drawable.ic_membership_vvip
            MembershipGradeType.VIP -> R.drawable.ic_membership_vip_star
            MembershipGradeType.DIAMOND -> R.drawable.ic_membership_diamond
            MembershipGradeType.GOLD -> R.drawable.ic_membership_gold
            MembershipGradeType.SILVER -> R.drawable.ic_membership_silver
            MembershipGradeType.WHITE -> R.drawable.white_circle
            MembershipGradeType.GENERAL -> R.drawable.ic_membership_general
        }
