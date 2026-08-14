package com.umc.itday.feature.payment.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.umc.itday.ui.theme.ItDayTheme

@Composable
fun PaymentFlowScreen(
    viewModel: PaymentViewModel,
    onExit: () -> Unit,
    onHome: () -> Unit,
    onChallenges: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val onBack = {
        when (uiState.step) {
            PaymentStep.Offer -> onExit()
            PaymentStep.Complete -> onHome()
            else -> viewModel.returnToOffer()
        }
    }

    BackHandler(onBack = onBack)

    PaymentFlowContent(
        uiState = uiState,
        onBack = onBack,
        onStartTrial = viewModel::startTrial,
        onOpenDirectly = viewModel::openDirectly,
        onRetry = viewModel::retry,
        onDemoComplete = viewModel::completeDemo,
        onConfettiShown = viewModel::consumeConfettiEvent,
        onHome = onHome,
        onChallenges = onChallenges,
    )
}

@Composable
private fun PaymentFlowContent(
    uiState: PaymentUiState,
    onBack: () -> Unit,
    onStartTrial: () -> Unit,
    onOpenDirectly: () -> Unit,
    onRetry: () -> Unit,
    onDemoComplete: () -> Unit,
    onConfettiShown: () -> Unit,
    onHome: () -> Unit,
    onChallenges: () -> Unit,
) {
    when (uiState.step) {
        PaymentStep.Offer ->
            PaymentOfferScreen(
                onBack = onBack,
                onStartTrial = onStartTrial,
            )
        PaymentStep.Opening ->
            PaymentOpeningScreen(
                onBack = onBack,
                onOpenDirectly = onOpenDirectly,
            )
        PaymentStep.Pending -> PaymentPendingScreen(onBack = onBack)
        PaymentStep.Failed ->
            PaymentFailureScreen(
                showDemoCompletion = uiState.allowDemoCompletion,
                onBack = onBack,
                onRetry = onRetry,
                onDemoComplete = onDemoComplete,
            )
        PaymentStep.Complete ->
            PaymentCompleteScreen(
                uiState = uiState,
                onBack = onBack,
                onConfettiShown = onConfettiShown,
                onHome = onHome,
                onChallenges = onChallenges,
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentFlowPreview() {
    ItDayTheme {
        PaymentFlowContent(
            uiState = PaymentUiState(),
            onBack = {},
            onStartTrial = {},
            onOpenDirectly = {},
            onRetry = {},
            onDemoComplete = {},
            onConfettiShown = {},
            onHome = {},
            onChallenges = {},
        )
    }
}
