package com.umc.itday.feature.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val success: Boolean,
    val message: String = "",
    val data: T? = null,
)

@Serializable
data class KakaoLoginRequestDto(
    val kakaoAccessToken: String,
)

@Serializable
data class KakaoLoginDataDto(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val isNewUser: Boolean,
)

@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String,
)

@Serializable
data class RefreshTokenDataDto(
    val accessToken: String,
    val refreshToken: String,
)
