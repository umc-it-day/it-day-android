package com.umc.itday.feature.map.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MapSortOption { DISTANCE, DISCOUNT }

data class MapUiState(
    val mapCenter: MapCoordinate = DefaultMapCoordinate,
    val currentLocation: MapCoordinate? = null,
    val stores: List<MapStoreUiModel> = previewStoresAround(DefaultMapCoordinate),
    val routePoints: List<MapCoordinate> = emptyList(),
    val selectedStoreId: String? = null,
    val sortOption: MapSortOption = MapSortOption.DISTANCE,
    val isLocationUnavailable: Boolean = false,
    val isLoading: Boolean = true,
    val reloadKey: Int = 0,
) {
    val selectedStore: MapStoreUiModel?
        get() = stores.firstOrNull { it.id == selectedStoreId }

    val markers: List<MapMarkerUiModel>
        get() = stores.map { MapMarkerUiModel(it.id, it.position) }

    val sortedStores: List<MapStoreUiModel>
        get() =
            when (sortOption) {
                MapSortOption.DISTANCE ->
                    stores.sortedBy {
                        it.distanceMeters.takeIf { value -> value >= 0 } ?: Int.MAX_VALUE
                    }
                MapSortOption.DISCOUNT -> stores.sortedByDescending { it.discountPercent }
            }
}

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun onLocationFound(coordinate: MapCoordinate) {
        _uiState.update { state ->
            state.copy(
                mapCenter = coordinate,
                currentLocation = coordinate,
                stores = previewStoresAround(coordinate),
                isLocationUnavailable = false,
                isLoading = false,
                reloadKey = state.reloadKey + 1,
            )
        }
    }

    fun onLocationUnavailable() {
        _uiState.update { it.copy(isLocationUnavailable = true, isLoading = false) }
    }

    fun retryMap() {
        _uiState.update { it.copy(reloadKey = it.reloadKey + 1) }
    }

    fun selectStore(storeId: String) {
        _uiState.update { it.copy(selectedStoreId = storeId) }
    }

    fun closeStore() {
        _uiState.update { it.copy(selectedStoreId = null) }
    }

    fun selectSort(option: MapSortOption) {
        _uiState.update { it.copy(sortOption = option) }
    }

    fun startDirections(storeId: String) {
        _uiState.update { state ->
            val start = state.currentLocation ?: return@update state
            val destination = state.stores.firstOrNull { it.id == storeId }?.position
                ?: return@update state
            state.copy(routePoints = listOf(start, destination))
        }
    }

    fun cancelDirections() {
        _uiState.update { it.copy(routePoints = emptyList()) }
    }
}
