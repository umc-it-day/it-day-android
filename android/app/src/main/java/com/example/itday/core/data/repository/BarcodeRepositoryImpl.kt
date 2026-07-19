package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.model.ItDayBarcodeData
import com.example.itday.core.data.result.apiSuccess as success

class BarcodeRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : BarcodeRepository {
    override suspend fun getBarcodeData(): ApiResult<ItDayBarcodeData> = success(mockDataSource.getBarcodeData())
}
