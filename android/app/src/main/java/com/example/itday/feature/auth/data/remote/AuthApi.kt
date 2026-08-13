package com.example.itday.feature.auth.data.remote

import com.example.itday.feature.auth.data.model.ApiResponseDto
import com.example.itday.feature.auth.data.model.KakaoLoginDataDto
import com.example.itday.feature.auth.data.model.KakaoLoginRequestDto
import com.example.itday.feature.auth.data.model.RefreshTokenDataDto
import com.example.itday.feature.auth.data.model.RefreshTokenRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthApi {
    @POST("api/oauth/social/callback")
    suspend fun loginWithKakao(
        @Body request: KakaoLoginRequestDto,
    ): ApiResponseDto<KakaoLoginDataDto>

    @POST("api/auth/refresh")
    suspend fun refreshTokens(
        @Body request: RefreshTokenRequestDto,
    ): ApiResponseDto<RefreshTokenDataDto>

    @POST("api/auth/logout")
    suspend fun logout(): ApiResponseDto<Unit?>

    @DELETE("api/members/me")
    suspend fun withdraw(): ApiResponseDto<Unit?>
}
