package com.umc.itday.core.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayBarcodeData

interface BarcodeRepository {
    suspend fun getBarcodeData(): ApiResult<ItDayBarcodeData>
}
