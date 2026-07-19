package com.example.itday.core.di

import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.repository.BarcodeRepository
import com.example.itday.core.data.repository.HomeRepository
import com.example.itday.core.data.repository.MapRepository
import com.example.itday.core.data.repository.OnboardingRepository
import com.example.itday.core.data.repository.PaymentRepository
import com.example.itday.core.data.repository.ReportRepository
import com.example.itday.core.data.repository.SettingsRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface AppContainer {
    val okHttpClient: OkHttpClient
    val retrofit: Retrofit
    val mockDataSource: ItDayMockDataSource
    val homeRepository: HomeRepository
    val mapRepository: MapRepository
    val barcodeRepository: BarcodeRepository
    val reportRepository: ReportRepository
    val onboardingRepository: OnboardingRepository
    val paymentRepository: PaymentRepository
    val settingsRepository: SettingsRepository
}
