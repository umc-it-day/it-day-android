package com.umc.itday.feature.settings.data.api

import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.settings.data.model.MembershipInfoDto
import com.umc.itday.feature.settings.data.model.UpdateMembershipRequestDto
import com.umc.itday.feature.settings.data.model.UpdateNameRequestDto
import com.umc.itday.feature.settings.data.model.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface  SettingsApi {
    @GET("api/members/me/membership")
    suspend fun getMembershipInfo(): ApiResponseDto<MembershipInfoDto>

    @GET("api/members/me/profile")
    suspend fun getUserProfile(): ApiResponseDto<UserProfileDto>

    @PATCH("api/members/me/name")
    suspend fun updateName(
        @Body request: UpdateNameRequestDto,
    ): ApiResponseDto<Unit>

    @PATCH("api/members/me/membership")
    suspend fun updateMembership(
        @Body request: UpdateMembershipRequestDto,
    ): ApiResponseDto<Unit>
}
