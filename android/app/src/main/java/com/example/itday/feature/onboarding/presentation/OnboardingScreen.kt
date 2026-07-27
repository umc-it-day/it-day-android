package com.example.itday.feature.onboarding.presentation

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

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onAgreementChange: (AgreementType, Boolean) -> Unit,
    onClose: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        ItDayFlowTopBar(
            onBackClick = onClose,
            navigationText = "×",
        )
        Column(
            modifier =
                Modifier
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
            onAgreementChange = { _, _ -> },
            onClose = {},
            onNext = {},
        )
    }
}
