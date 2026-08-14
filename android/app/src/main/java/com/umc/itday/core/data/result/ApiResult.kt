package com.umc.itday.core.data.result

sealed interface ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>

    data class Failure(
        val error: AppError,
    ) : ApiResult<Nothing>
}

fun <T> apiSuccess(data: T): ApiResult<T> = ApiResult.Success(data)

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> =
    when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Failure -> this
    }

inline fun <T, R> ApiResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (AppError) -> R,
): R =
    when (this) {
        is ApiResult.Success -> onSuccess(data)
        is ApiResult.Failure -> onFailure(error)
    }
