package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDayBarcodeData

class BarcodeRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : BarcodeRepository {
    override suspend fun getBarcodeData(): ItDayBarcodeData = mockDataSource.getBarcodeData()
}
