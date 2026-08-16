package com.umc.itday.feature.auth.data.mapper

import com.umc.itday.core.auth.AuthTokens
import com.umc.itday.feature.auth.data.model.KakaoLoginDataDto
import com.umc.itday.feature.auth.data.remote.RemoteLoginResult

fun KakaoLoginDataDto.toRemoteLoginResult(): RemoteLoginResult =
    RemoteLoginResult(
        tokens = AuthTokens(accessToken, refreshToken, userId),
        isNewUser = isNewUser,
    )
