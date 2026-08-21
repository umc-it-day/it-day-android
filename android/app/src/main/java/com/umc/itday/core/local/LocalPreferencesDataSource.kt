package com.umc.itday.core.local

import kotlinx.coroutines.flow.Flow

interface LocalPreferencesDataSource {
    val isLoggedIn: Flow<Boolean>
    val isOnboardingCompleted: Flow<Boolean>
    val isGuestMode: Flow<Boolean>
    val preferredBrandNames: Flow<Set<String>>
    val lastAttendanceDate: Flow<String>

    suspend fun setLoggedIn(loggedIn: Boolean)

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setGuestMode(enabled: Boolean)

    suspend fun setPreferredBrandNames(brands: Set<String>)

    suspend fun addPreferredBrandName(brandName: String)

    suspend fun setLastAttendanceDate(date: String)

    suspend fun clearUserSessionPreferences()
}
