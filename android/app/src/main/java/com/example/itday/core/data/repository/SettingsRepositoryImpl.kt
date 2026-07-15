package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDaySettingItem

class SettingsRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : SettingsRepository {
    override suspend fun getSettingItems(): List<ItDaySettingItem> = mockDataSource.getSettingItems()
}
