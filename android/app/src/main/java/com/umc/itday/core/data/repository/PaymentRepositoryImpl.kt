package com.umc.itday.core.data.repository

import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.result.apiSuccess as success

class PaymentRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : PaymentRepository {
    override suspend fun getPaymentPlans(): PaymentPlansResult = success(mockDataSource.getPaymentPlans())
}
