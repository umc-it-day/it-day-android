package com.umc.itday.core.data.repository

import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.model.ItDayReportData
import com.umc.itday.core.data.result.apiSuccess as success

class ReportRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : ReportRepository {
    override suspend fun getReportData(): ApiResult<ItDayReportData> = success(mockDataSource.getReportData())
}
