package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDayPaymentPlan

class PaymentRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : PaymentRepository {
    override suspend fun getPaymentPlans(): List<ItDayPaymentPlan> = mockDataSource.getPaymentPlans()
}
