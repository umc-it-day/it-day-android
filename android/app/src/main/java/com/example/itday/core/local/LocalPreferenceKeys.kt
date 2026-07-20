package com.example.itday.core.local

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object LocalPreferenceKeys {
    val IsOnboardingCompleted = booleanPreferencesKey("is_onboarding_completed")
    val IsGuestMode = booleanPreferencesKey("is_guest_mode")
}
