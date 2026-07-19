package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.apiSuccess as success

class PaymentRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : PaymentRepository {
    override suspend fun getPaymentPlans(): PaymentPlansResult = success(mockDataSource.getPaymentPlans())
}
