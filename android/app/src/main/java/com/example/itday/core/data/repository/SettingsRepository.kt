package com.example.itday.core.data.repository

interface SettingsRepository {
    suspend fun getSettingItems(): SettingsItemsResult
}
