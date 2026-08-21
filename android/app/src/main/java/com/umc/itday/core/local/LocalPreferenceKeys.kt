package com.umc.itday.core.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

internal object LocalPreferenceKeys {
    val IsLoggedIn = booleanPreferencesKey("is_logged_in")
    val IsOnboardingCompleted = booleanPreferencesKey("is_onboarding_completed")
    val IsGuestMode = booleanPreferencesKey("is_guest_mode")
    val PreferredBrandNames = stringSetPreferencesKey("preferred_brand_names")
    val LastAttendanceDate = androidx.datastore.preferences.core.stringPreferencesKey("last_attendance_date")
}
