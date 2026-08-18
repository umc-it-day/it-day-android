package com.umc.itday.feature.barcode.presentation

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.feature.barcode.domain.repository.BarcodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BarcodeRegistrationViewModelTest {
    private lateinit var repository: FakeBarcodeRepository
    private lateinit var viewModel: BarcodeRegistrationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeBarcodeRepository()
        viewModel = BarcodeRegistrationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `barcode input keeps only first 16 digits`() {
        viewModel.onBarcodeNumberChange("1234-5678-9012-3456-789")
        assertEquals("1234567890123456", viewModel.uiState.value.barcodeNumber)
        assertTrue(viewModel.uiState.value.isValidLength)
    }

    @Test
    fun `successful registration moves to Success`() {
        viewModel.onBarcodeNumberChange("1234567890123456")
        viewModel.submitRegistration()

        assertEquals("1234567890123456", repository.registeredBarcode)
        assertEquals(BarcodeStep.Success, viewModel.uiState.value.step)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `failed registration keeps Form and exposes server message`() {
        repository.registerResult =
            ApiResult.Failure(AppError.Server(statusCode = 409, message = "이미 등록된 바코드입니다."))
        viewModel.navigateStep(BarcodeStep.Form)
        viewModel.onBarcodeNumberChange("9999999999999999")
        viewModel.submitRegistration()

        assertEquals(BarcodeStep.Form, viewModel.uiState.value.step)
        assertEquals("이미 등록된 바코드입니다.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}

private class FakeBarcodeRepository : BarcodeRepository {
    var registerResult: ApiResult<Unit> = ApiResult.Success(Unit)
    var registeredBarcode: String? = null

    override suspend fun getBarcode(): ApiResult<String> = ApiResult.Success("1234567890123456")

    override suspend fun getLottery(): ApiResult<String> = ApiResult.Success("12345678")

    override suspend fun registerBarcode(barcodeNumber: String): ApiResult<Unit> {

        registeredBarcode = barcodeNumber
        return registerResult
    }

    override suspend fun updateBarcode(barcodeNumber: String): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun recordUsage(storeId: Long): ApiResult<Unit> = ApiResult.Success(Unit)
}
