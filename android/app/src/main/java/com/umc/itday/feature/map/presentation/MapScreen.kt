package com.umc.itday.feature.map.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.itday.core.di.appContainer

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val networkMonitor = LocalContext.current.appContainer.networkMonitor
    val isOnline by
        networkMonitor.isOnline.collectAsState(
            initial = networkMonitor.isCurrentlyConnected(),
        )

    CurrentLocationEffect(
        onLocationFound = viewModel::onLocationFound,
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
            showLocationUnavailable = uiState.isLocationUnavailable,
            mapReloadKey = uiState.reloadKey,
            onMapRetry = viewModel::retryMap,
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
