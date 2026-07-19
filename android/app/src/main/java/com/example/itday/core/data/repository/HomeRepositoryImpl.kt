package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.model.ItDayHomeData
import com.example.itday.core.data.result.apiSuccess as success

class HomeRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : HomeRepository {
    override suspend fun getHomeData(): ApiResult<ItDayHomeData> = success(mockDataSource.getHomeData())
}
