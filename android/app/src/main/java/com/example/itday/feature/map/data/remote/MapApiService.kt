package com.example.itday.feature.map.data.remote

import com.example.itday.feature.auth.data.model.ApiResponseDto
import com.example.itday.feature.map.data.model.MapSearchResponseDto
import com.example.itday.feature.map.data.model.StoreDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapApiService {
    @GET("/api/maps/search")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): ApiResponseDto<MapSearchResponseDto>

    @GET("/api/maps/stores/{storeId}")
    suspend fun getStoreDetail(
        @Path("storeId") storeId: Long,
    ): ApiResponseDto<StoreDetailDto>

    @GET("/api/maps/nearby")
    suspend fun getNearbyStores(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("radius") radius: Int? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): ApiResponseDto<MapSearchResponseDto>
}
