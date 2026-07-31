@file:Suppress("MagicNumber")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itday.R

@Composable
internal fun PaymentDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.White,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun BenefitCard() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF49699D).copy(alpha = 0.65f), RoundedCornerShape(24.dp))
                .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.payment_active_benefits),
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        CompleteBenefitRow(
            symbol = "\uD83D\uDEE1",
            title = stringResource(R.string.payment_benefit_ad_free),
            description = stringResource(R.string.payment_benefit_ad_free_description),
        )
        HorizontalDivider(color = Color.White.copy(alpha = 0.65f))
        CompleteBenefitRow(
            symbol = "\uD83C\uDFC6",
            title = stringResource(R.string.payment_benefit_challenge),
            description = stringResource(R.string.payment_benefit_challenge_description),
        )
        HorizontalDivider(color = Color.White.copy(alpha = 0.65f))
        CompleteBenefitRow(
            symbol = "\uD83D\uDCCA",
            title = stringResource(R.string.payment_benefit_report_complete),
            description = stringResource(R.string.payment_benefit_report_description),
        )
    }
}

@Composable
private fun CompleteBenefitRow(
    symbol: String,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BenefitIcon(symbol = symbol, tint = Color(0xFF8CC4FF), modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = description,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(text = "\u25CF", color = Color.White)
    }
}
