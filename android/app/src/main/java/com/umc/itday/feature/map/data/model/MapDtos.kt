package com.umc.itday.feature.map.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MapSearchResponseDto(
    val places: List<PlaceDto>,
    val page: Int,
    val size: Int,
    val isEnd: Boolean,
    val totalCount: Int,
)

@Serializable
data class PlaceDto(
    val kakaoPlaceId: String,
    val storeId: Long? = null,
    val partnerStore: Boolean,
    val placeName: String,
    val brandName: String? = null,
    val categoryName: String? = null,
    val addressName: String? = null,
    val roadAddressName: String? = null,
    val longitude: Double,
    val latitude: Double,
    val distanceMeters: Int? = null,
    val phone: String? = null,
    val placeUrl: String? = null,
    val brandImg: String? = null,
    val storeImg: String? = null,
    val businessHour: String? = null,
    val benefits: List<BenefitDto> = emptyList(),
)

@Serializable
data class BenefitDto(
    val benefitId: Long,
    val title: String,
    val description: String? = null,
    val benefitValue: Int? = null,
    val telecom: String? = null,
    val telecomGrade: String? = null,
    val validFrom: String? = null,
    val validTo: String? = null,
)

@Serializable
data class StoreDetailDto(
    val storeId: Long,
    val kakaoPlaceId: String,
    val storeName: String,
    val brandName: String? = null,
    val category: String? = null,
    val brandImg: String? = null,
    val storeImg: String? = null,
    val address: String? = null,
    val businessHour: String? = null,
    val telNum: String? = null,
    val longitude: Double,
    val latitude: Double,
    val distanceMeters: Int? = null,
    val placeUrl: String? = null,
    val benefits: List<BenefitDto> = emptyList(),
)

@Serializable
data class KakaoDirectionsResponseDto(
    val routes: List<KakaoRouteDto> = emptyList(),
)

@Serializable
data class KakaoRouteDto(
    val resultCode: Int = 0,
    val resultMsg: String = "",
    val sections: List<KakaoSectionDto> = emptyList(),
)

@Serializable
data class KakaoSectionDto(
    val distance: Int = 0,
    val duration: Int = 0,
    val roads: List<KakaoRoadDto> = emptyList(),
)

@Serializable
data class KakaoRoadDto(
    val name: String = "",
    val distance: Int = 0,
    val duration: Int = 0,
    val vertexes: List<Double> = emptyList(),
)


