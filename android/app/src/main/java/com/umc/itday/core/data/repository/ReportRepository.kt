package com.umc.itday.core.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayReportData

interface ReportRepository {
    suspend fun getReportData(): ApiResult<ItDayReportData>
}
