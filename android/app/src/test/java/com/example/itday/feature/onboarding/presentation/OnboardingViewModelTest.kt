package com.example.itday.feature.onboarding.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        viewModel = OnboardingViewModel()
    }

    @Test
    fun `initial state has terms step and cannot continue without mandatory agreements`() {
        val state = viewModel.uiState.value
        assertEquals(0, state.step)
        assertFalse(state.canContinue)
    }

    @Test
    fun `when mandatory terms agreed canContinue becomes true`() {
        viewModel.setAgreement(AgreementType.Location, true)
        viewModel.setAgreement(AgreementType.Privacy, true)

        val state = viewModel.uiState.value
        assertTrue(state.canContinue)
    }

    @Test
    fun `selecting carrier updates selectedCarrier state`() {
        viewModel.selectCarrier(CarrierType.KT)

        val state = viewModel.uiState.value
        assertEquals(CarrierType.KT, state.selectedCarrier)
    }

    @Test
    fun `selecting membership grade updates selectedMembershipGrade state`() {
        viewModel.selectMembershipGrade(MembershipGradeType.GOLD)

        val state = viewModel.uiState.value
        assertEquals(MembershipGradeType.GOLD, state.selectedMembershipGrade)
    }

    @Test
    fun `brand selection requires 3 or more items to continue`() {
        viewModel.setAgreement(AgreementType.Location, true)
        viewModel.setAgreement(AgreementType.Privacy, true)
        viewModel.next() // Location permission step (1)
        viewModel.next() // Carrier step (2)
        viewModel.next() // Membership step (3)
        viewModel.next() // Brand step (4)

        assertEquals(OnboardingUiState.BRAND_STEP, viewModel.uiState.value.step)
        assertFalse(viewModel.uiState.value.canContinue)

        viewModel.toggleBrand("CU")
        viewModel.toggleBrand("GS25")
        assertFalse(viewModel.uiState.value.canContinue)

        viewModel.toggleBrand("스타벅스")
        assertTrue(viewModel.uiState.value.canContinue)
        assertEquals("선택 완료", viewModel.uiState.value.selectedBrandCountText)
    }
}
