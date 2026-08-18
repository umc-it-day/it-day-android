package com.umc.itday.ui.state

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage

fun <T> ApiResult<T>.toUiState(isEmpty: (T) -> Boolean = { false }): UiState<T> =
    when (this) {
        is ApiResult.Success ->
            if (isEmpty(data)) {
                UiState.Empty
            } else {
                UiState.Success(data)
            }
        is ApiResult.Failure ->
            UiState.Error(
                message = error.toUserMessage(),
                throwable = error.cause,
            )
    }
