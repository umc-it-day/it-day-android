package com.example.itday.feature.payment.presentation

import kotlinx.coroutines.delay

interface PaymentGateway {
    suspend fun launch(): PaymentResult
}

sealed interface PaymentResult {
    data object Success : PaymentResult

    data class Failure(
        val reason: String,
    ) : PaymentResult

    data object Cancelled : PaymentResult
}

class DemoPaymentGateway(
    private val responseDelayMillis: Long = DEFAULT_RESPONSE_DELAY_MILLIS,
) : PaymentGateway {
    private var attemptCount = 0

    override suspend fun launch(): PaymentResult {
        delay(responseDelayMillis)
        attemptCount += 1
        return if (attemptCount == 1) {
            PaymentResult.Failure("Demo payment failed on the first attempt")
        } else {
            PaymentResult.Success
        }
    }

    private companion object {
        const val DEFAULT_RESPONSE_DELAY_MILLIS = 1_000L
    }
}
