package com.umc.itday.core.data.result

fun AppError.toUserMessage(): String =
    when (this) {
        is AppError.Network -> "네트워크 연결을 확인해주세요."
        is AppError.Server -> message ?: "서버 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요."
        is AppError.Auth ->
            when (reason) {
                AuthErrorReason.Unauthorized -> "로그인이 필요합니다."
                AuthErrorReason.TokenExpired -> "로그인이 만료되었습니다. 다시 로그인해주세요."
                AuthErrorReason.PermissionDenied -> "접근 권한이 없습니다."
            }
        is AppError.Validation -> message
        is AppError.Parsing -> "서버 응답을 읽지 못했습니다. 잠시 후 다시 시도해주세요."
        is AppError.Unknown -> "알 수 없는 오류가 발생했습니다."
    }
