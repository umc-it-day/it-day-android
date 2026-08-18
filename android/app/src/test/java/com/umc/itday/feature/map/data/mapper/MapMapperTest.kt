package com.umc.itday.feature.map.data.mapper

import com.umc.itday.feature.map.data.model.BenefitDto
import com.umc.itday.feature.map.data.model.PlaceDto
import org.junit.Assert.assertEquals
import org.junit.Test

class MapMapperTest {

    @Test
    fun `toUiModel correctly maps PlaceDto with benefits`() {
        val dto = PlaceDto(
            kakaoPlaceId = "123",
            storeId = 1L,
            partnerStore = true,
            placeName = "스타벅스",
            latitude = 37.0,
            longitude = 127.0,
            distanceMeters = 100,
            benefits = listOf(
                BenefitDto(
                    benefitId = 1,
                    title = "VIP 15%",
                    benefitValue = 15,
                    description = "Description"
                )
            )
        )

        val uiModel = dto.toUiModel()

        assertEquals("1", uiModel.id)
        assertEquals("스타벅스", uiModel.name)
        assertEquals(37.0, uiModel.position.latitude, 0.0)
        assertEquals(127.0, uiModel.position.longitude, 0.0)
        assertEquals(100, uiModel.distanceMeters)
        assertEquals(15, uiModel.discountPercent)
        assertEquals("VIP 15%", uiModel.detail?.benefit)
        assertEquals("Description", uiModel.detail?.benefitDescription)
    }

    @Test
    fun `toUiModel uses kakaoPlaceId when storeId is null`() {
        val dto = PlaceDto(
            kakaoPlaceId = "kakao123",
            storeId = null,
            partnerStore = false,
            placeName = "카페",
            latitude = 37.0,
            longitude = 127.0
        )

        val uiModel = dto.toUiModel()

        assertEquals("kakao123", uiModel.id)
    }
}

