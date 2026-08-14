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
