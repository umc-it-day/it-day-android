package com.umc.itday.core.config

import com.umc.itday.BuildConfig

object AppConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL
    val useMockData: Boolean = BuildConfig.USE_MOCK_DATA
    val useMockKakaoLogin: Boolean = BuildConfig.USE_MOCK_KAKAO_LOGIN
    val appEnv: AppEnvironment = AppEnvironment.from(BuildConfig.APP_ENV)
}

enum class AppEnvironment {
    Debug,
    Release,
    Unknown,
    ;

    companion object {
        fun from(value: String): AppEnvironment =
            when (value.lowercase()) {
                "debug" -> Debug
                "release" -> Release
                else -> Unknown
            }
    }
}
