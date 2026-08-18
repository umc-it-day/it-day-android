package com.umc.itday.feature.barcode.data.api

import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.barcode.data.model.BarcodeDataDto
import com.umc.itday.feature.barcode.data.model.BarcodeNumberRequestDto
import com.umc.itday.feature.barcode.data.model.BarcodeUsageRequestDto
import com.umc.itday.feature.barcode.data.model.LotteryDataDto
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface BarcodeApi {
    @GET("api/members/me/barcode")
    suspend fun getBarcode(): ApiResponseDto<BarcodeDataDto>

    @POST("api/members/me/barcode")
    suspend fun registerBarcode(
        @Body request: BarcodeNumberRequestDto,
    ): ApiResponseDto<JsonElement>

    @PUT("api/members/me/barcode")
    suspend fun updateBarcode(
        @Body request: BarcodeNumberRequestDto,
    ): ApiResponseDto<JsonElement>

    @POST("api/members/me/barcode/usage")
    suspend fun recordUsage(
        @Body request: BarcodeUsageRequestDto,
    ): ApiResponseDto<JsonElement>

    @GET("api/members/me/lottery")
    suspend fun getLotteryNumber(): ApiResponseDto<LotteryDataDto>
}
