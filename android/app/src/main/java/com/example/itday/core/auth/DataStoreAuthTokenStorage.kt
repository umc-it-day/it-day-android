package com.example.itday.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreAuthTokenStorage(
    private val dataStore: DataStore<Preferences>,
) : AuthTokenStorage {
    override val tokens: Flow<AuthTokens?> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }.map { preferences -> preferences.authTokens() }

    override suspend fun getTokens(): AuthTokens? = tokens.first()

    override suspend fun saveTokens(tokens: AuthTokens) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = tokens.accessToken
            preferences[REFRESH_TOKEN] = tokens.refreshToken
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    private fun Preferences.authTokens(): AuthTokens? {
        val accessToken = this[ACCESS_TOKEN]?.takeIf(String::isNotBlank)
        val refreshToken = this[REFRESH_TOKEN]?.takeIf(String::isNotBlank)
        return if (accessToken != null && refreshToken != null) {
            AuthTokens(accessToken, refreshToken)
        } else {
            null
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("itday_access_token")
        val REFRESH_TOKEN = stringPreferencesKey("itday_refresh_token")
    }
}
