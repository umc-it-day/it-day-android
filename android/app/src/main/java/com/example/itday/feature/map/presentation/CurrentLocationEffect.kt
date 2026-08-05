package com.example.itday.feature.map.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.itday.core.di.appContainer
import com.example.itday.core.permission.AppPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun CurrentLocationEffect(
    onLocationFound: (MapCoordinate) -> Unit,
    onLocationUnavailable: () -> Unit,
) {
    val context = LocalContext.current
    val permissionManager = context.appContainer.permissionManager
    val locationClient = remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    val loadCurrentLocation = {
        if (!permissionManager.isGranted(AppPermission.Location)) {
            onLocationUnavailable()
        } else {
            try {
                locationClient
                    .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location == null) {
                            onLocationUnavailable()
                        } else {
                            onLocationFound(MapCoordinate(location.latitude, location.longitude))
                        }
                    }.addOnFailureListener { onLocationUnavailable() }
            } catch (_: SecurityException) {
                onLocationUnavailable()
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
