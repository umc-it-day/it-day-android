package com.umc.itday.feature.map.presentation

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.feature.map.domain.repository.MapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepository = FakeMapRepository()
    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MapViewModel(mapRepository = fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `현재 위치를 찾으면 지도 중심과 매장 위치를 갱신한다`() = runTest {
        val location = MapCoordinate(35.1, 129.1)

        viewModel.onLocationFound(location)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(location, state.mapCenter)
        assertEquals(location, state.currentLocation)
        assertFalse(state.isLocationUnavailable)
        assertEquals(1, state.stores.size)
    }

    @Test
    fun `매장을 선택하고 닫으면 상세 선택 상태가 변경된다`() = runTest {
        val location = MapCoordinate(35.1, 129.1)
        viewModel.onLocationFound(location)
        runCurrent()

        val storeId = viewModel.uiState.value.stores.first().id

        viewModel.selectStore(storeId)
        assertEquals(storeId, viewModel.uiState.value.selectedStore?.id)

        viewModel.closeStore()
        assertNull(viewModel.uiState.value.selectedStore)
    }

    @Test
    fun `현재 위치가 있으면 선택한 매장까지 경로를 만든다`() = runTest {
        val location = MapCoordinate(35.1, 129.1)
        viewModel.onLocationFound(location)
        runCurrent()

        val store = viewModel.uiState.value.stores.first()

        viewModel.startDirections(store.id)

        assertEquals(listOf(location, store.position), viewModel.uiState.value.routePoints)
        viewModel.cancelDirections()
        assertTrue(viewModel.uiState.value.routePoints.isEmpty())
    }

    @Test
    fun `할인율순을 선택하면 정렬 상태가 변경된다`() {
        viewModel.selectSort(MapSortOption.DISCOUNT)

        assertEquals(MapSortOption.DISCOUNT, viewModel.uiState.value.sortOption)
    }
}

private class FakeMapRepository : MapRepository {
    val dummyStores =
        listOf(
            MapStoreUiModel(
                id = "1",
                name = "스타벅스",
                position = MapCoordinate(35.1, 129.1),
                distanceMeters = 100,
                discountPercent = 10,
            ),
        )

    override suspend fun searchPlaces(query: String): ApiResult<List<MapStoreUiModel>> =
        ApiResult.Success(dummyStores)

    override suspend fun getNearbyStores(latitude: Double, longitude: Double): ApiResult<List<MapStoreUiModel>> =
        ApiResult.Success(dummyStores)

    override suspend fun getStoreDetail(storeId: Long): ApiResult<MapStoreUiModel> =
        ApiResult.Success(dummyStores.first())
}


