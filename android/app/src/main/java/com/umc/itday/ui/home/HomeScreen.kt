package com.umc.itday.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.itday.BuildConfig
import com.umc.itday.core.di.appContainer
import com.umc.itday.ui.component.ItDayButton
import com.umc.itday.ui.component.ItDayButtonSize
import com.umc.itday.ui.component.ItDayButtonVariant
import com.umc.itday.ui.home.component.BrandDaySection
import com.umc.itday.ui.home.component.CarrierComparisonBanner
import com.umc.itday.ui.home.component.CurrentLocationRow
import com.umc.itday.ui.home.component.GuestMembershipCard
import com.umc.itday.ui.home.component.HomeTopBar

import com.umc.itday.ui.home.component.ItDayProSection
import com.umc.itday.ui.home.component.MembershipBarcodeCard
import com.umc.itday.ui.home.component.MembershipBenefitHeader
import com.umc.itday.ui.home.component.MembershipBenefitSection
import com.umc.itday.ui.home.component.MembershipStatusCard
import com.umc.itday.ui.home.component.MembershipUseDialog
import com.umc.itday.ui.theme.HomePrimary
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayTheme
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    isGuestMode: Boolean = false,
    onEvent: (HomeEvent) -> Unit = {},
) {
    val container = LocalContext.current.appContainer
    val viewModel: HomeViewModel =
        viewModel(
            factory =
                HomeViewModel.factory(
                    locationRepository = container.locationRepository,
                    localPreferencesDataSource = container.localPreferencesDataSource,
                    barcodeRepository = container.barcodeRepository,
                    onboardingRepository = container.onboardingRepository,
                ),
        )


    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showCarrierComparison by remember { mutableStateOf(false) }
    var showPartnerDetail by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.loadLocation()
        if (!isGuestMode) {
            viewModel.loadBarcodeAndLottery()
        }
        viewModel.events.collect { event ->

            when (event) {
                HomeEvent.OpenCarrierComparison -> showCarrierComparison = true
                HomeEvent.OpenBrandDetail -> showPartnerDetail = true
                is HomeEvent.ShowMessage -> {
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                else -> onEvent(event)
            }
        }
    }
    LaunchedEffect(isGuestMode) {
        viewModel.setGuestMode(isGuestMode)
    }

    if (showCarrierComparison) {
        CarrierComparisonScreen(onBack = { showCarrierComparison = false })
    } else if (showPartnerDetail) {
        PartnerBrandDetailScreen(
            onBack = { showPartnerDetail = false },
            onBrandClick = { brand ->
                viewModel.onAction(HomeAction.AddBrandBenefit(brand))
            },
        )
    } else {


        HomeScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = modifier,
        )
    }
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
        if (BuildConfig.SHOW_ONBOARDING_DEBUG_ENTRY) {
            Spacer(Modifier.height(ItDayDimens.Space16))
            ItDayButton(
                text = "온보딩 브랜드 이미지 확인",
                onClick = { onAction(HomeAction.OpenOnboarding) },
                modifier = Modifier.fillMaxWidth(),
                variant = ItDayButtonVariant.Secondary,
                size = ItDayButtonSize.Medium,
            )
        }
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
    // CarrierComparisonBanner(onClick = { onAction(HomeAction.OpenCarrierComparison) })
    // Spacer(Modifier.height(ItDayDimens.Space24))
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
                    remainingTimeSeconds = uiState.remainingTimeSeconds,
                    onActivate = { onAction(HomeAction.ActivateBarcode) },
                    onUse = { onAction(HomeAction.UseMembership) },
                    onBrandClick = { onAction(HomeAction.SelectPartnerBrand(it)) },
                    onRefresh = { onAction(HomeAction.RefreshBarcode) },
                    onBrandDetailClick = { onAction(HomeAction.OpenBrandDetail) },
                    onAddBrandClick = { onAction(HomeAction.OpenBrandDetail) },
                    onViewMapClick = { onAction(HomeAction.OpenMap) },
                )
            }
        MembershipState.BarcodeEnabled -> {
            val membership = uiState.membership ?: return
            MembershipBarcodeCard(
                membership = membership,
                brands = uiState.partnerBrands,
                barcodeEnabled = true,
                remainingTimeSeconds = uiState.remainingTimeSeconds,
                onActivate = { onAction(HomeAction.ActivateBarcode) },
                onUse = { onAction(HomeAction.UseMembership) },
                onBrandClick = { onAction(HomeAction.SelectPartnerBrand(it)) },
                onRefresh = { onAction(HomeAction.RefreshBarcode) },
                onBrandDetailClick = { onAction(HomeAction.OpenBrandDetail) },
                onAddBrandClick = { onAction(HomeAction.OpenBrandDetail) },
                onViewMapClick = { onAction(HomeAction.OpenMap) },
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
