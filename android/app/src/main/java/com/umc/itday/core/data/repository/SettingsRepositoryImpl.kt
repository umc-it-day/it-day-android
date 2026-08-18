package com.umc.itday.core.data.repository

import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.result.apiSuccess as success

class SettingsRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : SettingsRepository {
    override suspend fun getSettingItems(): SettingsItemsResult = success(mockDataSource.getSettingItems())
}
