package com.example.itday.feature.auth.domain.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.auth.domain.model.LoginSession

interface AuthRepository {
    suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<LoginSession>
    suspend fun logout(): ApiResult<Unit>
    suspend fun withdraw(): ApiResult<Unit>
}
