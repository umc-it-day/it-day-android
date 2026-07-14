package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDayReportData

class ReportRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : ReportRepository {
    override suspend fun getReportData(): ItDayReportData = mockDataSource.getReportData()
}
