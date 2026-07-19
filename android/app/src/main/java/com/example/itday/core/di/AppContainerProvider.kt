package com.example.itday.core.di

import android.content.Context
import com.example.itday.ItDayApplication

val Context.appContainer: AppContainer
    get() = (applicationContext as ItDayApplication).appContainer
