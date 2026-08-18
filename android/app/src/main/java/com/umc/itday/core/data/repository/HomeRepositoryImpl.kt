package com.umc.itday.core.data.repository

import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayHomeData
import com.umc.itday.core.data.result.apiSuccess as success

class HomeRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : HomeRepository {
    override suspend fun getHomeData(): ApiResult<ItDayHomeData> = success(mockDataSource.getHomeData())
}
