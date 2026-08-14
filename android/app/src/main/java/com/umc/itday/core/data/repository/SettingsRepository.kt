package com.umc.itday.core.data.repository

interface SettingsRepository {
    suspend fun getSettingItems(): SettingsItemsResult
}
