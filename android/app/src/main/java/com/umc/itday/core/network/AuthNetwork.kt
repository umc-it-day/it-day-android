package com.umc.itday.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AuthHeaderInterceptor(
    private val accessTokenProvider: () -> String?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val accessToken = accessTokenProvider()?.takeIf(String::isNotBlank)
        if (accessToken == null || request.header(AUTHORIZATION_HEADER) != null) {
            return chain.proceed(request)
        }
        return chain.proceed(request.withBearerToken(accessToken))
    }
}

class AuthTokenAuthenticator(
    private val refreshAccessToken: suspend (failedAccessToken: String?) -> String?,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        if (response.responseCount() >= MAX_AUTH_ATTEMPTS) return null

        val failedAccessToken = response.request.bearerToken()
        val refreshedAccessToken = runBlocking { refreshAccessToken(failedAccessToken) }
        return refreshedAccessToken?.let(response.request::withBearerToken)
    }

    private fun Response.responseCount(): Int {
        var count = 1
        var previous = priorResponse
        while (previous != null) {
            count++
            previous = previous.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_AUTH_ATTEMPTS = 2
    }
}

private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_PREFIX = "Bearer "

private fun Request.withBearerToken(accessToken: String): Request =
    newBuilder().header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$accessToken").build()

private fun Request.bearerToken(): String? =
    header(AUTHORIZATION_HEADER)
        ?.takeIf { it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
        ?.takeIf(String::isNotBlank)
