package com.umc.itday.core.network

import android.util.Log
import com.umc.itday.BuildConfig
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.data.result.AuthErrorReason
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

@Serializable
internal data class ApiErrorResponseDto(
    val success: Boolean = false,
    val code: String? = null,
    val message: String? = null,
    val timestamp: String? = null,
)

suspend fun <T> safeApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> =
    try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: HttpException) {
        ApiResult.Failure(error.toAppError())
    } catch (error: IOException) {
        ApiResult.Failure(AppError.Network(error))
    } catch (error: SerializationException) {
        ApiResult.Failure(AppError.Parsing(error))
    } catch (error: Exception) {
        ApiResult.Failure(AppError.Unknown(error))
    }

internal fun parseApiError(
    statusCode: Int,
    rawErrorBody: String?,
    cause: Throwable? = null,
    json: Json = NetworkClient.json,
): AppError.Server {
    val errorDto =
        rawErrorBody
            ?.takeIf(String::isNotBlank)
            ?.let { body ->
                runCatching { json.decodeFromString<ApiErrorResponseDto>(body) }.getOrNull()
            }

    return AppError.Server(
        statusCode = statusCode,
        errorCode = errorDto?.code,
        message = errorDto?.message,
        timestamp = errorDto?.timestamp,
        cause = cause,
    )
}

private fun HttpException.toAppError(): AppError {
    val rawErrorBody = response()?.errorBody()?.string()
    val serverError = parseApiError(code(), rawErrorBody, this)
    logApiError(serverError)

    return when (code()) {
        401 ->
            AppError.Auth(
                reason = AuthErrorReason.Unauthorized,
                cause = this,
                serverError = serverError,
            )
        403 ->
            AppError.Auth(
                reason = AuthErrorReason.PermissionDenied,
                cause = this,
                serverError = serverError,
            )
        else -> serverError
    }
}

private fun logApiError(error: AppError.Server) {
    if (!BuildConfig.DEBUG) return

    Log.e(
        "ApiError",
        "HTTP ${error.statusCode} | code=${error.errorCode ?: "UNKNOWN"} | " +
            "message=${error.message ?: "No server message"} | " +
            "timestamp=${error.timestamp ?: "-"}",
    )
}
