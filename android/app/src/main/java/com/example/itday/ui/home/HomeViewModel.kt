package com.example.itday.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(
    initialState: HomeUiState = HomePreviewData.barcodeDisabled,
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
            HomeAction.RefreshLocation -> Unit
            else -> sendNavigationEvent(action)
        }
    }

    fun setGuestMode(isGuestMode: Boolean) {
        _uiState.update { state ->
            when {
                isGuestMode -> HomePreviewData.guest
                state.membershipState == MembershipState.Guest -> HomePreviewData.barcodeDisabled
                else -> state
            }
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
}
