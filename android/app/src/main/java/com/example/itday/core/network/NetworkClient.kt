package com.example.itday.core.network

import com.example.itday.BuildConfig
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkClient {
    private const val DEFAULT_TIMEOUT_SECONDS = 15L

    val json: Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    fun createOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(defaultHeadersInterceptor)
            .build()

    fun createRetrofit(
        baseUrl: String = BuildConfig.API_BASE_URL,
        okHttpClient: OkHttpClient = createOkHttpClient(),
    ): Retrofit {
        require(baseUrl.isNotBlank()) { "BuildConfig.API_BASE_URL must not be blank." }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(jsonMediaType))
            .build()
    }

    inline fun <reified T> createApi(
        retrofit: Retrofit = createRetrofit(),
    ): T = retrofit.create(T::class.java)

    private val defaultHeadersInterceptor =
        Interceptor { chain ->
            val request =
                chain.request()
                    .newBuilder()
                    .header("Accept", "application/json")
                    .build()

            chain.proceed(request)
        }

    private val jsonMediaType = "application/json".toMediaType()
}
