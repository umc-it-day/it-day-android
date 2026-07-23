package com.example.itday

import android.app.Application
import com.example.itday.core.di.AppContainer
import com.example.itday.core.di.DefaultAppContainer
import com.kakao.sdk.common.KakaoSdk

class ItDayApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()) {
            KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
        appContainer = DefaultAppContainer(applicationContext)
    }
}
