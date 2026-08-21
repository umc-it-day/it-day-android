package com.umc.itday.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.core.location.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    initialState: HomeUiState = HomePreviewData.barcodeDisabled,
    private val locationRepository: LocationRepository? = null,
    private val localPreferencesDataSource: com.umc.itday.core.local.LocalPreferencesDataSource? = null,
    private val barcodeRepository: com.umc.itday.feature.barcode.domain.repository.BarcodeRepository? = null,
    private val onboardingRepository: com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var timerJob: Job? = null
    private var brandCache: List<com.umc.itday.feature.onboarding.domain.model.PreferredBrand> = emptyList()

    init {
        observePreferredBrands()
    }

    fun loadBarcodeAndLottery() {
        val repo = barcodeRepository ?: return
        viewModelScope.launch {
            val barcodeDeferred = async { repo.getBarcode() }
            val lotteryDeferred = async { repo.getLottery() }

            val barcodeResult = barcodeDeferred.await()
            val lotteryResult = lotteryDeferred.await()

            val barcodeNum = (barcodeResult as? com.umc.itday.core.data.result.ApiResult.Success)?.data
            val lotteryNum = (lotteryResult as? com.umc.itday.core.data.result.ApiResult.Success)?.data

            _uiState.update { state ->
                val currentMem = state.membership
                if (currentMem != null) {
                    state.copy(
                        membership =
                            currentMem.copy(
                                barcodeValue = lotteryNum ?: currentMem.barcodeValue,
                                userBarcodeNumber = barcodeNum ?: currentMem.userBarcodeNumber,
                            ),
                    )
                } else if (barcodeNum != null || lotteryNum != null) {
                    state.copy(
                        membership =
                            HomeMembershipUiModel(
                                carrier = "SKT",
                                grade = "VIP",
                                brandName = "제휴 매장 선택",
                                benefitText = "통신사 제휴 할인 혜택",
                                barcodeValue = lotteryNum ?: "12345678",
                                userBarcodeNumber = barcodeNum ?: "1234567890123456",
                                pointText = "0 P",
                            ),
                    )
                } else {
                    state
                }
            }
        }
    }

    private fun observePreferredBrands() {
        val local = localPreferencesDataSource ?: return
        viewModelScope.launch {
            if (brandCache.isEmpty()) {
                val result = onboardingRepository?.getBrands()
                if (result is com.umc.itday.core.data.result.ApiResult.Success) {
                    brandCache = result.data
                }
            }

            local.preferredBrandNames.collect { brandNames ->
                if (brandNames.isEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            partnerBrands = emptyList(),
                            benefits = emptyList(),
                        )
                    }
                    return@collect
                }

                if (brandCache.isEmpty()) {
                    val result = onboardingRepository?.getBrands()
                    if (result is com.umc.itday.core.data.result.ApiResult.Success) {
                        brandCache = result.data
                    }
                }
                val brandImageMap = brandCache.associate { it.name to it.imageUrl }

                _uiState.update { state ->
                    val existingBrands = state.partnerBrands.associateBy { it.displayName }
                    val existingBenefits = state.benefits.associateBy { it.brandName }
                    val previouslySelected = state.partnerBrands.firstOrNull { it.selected }?.displayName
                    val updatedPartnerBrands =
                        brandNames.mapIndexed { index, name ->
                            val isSelected =
                                if (previouslySelected != null) {
                                    name == previouslySelected
                                } else {
                                    index == 0
                                }
                            val cachedUrl = brandImageMap[name] ?: existingBrands[name]?.logoUrl
                            existingBrands[name]?.copy(selected = isSelected, logoUrl = cachedUrl ?: existingBrands[name]?.logoUrl)
                                ?: HomePartnerBrandUiModel(
                                    id = name,
                                    displayName = name,
                                    logoRes = com.umc.itday.core.util.BrandBenefitHelper.getBrandLogoRes(name) ?: 0,
                                    logoUrl = cachedUrl,
                                    selected = isSelected,
                                )
                        }
                    val selectedBrand = updatedPartnerBrands.firstOrNull { it.selected } ?: updatedPartnerBrands.firstOrNull()
                    val updatedMembership =
                        if (selectedBrand != null && state.membership != null) {
                            state.membership.copy(
                                brandName = selectedBrand.displayName,
                                benefitText = com.umc.itday.core.util.BrandBenefitHelper.getBenefitSummary(selectedBrand.displayName),
                            )
                        } else {
                            state.membership
                        }
                    val updatedBenefits =
                        brandNames.mapIndexed { index, name ->
                            existingBenefits[name]?.copy(rank = index + 1)
                                ?: HomeBenefitUiModel(
                                    id = name,
                                    rank = index + 1,
                                    brandName = name,
                                    benefitText = com.umc.itday.core.util.BrandBenefitHelper.getBenefitSummary(name),
                                )
                        }
                    state.copy(
                        partnerBrands = updatedPartnerBrands,
                        benefits = updatedBenefits,
                        membership = updatedMembership,
                    )
                }
            }
        }
    }

    fun onAction(action: HomeAction) {

        when (action) {
            HomeAction.ActivateBarcode -> {
                _uiState.update { state -> state.copy(membershipState = MembershipState.BarcodeEnabled) }
                startMembershipTimer()
            }
            HomeAction.UseMembership -> startMembershipTimer()
            HomeAction.ConfirmMembershipUse,
            HomeAction.DismissMembershipDialog,
            -> {
                stopMembershipTimer()
                _uiState.update { state -> state.copy(showMembershipDialog = false) }
            }
            HomeAction.RefreshBarcode -> startMembershipTimer()
            HomeAction.ToggleBenefits ->
                _uiState.update { state -> state.copy(isBenefitExpanded = !state.isBenefitExpanded) }
            HomeAction.AddBenefit,
            HomeAction.ViewAllBenefits -> sendEvent(HomeEvent.OpenBrandDetail)
            is HomeAction.AddBrandBenefit -> addBrandBenefit(action.brand)
            is HomeAction.SelectPartnerBrand -> selectPartnerBrand(action.id)
            HomeAction.RefreshLocation -> refreshLocation(forceRefresh = true)
            else -> sendNavigationEvent(action)
        }
    }

    private fun startMembershipTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val totalSeconds = 300 // 5분
            for (i in totalSeconds downTo 0) {
                _uiState.update { it.copy(remainingTimeSeconds = i) }
                if (i == 0) {
                    _uiState.update { it.copy(showMembershipDialog = true, remainingTimeSeconds = null) }
                } else {
                    delay(1000)
                }
            }
        }
    }

    private fun stopMembershipTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(remainingTimeSeconds = null) }
    }

    fun loadLocation() {
        refreshLocation(forceRefresh = false)
    }

    private fun refreshLocation(forceRefresh: Boolean) {
        val repository = locationRepository ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationRefreshing = true) }
            val coordinate = repository.getCurrentLocation(forceRefresh)

            val addressName =
                if (coordinate != null) {
                    repository.getAddress(coordinate.latitude, coordinate.longitude)
                } else {
                    null
                }

            _uiState.update { state ->
                state.copy(
                    locationCoordinate = coordinate ?: state.locationCoordinate,
                    location =
                        addressName?.let {
                            state.location.copy(name = "현재 위치", address = it)
                        } ?: state.location,
                    isLocationRefreshing = false,
                    isLocationUnavailable = coordinate == null,
                )
            }
        }
    }

    fun setGuestMode(isGuestMode: Boolean) {
        _uiState.update { state ->
            val target =
                when {
                    isGuestMode -> HomePreviewData.guest
                    state.membershipState == MembershipState.Guest -> HomePreviewData.barcodeDisabled
                    else -> return@update state
                }
            target.copy(
                locationCoordinate = state.locationCoordinate,
                isLocationRefreshing = state.isLocationRefreshing,
                isLocationUnavailable = state.isLocationUnavailable,
            )
        }
    }

    private fun selectPartnerBrand(id: String) {
        _uiState.update { state ->
            val updatedBrands = state.partnerBrands.map { brand -> brand.copy(selected = brand.id == id) }
            val selectedBrand = updatedBrands.firstOrNull { it.id == id }
            val updatedMembership =
                if (selectedBrand != null && state.membership != null) {
                    state.membership.copy(
                        brandName = selectedBrand.displayName,
                        benefitText = com.umc.itday.core.util.BrandBenefitHelper.getBenefitSummary(selectedBrand.displayName),
                    )
                } else {
                    state.membership
                }
            state.copy(
                partnerBrands = updatedBrands,
                membership = updatedMembership,
            )
        }
    }

    private fun addBrandBenefit(brand: com.umc.itday.feature.onboarding.domain.model.PreferredBrand) {
        val alreadyAdded = _uiState.value.benefits.any { it.brandName == brand.name }
        if (alreadyAdded) {
            sendEvent(HomeEvent.ShowMessage("이미 추가된 제휴 브랜드입니다."))
            return
        }

        viewModelScope.launch {
            localPreferencesDataSource?.addPreferredBrandName(brand.name)
        }

        val benefitDesc = com.umc.itday.core.util.BrandBenefitHelper.getBenefitSummary(brand.name, brand.category)
        val newBenefit =
            HomeBenefitUiModel(
                id = brand.id.toString(),
                rank = _uiState.value.benefits.size + 1,
                brandName = brand.name,
                benefitText = benefitDesc,
            )
        val brandLogoRes = com.umc.itday.core.util.BrandBenefitHelper.getBrandLogoRes(brand.name) ?: 0
        _uiState.update { state ->
            val wasEmpty = state.partnerBrands.isEmpty()
            val newPartnerBrand =
                HomePartnerBrandUiModel(
                    id = brand.id.toString(),
                    displayName = brand.name,
                    logoRes = brandLogoRes,
                    logoUrl = brand.imageUrl,
                    selected = wasEmpty,
                )

            val updatedBrands =
                if (state.partnerBrands.any { it.displayName == brand.name }) {
                    state.partnerBrands
                } else {
                    state.partnerBrands + newPartnerBrand
                }
            val updatedMembership =
                if (wasEmpty && state.membership != null) {
                    state.membership.copy(
                        brandName = brand.name,
                        benefitText = benefitDesc,
                    )
                } else {
                    state.membership
                }
            state.copy(
                benefits = state.benefits + newBenefit,
                partnerBrands = updatedBrands,
                membership = updatedMembership,
            )
        }
        sendEvent(HomeEvent.ShowMessage("${brand.name} 혜택이 추가되었습니다!"))
    }


    private fun sendEvent(event: HomeEvent) {
        _events.trySend(event)
    }

    private fun sendNavigationEvent(action: HomeAction) {
        val event =
            when (action) {
                HomeAction.OpenCalendar -> HomeEvent.OpenCalendar
                HomeAction.OpenProfile -> HomeEvent.OpenProfile
                HomeAction.OpenCarrierComparison -> HomeEvent.OpenCarrierComparison
                HomeAction.OpenMyMembership -> HomeEvent.OpenMyMembership
                HomeAction.Login,
                HomeAction.RegisterMembership,
                -> HomeEvent.OpenLogin
                HomeAction.OpenProStore -> HomeEvent.ShowMessage("상점 기능은 준비 중이에요! 곧 오픈될 예정입니다.")
                HomeAction.OpenProChallenge -> HomeEvent.OpenProChallenge

                HomeAction.OpenAdvertisement -> HomeEvent.OpenAdvertisement
                HomeAction.OpenMap -> HomeEvent.OpenMap
                HomeAction.OpenBrandDetail -> HomeEvent.OpenBrandDetail
                HomeAction.OpenOnboarding -> HomeEvent.OpenOnboarding
                else -> return
            }
        sendEvent(event)
    }

    companion object {
        fun factory(
            locationRepository: LocationRepository,
            localPreferencesDataSource: com.umc.itday.core.local.LocalPreferencesDataSource? = null,
            barcodeRepository: com.umc.itday.feature.barcode.domain.repository.BarcodeRepository? = null,
            onboardingRepository: com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository? = null,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(HomeViewModel::class.java))
                    return HomeViewModel(
                        locationRepository = locationRepository,
                        localPreferencesDataSource = localPreferencesDataSource,
                        barcodeRepository = barcodeRepository,
                        onboardingRepository = onboardingRepository,
                    ) as T
                }
            }
    }
}
