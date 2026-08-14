package com.umc.itday.feature.payment.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsOffer() {
        val viewModel = createViewModel()

        assertEquals(PaymentStep.Offer, viewModel.uiState.value.step)
        assertNull(viewModel.uiState.value.confettiEventId)
    }

    @Test
    fun firstAttemptMovesThroughOpeningAndPendingToFailure() =
        runTest(dispatcher.scheduler) {
            val viewModel = createViewModel()

            viewModel.startTrial()
            assertEquals(PaymentStep.Opening, viewModel.uiState.value.step)

            advanceUntilIdle()

            assertEquals(PaymentStep.Failed, viewModel.uiState.value.step)
        }

    @Test
    fun retrySucceedsAndEmitsOneConfettiEvent() =
        runTest(dispatcher.scheduler) {
            val viewModel = createViewModel()
            viewModel.startTrial()
            advanceUntilIdle()

            viewModel.retry()
            advanceUntilIdle()

            assertEquals(PaymentStep.Complete, viewModel.uiState.value.step)
            assertEquals(1L, viewModel.uiState.value.confettiEventId)

            viewModel.consumeConfettiEvent()
            assertNull(viewModel.uiState.value.confettiEventId)
        }

    @Test
    fun debugCompletionMovesFailureToComplete() =
        runTest(dispatcher.scheduler) {
            val viewModel = createViewModel(allowDemoCompletion = true)
            viewModel.startTrial()
            advanceUntilIdle()

            viewModel.completeDemo()

            assertEquals(PaymentStep.Complete, viewModel.uiState.value.step)
        }

    @Test
    fun returningToOfferCancelsPendingGateway() =
        runTest(dispatcher.scheduler) {
            var cancelled = false
            val gateway =
                object : PaymentGateway {
                    override suspend fun launch(): PaymentResult =
                        try {
                            awaitCancellation()
                        } finally {
                            cancelled = true
                        }
                }
            val viewModel =
                PaymentViewModel(
                    gateway = gateway,
                    openingDelayMillis = 0L,
                    allowDemoCompletion = true,
                )

            viewModel.startTrial()
            runCurrent()
            assertEquals(PaymentStep.Pending, viewModel.uiState.value.step)

            viewModel.returnToOffer()
            runCurrent()

            assertEquals(PaymentStep.Offer, viewModel.uiState.value.step)
            assertTrue(cancelled)
        }

    private fun createViewModel(allowDemoCompletion: Boolean = true): PaymentViewModel =
        PaymentViewModel(
            gateway = DemoPaymentGateway(responseDelayMillis = 0L),
            openingDelayMillis = 0L,
            allowDemoCompletion = allowDemoCompletion,
        )
}
