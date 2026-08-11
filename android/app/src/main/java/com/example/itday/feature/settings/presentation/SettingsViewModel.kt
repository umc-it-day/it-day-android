package com.example.itday.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
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
        viewModelScope.launch {
            // 실제 탈퇴 API 호출 로직이 들어갈 자리
            _events.send(SettingsUiEvent.UserWithdrawn)
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
}
