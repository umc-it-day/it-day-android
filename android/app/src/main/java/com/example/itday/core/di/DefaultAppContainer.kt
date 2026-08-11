package com.example.itday.core.di

import android.content.Context
import com.example.itday.core.auth.AuthRemoteDataSource
import com.example.itday.core.auth.AuthTokenStorage
import com.example.itday.core.auth.DataStoreAuthTokenStorage
import com.example.itday.core.auth.KakaoLoginClient
import com.example.itday.core.auth.KakaoSdkLoginClient
import com.example.itday.core.auth.MockKakaoLoginClient
import com.example.itday.core.auth.PendingAuthRemoteDataSource
import com.example.itday.core.auth.TokenRefresher
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
import com.example.itday.core.location.FusedLocationRepository
import com.example.itday.core.location.LocationRepository
import com.example.itday.core.network.AuthHeaderInterceptor
import com.example.itday.core.network.AuthTokenAuthenticator
import com.example.itday.core.network.AndroidNetworkMonitor
import com.example.itday.core.network.NetworkClient
import com.example.itday.core.network.NetworkMonitor
import com.example.itday.core.permission.AndroidPermissionManager
import com.example.itday.core.permission.PermissionManager
import com.example.itday.feature.auth.data.remote.AuthApi
import com.example.itday.feature.auth.data.remote.AuthLoginRemoteDataSource
import com.example.itday.feature.auth.data.remote.MockAuthLoginRemoteDataSource
import com.example.itday.feature.auth.data.remote.RetrofitAuthRemoteDataSource
import com.example.itday.feature.auth.data.repository.DefaultAuthRepository
import com.example.itday.feature.auth.domain.repository.AuthRepository
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
