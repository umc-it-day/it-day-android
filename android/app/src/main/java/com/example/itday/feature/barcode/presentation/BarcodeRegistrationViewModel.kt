package com.example.itday.feature.barcode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.member.domain.repository.MemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BarcodeRegistrationViewModel(
    private val memberRepository: MemberRepository? = null,
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
        _uiState.update { it.copy(barcodeNumber = digitsOnly) }
    }

    fun navigateStep(step: BarcodeStep) {
        _uiState.update { it.copy(step = step) }
    }

    fun submitRegistration() {
        val currentNumber = _uiState.value.barcodeNumber
        if (currentNumber.length < MAX_BARCODE_LENGTH) return

        if (currentNumber == DUPLICATE_TEST_NUMBER) {
            _uiState.update { it.copy(step = BarcodeStep.Duplicate) }
            return
        }

        val repository = memberRepository
        if (repository == null) {
            _uiState.update { it.copy(step = BarcodeStep.Success) }
            return
        }

        viewModelScope.launch {
            when (val result = repository.registerBarcode(currentNumber)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(step = BarcodeStep.Success) }
                }
                is ApiResult.Failure -> {
                    _uiState.update { it.copy(step = BarcodeStep.Duplicate) }
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
        const val DUPLICATE_TEST_NUMBER = "9999999999999999"

        fun factory(memberRepository: MemberRepository? = null): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(BarcodeRegistrationViewModel::class.java))
                    return BarcodeRegistrationViewModel(memberRepository = memberRepository) as T
                }
            }
    }
}

