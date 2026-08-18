package com.umc.itday.feature.map.data.mapper

import com.umc.itday.feature.map.data.model.PlaceDto
import com.umc.itday.feature.map.presentation.MapCoordinate
import com.umc.itday.feature.map.presentation.MapStoreDetailUiModel
import com.umc.itday.feature.map.presentation.MapStoreUiModel

fun PlaceDto.toUiModel(): MapStoreUiModel {
    val firstBenefit = benefits.firstOrNull()
    return MapStoreUiModel(
        id = storeId?.toString() ?: kakaoPlaceId,
        name = placeName,
        position = MapCoordinate(latitude, longitude),
        distanceMeters = distanceMeters ?: -1,
        discountPercent = firstBenefit?.benefitValue ?: -1,
        detail = MapStoreDetailUiModel(
            benefit = firstBenefit?.title ?: "",
            benefitDescription = firstBenefit?.description ?: "",
            productSaving = "", // API doesn't provide these yet
            monthlySaving = "",
            address = roadAddressName ?: addressName ?: "",
            businessHours = businessHour ?: "",
            phoneNumber = phone ?: "",
        )
    )
}

fun List<PlaceDto>.toUiModels(): List<MapStoreUiModel> = map { it.toUiModel() }

