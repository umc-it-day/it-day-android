package com.example.itday.feature.auth.data.mapper

import com.example.itday.core.auth.AuthTokens
import com.example.itday.feature.auth.data.model.KakaoLoginDataDto
import com.example.itday.feature.auth.data.remote.RemoteLoginResult

fun KakaoLoginDataDto.toRemoteLoginResult(): RemoteLoginResult =
    RemoteLoginResult(
        tokens = AuthTokens(accessToken, refreshToken),
        isNewUser = isNewUser,
    )
