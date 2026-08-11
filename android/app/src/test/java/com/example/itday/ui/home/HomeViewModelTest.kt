package com.example.itday.ui.home

import com.example.itday.core.location.LocationCoordinate
import com.example.itday.core.location.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `위치 새로고침은 강제 측정한 좌표를 상태에 반영한다`() = runTest(testDispatcher) {
        val repository = FakeLocationRepository()
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled, repository)

        viewModel.onAction(HomeAction.RefreshLocation)
        advanceUntilIdle()

        assertTrue(repository.forceRefreshRequested)
        assertEquals(repository.coordinate, viewModel.uiState.value.locationCoordinate)
        assertFalse(viewModel.uiState.value.isLocationRefreshing)
    }

    @Test
    fun `바코드 활성화 액션은 활성 상태로 변경한다`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled)

        viewModel.onAction(HomeAction.ActivateBarcode)
        advanceUntilIdle()

        assertEquals(MembershipState.BarcodeEnabled, viewModel.uiState.value.membershipState)
    }

    @Test
    fun `멤버십 사용 시 타이머가 동작하고 종료되면 팝업이 뜬다`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)

        viewModel.onAction(HomeAction.UseMembership)
        
        // 첫 번째 루프 실행 (300초 설정)
        advanceTimeBy(1) 
        assertEquals(300, viewModel.uiState.value.remainingTimeSeconds)
        
        // 타이머 종료 시점까지 시간 진행 (300초 * 1000ms = 300,000ms)
        advanceTimeBy(300_000) 
        
        assertTrue(viewModel.uiState.value.showMembershipDialog)
        assertEquals(null, viewModel.uiState.value.remainingTimeSeconds)
    }

    @Test
    fun `브랜드 선택은 하나의 브랜드만 선택한다`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)

        viewModel.onAction(HomeAction.SelectPartnerBrand("cu"))
        advanceUntilIdle()

        val selected = viewModel.uiState.value.partnerBrands.filter { it.selected }
        assertEquals(listOf("cu"), selected.map { it.id })
    }

    @Test
    fun `혜택 토글은 펼침 상태를 반전한다`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(HomePreviewData.barcodeEnabled)
        val initial = viewModel.uiState.value.isBenefitExpanded

        viewModel.onAction(HomeAction.ToggleBenefits)
        advanceUntilIdle()

        assertEquals(!initial, viewModel.uiState.value.isBenefitExpanded)
    }

    @Test
    fun `혜택 추가 액션은 제휴 매장 상세 이벤트를 보낸다`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(HomePreviewData.noBenefits)
        
        val events = mutableListOf<HomeEvent>()
        val job = launch {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.onAction(HomeAction.AddBenefit)
        advanceUntilIdle()

        assertTrue(events.contains(HomeEvent.OpenBrandDetail))
        job.cancel()
    }

    @Test
    fun `게스트 모드는 홈 상태를 게스트 디자인으로 전환한다`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(HomePreviewData.barcodeDisabled)

        viewModel.setGuestMode(true)
        advanceUntilIdle()

        assertEquals(MembershipState.Guest, viewModel.uiState.value.membershipState)
        assertFalse(viewModel.uiState.value.showProSection)
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
