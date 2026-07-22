package com.example.itday.core.di

import android.content.Context
import com.example.itday.core.config.AppConfig
import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.repository.BarcodeRepository
import com.example.itday.core.data.repository.BarcodeRepositoryImpl
import com.example.itday.core.data.repository.HomeRepository
import com.example.itday.core.data.repository.HomeRepositoryImpl
import com.example.itday.core.data.repository.MapRepository
import com.example.itday.core.data.repository.MapRepositoryImpl
import com.example.itday.core.data.repository.OnboardingRepository
import com.example.itday.core.data.repository.OnboardingRepositoryImpl
import com.example.itday.core.data.repository.PaymentRepository
import com.example.itday.core.data.repository.PaymentRepositoryImpl
import com.example.itday.core.data.repository.ReportRepository
import com.example.itday.core.data.repository.ReportRepositoryImpl
import com.example.itday.core.data.repository.SettingsRepository
import com.example.itday.core.data.repository.SettingsRepositoryImpl
import com.example.itday.core.local.DataStoreLocalPreferencesDataSource
import com.example.itday.core.local.LocalPreferencesDataSource
import com.example.itday.core.local.itDayPreferencesDataStore
import com.example.itday.core.network.NetworkClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val appContext = context.applicationContext
    override val okHttpClient: OkHttpClient by lazy {
        NetworkClient.createOkHttpClient()
    }

    override val retrofit: Retrofit by lazy {
        NetworkClient.createRetrofit(
            baseUrl = AppConfig.apiBaseUrl,
            okHttpClient = okHttpClient,
        )
    }

    override val mockDataSource: ItDayMockDataSource by lazy {
        ItDayMockDataSource()
    }

    override val localPreferencesDataSource: LocalPreferencesDataSource by lazy {
        DataStoreLocalPreferencesDataSource(appContext.itDayPreferencesDataStore)
    }

    override val homeRepository: HomeRepository by lazy {
        HomeRepositoryImpl(mockDataSource)
    }

    override val mapRepository: MapRepository by lazy {
        MapRepositoryImpl(mockDataSource)
    }

    override val barcodeRepository: BarcodeRepository by lazy {
        BarcodeRepositoryImpl(mockDataSource)
    }

    override val reportRepository: ReportRepository by lazy {
        ReportRepositoryImpl(mockDataSource)
    }

    override val onboardingRepository: OnboardingRepository by lazy {
        OnboardingRepositoryImpl(mockDataSource)
    }

    override val paymentRepository: PaymentRepository by lazy {
        PaymentRepositoryImpl(mockDataSource)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(mockDataSource)
    }
}
