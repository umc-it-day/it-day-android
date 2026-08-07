package com.example.itday.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itday.core.di.appContainer
import com.example.itday.ui.home.component.BrandDaySection
import com.example.itday.ui.home.component.CarrierComparisonBanner
import com.example.itday.ui.home.component.CurrentLocationRow
import com.example.itday.ui.home.component.GuestMembershipCard
import com.example.itday.ui.home.component.HomeAdvertisement
import com.example.itday.ui.home.component.HomeTopBar
import com.example.itday.ui.home.component.ItDayProSection
import com.example.itday.ui.home.component.MembershipBarcodeCard
import com.example.itday.ui.home.component.MembershipBenefitHeader
import com.example.itday.ui.home.component.MembershipBenefitSection
import com.example.itday.ui.home.component.MembershipStatusCard
import com.example.itday.ui.home.component.MembershipUseDialog
import com.example.itday.ui.theme.HomePrimary
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayTheme
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel =
        viewModel(
            factory = HomeViewModel.factory(LocalContext.current.appContainer.locationRepository),
        ),
    isGuestMode: Boolean = false,
    onEvent: (HomeEvent) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.loadLocation()
        viewModel.events.collect(onEvent)
    }
    LaunchedEffect(isGuestMode) {
        viewModel.setGuestMode(isGuestMode)
    }
    HomeScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = HomePrimary)) {
        HomeScreenContent(uiState = uiState, onAction = onAction, modifier = modifier)
    }
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(ItDayWhite)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ItDayDimens.Space24),
    ) {
        Spacer(Modifier.height(ItDayDimens.Space8))
        HomeTopBar(
            onCalendarClick = { onAction(HomeAction.OpenCalendar) },
            onProfileClick = { onAction(HomeAction.OpenProfile) },
        )
        Spacer(Modifier.height(ItDayDimens.Space16))
        CurrentLocationRow(
            location = uiState.location,
            isRefreshing = uiState.isLocationRefreshing,
            onRefresh = { onAction(HomeAction.RefreshLocation) },
        )
        Spacer(Modifier.height(ItDayDimens.Space16))
        MembershipContent(uiState, onAction)
        HomeDashboardSections(uiState, onAction)
    }

    if (uiState.showMembershipDialog) {
        MembershipUseDialog(
            onConfirm = { onAction(HomeAction.ConfirmMembershipUse) },
            onDismiss = { onAction(HomeAction.DismissMembershipDialog) },
        )
    }
}

@Composable
private fun HomeDashboardSections(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    Spacer(Modifier.height(ItDayDimens.Space24))
    if (uiState.membershipState == MembershipState.Guest) {
        HomeAdvertisement(onClick = { onAction(HomeAction.OpenAdvertisement) })
    } else {
        CarrierComparisonBanner(onClick = { onAction(HomeAction.OpenCarrierComparison) })
    }
    Spacer(Modifier.height(ItDayDimens.Space24))
    MembershipBenefitHeader(onMyMembership = { onAction(HomeAction.OpenMyMembership) })
    Spacer(Modifier.height(ItDayDimens.Space16))
    MembershipBenefitSection(
        benefits = uiState.benefits,
        expanded = uiState.isBenefitExpanded,
        onToggle = { onAction(HomeAction.ToggleBenefits) },
        onAddBenefit = { onAction(HomeAction.AddBenefit) },
        onViewAll = { onAction(HomeAction.ViewAllBenefits) },
    )
    Spacer(Modifier.height(ItDayDimens.Space24))
    BrandDaySection(brandDays = uiState.brandDays)
    if (uiState.showProSection && uiState.membershipState != MembershipState.Guest) {
        Spacer(Modifier.height(ItDayDimens.Space24))
        ItDayProSection(
            onStoreClick = { onAction(HomeAction.OpenProStore) },
            onChallengeClick = { onAction(HomeAction.OpenProChallenge) },
        )
    }
    Spacer(Modifier.height(ItDayDimens.Space24))
    HomeAdvertisement(
        onClick = { onAction(HomeAction.OpenAdvertisement) },
        mint = uiState.membershipState == MembershipState.Guest,
    )
    Spacer(Modifier.height(ItDayDimens.Space24))
}

@Composable
private fun MembershipContent(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    when (uiState.membershipState) {
        MembershipState.Guest ->
            GuestMembershipCard(onLogin = { onAction(HomeAction.Login) })
        MembershipState.NotRegistered ->
            MembershipStatusCard(
                title = "내 멤버십",
                description = "등록된 멤버십이 없어요.",
                actionText = "등록하기",
                onAction = { onAction(HomeAction.RegisterMembership) },
            )
        MembershipState.BarcodeDisabled ->
            uiState.membership?.let { membership ->
                MembershipBarcodeCard(
                    membership = membership,
                    brands = uiState.partnerBrands,
                    barcodeEnabled = false,
                    onActivate = { onAction(HomeAction.ActivateBarcode) },
                    onUse = { onAction(HomeAction.UseMembership) },
                    onBrandClick = { onAction(HomeAction.SelectPartnerBrand(it)) },
                )
            }
        MembershipState.BarcodeEnabled -> {
            val membership = uiState.membership ?: return
            MembershipBarcodeCard(
                membership = membership,
                brands = uiState.partnerBrands,
                barcodeEnabled = true,
                onActivate = { onAction(HomeAction.ActivateBarcode) },
                onUse = { onAction(HomeAction.UseMembership) },
                onBrandClick = { onAction(HomeAction.SelectPartnerBrand(it)) },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1800)
@Composable
private fun BarcodeEnabledHomePreview() {
    ItDayTheme(dynamicColor = false) {
        HomeScreen(HomePreviewData.noBenefits, onAction = {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun BarcodeDisabledHomePreview() {
    ItDayTheme(dynamicColor = false) {
        HomeScreen(HomePreviewData.barcodeDisabled, onAction = {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun GuestHomePreview() {
    ItDayTheme(dynamicColor = false) {
        HomeScreen(HomePreviewData.guest, onAction = {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EmptyHomePreview() {
    ItDayTheme(dynamicColor = false) {
        HomeScreen(HomePreviewData.empty, onAction = {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun PreferredBrandsSelectedHomePreview() {
    ItDayTheme(dynamicColor = false) {
        HomeScreen(HomePreviewData.barcodeEnabled, onAction = {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun NoBenefitHomePreview() {
    ItDayTheme(dynamicColor = false) {
        HomeScreen(HomePreviewData.noBenefits, onAction = {})
    }
}
