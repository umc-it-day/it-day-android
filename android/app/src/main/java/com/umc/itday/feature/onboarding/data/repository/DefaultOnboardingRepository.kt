package com.umc.itday.feature.onboarding.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.network.safeApiCall
import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.onboarding.data.model.OnboardingRequestDto
import com.umc.itday.feature.onboarding.data.model.TermAgreementDto
import com.umc.itday.feature.onboarding.data.remote.OnboardingApi
import com.umc.itday.feature.onboarding.domain.model.OnboardingSubmission
import com.umc.itday.feature.onboarding.domain.model.OnboardingTerm
import com.umc.itday.feature.onboarding.domain.model.PreferredBrand
import com.umc.itday.feature.onboarding.domain.model.Telecom
import com.umc.itday.feature.onboarding.domain.model.TelecomGrade
import com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository

class DefaultOnboardingRepository(
    private val api: OnboardingApi,
) : OnboardingRepository {
    override suspend fun getTerms(): ApiResult<List<OnboardingTerm>> =
        safeApiCall {
            api.getTerms().toResult { terms ->
                terms.map { OnboardingTerm(it.termsId, it.title, it.content, it.isRequired) }
            }
        }

    override suspend fun getTelecoms(): ApiResult<List<Telecom>> =
        safeApiCall {
            api.getTelecoms().toResult { telecoms -> telecoms.map { Telecom(it.telecom, it.label) } }
        }

    override suspend fun getGrades(telecom: String): ApiResult<List<TelecomGrade>> =
        safeApiCall {
            api.getGrades(telecom).toResult { grades ->
                grades.map { TelecomGrade(it.membershipId, it.telecomGrade, it.gradeContent) }
            }
        }

    override suspend fun getBrands(): ApiResult<List<PreferredBrand>> =
        safeApiCall {
            api.getBrands().toResult { brands ->
                brands.map { PreferredBrand(it.brandId, it.brandName, it.brandImg, it.category) }
            }
        }

    override suspend fun submitOnboarding(submission: OnboardingSubmission): ApiResult<Unit> =
        safeApiCall {
            val request =
                OnboardingRequestDto(
                    termAgreements = submission.termAgreements.map { TermAgreementDto(it.termId, it.isAgreed) },
                    membershipId = submission.membershipId,
                    preferredBrandIds = submission.preferredBrandIds,
                )
            val response = api.submitOnboarding(request)
            if (response.success) ApiResult.Success(Unit) else response.failure()
        }

    private fun <T, R> ApiResponseDto<T>.toResult(transform: (T) -> R): ApiResult<R> {
        val value = data
        return if (success && value != null) ApiResult.Success(transform(value)) else failure()
    }

    private fun ApiResponseDto<*>.failure(): ApiResult.Failure =
        ApiResult.Failure(AppError.Server(statusCode = 200, message = message))
}
