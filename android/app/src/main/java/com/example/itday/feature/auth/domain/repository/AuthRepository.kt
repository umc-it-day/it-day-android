package com.example.itday.feature.auth.domain.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.auth.domain.model.LoginSession

fun interface AuthRepository {
    suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<LoginSession>
}
