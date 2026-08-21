package com.umc.itday.feature.map.domain.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.feature.map.presentation.MapCoordinate
import com.umc.itday.feature.map.presentation.MapStoreUiModel

interface MapRepository {
    suspend fun searchPlaces(query: String): ApiResult<List<MapStoreUiModel>>
    suspend fun getNearbyStores(latitude: Double, longitude: Double): ApiResult<List<MapStoreUiModel>>
    suspend fun getStoreDetail(storeId: Long): ApiResult<MapStoreUiModel>
    suspend fun getDirections(
        startLat: Double,
        startLng: Double,
        destLat: Double,
        destLng: Double,
    ): ApiResult<List<MapCoordinate>>
}
