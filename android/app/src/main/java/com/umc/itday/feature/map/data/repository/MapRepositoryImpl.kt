package com.umc.itday.feature.map.data.repository

import android.util.Log
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.data.result.AuthErrorReason
import com.umc.itday.feature.map.data.mapper.toRoutePoints
import com.umc.itday.feature.map.data.mapper.toUiModel
import com.umc.itday.feature.map.data.mapper.toUiModels

import com.umc.itday.feature.map.data.remote.MapApiService
import com.umc.itday.feature.map.domain.repository.MapRepository
import com.umc.itday.feature.map.presentation.MapCoordinate
import com.umc.itday.feature.map.presentation.MapStoreDetailUiModel
import com.umc.itday.feature.map.presentation.MapStoreUiModel
import retrofit2.HttpException
import java.io.IOException

class MapRepositoryImpl(
    private val api: MapApiService,
) : MapRepository {

    override suspend fun searchPlaces(query: String): ApiResult<List<MapStoreUiModel>> =
        runApiCall {
            val response = api.searchPlaces(query)
            if (response.success && response.data != null) {
                ApiResult.Success(response.data.places.toUiModels())
            } else {
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            }
        }

    override suspend fun getNearbyStores(latitude: Double, longitude: Double): ApiResult<List<MapStoreUiModel>> =
        runApiCall {
            val response = api.getNearbyStores(longitude, latitude)
            if (response.success && response.data != null) {
                ApiResult.Success(response.data.places.toUiModels())
            } else {
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            }
        }

    override suspend fun getStoreDetail(storeId: Long): ApiResult<MapStoreUiModel> =
        runApiCall {
            val response = api.getStoreDetail(storeId)
            val data = response.data
            if (response.success && data != null) {
                val benefitUiModels = data.benefits.map { it.toUiModel() }
                val firstBenefit = data.benefits.firstOrNull()
                ApiResult.Success(
                    MapStoreUiModel(
                        id = data.storeId.toString(),
                        name = data.storeName,
                        brandName = data.brandName,
                        categoryName = data.category,
                        brandImg = data.brandImg?.takeIf { it.isNotBlank() },
                        position = MapCoordinate(data.latitude, data.longitude),
                        distanceMeters = data.distanceMeters ?: -1,
                        discountPercent = firstBenefit?.benefitValue ?: -1,
                        benefitTitle = firstBenefit?.title ?: "",
                        detail =
                            MapStoreDetailUiModel(
                                benefit = firstBenefit?.title ?: "",
                                benefitDescription = firstBenefit?.description ?: "",
                                productSaving = "",
                                monthlySaving = "",
                                address = data.address ?: "",
                                businessHours = data.businessHour ?: "",
                                phoneNumber = data.telNum ?: "",
                                benefits = benefitUiModels,
                            ),
                    ),
                )
            } else {
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            }
        }

    override suspend fun getDirections(
        startLat: Double,
        startLng: Double,
        destLat: Double,
        destLng: Double,
    ): ApiResult<List<MapCoordinate>> =
        runApiCall {
            val restKey = com.umc.itday.BuildConfig.KAKAO_REST_API_KEY
            if (restKey.isBlank()) {
                return@runApiCall ApiResult.Success(
                    listOf(MapCoordinate(startLat, startLng), MapCoordinate(destLat, destLng)),
                )
            }
            val response =
                api.getKakaoDirections(
                    authorization = "KakaoAK $restKey",
                    origin = "$startLng,$startLat",
                    destination = "$destLng,$destLat",
                )
            val routePoints = response.toRoutePoints()
            if (routePoints.isNotEmpty()) {
                ApiResult.Success(routePoints)
            } else {
                ApiResult.Success(
                    listOf(MapCoordinate(startLat, startLng), MapCoordinate(destLat, destLng)),
                )
            }

        }



    private suspend fun <T> runApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> =
        try {
            block()
        } catch (error: IOException) {
            Log.e("MapRepository", "Network Error: ${error.message}", error)
            ApiResult.Failure(AppError.Network(error))
        } catch (error: HttpException) {
            Log.e("MapRepository", "HTTP Error: code=${error.code()}, message=${error.message()}", error)
            when (error.code()) {
                401, 403 -> ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized, error))
                else -> ApiResult.Failure(AppError.Server(error.code(), cause = error))
            }
        } catch (error: Exception) {
            Log.e("MapRepository", "Unknown Error: ${error.message}", error)
            ApiResult.Failure(AppError.Unknown(error))
        }
}
