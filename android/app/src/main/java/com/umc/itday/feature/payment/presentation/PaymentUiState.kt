package com.umc.itday.feature.payment.presentation

enum class PaymentStep {
    Offer,
    Opening,
    Pending,
    Failed,
    Complete,
}

data class PaymentUiState(
    val step: PaymentStep = PaymentStep.Offer,
    val planName: String = "\uC787\uB370\uC774 PRO",
    val amount: String = "2,900\uC6D0",
    val nextPaymentDate: String = "2026.08.04",
    val allowDemoCompletion: Boolean = false,
    val confettiEventId: Long? = null,
)
