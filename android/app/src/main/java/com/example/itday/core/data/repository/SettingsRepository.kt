package com.example.itday.core.data.repository

import com.example.itday.core.model.ItDaySettingItem

interface SettingsRepository {
    suspend fun getSettingItems(): List<ItDaySettingItem>
}
