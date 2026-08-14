package com.umc.itday.feature.settings.presentation

sealed interface SettingsUiEvent {
    data class OpenExternalUrl(val url: String) : SettingsUiEvent
    data class ShowMessage(val message: String) : SettingsUiEvent
    data object UserWithdrawn : SettingsUiEvent
}
