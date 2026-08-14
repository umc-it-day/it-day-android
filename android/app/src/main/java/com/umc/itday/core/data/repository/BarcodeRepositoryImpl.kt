package com.umc.itday.core.data.repository

import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayBarcodeData
import com.umc.itday.core.data.result.apiSuccess as success

class BarcodeRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : BarcodeRepository {
    override suspend fun getBarcodeData(): ApiResult<ItDayBarcodeData> = success(mockDataSource.getBarcodeData())
}
