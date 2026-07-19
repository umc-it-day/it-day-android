package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.model.ItDayReportData
import com.example.itday.core.data.result.apiSuccess as success

class ReportRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : ReportRepository {
    override suspend fun getReportData(): ApiResult<ItDayReportData> = success(mockDataSource.getReportData())
}
