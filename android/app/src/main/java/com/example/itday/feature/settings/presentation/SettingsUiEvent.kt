package com.example.itday.feature.settings.presentation

sealed interface SettingsUiEvent {
    data class OpenExternalUrl(val url: String) : SettingsUiEvent
    data object UserWithdrawn : SettingsUiEvent
}
