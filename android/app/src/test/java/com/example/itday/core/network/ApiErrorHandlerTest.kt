package com.example.itday.core.network

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiErrorHandlerTest {
    @Test
    fun `parses common server error response`() {
        val error = parseApiError(500, COMMON_ERROR_BODY)

        assertEquals(500, error.statusCode)
        assertEquals("COMMON_002", error.errorCode)
        assertEquals("서버 내부 오류가 발생했습니다.", error.message)
        assertEquals("2026-08-14T02:05:45.132756056", error.timestamp)
    }

    @Test
    fun `preserves each supported http status code`() {
        listOf(400, 401, 403, 404, 409, 500).forEach { statusCode ->
            assertEquals(statusCode, parseApiError(statusCode, COMMON_ERROR_BODY).statusCode)
        }
    }

    @Test
    fun `falls back safely for empty error body`() {
        val error = parseApiError(500, null)
        assertNull(error.errorCode)
        assertNull(error.message)
        assertNull(error.timestamp)
    }

    @Test
    fun `falls back safely for non json error body`() {
        val error = parseApiError(500, "Internal Server Error")
        assertNull(error.errorCode)
        assertNull(error.message)
        assertNull(error.timestamp)
    }

    @Test
    fun `accepts missing and unknown fields`() {
        val error =
            parseApiError(
                400,
                """{"success":false,"message":"잘못된 요청입니다.","traceId":"abc"}""",
            )

        assertNull(error.errorCode)
        assertEquals("잘못된 요청입니다.", error.message)
        assertNull(error.timestamp)
    }

    @Test
    fun `maps io exception to network error`() =
        runTest {
            val result = safeApiCall<Unit> { throw IOException("offline") }
            assertTrue(result is ApiResult.Failure && result.error is AppError.Network)
        }

    @Test
    fun `maps serialization exception to parsing error`() =
        runTest {
            val result = safeApiCall<Unit> { throw SerializationException("invalid json") }
            assertTrue(result is ApiResult.Failure && result.error is AppError.Parsing)
        }

    @Test
    fun `does not swallow coroutine cancellation`() {
        assertThrows(CancellationException::class.java) {
            kotlinx.coroutines.runBlocking {
                safeApiCall<Unit> { throw CancellationException("cancelled") }
            }
        }
    }

    private companion object {
        val COMMON_ERROR_BODY =
            """
            {
              "success": false,
              "code": "COMMON_002",
              "message": "서버 내부 오류가 발생했습니다.",
              "timestamp": "2026-08-14T02:05:45.132756056"
            }
            """.trimIndent()
    }
}
