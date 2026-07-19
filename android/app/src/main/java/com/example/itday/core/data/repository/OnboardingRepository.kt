package com.example.itday.core.data.repository

interface OnboardingRepository {
    suspend fun getOnboardingPages(): OnboardingPagesResult
}
