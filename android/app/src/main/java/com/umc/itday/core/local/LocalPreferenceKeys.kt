package com.umc.itday.core.local

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object LocalPreferenceKeys {
    val IsLoggedIn = booleanPreferencesKey("is_logged_in")
    val IsOnboardingCompleted = booleanPreferencesKey("is_onboarding_completed")
    val IsGuestMode = booleanPreferencesKey("is_guest_mode")
}
