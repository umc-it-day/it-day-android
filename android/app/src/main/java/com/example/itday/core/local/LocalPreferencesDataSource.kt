package com.example.itday.core.local

import kotlinx.coroutines.flow.Flow

interface LocalPreferencesDataSource {
    val isLoggedIn: Flow<Boolean>
    val isOnboardingCompleted: Flow<Boolean>
    val isGuestMode: Flow<Boolean>

    suspend fun setLoggedIn(loggedIn: Boolean)

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setGuestMode(enabled: Boolean)

    suspend fun clearUserSessionPreferences()
}
