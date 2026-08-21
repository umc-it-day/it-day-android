package com.umc.itday.feature.map.data.mapper

import com.umc.itday.feature.map.data.model.BenefitDto
import com.umc.itday.feature.map.data.model.KakaoDirectionsResponseDto
import com.umc.itday.feature.map.data.model.PlaceDto
import com.umc.itday.feature.map.presentation.MapBenefitUiModel
import com.umc.itday.feature.map.presentation.MapCoordinate
import com.umc.itday.feature.map.presentation.MapStoreDetailUiModel
import com.umc.itday.feature.map.presentation.MapStoreUiModel

fun PlaceDto.toUiModel(): MapStoreUiModel {
    val benefitUiModels = benefits.map { it.toUiModel() }
    val primaryBenefit = selectPrimaryBenefit(benefits)
    val parsedPercent = primaryBenefit?.benefitValue ?: extractPercentFromTitle(primaryBenefit?.title)

    return MapStoreUiModel(
        id = storeId?.toString() ?: kakaoPlaceId,
        name = placeName,
        brandName = brandName,
        categoryName = categoryName?.toKoreanCategory(),
        brandImg = brandImg?.takeIf { it.isNotBlank() },
        position = MapCoordinate(latitude, longitude),
        distanceMeters = distanceMeters ?: -1,
        discountPercent = parsedPercent ?: -1,
        benefitTitle = cleanBenefitBadgeText(primaryBenefit?.title),
        detail =
            MapStoreDetailUiModel(
                benefit = primaryBenefit?.title ?: "",
                benefitDescription = primaryBenefit?.description ?: "",
                productSaving = "",
                monthlySaving = "",
                address = roadAddressName?.takeIf { it.isNotBlank() } ?: addressName ?: "",
                businessHours = businessHour ?: "",
                phoneNumber = phone ?: "",
                placeUrl = placeUrl?.takeIf { it.isNotBlank() },
                benefits = benefitUiModels,
            ),
    )
}

fun BenefitDto.toUiModel(): MapBenefitUiModel =
    MapBenefitUiModel(
        id = benefitId,
        title = title,
        description = description,
        telecom = telecom,
        telecomGrade = telecomGrade,
    )

fun List<PlaceDto>.toUiModels(): List<MapStoreUiModel> = map { it.toUiModel() }

fun KakaoDirectionsResponseDto.toRoutePoints(): List<MapCoordinate> {
    val points = mutableListOf<MapCoordinate>()
    val sections = routes.firstOrNull()?.sections ?: return emptyList()
    for (section in sections) {
        for (road in section.roads) {
            val v = road.vertexes
            var i = 0
            while (i + 1 < v.size) {
                val lng = v[i]
                val lat = v[i + 1]
                points.add(MapCoordinate(latitude = lat, longitude = lng))
                i += 2
            }
        }
    }
    return points
}


private fun selectPrimaryBenefit(benefits: List<BenefitDto>): BenefitDto? {
    if (benefits.isEmpty()) return null
    // 1. % 할인이 명시된 혜택 우선
    val percentBenefit = benefits.firstOrNull { "%" in it.title }
    if (percentBenefit != null) return percentBenefit
    // 2. 할인 금액/조건이 명시된 혜택
    val discountBenefit = benefits.firstOrNull { "할인" in it.title }
    if (discountBenefit != null) return discountBenefit
    // 3. 기본 첫 번째 혜택
    return benefits.first()
}

private fun cleanBenefitBadgeText(rawTitle: String?): String {
    if (rawTitle.isNullOrBlank()) return ""
    // 1. [달.달.혜택], [상시] 등 대괄호 태그 제거
    var cleaned = rawTitle.replace(Regex("""\[.*?\]"""), "").trim()

    // 2. 핵심 % 할인 추출 (예: "인기 음료 6종 50% 할인" -> "50% 할인")
    val percentMatch = Regex("""(\d+%\s*할인)""").find(cleaned)
    if (percentMatch != null) return percentMatch.value

    // 3. 1,000원당 100원 할인 패턴 추출
    val perWonMatch = Regex("""(1[,\d]*원당\s*\d+[,\d]*원\s*할인)""").find(cleaned)
    if (perWonMatch != null) return perWonMatch.value

    val perWonShortMatch = Regex("""(1천\s*원당\s*\d+원\s*할인)""").find(cleaned)
    if (perWonShortMatch != null) return perWonShortMatch.value

    // 4. 무료/사이즈업 추출
    val freeMatch = Regex("""(사이즈업\s*무료|[\w\s]+무료|[\w\s]+무료\s*교환)""").find(cleaned)
    if (freeMatch != null && freeMatch.value.length <= 16) return freeMatch.value.trim()

    // 5. 파이프(|)나 쉼표(,) 등으로 나뉜 경우 정리
    if ("|" in cleaned) {
        cleaned = cleaned.substringBefore("|").trim()
    }
    if ("," in cleaned && cleaned.length > 18) {
        val candidate = cleaned.substringAfter(",").trim()
        if ("할인" in candidate) cleaned = candidate
    }

    // 6. 괄호 내용 제거 (예: "(할인 한도 5천원)")
    cleaned = cleaned.replace(Regex("""\(.*?\)"""), "").trim()

    return if (cleaned.length > 18) cleaned.take(18) + "..." else cleaned
}

private fun extractPercentFromTitle(title: String?): Int? {
    if (title == null) return null
    val regex = Regex("""(\d+)%""")
    val match = regex.find(title) ?: return null
    return match.groupValues.getOrNull(1)?.toIntOrNull()
}

private fun String.toKoreanCategory(): String =
    when (uppercase()) {
        "RESTAURANT" -> "음식점"
        "CAFE" -> "카페"
        "STORE", "CONVENIENCE" -> "편의점"
        "FASTFOOD", "DESSERT" -> "디저트/패스트푸드"
        "STUDY", "EDUCATION" -> "교육/학원"
        "HEALTH", "HOSPITAL" -> "의료/건강"
        "CULTURE", "CINEMA" -> "문화/여가"
        "SHOPPING" -> "쇼핑"
        "LIFE" -> "생활/편의"
        else -> this
    }
