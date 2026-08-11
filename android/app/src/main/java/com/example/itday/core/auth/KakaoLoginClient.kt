package com.example.itday.core.auth

import android.content.Context
import android.util.Log
import com.example.itday.BuildConfig
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed interface KakaoLoginResult {
    data class Success(
        val accessToken: String,
    ) : KakaoLoginResult

    data object Cancelled : KakaoLoginResult

    data class Failure(
        val message: String,
        val cause: Throwable? = null,
    ) : KakaoLoginResult
}

fun interface KakaoLoginClient {
    suspend fun login(context: Context): KakaoLoginResult
}

class MockKakaoLoginClient : KakaoLoginClient {
    override suspend fun login(context: Context): KakaoLoginResult {
        delay(MOCK_LOGIN_DELAY_MILLIS)
        return KakaoLoginResult.Success(accessToken = MOCK_ACCESS_TOKEN)
    }

    private companion object {
        const val MOCK_LOGIN_DELAY_MILLIS = 500L
        const val MOCK_ACCESS_TOKEN = "mock-kakao-access-token"
    }
}

class KakaoSdkLoginClient : KakaoLoginClient {
    override suspend fun login(context: Context): KakaoLoginResult =
        when {
            BuildConfig.KAKAO_NATIVE_APP_KEY.isBlank() ->
                KakaoLoginResult.Failure(
                    message = "카카오 Native App Key가 설정되지 않았습니다.",
                )
            !UserApiClient.instance.isKakaoTalkLoginAvailable(context) ->
                loginWithKakaoAccount(context)
            else -> loginWithKakaoTalk(context)
        }

    private suspend fun loginWithKakaoTalk(context: Context): KakaoLoginResult {
        val talkResult = awaitLogin { callback ->
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        }
        return when {
            talkResult.token != null -> talkResult.token.toSuccess()
            talkResult.error.isCancelled() -> {
                Log.d("KakaoLogin", "카카오톡 앱 로그인 취소됨")
                KakaoLoginResult.Cancelled
            }
            else -> {
                Log.e("KakaoLogin", "카카오톡 앱 로그인 실패: ${talkResult.error?.message}", talkResult.error)
                loginWithKakaoAccount(context)
            }
        }
    }

    private suspend fun loginWithKakaoAccount(context: Context): KakaoLoginResult {
        val result = awaitLogin { callback ->
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
        return when {
            result.token != null -> result.token.toSuccess()
            result.error.isCancelled() -> {
                Log.d("KakaoLogin", "카카오 계정 로그인 취소됨")
                KakaoLoginResult.Cancelled
            }
            else -> {
                Log.e("KakaoLogin", "카카오 계정 로그인 최종 실패: ${result.error?.message}", result.error)
                KakaoLoginResult.Failure(
                    message = "카카오 로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.",
                    cause = result.error,
                )
            }
        }
    }

    private suspend fun awaitLogin(
        request: ((OAuthToken?, Throwable?) -> Unit) -> Unit,
    ): LoginAttempt =
        suspendCancellableCoroutine { continuation ->
            request { token, error ->
                if (continuation.isActive) {
                    continuation.resume(LoginAttempt(token = token, error = error))
                }
            }
        }

    private fun OAuthToken.toSuccess(): KakaoLoginResult.Success =
        KakaoLoginResult.Success(accessToken = accessToken)

    private fun Throwable?.isCancelled(): Boolean =
        this is ClientError && reason == ClientErrorCause.Cancelled

    private data class LoginAttempt(
        val token: OAuthToken?,
        val error: Throwable?,
    )
}
