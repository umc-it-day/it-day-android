package com.umc.itday.feature.onboarding.data.remote

import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.onboarding.data.model.BrandDto
import com.umc.itday.feature.onboarding.data.model.OnboardingRequestDto
import com.umc.itday.feature.onboarding.data.model.TelecomDto
import com.umc.itday.feature.onboarding.data.model.TelecomGradeDto
import com.umc.itday.feature.onboarding.data.model.TermDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OnboardingApi {
    @GET("api/terms")
    suspend fun getTerms(): ApiResponseDto<List<TermDto>>

    @GET("api/telecoms")
    suspend fun getTelecoms(): ApiResponseDto<List<TelecomDto>>

    @GET("api/telecoms/{telecom}/grades")
    suspend fun getGrades(
        @Path("telecom") telecom: String,
    ): ApiResponseDto<List<TelecomGradeDto>>

    @GET("api/brands")
    suspend fun getBrands(): ApiResponseDto<List<BrandDto>>

    @POST("api/members/onboarding")
    suspend fun submitOnboarding(
        @Body request: OnboardingRequestDto,
    ): ApiResponseDto<Unit?>
}
