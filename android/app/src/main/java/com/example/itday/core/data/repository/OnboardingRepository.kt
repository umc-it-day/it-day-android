package com.example.itday.core.data.repository

import com.example.itday.core.model.ItDayOnboardingPage

interface OnboardingRepository {
    suspend fun getOnboardingPages(): List<ItDayOnboardingPage>
}
