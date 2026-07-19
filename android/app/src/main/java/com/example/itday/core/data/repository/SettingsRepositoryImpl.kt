package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.apiSuccess as success

class SettingsRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : SettingsRepository {
    override suspend fun getSettingItems(): SettingsItemsResult = success(mockDataSource.getSettingItems())
}
