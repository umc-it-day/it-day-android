@file:Suppress("MagicNumber")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itday.R

internal val PaymentGradient =
    Brush.linearGradient(
        listOf(
            Color(0xFF32106D),
            Color(0xFF062D73),
            Color(0xFF071344),
        ),
    )

@Composable
internal fun PaymentBackButton(
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .width(52.dp)
                .height(52.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics {
                    contentDescription = "\uB4A4\uB85C\uAC00\uAE30"
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "\u2039",
            color = color,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Light,
        )
    }
}

@Composable
internal fun ProviderTopBar(
    onBack: () -> Unit,
    backgroundColor: Color = Color.Black,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PaymentBackButton(onClick = onBack, color = Color(0xFFB7BBC0))
        Spacer(modifier = Modifier.width(80.dp))
        Text(
            text =
                androidx.compose.ui.res
                    .stringResource(R.string.payment_provider_title),
            color = if (backgroundColor == Color.Black) Color.White else Color.Black,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun BenefitIcon(
    symbol: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(tint.copy(alpha = 0.18f), CircleShape)
                .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = symbol, style = MaterialTheme.typography.titleLarge)
    }
}
