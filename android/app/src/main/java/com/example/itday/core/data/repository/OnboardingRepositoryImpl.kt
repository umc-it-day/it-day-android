package com.example.itday.core.data.repository

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.result.apiSuccess as success

class OnboardingRepositoryImpl(
    private val mockDataSource: ItDayMockDataSource,
) : OnboardingRepository {
    override suspend fun getOnboardingPages(): OnboardingPagesResult = success(mockDataSource.getOnboardingPages())
}
