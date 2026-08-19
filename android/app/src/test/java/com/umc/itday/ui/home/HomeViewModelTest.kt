package com.umc.itday.ui.home

import com.umc.itday.core.location.LocationCoordinate
import com.umc.itday.core.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test
    fun `위치 새로고침은 강제 측정한 좌표를 상태에 반영한다`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            val repository = FakeLocationRepository()
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled, repository)

                viewModel.onAction(HomeAction.RefreshLocation)

                assertTrue(repository.forceRefreshRequested)
                assertEquals(repository.coordinate, viewModel.uiState.value.locationCoordinate)
                assertFalse(viewModel.uiState.value.isLocationRefreshing)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `바코드 활성화 액션은 활성 상태로 변경한다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled)

        viewModel.onAction(HomeAction.ActivateBarcode)

        assertEquals(MembershipState.BarcodeEnabled, viewModel.uiState.value.membershipState)
    }

    @Test
    fun `멤버십 사용 후 5분이 지나면 확인 팝업을 표시하고 닫을 수 있다`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)

                viewModel.onAction(HomeAction.UseMembership)
                assertFalse(viewModel.uiState.value.showMembershipDialog)
                advanceTimeBy(300_000)
                runCurrent()
                assertTrue(viewModel.uiState.value.showMembershipDialog)

                viewModel.onAction(HomeAction.DismissMembershipDialog)
                assertFalse(viewModel.uiState.value.showMembershipDialog)
            } finally {
                Dispatchers.resetMain()
            }
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
    fun `혜택 추가 액션은 브랜드 상세 이동 이벤트를 보낸다`() =
        runTest {
            val viewModel = HomeViewModel(HomePreviewData.noBenefits)

            viewModel.onAction(HomeAction.AddBenefit)

            assertEquals(HomeEvent.OpenBrandDetail, viewModel.events.first())
        }

    @Test
    fun `게스트 모드는 홈 상태를 게스트 디자인으로 전환한다`() {
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled)

        viewModel.setGuestMode(true)

        assertEquals(MembershipState.Guest, viewModel.uiState.value.membershipState)
        assertFalse(viewModel.uiState.value.showProSection)
    }

    @Test
    fun `AddBrandBenefit 액션은 홈 화면 혜택 목록에 해당 브랜드를 추가하고 로컬 저장소에 저장한다`() = runTest {
        val fakeLocal = FakeLocalPreferencesDataSource()
        val viewModel = HomeViewModel(HomePreviewData.noBenefits, localPreferencesDataSource = fakeLocal)
        val brand = com.umc.itday.feature.onboarding.domain.model.PreferredBrand(
            id = 100L,
            name = "스타벅스",
            imageUrl = null,
            category = "카페",
        )

        viewModel.onAction(HomeAction.AddBrandBenefit(brand))
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(1, state.benefits.size)
        assertEquals("스타벅스", state.benefits.first().brandName)
        assertEquals("사이즈업 또는 아메리카노 무료", state.benefits.first().benefitText)
        assertTrue(fakeLocal.savedBrandNames.contains("스타벅스"))
    }


    @Test
    fun `loadBarcodeAndLottery 호출 시 lotteryNum으로 바코드값을 설정하고 유저 바코드 번호를 바인딩한다`() = runTest {
        val fakeBarcodeRepo = FakeBarcodeRepository()
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled, barcodeRepository = fakeBarcodeRepo)

        viewModel.loadBarcodeAndLottery()
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("87654321", state.membership?.barcodeValue)
        assertEquals("1234567890123456", state.membership?.userBarcodeNumber)
    }
}

private class FakeBarcodeRepository : com.umc.itday.feature.barcode.domain.repository.BarcodeRepository {
    override suspend fun getBarcode(): com.umc.itday.core.data.result.ApiResult<String> =
        com.umc.itday.core.data.result.ApiResult.Success("1234567890123456")

    override suspend fun getLottery(): com.umc.itday.core.data.result.ApiResult<String> =
        com.umc.itday.core.data.result.ApiResult.Success("87654321")

    override suspend fun registerBarcode(barcodeNumber: String): com.umc.itday.core.data.result.ApiResult<Unit> =
        com.umc.itday.core.data.result.ApiResult.Success(Unit)

    override suspend fun updateBarcode(barcodeNumber: String): com.umc.itday.core.data.result.ApiResult<Unit> =
        com.umc.itday.core.data.result.ApiResult.Success(Unit)

    override suspend fun recordUsage(storeId: Long): com.umc.itday.core.data.result.ApiResult<Unit> =
        com.umc.itday.core.data.result.ApiResult.Success(Unit)
}


private class FakeLocalPreferencesDataSource : com.umc.itday.core.local.LocalPreferencesDataSource {
    val savedBrandNames = mutableSetOf<String>()
    private val _brandFlow = kotlinx.coroutines.flow.MutableStateFlow<Set<String>>(emptySet())
    override val preferredBrandNames: kotlinx.coroutines.flow.Flow<Set<String>> = _brandFlow
    override val lastAttendanceDate: kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.flowOf("")

    override val isLoggedIn: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
    override val isOnboardingCompleted: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
    override val isGuestMode: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)

    override suspend fun setLoggedIn(loggedIn: Boolean) {}
    override suspend fun setOnboardingCompleted(completed: Boolean) {}
    override suspend fun setGuestMode(enabled: Boolean) {}
    override suspend fun clearUserSessionPreferences() {}
    override suspend fun setLastAttendanceDate(date: String) {}

    override suspend fun setPreferredBrandNames(brands: Set<String>) {

        savedBrandNames.clear()
        savedBrandNames.addAll(brands)
        _brandFlow.value = savedBrandNames
    }

    override suspend fun addPreferredBrandName(brandName: String) {
        savedBrandNames.add(brandName)
        _brandFlow.value = savedBrandNames
    }
}

private class FakeLocationRepository : LocationRepository {
    val coordinate = LocationCoordinate(35.1, 129.1)
    var forceRefreshRequested = false

    override suspend fun getCurrentLocation(forceRefresh: Boolean): LocationCoordinate {
        forceRefreshRequested = forceRefresh
        return coordinate
    }

    override suspend fun getAddress(
        latitude: Double,
        longitude: Double,
    ): String = "테스트 주소"
}

