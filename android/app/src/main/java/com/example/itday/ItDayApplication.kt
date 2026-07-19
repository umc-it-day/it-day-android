package com.example.itday

import android.app.Application
import com.example.itday.core.di.AppContainer
import com.example.itday.core.di.DefaultAppContainer

class ItDayApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer()
    }
}
