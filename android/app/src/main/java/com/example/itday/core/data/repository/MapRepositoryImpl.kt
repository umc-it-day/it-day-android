package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDayMapPlace

class MapRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : MapRepository {
    override suspend fun getMapPlaces(): List<ItDayMapPlace> = mockDataSource.getMapPlaces()
}
