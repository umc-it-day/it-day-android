package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.model.ItDayOnboardingPage

class OnboardingRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : OnboardingRepository {
    override suspend fun getOnboardingPages(): List<ItDayOnboardingPage> =
        mockDataSource.getOnboardingPages()
}
