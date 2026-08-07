package com.example.itday.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.itday.core.location.LocationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    initialState: HomeUiState = HomePreviewData.barcodeDisabled,
    private val locationRepository: LocationRepository? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ActivateBarcode ->
                _uiState.update { state -> state.copy(membershipState = MembershipState.BarcodeEnabled) }
            HomeAction.UseMembership ->
                _uiState.update { state -> state.copy(showMembershipDialog = true) }
            HomeAction.ConfirmMembershipUse,
            HomeAction.DismissMembershipDialog,
            -> _uiState.update { state -> state.copy(showMembershipDialog = false) }
            HomeAction.ToggleBenefits ->
                _uiState.update { state -> state.copy(isBenefitExpanded = !state.isBenefitExpanded) }
            HomeAction.AddBenefit -> addMockBenefits()
            is HomeAction.SelectPartnerBrand -> selectPartnerBrand(action.id)
            HomeAction.RefreshLocation -> refreshLocation(forceRefresh = true)
            else -> sendNavigationEvent(action)
        }
    }

    fun loadLocation() {
        refreshLocation(forceRefresh = false)
    }

    private fun refreshLocation(forceRefresh: Boolean) {
        val repository = locationRepository ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationRefreshing = true) }
            val coordinate = repository.getCurrentLocation(forceRefresh)
            _uiState.update {
                it.copy(
                    locationCoordinate = coordinate ?: it.locationCoordinate,
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
            state.copy(
                partnerBrands = state.partnerBrands.map { brand -> brand.copy(selected = brand.id == id) },
            )
        }
    }

    private fun addMockBenefits() {
        if (_uiState.value.membershipState == MembershipState.Guest) {
            sendEvent(HomeEvent.OpenLogin)
            return
        }
        _uiState.update { state ->
            state.copy(benefits = HomePreviewData.barcodeEnabled.benefits)
        }
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
                HomeAction.ViewAllBenefits -> HomeEvent.OpenAllBenefits
                HomeAction.Login,
                HomeAction.RegisterMembership,
                -> HomeEvent.OpenLogin
                HomeAction.OpenProStore -> HomeEvent.OpenProStore
                HomeAction.OpenProChallenge -> HomeEvent.OpenProChallenge
                HomeAction.OpenAdvertisement -> HomeEvent.OpenAdvertisement
                HomeAction.OpenMap -> HomeEvent.OpenMap
                else -> return
            }
        sendEvent(event)
    }

    companion object {
        fun factory(locationRepository: LocationRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(HomeViewModel::class.java))
                    return HomeViewModel(locationRepository = locationRepository) as T
                }
            }
    }
}
