package com.umc.itday.core.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val ITDAY_PREFERENCES_NAME = "itday_preferences"

val Context.itDayPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = ITDAY_PREFERENCES_NAME,
)
