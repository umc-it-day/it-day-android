package com.example.itday.feature.auth.data.repository

import com.example.itday.core.auth.AuthTokenStorage
import com.example.itday.core.auth.AuthTokens
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.feature.auth.data.remote.AuthLoginRemoteDataSource
import com.example.itday.feature.auth.data.remote.RemoteLoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAuthRepositoryTest {
    @Test
    fun `login saves service tokens and returns session`() =
        runTest {
            val tokens = AuthTokens("access", "refresh")
            val storage = FakeTokenStorage()
            val repository =
                DefaultAuthRepository(
                    remoteDataSource =
                        FakeAuthLoginRemoteDataSource(
                            loginResult = ApiResult.Success(RemoteLoginResult(tokens, isNewUser = true)),
                        ),
                    tokenStorage = storage,
                )

            val result = repository.loginWithKakao("kakao-token")

            assertTrue(result is ApiResult.Success)
            assertTrue((result as ApiResult.Success).data.isNewUser)
            assertEquals(tokens, storage.getTokens())
        }

    @Test
    fun `login failure does not save tokens`() =
        runTest {
            val storage = FakeTokenStorage()
            val repository =
                DefaultAuthRepository(
                    remoteDataSource =
                        FakeAuthLoginRemoteDataSource(
                            loginResult = ApiResult.Failure(AppError.Network()),
                        ),
                    tokenStorage = storage,
                )

            val result = repository.loginWithKakao("kakao-token")

            assertTrue(result is ApiResult.Failure)
            assertNull(storage.getTokens())
        }
}

private class FakeAuthLoginRemoteDataSource(
    private val loginResult: ApiResult<RemoteLoginResult>,
) : AuthLoginRemoteDataSource {
    override suspend fun loginWithKakao(kakaoAccessToken: String) = loginResult

    override suspend fun logout(): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun withdraw(): ApiResult<Unit> = ApiResult.Success(Unit)
}

private class FakeTokenStorage : AuthTokenStorage {
    private val state = MutableStateFlow<AuthTokens?>(null)

    override val tokens: Flow<AuthTokens?> = state

    override suspend fun getTokens(): AuthTokens? = state.value

    override suspend fun saveTokens(tokens: AuthTokens) {
        state.value = tokens
    }

    override suspend fun clearTokens() {
        state.value = null
    }
}
