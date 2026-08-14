package com.umc.itday.core.data.repository

interface OnboardingRepository {
    suspend fun getOnboardingPages(): OnboardingPagesResult
}
