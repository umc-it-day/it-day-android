package com.umc.itday.feature.onboarding.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.BuildConfig
import com.umc.itday.R
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.feature.onboarding.domain.model.OnboardingSubmission
import com.umc.itday.feature.onboarding.domain.model.OnboardingTerm
import com.umc.itday.feature.onboarding.domain.model.TermAgreement
import com.umc.itday.feature.onboarding.domain.model.TelecomGrade
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
    private val debugLogger: (String) -> Unit = { message ->
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    },
    private val infoLogger: (String) -> Unit = { message ->
        if (BuildConfig.DEBUG) Log.i(TAG, message)
    },
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
            logDebug("GET /api/terms, GET /api/telecoms 요청 시작")
            val terms = async { repository.getTerms() }
            val telecoms = async { repository.getTelecoms() }
            val termsResult = terms.await()
            val telecomsResult = telecoms.await()

            if (termsResult is ApiResult.Success) {
                logInfo("GET /api/terms 성공: ${termsResult.data.size}개")
            }
            if (telecomsResult is ApiResult.Success) {
                logInfo(
                    "GET /api/telecoms 성공: ${telecomsResult.data.size}개, " +
                        "values=${telecomsResult.data.map { "${it.code}/${it.label}" }}",
                )
            }

            _uiState.update { state ->
                val carrierCodes =
                    (telecomsResult as? ApiResult.Success)
                        ?.data
                        .orEmpty()
                        .mapNotNull { telecom ->
                            val carrier = carrierTypeFromApiCode(telecom.code) ?: carrierTypeFromApiCode(telecom.label)
                            carrier?.let { it to telecom.code.ifBlank { telecom.label } }
                        }.toMap()
                val carriers = carrierCodes.keys.toList().ifEmpty { CarrierType.entries }

                state.copy(
                    terms = (termsResult as? ApiResult.Success)?.data.orEmpty().toAgreementMap(),
                    availableCarriers = carriers,
                    carrierApiCodes = carrierCodes,
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
            val telecomCode = _uiState.value.carrierApiCodes[carrier] ?: carrier.apiCode
            logDebug("GET /api/telecoms/$telecomCode/grades 요청 시작")
            when (val result = repository.getGradesWithFallback(carrier, telecomCode)) {
                is ApiResult.Success -> {
                    logInfo("GET /api/telecoms/$telecomCode/grades 성공: ${result.data.size}개")
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

    fun toggleBrand(value: Long) =
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
            logDebug("GET /api/brands 요청 시작")
            when (val result = repository.getBrands()) {
                is ApiResult.Success -> {
                    logInfo("GET /api/brands 성공: ${result.data.size}개")
                    _uiState.update { it.copy(availableBrands = result.data, isLoading = false) }
                }
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
                    preferredBrandIds = state.preferredBrands.toList(),
                )
            logDebug(
                "POST /api/members/onboarding 요청 시작: " +
                    "membershipId=${submission.membershipId}, " +
                    "preferredBrandIds=${submission.preferredBrandIds}",
            )
            when (val result = repository.submitOnboarding(submission)) {
                is ApiResult.Success -> {
                    logInfo("POST /api/members/onboarding 성공")
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

    private fun logDebug(message: String) {
        debugLogger(message)
    }

    private fun logInfo(message: String) {
        infoLogger(message)
    }

    private suspend fun OnboardingRepository.getGradesWithFallback(
        carrier: CarrierType,
        telecomCode: String,
    ): ApiResult<List<TelecomGrade>> {
        val firstResult = getGrades(telecomCode)
        if (firstResult is ApiResult.Success || carrier != CarrierType.LGU_PLUS) return firstResult

        val fallbackCode = "LGU+"
        if (telecomCode == fallbackCode) return firstResult

        logDebug("GET /api/telecoms/$telecomCode/grades 실패, $fallbackCode 로 재시도")
        return getGrades(fallbackCode)
    }

    class Factory(
        private val repository: OnboardingRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OnboardingViewModel(repository = repository) as T
    }

    private companion object {
        const val TAG = "OnboardingApi"
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
    when (value.normalizedCarrierValue()) {
        "SKT" -> CarrierType.SKT
        "KT" -> CarrierType.KT
        "LGU", "LGU+", "LGUPLUS", "LGUPLUS멤버십" -> CarrierType.LGU_PLUS
        "SKTELECOM", "SK텔레콤", "SKT멤버십" -> CarrierType.SKT
        "KT멤버십" -> CarrierType.KT
        else -> null
    }

private fun String.normalizedCarrierValue(): String =
    trim()
        .uppercase()
        .replace(" ", "")
        .replace("_", "")
        .replace("-", "")

private fun membershipGradeFromApiValue(value: String): MembershipGradeType? =
    MembershipGradeType.entries.firstOrNull { grade ->
        val normalizedValue = value.normalizedGradeValue()
        val normalizedName = grade.name.normalizedGradeValue()
        val normalizedDisplayName = grade.displayName.normalizedGradeValue()

        normalizedValue == normalizedName ||
            normalizedValue == normalizedDisplayName ||
            normalizedValue.contains(normalizedName) ||
            normalizedValue.contains(normalizedDisplayName)
    } ?: when (value.normalizedGradeValue()) {
        "브이브이아이피", "최우수" -> MembershipGradeType.VVIP
        "브이아이피", "우수" -> MembershipGradeType.VIP
        "다이아", "다이아몬드" -> MembershipGradeType.DIAMOND
        "골드" -> MembershipGradeType.GOLD
        "실버" -> MembershipGradeType.SILVER
        "화이트" -> MembershipGradeType.WHITE
        "일반", "기본" -> MembershipGradeType.GENERAL
        else -> null
    }

private fun String.normalizedGradeValue(): String =
    trim()
        .lowercase()
        .replace("등급", "")
        .replace("grade", "")
        .filter { it.isLetterOrDigit() }

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
