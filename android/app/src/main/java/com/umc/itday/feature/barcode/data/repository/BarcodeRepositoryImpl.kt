package com.umc.itday.feature.barcode.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.network.safeApiCall
import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.barcode.data.api.BarcodeApi
import com.umc.itday.feature.barcode.data.model.BarcodeNumberRequestDto
import com.umc.itday.feature.barcode.data.model.BarcodeUsageRequestDto
import com.umc.itday.feature.barcode.domain.repository.BarcodeRepository

class BarcodeRepositoryImpl(
    private val api: BarcodeApi,
) : BarcodeRepository {
    override suspend fun getBarcode(): ApiResult<String> =
        safeApiCall {
            val response = api.getBarcode()
            val barcodeNumber = response.data?.barcodeNum
            if (response.success && barcodeNumber != null) {
                ApiResult.Success(barcodeNumber)
            } else {
                response.failure()
            }
        }

    override suspend fun registerBarcode(barcodeNumber: String): ApiResult<Unit> =
        mutation { api.registerBarcode(BarcodeNumberRequestDto(barcodeNumber)) }

    override suspend fun updateBarcode(barcodeNumber: String): ApiResult<Unit> =
        mutation { api.updateBarcode(BarcodeNumberRequestDto(barcodeNumber)) }

    override suspend fun recordUsage(storeId: Long): ApiResult<Unit> =
        mutation { api.recordUsage(BarcodeUsageRequestDto(storeId)) }

    private suspend fun mutation(call: suspend () -> ApiResponseDto<*>): ApiResult<Unit> =
        safeApiCall {
            val response = call()
            if (response.success) ApiResult.Success(Unit) else response.failure()
        }

    private fun ApiResponseDto<*>.failure(): ApiResult.Failure =
        ApiResult.Failure(
            AppError.Server(
                statusCode = 200,
                message = message,
            ),
        )
}
