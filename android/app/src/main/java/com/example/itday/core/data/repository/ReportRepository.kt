package com.example.itday.core.data.repository

import com.example.itday.core.model.ItDayReportData

interface ReportRepository {
    suspend fun getReportData(): ItDayReportData
}
