package com.umc.itday.feature.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.BuildConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val gateway: PaymentGateway,
    private val openingDelayMillis: Long = DEFAULT_OPENING_DELAY_MILLIS,
    allowDemoCompletion: Boolean = BuildConfig.DEBUG,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            PaymentUiState(allowDemoCompletion = allowDemoCompletion),
        )
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var paymentJob: Job? = null
    private var confettiEventCounter = 0L

    fun startTrial() {
        startOpening()
    }

    fun openDirectly() {
        if (_uiState.value.step != PaymentStep.Opening) return
        launchGateway()
    }

    fun retry() {
        if (_uiState.value.step != PaymentStep.Failed) return
        startOpening()
    }

    fun returnToOffer() {
        paymentJob?.cancel()
        paymentJob = null
        _uiState.value = _uiState.value.copy(step = PaymentStep.Offer)
    }

    fun completeDemo() {
        if (!_uiState.value.allowDemoCompletion) return
        enterComplete()
    }

    fun consumeConfettiEvent() {
        if (_uiState.value.confettiEventId == null) return
        _uiState.value = _uiState.value.copy(confettiEventId = null)
    }

    private fun startOpening() {
        if (_uiState.value.step == PaymentStep.Opening ||
            _uiState.value.step == PaymentStep.Pending
        ) {
            return
        }
        paymentJob?.cancel()
        _uiState.value = _uiState.value.copy(step = PaymentStep.Opening)
        paymentJob =
            viewModelScope.launch {
                delay(openingDelayMillis)
                performGatewayLaunch()
            }
    }

    private fun launchGateway() {
        paymentJob?.cancel()
        paymentJob =
            viewModelScope.launch {
                performGatewayLaunch()
            }
    }

    private suspend fun performGatewayLaunch() {
        if (_uiState.value.step != PaymentStep.Opening) return
        _uiState.value = _uiState.value.copy(step = PaymentStep.Pending)
        when (gateway.launch()) {
            PaymentResult.Success -> enterComplete()
            is PaymentResult.Failure -> {
                _uiState.value = _uiState.value.copy(step = PaymentStep.Failed)
            }
            PaymentResult.Cancelled -> returnToOffer()
        }
        paymentJob = null
    }

    private fun enterComplete() {
        paymentJob?.cancel()
        paymentJob = null
        confettiEventCounter += 1
        _uiState.value =
            _uiState.value.copy(
                step = PaymentStep.Complete,
                confettiEventId = confettiEventCounter,
            )
    }

    companion object {
        private const val DEFAULT_OPENING_DELAY_MILLIS = 1_000L

        fun factory(gateway: PaymentGateway = DemoPaymentGateway()): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(PaymentViewModel::class.java))
                    return PaymentViewModel(gateway = gateway) as T
                }
            }
    }
}
