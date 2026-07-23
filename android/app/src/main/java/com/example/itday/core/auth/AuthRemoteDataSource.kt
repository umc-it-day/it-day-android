package com.example.itday.core.auth

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError

fun interface AuthRemoteDataSource {
    suspend fun refreshTokens(refreshToken: String): ApiResult<AuthTokens>
}

class PendingAuthRemoteDataSource : AuthRemoteDataSource {
    override suspend fun refreshTokens(refreshToken: String): ApiResult<AuthTokens> =
        ApiResult.Failure(
            AppError.Unknown(
                UnsupportedOperationException("The server token refresh endpoint is not configured."),
            ),
        )
}
