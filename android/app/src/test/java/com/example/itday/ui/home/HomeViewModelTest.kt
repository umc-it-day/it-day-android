package com.example.itday.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun `바코드 활성화 액션은 활성 상태로 변경한다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled)

        viewModel.onAction(HomeAction.ActivateBarcode)

        assertEquals(MembershipState.BarcodeEnabled, viewModel.uiState.value.membershipState)
    }

    @Test
    fun `멤버십 사용 액션은 확인 팝업을 표시하고 닫을 수 있다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)

        viewModel.onAction(HomeAction.UseMembership)
        assertTrue(viewModel.uiState.value.showMembershipDialog)

        viewModel.onAction(HomeAction.DismissMembershipDialog)
        assertFalse(viewModel.uiState.value.showMembershipDialog)
    }

    @Test
    fun `브랜드 선택은 하나의 브랜드만 선택한다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)

        viewModel.onAction(HomeAction.SelectPartnerBrand("cu"))

        val selected =
            viewModel.uiState.value.partnerBrands
                .filter { it.selected }
        assertEquals(listOf("cu"), selected.map { it.id })
    }

    @Test
    fun `혜택 토글은 펼침 상태를 반전한다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)
        val initial = viewModel.uiState.value.isBenefitExpanded

        viewModel.onAction(HomeAction.ToggleBenefits)

        assertEquals(!initial, viewModel.uiState.value.isBenefitExpanded)
    }

    @Test
    fun `혜택 추가 액션은 mock 선호 브랜드 혜택을 표시한다`() {
        val viewModel = HomeViewModel(HomePreviewData.noBenefits)

        viewModel.onAction(HomeAction.AddBenefit)

        assertEquals(3, viewModel.uiState.value.benefits.size)
    }

    @Test
    fun `게스트 모드는 홈 상태를 게스트 디자인으로 전환한다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled)

        viewModel.setGuestMode(true)

        assertEquals(MembershipState.Guest, viewModel.uiState.value.membershipState)
        assertFalse(viewModel.uiState.value.showProSection)
    }
}
