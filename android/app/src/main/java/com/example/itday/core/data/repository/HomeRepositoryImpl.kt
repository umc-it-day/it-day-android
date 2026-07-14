package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDayHomeData

class HomeRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : HomeRepository {
    override suspend fun getHomeData(): ItDayHomeData = mockDataSource.getHomeData()
}
