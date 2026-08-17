package com.example.itday.feature.barcode.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BarcodeRegistrationViewModelTest {
    private lateinit var viewModel: BarcodeRegistrationViewModel

    @Before
    fun setUp() {
        viewModel = BarcodeRegistrationViewModel()
    }

    @Test
    fun `initial state is Intro with SKT and GOLD defaults`() {
        val state = viewModel.uiState.value
        assertEquals(BarcodeStep.Intro, state.step)
        assertEquals(Carrier.SKT, state.selectedCarrier)
        assertEquals(MembershipGrade.GOLD, state.selectedGrade)
        assertEquals("", state.barcodeNumber)
        assertFalse(state.isValidLength)
    }

    @Test
    fun `selectCarrier updates selectedCarrier in state`() {
        viewModel.selectCarrier(Carrier.LGU_PLUS)
        assertEquals(Carrier.LGU_PLUS, viewModel.uiState.value.selectedCarrier)
    }

    @Test
    fun `selectGrade updates selectedGrade in state`() {
        viewModel.selectGrade(MembershipGrade.VIP)
        assertEquals(MembershipGrade.VIP, viewModel.uiState.value.selectedGrade)
    }

    @Test
    fun `onBarcodeNumberChange filters non-digit characters and limits to 16 digits`() {
        viewModel.onBarcodeNumberChange("1234-5678-9012-3456-789")
        assertEquals("1234567890123456", viewModel.uiState.value.barcodeNumber)
        assertTrue(viewModel.uiState.value.isValidLength)
    }

    @Test
    fun `submitRegistration transitions to Duplicate step when 16 Nines entered`() {
        viewModel.onBarcodeNumberChange("9999999999999999")
        viewModel.submitRegistration()
        assertEquals(BarcodeStep.Duplicate, viewModel.uiState.value.step)
    }

    @Test
    fun `submitRegistration transitions to Success step when normal 16 digits entered`() {
        viewModel.onBarcodeNumberChange("1234567890123456")
        viewModel.submitRegistration()
        assertEquals(BarcodeStep.Success, viewModel.uiState.value.step)
    }

    @Test
    fun `resetFormToReentry resets barcodeNumber and navigates to Form step`() {
        viewModel.onBarcodeNumberChange("9999999999999999")
        viewModel.submitRegistration()
        assertEquals(BarcodeStep.Duplicate, viewModel.uiState.value.step)

        viewModel.resetFormToReentry()
        assertEquals(BarcodeStep.Form, viewModel.uiState.value.step)
        assertEquals("", viewModel.uiState.value.barcodeNumber)
    }

    @Test
    fun `submitRegistration with memberRepository calls registerBarcode and transitions to Success`() =
        kotlinx.coroutines.test.runTest {
            val fakeMemberRepo =
                object : com.example.itday.feature.member.domain.repository.MemberRepository {
                    override suspend fun getProfile(): com.example.itday.core.data.result.ApiResult<com.example.itday.feature.member.domain.model.MemberProfile> =
                        com.example.itday.core.data.result.ApiResult.Success(com.example.itday.feature.member.domain.model.MemberProfile("", "", ""))

                    override suspend fun updateName(name: String): com.example.itday.core.data.result.ApiResult<Unit> =
                        com.example.itday.core.data.result.ApiResult.Success(Unit)

                    override suspend fun getMembership(): com.example.itday.core.data.result.ApiResult<com.example.itday.feature.member.domain.model.MemberMembership> =
                        com.example.itday.core.data.result.ApiResult.Success(com.example.itday.feature.member.domain.model.MemberMembership("", ""))

                    override suspend fun updateMembership(membershipId: Long): com.example.itday.core.data.result.ApiResult<Unit> =
                        com.example.itday.core.data.result.ApiResult.Success(Unit)

                    override suspend fun getBarcode(): com.example.itday.core.data.result.ApiResult<com.example.itday.feature.member.domain.model.MemberBarcode> =
                        com.example.itday.core.data.result.ApiResult.Success(com.example.itday.feature.member.domain.model.MemberBarcode(""))

                    override suspend fun registerBarcode(barcodeNum: String): com.example.itday.core.data.result.ApiResult<String> =
                        com.example.itday.core.data.result.ApiResult.Success("성공")

                    override suspend fun updateBarcode(barcodeNum: String): com.example.itday.core.data.result.ApiResult<Unit> =
                        com.example.itday.core.data.result.ApiResult.Success(Unit)

                    override suspend fun recordBarcodeUsage(storeId: Long): com.example.itday.core.data.result.ApiResult<String> =
                        com.example.itday.core.data.result.ApiResult.Success("성공")

                    override suspend fun getLottery(): com.example.itday.core.data.result.ApiResult<com.example.itday.feature.member.domain.model.MemberLottery> =
                        com.example.itday.core.data.result.ApiResult.Success(com.example.itday.feature.member.domain.model.MemberLottery(""))

                    override suspend fun withdraw(): com.example.itday.core.data.result.ApiResult<Unit> =
                        com.example.itday.core.data.result.ApiResult.Success(Unit)
                }

            val repoViewModel = BarcodeRegistrationViewModel(memberRepository = fakeMemberRepo)
            repoViewModel.onBarcodeNumberChange("1234567890123456")
            repoViewModel.submitRegistration()

            assertEquals(BarcodeStep.Success, repoViewModel.uiState.value.step)
        }
}

