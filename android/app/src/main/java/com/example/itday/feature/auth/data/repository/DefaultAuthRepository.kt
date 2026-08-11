package com.example.itday.feature.auth.data.repository

import com.example.itday.core.auth.AuthTokenStorage
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.feature.auth.data.remote.AuthLoginRemoteDataSource
import com.example.itday.feature.auth.data.remote.RemoteLoginResult
import com.example.itday.feature.auth.domain.model.LoginSession
import com.example.itday.feature.auth.domain.repository.AuthRepository
import java.io.IOException

class DefaultAuthRepository(
    private val remoteDataSource: AuthLoginRemoteDataSource,
    private val tokenStorage: AuthTokenStorage,
) : AuthRepository {
    override suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<LoginSession> =
        if (kakaoAccessToken.isBlank()) {
            ApiResult.Failure(AppError.Validation("카카오 액세스 토큰이 비어 있습니다."))
        } else {
            handleLoginResult(remoteDataSource.loginWithKakao(kakaoAccessToken))
        }

    private suspend fun handleLoginResult(
        result: ApiResult<RemoteLoginResult>,
    ): ApiResult<LoginSession> =
        when (result) {
            is ApiResult.Success -> {
                try {
                    tokenStorage.saveTokens(result.data.tokens)
                    ApiResult.Success(
                        LoginSession(
                            isNewUser = result.data.isNewUser,
                        ),
                    )
                } catch (error: IOException) {
                    ApiResult.Failure(AppError.Network(error))
                } catch (error: Exception) {
                    ApiResult.Failure(AppError.Unknown(error))
                }
            }
            is ApiResult.Failure -> result
        }
}
