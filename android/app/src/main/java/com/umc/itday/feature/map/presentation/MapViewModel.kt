package com.umc.itday.feature.map.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.feature.map.domain.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MapSortOption { DISTANCE, DISCOUNT }

data class MapUiState(
    val mapCenter: MapCoordinate = DefaultMapCoordinate,
    val currentCameraCenter: MapCoordinate = DefaultMapCoordinate,
    val lastSearchedCoordinate: MapCoordinate? = null,
    val showResearchButton: Boolean = false,
    val currentLocation: MapCoordinate? = null,
    val stores: List<MapStoreUiModel> = emptyList(),
    val routePoints: List<MapCoordinate> = emptyList(),
    val selectedStoreId: String? = null,
    val filteredClusterStoreIds: List<String>? = null,
    val sortOption: MapSortOption = MapSortOption.DISTANCE,
    val isLocationUnavailable: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val reloadKey: Int = 0,
    val myLocationTrigger: Long = 0L,
) {

    val selectedStore: MapStoreUiModel?
        get() = stores.firstOrNull { it.id == selectedStoreId }

    val markers: List<MapMarkerUiModel>
        get() = stores.map { MapMarkerUiModel(it.id, it.position) }

    val displayedStores: List<MapStoreUiModel>
        get() {
            val list =
                if (filteredClusterStoreIds != null) {
                    stores.filter { it.id in filteredClusterStoreIds }
                } else {
                    stores
                }
            return when (sortOption) {
                MapSortOption.DISTANCE ->
                    list.sortedBy {
                        it.distanceMeters.takeIf { value -> value >= 0 } ?: Int.MAX_VALUE
                    }
                MapSortOption.DISCOUNT -> list.sortedByDescending { it.discountPercent }
            }
        }

    val sortedStores: List<MapStoreUiModel>
        get() = displayedStores
}


class MapViewModel(
    private val mapRepository: MapRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun onLocationFound(coordinate: MapCoordinate) {
        _uiState.update { state ->
            state.copy(
                mapCenter = coordinate,
                currentCameraCenter = coordinate,
                lastSearchedCoordinate = coordinate,
                showResearchButton = false,
                currentLocation = coordinate,
                isLocationUnavailable = false,
            )
        }
        fetchNearbyStores(coordinate)
    }

    fun onCameraMoved(coordinate: MapCoordinate) {
        _uiState.update { state ->
            val last = state.lastSearchedCoordinate
            val hasMovedSignificantly =
                last == null ||
                    kotlin.math.hypot(
                        coordinate.latitude - last.latitude,
                        coordinate.longitude - last.longitude,
                    ) >= MIN_SEARCH_MOVE_DELTA

            state.copy(
                currentCameraCenter = coordinate,
                showResearchButton = hasMovedSignificantly,
            )
        }
    }

    fun searchCurrentLocation() {
        val currentCenter = _uiState.value.currentCameraCenter
        Log.i(TAG, "Re-searching stores at coordinate: (latitude=${currentCenter.latitude}, longitude=${currentCenter.longitude})")
        _uiState.update { it.copy(showResearchButton = false, lastSearchedCoordinate = currentCenter) }
        fetchNearbyStores(currentCenter)
    }

    private fun fetchNearbyStores(coordinate: MapCoordinate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = mapRepository.getNearbyStores(coordinate.latitude, coordinate.longitude)) {
                is ApiResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            stores = result.data,
                            lastSearchedCoordinate = coordinate,
                            showResearchButton = false,
                            isLoading = false,
                        )
                    }
                }
                is ApiResult.Failure -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage(),
                        )
                    }
                }
            }

        }
    }

    fun onLocationUnavailable() {
        _uiState.update { it.copy(isLocationUnavailable = true, isLoading = false) }
    }

    fun retryMap() {
        val currentCenter = _uiState.value.currentCameraCenter
        fetchNearbyStores(currentCenter)
    }

    fun moveToMyLocation() {
        val currentLoc = _uiState.value.currentLocation ?: return
        _uiState.update { state ->
            state.copy(
                myLocationTrigger = state.myLocationTrigger + 1,
                currentCameraCenter = currentLoc,
                showResearchButton = false,
            )
        }
        fetchNearbyStores(currentLoc)
    }


    private companion object {
        const val TAG = "MapViewModel"
        const val MIN_SEARCH_MOVE_DELTA = 0.002 // 약 200m 이상 이동 시 재검색 버튼 노출
    }

    fun selectStore(storeId: String) {
        _uiState.update { it.copy(selectedStoreId = storeId) }
    }

    fun selectCluster(storeIds: List<String>) {
        if (storeIds.isEmpty()) return
        if (storeIds.size == 1) {
            _uiState.update { it.copy(selectedStoreId = storeIds.first(), filteredClusterStoreIds = null) }
        } else {
            _uiState.update { it.copy(selectedStoreId = null, filteredClusterStoreIds = storeIds) }
        }
    }

    fun clearClusterFilter() {
        _uiState.update { it.copy(filteredClusterStoreIds = null) }
    }

    fun closeStore() {
        _uiState.update { it.copy(selectedStoreId = null) }
    }


    fun selectSort(option: MapSortOption) {
        _uiState.update { it.copy(sortOption = option) }
    }

    fun startDirections(storeId: String) {
        val start = _uiState.value.currentLocation ?: return
        val destination =
            _uiState.value.stores.firstOrNull { it.id == storeId }?.position
                ?: return

        _uiState.update { it.copy(routePoints = listOf(start, destination)) }

        viewModelScope.launch {
            when (val result = mapRepository.getDirections(start.latitude, start.longitude, destination.latitude, destination.longitude)) {
                is ApiResult.Success -> {
                    if (result.data.isNotEmpty()) {
                        _uiState.update { it.copy(routePoints = result.data) }
                    }
                }
                is ApiResult.Failure -> Unit
            }
        }
    }


    fun cancelDirections() {
        _uiState.update { it.copy(routePoints = emptyList()) }
    }

    class Factory(
        private val mapRepository: MapRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MapViewModel(mapRepository) as T
    }
}

