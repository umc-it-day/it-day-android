package com.example.itday.feature.benefit.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BenefitUiModelTest {
    @Test
    fun `통신사와 카테고리에 맞는 혜택만 반환한다`() {
        val result = benefitMockItems.filteredBy(CarrierUiModel.KT, "편의점")

        assertEquals(listOf("GS25"), result.map(BenefitUiModel::storeName))
    }

    @Test
    fun `전체 카테고리는 선택한 통신사의 모든 혜택을 반환한다`() {
        val result = benefitMockItems.filteredBy(CarrierUiModel.SKT, ALL_CATEGORY)

        assertEquals(2, result.size)
        assertTrue(result.all { it.carrier == CarrierUiModel.SKT })
    }

    @Test
    fun `조건에 맞는 혜택이 없으면 빈 목록을 반환한다`() {
        val result = benefitMockItems.filteredBy(CarrierUiModel.LGU_PLUS, "카페")

        assertTrue(result.isEmpty())
    }
}
