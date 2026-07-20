package com.example.itday.core.local

import kotlinx.coroutines.flow.Flow

interface LocalPreferencesDataSource {
    val isOnboardingCompleted: Flow<Boolean>
    val isGuestMode: Flow<Boolean>

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setGuestMode(enabled: Boolean)

    suspend fun clearUserSessionPreferences()
}
