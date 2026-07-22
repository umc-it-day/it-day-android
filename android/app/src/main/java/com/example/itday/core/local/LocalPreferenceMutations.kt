package com.example.itday.core.local

import androidx.datastore.preferences.core.MutablePreferences

internal fun MutablePreferences.setOnboardingCompletedPreference(completed: Boolean) {
    this[LocalPreferenceKeys.IsOnboardingCompleted] = completed
}

internal fun MutablePreferences.setGuestModePreference(enabled: Boolean) {
    this[LocalPreferenceKeys.IsGuestMode] = enabled
}

internal fun MutablePreferences.clearUserSessionPreferences() {
    this[LocalPreferenceKeys.IsGuestMode] = false
}
