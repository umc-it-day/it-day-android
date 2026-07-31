@file:Suppress("LongMethod")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.ui.theme.ItDayTheme

@Composable
fun PaymentOfferScreen(
    onBack: () -> Unit,
    onStartTrial: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(PaymentGradient)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        PaymentBackButton(
            onClick = onBack,
            color = Color.White,
            modifier = Modifier.padding(start = 2.dp),
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            RoyalCharacterPlaceholder()
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "\uD83C\uDF3F  " + stringResource(R.string.payment_trial_title) + "  \uD83C\uDF3F",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.payment_trial_subtitle),
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(38.dp))
            PlanHeader()
            BenefitComparisonRow(
                title = stringResource(R.string.payment_benefit_ad_free),
                description = stringResource(R.string.payment_benefit_ad_free_description),
            )
            BenefitComparisonRow(
                title = stringResource(R.string.payment_benefit_challenge),
                description = stringResource(R.string.payment_benefit_challenge_description),
            )
            BenefitComparisonRow(
                title = stringResource(R.string.payment_benefit_report),
                description = stringResource(R.string.payment_benefit_report_description),
                showDivider = false,
            )
            Spacer(modifier = Modifier.height(26.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(R.string.payment_monthly_price),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "  " + stringResource(R.string.payment_monthly_suffix),
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.22f))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.payment_trial_notice),
                color = Color.White.copy(alpha = 0.58f),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onStartTrial,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                shape = RoundedCornerShape(30.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.payment_start_trial),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "\u2192", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentOfferPreview() {
    ItDayTheme {
        PaymentOfferScreen(onBack = {}, onStartTrial = {})
    }
}
