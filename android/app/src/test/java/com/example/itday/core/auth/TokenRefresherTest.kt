package com.example.itday.core.auth

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.core.data.result.AuthErrorReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenRefresherTest {
    @Test
    fun `saves rotated tokens after successful refresh`() =
        runTest {
            val storage = FakeAuthTokenStorage(AuthTokens("old-access", "old-refresh"))
            val refreshed = AuthTokens("new-access", "new-refresh")
            val refresher = TokenRefresher(storage) { ApiResult.Success(refreshed) }

            val result = refresher.refreshAccessToken("old-access")

            assertEquals("new-access", result)
            assertEquals(refreshed, storage.getTokens())
        }

    @Test
    fun `uses already rotated access token without another refresh`() =
        runTest {
            val storage = FakeAuthTokenStorage(AuthTokens("current-access", "refresh"))
            var remoteCallCount = 0
            val refresher =
                TokenRefresher(storage) {
                    remoteCallCount++
                    ApiResult.Success(AuthTokens("unused", "unused"))
                }

            val result = refresher.refreshAccessToken("stale-access")

            assertEquals("current-access", result)
            assertEquals(0, remoteCallCount)
        }

    @Test
    fun `clears tokens when refresh is rejected for authentication`() =
        runTest {
            val storage = FakeAuthTokenStorage(AuthTokens("access", "expired-refresh"))
            val refresher =
                TokenRefresher(storage) {
                    ApiResult.Failure(AppError.Auth(AuthErrorReason.TokenExpired))
                }

            val result = refresher.refreshAccessToken("access")

            assertNull(result)
            assertNull(storage.getTokens())
            assertTrue(storage.cleared)
        }

    @Test
    fun `keeps tokens when refresh fails for a transient error`() =
        runTest {
            val tokens = AuthTokens("access", "refresh")
            val storage = FakeAuthTokenStorage(tokens)
            val refresher =
                TokenRefresher(storage) {
                    ApiResult.Failure(AppError.Network())
                }

            val result = refresher.refreshAccessToken("access")

            assertNull(result)
            assertEquals(tokens, storage.getTokens())
        }
}

private class FakeAuthTokenStorage(
    initialTokens: AuthTokens?,
) : AuthTokenStorage {
    private val state = MutableStateFlow(initialTokens)
    var cleared: Boolean = false
        private set

    override val tokens: Flow<AuthTokens?> = state

    override suspend fun getTokens(): AuthTokens? = state.value

    override suspend fun saveTokens(tokens: AuthTokens) {
        state.value = tokens
    }

    override suspend fun clearTokens() {
        cleared = true
        state.value = null
    }
}
