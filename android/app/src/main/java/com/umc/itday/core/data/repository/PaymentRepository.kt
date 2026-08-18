package com.umc.itday.core.data.repository

interface PaymentRepository {
    suspend fun getPaymentPlans(): PaymentPlansResult
}
