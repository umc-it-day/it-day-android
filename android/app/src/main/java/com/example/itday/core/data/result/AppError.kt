package com.example.itday.core.data.result

sealed interface AppError {
    val cause: Throwable?

    data class Network(
        override val cause: Throwable? = null,
    ) : AppError

    data class Server(
        val statusCode: Int,
        val errorCode: String? = null,
        val message: String? = null,
        val timestamp: String? = null,
        override val cause: Throwable? = null,
    ) : AppError

    data class Auth(
        val reason: AuthErrorReason,
        override val cause: Throwable? = null,
        val serverError: Server? = null,
    ) : AppError

    data class Validation(
        val message: String,
        override val cause: Throwable? = null,
    ) : AppError

    data class Parsing(
        override val cause: Throwable? = null,
    ) : AppError

    data class Unknown(
        override val cause: Throwable? = null,
    ) : AppError
}

enum class AuthErrorReason {
    Unauthorized,
    TokenExpired,
    PermissionDenied,
}

fun Throwable.toAppError(): AppError = AppError.Unknown(cause = this)
