package com.example.itday.feature.payment.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.theme.ItDayTheme

@Preview
@Composable
private fun PaymentOpeningPreview() {
    ItDayTheme {
        PaymentOpeningScreen(onBack = {}, onOpenDirectly = {})
    }
}

@Preview
@Composable
private fun PaymentFailurePreview() {
    ItDayTheme {
        PaymentFailureScreen(
            showDemoCompletion = true,
            onBack = {},
            onRetry = {},
            onDemoComplete = {},
        )
    }
}
