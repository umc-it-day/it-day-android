package com.example.itday.core.config

import com.example.itday.BuildConfig

object AppConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL
    val useMockData: Boolean = BuildConfig.USE_MOCK_DATA
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
