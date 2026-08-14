package com.umc.itday.core.auth

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError

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
