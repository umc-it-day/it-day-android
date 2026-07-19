package com.example.itday.core.data.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.model.ItDayBarcodeData

interface BarcodeRepository {
    suspend fun getBarcodeData(): ApiResult<ItDayBarcodeData>
}
