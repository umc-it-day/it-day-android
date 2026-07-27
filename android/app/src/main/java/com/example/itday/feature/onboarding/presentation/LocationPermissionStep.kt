package com.example.itday.feature.onboarding.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.R
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayTheme

@Composable
fun LocationPermissionStep(
    showError: Boolean,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_onboarding_location),
            contentDescription = null,
            modifier = Modifier.size(ItDayDimens.ButtonHeight),
        )
        Text(
            text = stringResource(R.string.onboarding_location_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = ItDayDimens.Space16),
        )
        Text(
            text =
                buildAnnotatedString {
                    append(stringResource(R.string.onboarding_location_description_prefix))
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(stringResource(R.string.onboarding_location_always_allow))
                    }
                    append(stringResource(R.string.onboarding_location_description_suffix))
                },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = ItDayDimens.Space16),
        )
        if (showError) {
            Text(
                text = stringResource(R.string.onboarding_location_error),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = ItDayDimens.Space8),
            )
        }
        ItDayButton(
            text = stringResource(R.string.onboarding_location_settings),
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space24),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationPermissionStepPreview() =
    ItDayTheme {
        LocationPermissionStep(showError = false, onSettingsClick = {})
    }
