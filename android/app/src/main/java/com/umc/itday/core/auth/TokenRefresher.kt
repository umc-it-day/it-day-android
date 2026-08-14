package com.umc.itday.core.auth

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenRefresher(
    private val tokenStorage: AuthTokenStorage,
    private val remoteDataSource: AuthRemoteDataSource,
) {
    private val refreshMutex = Mutex()

    suspend fun refreshAccessToken(failedAccessToken: String?): String? =
        refreshMutex.withLock {
            val currentTokens = tokenStorage.getTokens() ?: return@withLock null
            if (failedAccessToken != null && currentTokens.accessToken != failedAccessToken) {
                return@withLock currentTokens.accessToken
            }

            when (val result = remoteDataSource.refreshTokens(currentTokens.refreshToken)) {
                is ApiResult.Success -> {
                    val refreshedTokens =
                        result.data.takeIf { it.accessToken.isNotBlank() && it.refreshToken.isNotBlank() }
                    refreshedTokens?.also { tokenStorage.saveTokens(it) }?.accessToken
                }
                is ApiResult.Failure -> {
                    if (result.error is AppError.Auth) tokenStorage.clearTokens()
                    null
                }
            }
        }
}
