package com.umc.itday.feature.barcode.domain.repository

import com.umc.itday.core.data.result.ApiResult

interface BarcodeRepository {
    suspend fun getBarcode(): ApiResult<String>

    suspend fun registerBarcode(barcodeNumber: String): ApiResult<Unit>

    suspend fun updateBarcode(barcodeNumber: String): ApiResult<Unit>

    suspend fun recordUsage(storeId: Long): ApiResult<Unit>
}
