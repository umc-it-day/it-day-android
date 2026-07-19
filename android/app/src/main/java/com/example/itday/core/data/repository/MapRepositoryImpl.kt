package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.apiSuccess as success

class MapRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : MapRepository {
    override suspend fun getMapPlaces(): MapPlacesResult = success(mockDataSource.getMapPlaces())
}
