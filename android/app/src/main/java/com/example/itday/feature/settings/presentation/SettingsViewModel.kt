package com.example.itday.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.toUserMessage
import com.example.itday.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<SettingsUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun navigateToScreen(screen: SettingsScreenType) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun togglePromotionNotification(enabled: Boolean) {
        _uiState.update { it.copy(promotionNotification = enabled) }
    }

    fun toggleCharacterNotification(enabled: Boolean) {
        _uiState.update { it.copy(characterNotification = enabled) }
    }

    fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun openPrivacyPolicy() {
        viewModelScope.launch {
            _events.send(SettingsUiEvent.OpenExternalUrl("https://example.com/privacy"))
        }
    }

    fun openTermsOfService() {
        viewModelScope.launch {
            _events.send(SettingsUiEvent.OpenExternalUrl("https://example.com/terms"))
        }
    }

    fun withdraw() {
        if (_uiState.value.isWithdrawing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isWithdrawing = true, errorMessage = null) }
            when (val result = authRepository.withdraw()) {
                is ApiResult.Success -> {
                    _events.send(SettingsUiEvent.UserWithdrawn)
                }
                is ApiResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isWithdrawing = false,
                            errorMessage = result.error.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    fun toggleFaqItem(id: Int) {
        _uiState.update { state ->
            val updatedList =
                state.faqList.map { item ->
                    if (item.id == id) {
                        item.copy(isExpanded = !item.isExpanded)
                    } else {
                        item
                    }
                }
            state.copy(faqList = updatedList)
        }
    }

    class Factory(
        private val authRepository: AuthRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(authRepository) as T
    }
}
