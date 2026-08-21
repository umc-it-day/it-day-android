package com.umc.itday.feature.map.data.mapper

import com.umc.itday.feature.map.data.model.BenefitDto
import com.umc.itday.feature.map.data.model.PlaceDto
import org.junit.Assert.assertEquals
import org.junit.Test

class MapMapperTest {

    @Test
    fun `toUiModel correctly maps PlaceDto with benefits and brandImg`() {
        val dto =
            PlaceDto(
                kakaoPlaceId = "123",
                storeId = 1L,
                partnerStore = true,
                placeName = "파리바게뜨 서면점",
                brandName = "파리바게뜨",
                categoryName = "RESTAURANT",
                brandImg = "https://example.com/pb.png",
                latitude = 37.0,
                longitude = 127.0,
                distanceMeters = 100,
                benefits =
                    listOf(
                        BenefitDto(
                            benefitId = 1,
                            title = "10% 할인",
                            benefitValue = null,
                            telecom = "SKT",
                            telecomGrade = "VIP",
                        ),
                    ),
            )

        val uiModel = dto.toUiModel()

        assertEquals("1", uiModel.id)
        assertEquals("파리바게뜨 서면점", uiModel.name)
        assertEquals("음식점", uiModel.categoryName)
        assertEquals("https://example.com/pb.png", uiModel.brandImg)
        assertEquals(10, uiModel.discountPercent)
        assertEquals("10% 할인", uiModel.benefitTitle)
        assertEquals(1, uiModel.detail?.benefits?.size)
        assertEquals("SKT", uiModel.detail?.benefits?.first()?.telecom)
    }

    @Test
    fun `toUiModel cleans long benefit titles with tags for badge`() {
        val dto =
            PlaceDto(
                kakaoPlaceId = "kakao123",
                storeId = null,
                partnerStore = false,
                placeName = "공차",
                latitude = 37.0,
                longitude = 127.0,
                benefits =
                    listOf(
                        BenefitDto(
                            benefitId = 1,
                            title = "[달.달.혜택] 인기 음료 6종 50% 할인(할인 한도 5천원) [상시]제조음료 구매 시 10% 할인",
                            benefitValue = null,
                        ),
                    ),
            )

        val uiModel = dto.toUiModel()

        assertEquals("50% 할인", uiModel.benefitTitle)
        assertEquals(50, uiModel.discountPercent)
    }

    @Test
    fun `toUiModel uses kakaoPlaceId when storeId is null`() {
        val dto =
            PlaceDto(
                kakaoPlaceId = "kakao123",
                storeId = null,
                partnerStore = false,
                placeName = "카페",
                latitude = 37.0,
                longitude = 127.0,
            )

        val uiModel = dto.toUiModel()

        assertEquals("kakao123", uiModel.id)
    }

    @Test
    fun `toRoutePoints correctly converts KakaoDirectionsResponseDto to MapCoordinates`() {

        val dto =
            com.umc.itday.feature.map.data.model.KakaoDirectionsResponseDto(
                routes =
                    listOf(
                        com.umc.itday.feature.map.data.model.KakaoRouteDto(
                            sections =
                                listOf(
                                    com.umc.itday.feature.map.data.model.KakaoSectionDto(
                                        roads =
                                            listOf(
                                                com.umc.itday.feature.map.data.model.KakaoRoadDto(
                                                    vertexes = listOf(129.05, 35.15, 129.06, 35.16),
                                                ),
                                            ),
                                    ),
                                ),
                        ),
                    ),
            )

        val points = dto.toRoutePoints()

        assertEquals(2, points.size)
        assertEquals(35.15, points[0].latitude, 0.0001)
        assertEquals(129.05, points[0].longitude, 0.0001)
        assertEquals(35.16, points[1].latitude, 0.0001)
        assertEquals(129.06, points[1].longitude, 0.0001)
    }
}
