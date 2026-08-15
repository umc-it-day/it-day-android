package com.example.itday.feature.map.domain.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.map.presentation.MapStoreUiModel

interface MapRepository {
    suspend fun searchPlaces(query: String): ApiResult<List<MapStoreUiModel>>
    suspend fun getNearbyStores(latitude: Double, longitude: Double): ApiResult<List<MapStoreUiModel>>
    suspend fun getStoreDetail(storeId: Long): ApiResult<MapStoreUiModel>
}
