package com.umc.itday.feature.map.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.itday.core.di.appContainer

@Composable
fun MapScreen(
    initialQuery: String? = null,
) {
    val context = LocalContext.current
    val container = context.appContainer
    val viewModel: MapViewModel = viewModel(
        factory = MapViewModel.Factory(container.mapRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val networkMonitor = container.networkMonitor
    val isOnline by
        networkMonitor.isOnline.collectAsState(
            initial = networkMonitor.isCurrentlyConnected(),
        )

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            viewModel.searchByKeyword(initialQuery)
        }
    }

    CurrentLocationEffect(
        onLocationFound = { loc ->
            if (initialQuery.isNullOrBlank()) {
                viewModel.onLocationFound(loc)
            }
        },
        onLocationUnavailable = viewModel::onLocationUnavailable,
    )


    Box(modifier = Modifier.fillMaxSize()) {
        MapContent(
            stores = uiState.sortedStores,
            isOnline = isOnline,
            initialPosition = uiState.mapCenter,
            currentLocation = uiState.currentLocation,
            markers = uiState.markers,
            routePoints = uiState.routePoints,
            selectedStore = uiState.selectedStore,
            isClusterFiltered = uiState.filteredClusterStoreIds != null,
            onClearClusterFilter = viewModel::clearClusterFilter,
            showLocationUnavailable = uiState.isLocationUnavailable,
            showResearchButton = uiState.showResearchButton,
            mapReloadKey = uiState.reloadKey,
            myLocationTrigger = uiState.myLocationTrigger,
            onMyLocationClick = viewModel::moveToMyLocation,
            onMapRetry = viewModel::retryMap,
            onResearchClick = viewModel::searchCurrentLocation,
            onCameraMoveEnd = viewModel::onCameraMoved,
            onMarkerClick = viewModel::selectCluster,
            onDirectionsClick = viewModel::startDirections,
            onDirectionsCancel = viewModel::cancelDirections,
            onStoreClick = viewModel::selectStore,
            onStoreClose = viewModel::closeStore,
            sortOption = uiState.sortOption,
            onSortClick = viewModel::selectSort,
        )



        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

