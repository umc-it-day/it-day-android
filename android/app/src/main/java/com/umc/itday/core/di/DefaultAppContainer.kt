package com.umc.itday.core.di

import android.content.Context
import com.umc.itday.core.auth.AuthRemoteDataSource
import com.umc.itday.core.auth.AuthTokenStorage
import com.umc.itday.core.auth.DataStoreAuthTokenStorage
import com.umc.itday.core.auth.KakaoLoginClient
import com.umc.itday.core.auth.KakaoSdkLoginClient
import com.umc.itday.core.auth.MockKakaoLoginClient
import com.umc.itday.core.auth.PendingAuthRemoteDataSource
import com.umc.itday.core.auth.TokenRefresher
import com.umc.itday.core.config.AppConfig
import com.umc.itday.core.data.mock.ItDayMockDataSource
import com.umc.itday.core.data.repository.HomeRepository
import com.umc.itday.core.data.repository.HomeRepositoryImpl
import com.umc.itday.core.data.repository.PaymentRepository

import com.umc.itday.core.data.repository.PaymentRepositoryImpl
import com.umc.itday.core.data.repository.SettingsRepository
import com.umc.itday.core.data.repository.SettingsRepositoryImpl
import com.umc.itday.core.local.DataStoreLocalPreferencesDataSource
import com.umc.itday.core.local.LocalPreferencesDataSource
import com.umc.itday.core.local.itDayPreferencesDataStore
import com.umc.itday.core.location.FusedLocationRepository
import com.umc.itday.core.location.LocationRepository
import com.umc.itday.core.network.AuthHeaderInterceptor
import com.umc.itday.core.network.AuthTokenAuthenticator
import com.umc.itday.core.network.AndroidNetworkMonitor
import com.umc.itday.core.network.NetworkClient
import com.umc.itday.core.network.NetworkMonitor
import com.umc.itday.core.permission.AndroidPermissionManager
import com.umc.itday.core.permission.PermissionManager
import com.umc.itday.feature.auth.data.remote.AuthApi
import com.umc.itday.feature.auth.data.remote.AuthLoginRemoteDataSource
import com.umc.itday.feature.auth.data.remote.MockAuthLoginRemoteDataSource
import com.umc.itday.feature.auth.data.remote.RetrofitAuthRemoteDataSource
import com.umc.itday.feature.auth.data.repository.DefaultAuthRepository
import com.umc.itday.feature.auth.domain.repository.AuthRepository
import com.umc.itday.feature.onboarding.data.remote.OnboardingApi
import com.umc.itday.feature.onboarding.data.repository.DefaultOnboardingRepository
import com.umc.itday.feature.onboarding.data.repository.GuestOnboardingRepository
import com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository
import com.umc.itday.feature.barcode.data.api.BarcodeApi
import com.umc.itday.feature.barcode.data.repository.BarcodeRepositoryImpl
import com.umc.itday.feature.barcode.domain.repository.BarcodeRepository
import com.umc.itday.feature.settings.data.api.SettingsApi
import com.umc.itday.feature.settings.data.repository.SettingsRepositoryImpl as FeatureSettingsRepositoryImpl
import com.umc.itday.feature.settings.domain.repository.SettingsRepository as FeatureSettingsRepository
import com.umc.itday.feature.report.data.remote.ReportApi

import com.umc.itday.feature.report.data.repository.DefaultReportRepository
import com.umc.itday.feature.report.domain.repository.ReportRepository
import com.umc.itday.feature.map.data.remote.MapApiService
import com.umc.itday.feature.map.data.repository.MapRepositoryImpl as FeatureMapRepositoryImpl
import com.umc.itday.feature.map.domain.repository.MapRepository as FeatureMapRepository
import kotlinx.coroutines.runBlocking

import okhttp3.OkHttpClient
import retrofit2.Retrofit

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val appContext = context.applicationContext

    override val authTokenStorage: AuthTokenStorage by lazy {
        DataStoreAuthTokenStorage(appContext.itDayPreferencesDataStore)
    }

    private val retrofitAuthRemoteDataSource: RetrofitAuthRemoteDataSource by lazy {
        val authRetrofit =
            NetworkClient.createRetrofit(
                baseUrl = AppConfig.apiBaseUrl,
                okHttpClient = NetworkClient.createOkHttpClient(),
            )
        RetrofitAuthRemoteDataSource(NetworkClient.createApi<AuthApi>(authRetrofit))
    }

    override val authRemoteDataSource: AuthRemoteDataSource by lazy {
        if (AppConfig.useMockData) PendingAuthRemoteDataSource() else retrofitAuthRemoteDataSource
    }

    override val tokenRefresher: TokenRefresher by lazy {
        TokenRefresher(authTokenStorage, authRemoteDataSource)
    }

    override val kakaoLoginClient: KakaoLoginClient by lazy {
        if (AppConfig.useMockKakaoLogin) MockKakaoLoginClient() else KakaoSdkLoginClient()
    }

    private val authLoginRemoteDataSource: AuthLoginRemoteDataSource by lazy {
        if (AppConfig.useMockData) MockAuthLoginRemoteDataSource() else retrofitAuthRemoteDataSource
    }

    override val authRepository: AuthRepository by lazy {
        DefaultAuthRepository(authLoginRemoteDataSource, authTokenStorage)
    }

    override val permissionManager: PermissionManager by lazy {
        AndroidPermissionManager(appContext)
    }

    override val locationRepository: LocationRepository by lazy {
        FusedLocationRepository(appContext)
    }

    override val networkMonitor: NetworkMonitor by lazy {
        AndroidNetworkMonitor(appContext)
    }

    override val okHttpClient: OkHttpClient by lazy {
        NetworkClient.createOkHttpClient(
            authInterceptor =
                AuthHeaderInterceptor {
                    runBlocking { authTokenStorage.getTokens()?.accessToken }
                },
            authenticator = AuthTokenAuthenticator(tokenRefresher::refreshAccessToken),
        )
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

    override val mapRepository: FeatureMapRepository by lazy {
        FeatureMapRepositoryImpl(NetworkClient.createApi<MapApiService>(retrofit))
    }


    override val barcodeRepository: BarcodeRepository by lazy {
        BarcodeRepositoryImpl(NetworkClient.createApi<BarcodeApi>(retrofit))
    }

    override val reportRepository: ReportRepository by lazy {
        DefaultReportRepository(
            api = NetworkClient.createApi<ReportApi>(retrofit),
            tokenStorage = authTokenStorage,
        )
    }

    override val onboardingRepository: OnboardingRepository by lazy {
        DefaultOnboardingRepository(NetworkClient.createApi<OnboardingApi>(retrofit))
    }

    override val guestOnboardingRepository: OnboardingRepository by lazy {
        GuestOnboardingRepository(NetworkClient.createApi<OnboardingApi>(retrofit))
    }

    override val paymentRepository: PaymentRepository by lazy {
        PaymentRepositoryImpl(mockDataSource)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(mockDataSource)
    }

    override val featureSettingsRepository: FeatureSettingsRepository by lazy {
        FeatureSettingsRepositoryImpl(NetworkClient.createApi<SettingsApi>(retrofit))
    }
}
