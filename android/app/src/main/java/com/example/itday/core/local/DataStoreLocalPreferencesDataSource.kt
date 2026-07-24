package com.example.itday.core.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreLocalPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) : LocalPreferencesDataSource {
    override val isLoggedIn: Flow<Boolean> =
        dataStore.booleanValue(LocalPreferenceKeys.IsLoggedIn, defaultValue = false)

    override val isOnboardingCompleted: Flow<Boolean> =
        dataStore.booleanValue(LocalPreferenceKeys.IsOnboardingCompleted, defaultValue = false)

    override val isGuestMode: Flow<Boolean> =
        dataStore.booleanValue(LocalPreferenceKeys.IsGuestMode, defaultValue = false)

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences.setLoggedInPreference(loggedIn)
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences.setOnboardingCompletedPreference(completed)
        }
    }

    override suspend fun setGuestMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences.setGuestModePreference(enabled)
        }
    }

    override suspend fun clearUserSessionPreferences() {
        dataStore.edit { preferences ->
            preferences.clearUserSessionPreferences()
        }
    }

    private fun DataStore<Preferences>.booleanValue(
        key: Preferences.Key<Boolean>,
        defaultValue: Boolean,
    ): Flow<Boolean> =
        data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences -> preferences[key] ?: defaultValue }
}
