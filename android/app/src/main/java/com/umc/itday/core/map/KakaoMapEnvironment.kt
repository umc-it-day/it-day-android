package com.umc.itday.core.map

import android.os.Build

object KakaoMapEnvironment {
    val isSupportedDevice: Boolean
        get() = Build.SUPPORTED_ABIS.firstOrNull() in SUPPORTED_ABIS

    private val SUPPORTED_ABIS = setOf("armeabi-v7a", "arm64-v8a")
}
