package com.example.itday.core.data.repository

import com.example.itday.core.model.ItDayPaymentPlan

interface PaymentRepository {
    suspend fun getPaymentPlans(): List<ItDayPaymentPlan>
}
