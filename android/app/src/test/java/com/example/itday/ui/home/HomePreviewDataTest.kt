package com.example.itday.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomePreviewDataTest {
    @Test
    fun `활성 바코드 상태는 멤버십과 혜택을 제공한다`() {
        val state = HomePreviewData.barcodeEnabled

        assertEquals(MembershipState.BarcodeEnabled, state.membershipState)
        assertNotNull(state.membership)
        assertTrue(state.benefits.isNotEmpty())
        assertTrue(state.partnerBrands.isNotEmpty())
        assertTrue(state.brandDays.isNotEmpty())
    }

    @Test
    fun `미등록 상태는 멤버십과 혜택을 제공하지 않는다`() {
        val state = HomePreviewData.notRegistered

        assertEquals(MembershipState.NotRegistered, state.membershipState)
        assertNull(state.membership)
        assertTrue(state.benefits.isEmpty())
    }

    @Test
    fun `빈 상태는 홈 목록을 비운다`() {
        val state = HomePreviewData.empty

        assertTrue(state.partnerBrands.isEmpty())
        assertTrue(state.brandDays.isEmpty())
        assertTrue(state.benefits.isEmpty())
    }

    @Test
    fun `혜택 상태는 0개 1개 여러 개를 제공한다`() {
        assertTrue(HomePreviewData.noBenefits.benefits.isEmpty())
        assertEquals(1, HomePreviewData.singleBenefit.benefits.size)
        assertTrue(HomePreviewData.barcodeEnabled.benefits.size > 1)
    }

    @Test
    fun `팝업 상태만 확인 다이얼로그를 노출한다`() {
        assertTrue(HomePreviewData.dialogVisible.showMembershipDialog)
        assertFalse(HomePreviewData.barcodeEnabled.showMembershipDialog)
    }

    @Test
    fun `게스트 상태는 프로 섹션을 숨긴다`() {
        assertFalse(HomePreviewData.guest.showProSection)
    }
}
