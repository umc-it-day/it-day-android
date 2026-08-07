package com.example.itday.feature.map.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.itday.core.di.appContainer
import com.example.itday.core.permission.AppPermission
import kotlinx.coroutines.launch

@Composable
fun CurrentLocationEffect(
    onLocationFound: (MapCoordinate) -> Unit,
    onLocationUnavailable: () -> Unit,
) {
    val context = LocalContext.current
    val appContainer = context.appContainer
    val permissionManager = appContainer.permissionManager
    val coroutineScope = rememberCoroutineScope()
    val loadCurrentLocation: () -> Unit = {
        if (!permissionManager.isGranted(AppPermission.Location)) {
            onLocationUnavailable()
        } else {
            coroutineScope.launch {
                val location = appContainer.locationRepository.getCurrentLocation()
                if (location == null) {
                    onLocationUnavailable()
                } else {
                    onLocationFound(MapCoordinate(location.latitude, location.longitude))
                }
            }
        }
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            loadCurrentLocation()
        }

    LaunchedEffect(Unit) {
        if (permissionManager.isGranted(AppPermission.Location)) {
            loadCurrentLocation()
        } else {
            permissionLauncher.launch(
                permissionManager.permissionsFor(AppPermission.Location).toTypedArray(),
            )
        }
    }
}
