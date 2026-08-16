package com.umc.itday.core.di

import com.umc.itday.core.auth.AuthRemoteDataSource
import com.umc.itday.core.auth.AuthTokenStorage
import com.umc.itday.core.auth.KakaoLoginClient
import com.umc.itday.core.auth.TokenRefresher
import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.repository.HomeRepository
import com.umc.itday.core.data.repository.MapRepository
import com.umc.itday.core.data.repository.OnboardingRepository
import com.umc.itday.core.data.repository.PaymentRepository
import com.umc.itday.core.data.repository.SettingsRepository
import com.umc.itday.feature.settings.domain.repository.SettingsRepository as FeatureSettingsRepository
import com.umc.itday.feature.report.domain.repository.ReportRepository
import com.umc.itday.core.local.LocalPreferencesDataSource
import com.umc.itday.core.location.LocationRepository
import com.umc.itday.core.network.NetworkMonitor
import com.umc.itday.core.permission.PermissionManager
import com.umc.itday.feature.auth.domain.repository.AuthRepository
import com.umc.itday.feature.barcode.domain.repository.BarcodeRepository
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
    val authRepository: AuthRepository
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
    val featureSettingsRepository: FeatureSettingsRepository
}
