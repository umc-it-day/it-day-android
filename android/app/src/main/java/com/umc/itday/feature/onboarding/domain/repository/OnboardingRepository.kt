package com.umc.itday.feature.onboarding.domain.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.feature.onboarding.domain.model.OnboardingSubmission
import com.umc.itday.feature.onboarding.domain.model.OnboardingTerm
import com.umc.itday.feature.onboarding.domain.model.PreferredBrand
import com.umc.itday.feature.onboarding.domain.model.Telecom
import com.umc.itday.feature.onboarding.domain.model.TelecomGrade

interface OnboardingRepository {
    suspend fun getTerms(): ApiResult<List<OnboardingTerm>>
    suspend fun getTelecoms(): ApiResult<List<Telecom>>
    suspend fun getGrades(telecom: String): ApiResult<List<TelecomGrade>>
    suspend fun getBrands(): ApiResult<List<PreferredBrand>>
    suspend fun submitOnboarding(submission: OnboardingSubmission): ApiResult<Unit>
}
