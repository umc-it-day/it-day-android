package com.umc.itday.core.di

import android.content.Context
import com.umc.itday.ItDayApplication

val Context.appContainer: AppContainer
    get() = (applicationContext as ItDayApplication).appContainer
