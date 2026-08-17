package com.umc.itday.feature.barcode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.feature.barcode.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BarcodeRegistrationViewModel(
    private val repository: BarcodeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BarcodeRegistrationUiState())
    val uiState: StateFlow<BarcodeRegistrationUiState> = _uiState.asStateFlow()

    fun selectCarrier(carrier: Carrier) {
        _uiState.update { it.copy(selectedCarrier = carrier) }
    }

    fun selectGrade(grade: MembershipGrade) {
        _uiState.update { it.copy(selectedGrade = grade) }
    }

    fun onBarcodeNumberChange(input: String) {
        val digitsOnly = input.filter { it.isDigit() }.take(MAX_BARCODE_LENGTH)
        _uiState.update { it.copy(barcodeNumber = digitsOnly, errorMessage = null) }
    }

    fun navigateStep(step: BarcodeStep) {
        _uiState.update { it.copy(step = step) }
    }

    fun submitRegistration() {
        val currentNumber = _uiState.value.barcodeNumber
        if (currentNumber.length < MAX_BARCODE_LENGTH || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.registerBarcode(currentNumber)) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(step = BarcodeStep.Success, isLoading = false) }
                is ApiResult.Failure ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage(),
                        )
                    }
            }
        }
    }

    fun resetFormToReentry() {
        _uiState.update {
            it.copy(
                step = BarcodeStep.Form,
                barcodeNumber = "",
            )
        }
    }

    companion object {
        const val MAX_BARCODE_LENGTH = 16
    }

    class Factory(
        private val repository: BarcodeRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BarcodeRegistrationViewModel(repository) as T
    }
}
