package com.example.itday.feature.map.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapViewModelTest {
    private val viewModel = MapViewModel()

    @Test
    fun `현재 위치를 찾으면 지도 중심과 매장 위치를 갱신한다`() {
        val location = MapCoordinate(35.1, 129.1)

        viewModel.onLocationFound(location)

        val state = viewModel.uiState.value
        assertEquals(location, state.mapCenter)
        assertEquals(location, state.currentLocation)
        assertFalse(state.isLocationUnavailable)
        assertEquals(1, state.reloadKey)
    }

    @Test
    fun `매장을 선택하고 닫으면 상세 선택 상태가 변경된다`() {
        val storeId = viewModel.uiState.value.stores.first().id

        viewModel.selectStore(storeId)
        assertEquals(storeId, viewModel.uiState.value.selectedStore?.id)

        viewModel.closeStore()
        assertNull(viewModel.uiState.value.selectedStore)
    }

    @Test
    fun `현재 위치가 있으면 선택한 매장까지 경로를 만든다`() {
        val location = MapCoordinate(35.1, 129.1)
        viewModel.onLocationFound(location)
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
