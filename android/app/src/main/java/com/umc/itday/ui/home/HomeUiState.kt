package com.umc.itday.ui.home

import androidx.annotation.DrawableRes
import com.umc.itday.core.location.LocationCoordinate

enum class MembershipState {
    Guest,
    NotRegistered,
    BarcodeDisabled,
    BarcodeEnabled,
}

data class HomeUiState(
    val membershipState: MembershipState,
    val location: HomeLocationUiModel,
    val membership: HomeMembershipUiModel?,
    val partnerBrands: List<HomePartnerBrandUiModel>,
    val benefits: List<HomeBenefitUiModel>,
    val brandDays: List<HomeBrandDayUiModel>,
    val isBenefitExpanded: Boolean = true,
    val showProSection: Boolean = true,
    val showMembershipDialog: Boolean = false,
    val remainingTimeSeconds: Int? = null,
    val locationCoordinate: LocationCoordinate? = null,
    val isLocationRefreshing: Boolean = false,
    val isLocationUnavailable: Boolean = false,
)

data class HomeLocationUiModel(
    val name: String,
    val address: String,
)

data class HomeMembershipUiModel(
    val carrier: String,
    val grade: String,
    val brandName: String,
    val benefitText: String,
    val barcodeValue: String,
    val userBarcodeNumber: String = barcodeValue,
    val pointText: String,
)


data class HomePartnerBrandUiModel(
    val id: String,
    val displayName: String,
    @DrawableRes val logoRes: Int,
    val logoUrl: String? = null,
    val selected: Boolean = false,
)

data class HomeBenefitUiModel(
    val id: String,
    val rank: Int,
    val brandName: String,
    val benefitText: String,
)

data class HomeBrandDayUiModel(
    val id: String,
    val brandName: String,
    val benefitText: String,
    val scheduleText: String,
    val highlighted: Boolean = false,
    val categoryText: String = "",
)

sealed interface HomeAction {
    data object OpenCalendar : HomeAction

    data object OpenProfile : HomeAction

    data object RefreshLocation : HomeAction

    data object RegisterMembership : HomeAction

    data object ActivateBarcode : HomeAction

    data object UseMembership : HomeAction

    data object ConfirmMembershipUse : HomeAction

    data object DismissMembershipDialog : HomeAction

    data object RefreshBarcode : HomeAction

    data object OpenBrandDetail : HomeAction

    data object OpenCarrierComparison : HomeAction

    data object AddBenefit : HomeAction

    data object ToggleBenefits : HomeAction

    data object ViewAllBenefits : HomeAction

    data object OpenMyMembership : HomeAction

    data object Login : HomeAction

    data object OpenProStore : HomeAction

    data object OpenProChallenge : HomeAction

    data object OpenAdvertisement : HomeAction

    data object OpenMap : HomeAction

    data object OpenOnboarding : HomeAction

    data class AddBrandBenefit(
        val brand: com.umc.itday.feature.onboarding.domain.model.PreferredBrand,
    ) : HomeAction

    data class SelectPartnerBrand(
        val id: String,
    ) : HomeAction
}

sealed interface HomeEvent {
    data object OpenCalendar : HomeEvent

    data object OpenProfile : HomeEvent

    data object OpenCarrierComparison : HomeEvent

    data object OpenMyMembership : HomeEvent

    data object OpenBenefitSelection : HomeEvent

    data object OpenAllBenefits : HomeEvent

    data object OpenLogin : HomeEvent

    data object OpenProStore : HomeEvent

    data object OpenProChallenge : HomeEvent

    data object OpenAdvertisement : HomeEvent

    data object OpenMap : HomeEvent

    data class ShowMessage(
        val message: String,
    ) : HomeEvent

    data object OpenBrandDetail : HomeEvent

    data object OpenOnboarding : HomeEvent
}
