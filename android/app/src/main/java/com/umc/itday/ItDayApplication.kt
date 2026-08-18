package com.umc.itday

import android.app.Application
import android.util.Log
import com.umc.itday.core.di.AppContainer
import com.umc.itday.core.di.DefaultAppContainer
import com.umc.itday.core.map.KakaoMapEnvironment
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.KakaoMapSdk

class ItDayApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d("KakaoKeyHash", "Debug Key Hash: ${Utility.getKeyHash(this)}")
        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank() && KakaoMapEnvironment.isSupportedDevice) {
            KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
            KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
        appContainer = DefaultAppContainer(applicationContext)
    }
}
