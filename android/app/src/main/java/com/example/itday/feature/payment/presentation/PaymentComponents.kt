@file:Suppress("MagicNumber")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.itday.R

internal val PaymentGradient =
    Brush.linearGradient(
        listOf(
            Color(0xFF392073),
            Color(0xFF0B2B73),
            Color(0xFF0C0F4A),
            Color(0xFF143A68),
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
                .size(52.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics {
                    contentDescription = "\uB4A4\uB85C\uAC00\uAE30"
                },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val strokeWidth = 2.dp.toPx()
            drawLine(
                color = color,
                start = Offset(size.width * 0.62f, size.height * 0.18f),
                end = Offset(size.width * 0.32f, size.height * 0.5f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(size.width * 0.32f, size.height * 0.5f),
                end = Offset(size.width * 0.62f, size.height * 0.82f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
internal fun ProviderTopBar(
    onBack: () -> Unit,
    backgroundColor: Color = Color.Black,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
    ) {
        PaymentBackButton(
            onClick = onBack,
            color = Color(0xFFB7BBC0),
            modifier = Modifier.align(Alignment.CenterStart),
        )
        Text(
            text = stringResource(R.string.payment_provider_title),
            modifier = Modifier.align(Alignment.Center),
            color = if (backgroundColor == Color.Black) Color.White else Color.Black,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun PaymentFailureMark() {
    Canvas(modifier = Modifier.size(40.dp)) {
        val strokeWidth = 2.dp.toPx()
        drawLine(
            color = Color.White,
            start = Offset(size.width * 0.15f, size.height * 0.15f),
            end = Offset(size.width * 0.85f, size.height * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start = Offset(size.width * 0.85f, size.height * 0.15f),
            end = Offset(size.width * 0.15f, size.height * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
