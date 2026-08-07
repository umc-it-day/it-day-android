package com.example.itday.core.di

import com.example.itday.core.auth.AuthRemoteDataSource
import com.example.itday.core.auth.AuthTokenStorage
import com.example.itday.core.auth.KakaoLoginClient
import com.example.itday.core.auth.TokenRefresher
import com.example.itday.core.data.mock.ItDayMockDataSource
import com.example.itday.core.data.repository.BarcodeRepository
import com.example.itday.core.data.repository.HomeRepository
import com.example.itday.core.data.repository.MapRepository
import com.example.itday.core.data.repository.OnboardingRepository
import com.example.itday.core.data.repository.PaymentRepository
import com.example.itday.core.data.repository.ReportRepository
import com.example.itday.core.data.repository.SettingsRepository
import com.example.itday.core.local.LocalPreferencesDataSource
import com.example.itday.core.location.LocationRepository
import com.example.itday.core.network.NetworkMonitor
import com.example.itday.core.permission.PermissionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface AppContainer {
    val okHttpClient: OkHttpClient
    val retrofit: Retrofit
    val mockDataSource: ItDayMockDataSource
    val localPreferencesDataSource: LocalPreferencesDataSource
    val authTokenStorage: AuthTokenStorage
    val authRemoteDataSource: AuthRemoteDataSource
    val tokenRefresher: TokenRefresher
    val kakaoLoginClient: KakaoLoginClient
    val permissionManager: PermissionManager
    val locationRepository: LocationRepository
    val networkMonitor: NetworkMonitor
    val homeRepository: HomeRepository
    val mapRepository: MapRepository
    val barcodeRepository: BarcodeRepository
    val reportRepository: ReportRepository
    val onboardingRepository: OnboardingRepository
    val paymentRepository: PaymentRepository
    val settingsRepository: SettingsRepository
}
