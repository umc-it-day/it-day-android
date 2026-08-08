package com.example.itday.feature.settings.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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
