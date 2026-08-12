package com.example.itday.feature.auth.data.remote

import com.example.itday.core.auth.AuthRemoteDataSource
import com.example.itday.core.auth.AuthTokens
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.core.data.result.AuthErrorReason
import com.example.itday.feature.auth.data.mapper.toRemoteLoginResult
import com.example.itday.feature.auth.data.model.KakaoLoginRequestDto
import com.example.itday.feature.auth.data.model.RefreshTokenRequestDto
import java.io.IOException
import retrofit2.HttpException

data class RemoteLoginResult(
    val tokens: AuthTokens,
    val isNewUser: Boolean,
)

interface AuthLoginRemoteDataSource {
    suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<RemoteLoginResult>
    suspend fun logout(): ApiResult<Unit>
    suspend fun withdraw(): ApiResult<Unit>
}

class RetrofitAuthRemoteDataSource(
    private val api: AuthApi,
) : AuthLoginRemoteDataSource, AuthRemoteDataSource {
    override suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<RemoteLoginResult> =
        runApiCall {
            val response = api.loginWithKakao(KakaoLoginRequestDto(kakaoAccessToken))
            val data = response.data
            if (!response.success || data == null) {
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            } else if (data.accessToken.isBlank() || data.refreshToken.isBlank()) {
                ApiResult.Failure(
                    AppError.Server(
                        statusCode = 200,
                        message = "로그인 응답에 인증 토큰이 없습니다.",
                    ),
                )
            } else {
                ApiResult.Success(data.toRemoteLoginResult())
            }
        }

    override suspend fun logout(): ApiResult<Unit> =
        runApiCall {
            val response = api.logout()
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            }
        }

    override suspend fun withdraw(): ApiResult<Unit> =
        runApiCall {
            val response = api.withdraw()
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            }
        }

    override suspend fun refreshTokens(refreshToken: String): ApiResult<AuthTokens> =
        runApiCall {
            val response = api.refreshTokens(RefreshTokenRequestDto(refreshToken))
            val data = response.data
            if (!response.success || data == null) {
                ApiResult.Failure(AppError.Auth(AuthErrorReason.TokenExpired))
            } else if (data.accessToken.isBlank() || data.refreshToken.isBlank()) {
                ApiResult.Failure(
                    AppError.Server(
                        statusCode = 200,
                        message = "토큰 재발급 응답에 인증 토큰이 없습니다.",
                    ),
                )
            } else {
                ApiResult.Success(AuthTokens(data.accessToken, data.refreshToken))
            }
        }

    private suspend fun <T> runApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> =
        try {
            block()
        } catch (error: IOException) {
            ApiResult.Failure(AppError.Network(error))
        } catch (error: HttpException) {
            when (error.code()) {
                401, 403 -> ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized, error))
                else -> ApiResult.Failure(AppError.Server(error.code(), cause = error))
            }
        } catch (error: Exception) {
            ApiResult.Failure(AppError.Unknown(error))
        }
}

class MockAuthLoginRemoteDataSource : AuthLoginRemoteDataSource {
    override suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<RemoteLoginResult> =
        if (kakaoAccessToken.isBlank()) {
            ApiResult.Failure(AppError.Validation("카카오 액세스 토큰이 비어 있습니다."))
        } else {
            ApiResult.Success(
                RemoteLoginResult(
                    tokens = AuthTokens("mock-itday-access-token", "mock-itday-refresh-token"),
                    isNewUser = true,
                ),
            )
        }

    override suspend fun logout(): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun withdraw(): ApiResult<Unit> = ApiResult.Success(Unit)
}
