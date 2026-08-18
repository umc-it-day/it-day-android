package com.umc.itday.core.auth

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long? = null,
)
