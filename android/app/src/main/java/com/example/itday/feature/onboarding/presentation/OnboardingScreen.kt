package com.example.itday.feature.onboarding.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.R
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayFlowTopBar
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    events: Flow<OnboardingUiEvent>,
    onAgreementChange: (AgreementType, Boolean) -> Unit,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onLocationResult: (Boolean) -> Unit,
    onCarrierSelect: (String) -> Unit,
    onMembershipSelect: (String) -> Unit,
    onBrandToggle: (String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            onLocationResult(it)
        }
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                OnboardingUiEvent.RequestLocationPermission ->
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                OnboardingUiEvent.Complete -> onComplete()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        ItDayFlowTopBar(
            onBackClick = if (state.step == TERMS_STEP) onClose else onBack,
            navigationText = if (state.step == TERMS_STEP) "×" else "<",
        )
        if (state.step == TERMS_STEP) {
            TermsContent(
                state = state,
                onAgreementChange = onAgreementChange,
                onNext = onNext,
                modifier = Modifier.weight(1f),
            )
        } else if (state.step == LOCATION_STEP) {
            LocationPermissionStep(
                showError = state.locationError,
                onSettingsClick = onNext,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = ItDayDimens.ScreenHorizontalPadding),
            )
        } else {
            SelectionSteps(
                state = state,
                onCarrierSelect = onCarrierSelect,
                onMembershipSelect = onMembershipSelect,
                onBrandToggle = onBrandToggle,
                onNext = onNext,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TermsContent(
    state: OnboardingUiState,
    onAgreementChange: (AgreementType, Boolean) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = ItDayDimens.ScreenHorizontalPadding),
    ) {
            Text(
                text = stringResource(R.string.onboarding_terms_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.onboarding_terms_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = ItDayDimens.Space8),
            )
            AgreementRow(
                title = stringResource(R.string.onboarding_location_terms),
                description = stringResource(R.string.onboarding_location_terms_description),
                checked = state.locationAgreed,
                onCheckedChange = { onAgreementChange(AgreementType.Location, it) },
            )
            AgreementRow(
                title = stringResource(R.string.onboarding_privacy_terms),
                description = stringResource(R.string.onboarding_privacy_terms_description),
                checked = state.privacyAgreed,
                onCheckedChange = { onAgreementChange(AgreementType.Privacy, it) },
            )
            AgreementRow(
                title = stringResource(R.string.onboarding_notification_terms),
                description = stringResource(R.string.onboarding_notification_terms_description),
                checked = state.notificationAgreed,
                onCheckedChange = { onAgreementChange(AgreementType.Notification, it) },
            )
            Spacer(modifier = Modifier.weight(1f))
            ItDayButton(
                text = stringResource(R.string.onboarding_next),
                enabled = state.canContinue,
                onClick = onNext,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = ItDayDimens.Space24),
            )
    }
}

@Composable
private fun AgreementRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Column(modifier = Modifier.padding(top = ItDayDimens.Space12)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    ItDayTheme {
        OnboardingScreen(
            state = OnboardingUiState(locationAgreed = true),
            events = kotlinx.coroutines.flow.emptyFlow(),
            onAgreementChange = { _, _ -> },
            onClose = {},
            onBack = {},
            onNext = {},
            onLocationResult = {},
            onCarrierSelect = {},
            onMembershipSelect = {},
            onBrandToggle = {},
            onComplete = {},
        )
    }
}

private const val TERMS_STEP = 0
private const val LOCATION_STEP = 1
