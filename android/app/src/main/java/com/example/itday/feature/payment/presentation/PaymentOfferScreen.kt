@file:Suppress("LongMethod")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
            Spacer(modifier = Modifier.height(51.dp))
            RoyalCharacter()
            Spacer(modifier = Modifier.height(8.dp))
            TrialTitle()
            Text(
                text = stringResource(R.string.payment_trial_subtitle),
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            PlanComparisonTable()
            Spacer(modifier = Modifier.height(22.dp))
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.payment_start_trial),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "\u2192",
                        modifier = Modifier.align(Alignment.CenterEnd),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
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
