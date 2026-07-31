@file:Suppress("MagicNumber")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
internal fun RoyalCharacterPlaceholder() {
    Box(
        modifier =
            Modifier
                .size(88.dp)
                .background(Color(0xFFFFD681), RoundedCornerShape(44.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "\uD83D\uDC51", style = MaterialTheme.typography.displayMedium)
    }
}

@Composable
internal fun PlanHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.payment_free),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(modifier = Modifier.size(26.dp))
        Box(
            modifier =
                Modifier
                    .background(
                        Color(0xFF536FA5),
                        RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp),
                    ).padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = "\u2726 " + stringResource(R.string.payment_pro),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun BenefitComparisonRow(
    title: String,
    description: String,
    showDivider: Boolean = true,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(text = "\u2014", color = Color(0xFF8666A7), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(58.dp))
        Text(text = "\u2713", color = Color.White, style = MaterialTheme.typography.headlineSmall)
    }
    if (showDivider) {
        HorizontalDivider(color = Color.White.copy(alpha = 0.42f))
    }
}
