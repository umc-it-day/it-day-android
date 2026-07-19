package com.example.itday.core.data.repository

interface PaymentRepository {
    suspend fun getPaymentPlans(): PaymentPlansResult
}
