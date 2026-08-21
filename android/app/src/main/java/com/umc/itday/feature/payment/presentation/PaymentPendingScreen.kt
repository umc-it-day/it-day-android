@file:Suppress("MagicNumber")

package com.umc.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.itday.ui.component.ItDayLoadingOverlay
import com.umc.itday.R
import com.umc.itday.ui.theme.ItDayTheme

@Composable
fun PaymentPendingScreen(onBack: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding(),
    ) {
        PaymentBackButton(
            onClick = onBack,
            color = Color(0xFFB8B8B8),
            modifier = Modifier.align(Alignment.TopStart),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ItDayLoadingOverlay(
                message = stringResource(R.string.payment_pending_description)
            )
        }
    }
}

@Preview
@Composable
private fun PaymentPendingPreview() {
    ItDayTheme {
        PaymentPendingScreen(onBack = {})
    }
}
