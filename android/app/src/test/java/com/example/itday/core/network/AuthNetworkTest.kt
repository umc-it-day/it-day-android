package com.example.itday.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthNetworkTest {
    @Test
    fun `adds bearer token when access token exists`() {
        var capturedRequest: Request? = null
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(AuthHeaderInterceptor { "access-token" })
                .addInterceptor(capturingInterceptor { capturedRequest = it })
                .build()

        client.newCall(request()).execute().close()

        assertEquals("Bearer access-token", capturedRequest?.header("Authorization"))
    }

    @Test
    fun `does not add authorization header when token is absent`() {
        var capturedRequest: Request? = null
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(AuthHeaderInterceptor { null })
                .addInterceptor(capturingInterceptor { capturedRequest = it })
                .build()

        client.newCall(request()).execute().close()

        assertNull(capturedRequest?.header("Authorization"))
    }

    @Test
    fun `authenticator retries with refreshed access token`() {
        val request = request().newBuilder().header("Authorization", "Bearer old-token").build()
        val authenticator =
            AuthTokenAuthenticator { failedToken ->
                assertEquals("old-token", failedToken)
                "new-token"
            }

        val retriedRequest = authenticator.authenticate(null, unauthorizedResponse(request))

        assertEquals("Bearer new-token", retriedRequest?.header("Authorization"))
    }

    @Test
    fun `authenticator stops after one retry`() {
        val request = request().newBuilder().header("Authorization", "Bearer token").build()
        val firstResponse = unauthorizedResponse(request)
        val secondResponse = unauthorizedResponse(request, priorResponse = firstResponse)
        val authenticator = AuthTokenAuthenticator { "another-token" }

        assertNull(authenticator.authenticate(null, secondResponse))
    }

    private fun request(): Request = Request.Builder().url("https://example.com/").build()

    private fun unauthorizedResponse(
        request: Request,
        priorResponse: Response? = null,
    ): Response =
        Response
            .Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("".toResponseBody())
            .priorResponse(priorResponse)
            .build()

    private fun capturingInterceptor(capture: (Request) -> Unit): Interceptor =
        Interceptor { chain ->
            capture(chain.request())
            Response
                .Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("".toResponseBody())
                .build()
        }
}
