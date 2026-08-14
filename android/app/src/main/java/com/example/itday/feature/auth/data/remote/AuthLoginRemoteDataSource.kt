package com.example.itday.feature.auth.data.remote

import com.example.itday.core.auth.AuthRemoteDataSource
import com.example.itday.core.auth.AuthTokens
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.core.data.result.AuthErrorReason
import com.example.itday.core.network.safeApiCall
import com.example.itday.feature.auth.data.mapper.toRemoteLoginResult
import com.example.itday.feature.auth.data.model.KakaoLoginRequestDto
import com.example.itday.feature.auth.data.model.RefreshTokenRequestDto
import android.util.Log

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
        safeApiCall {
            val response = api.loginWithKakao(KakaoLoginRequestDto(kakaoAccessToken))
            val data = response.data
            if (!response.success || data == null) {
                Log.e("AuthDataSource", "Login Failed: success=${response.success}, message=${response.message}")
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            } else if (data.accessToken.isBlank() || data.refreshToken.isBlank()) {
                Log.e("AuthDataSource", "Login Failed: Missing tokens in response data")
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
        safeApiCall {
            val response = api.logout()
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                Log.e("AuthDataSource", "Logout Failed: success=${response.success}, message=${response.message}")
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            }
        }

    override suspend fun withdraw(): ApiResult<Unit> =
        safeApiCall {
            val response = api.withdraw()
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                Log.e("AuthDataSource", "Withdraw Failed: success=${response.success}, message=${response.message}")
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            }
        }

    override suspend fun refreshTokens(refreshToken: String): ApiResult<AuthTokens> =
        safeApiCall {
            val response = api.refreshTokens(RefreshTokenRequestDto(refreshToken))
            val data = response.data
            if (!response.success || data == null) {
                Log.e("AuthDataSource", "Token Refresh Failed: success=${response.success}, message=${response.message}")
                ApiResult.Failure(AppError.Auth(AuthErrorReason.TokenExpired))
            } else if (data.accessToken.isBlank() || data.refreshToken.isBlank()) {
                Log.e("AuthDataSource", "Token Refresh Failed: Missing tokens in response data")
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
