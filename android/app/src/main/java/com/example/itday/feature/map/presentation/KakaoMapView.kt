package com.example.itday.feature.map.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnAttach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.itday.R
import com.example.itday.core.map.KakaoMapEnvironment
import com.example.itday.ui.component.ItDayButton
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

@Composable
fun KakaoMapView(
    isOnline: Boolean,
    initialPosition: MapCoordinate,
    reloadKey: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!KakaoMapEnvironment.isSupportedDevice) {
        MapStatusText(R.string.map_device_not_supported)
        return
    }
    var isWaitingForNetwork by remember { mutableStateOf(false) }
    LaunchedEffect(isOnline) {
        if (isOnline) isWaitingForNetwork = false
    }
    if (!isOnline) {
        MapErrorState(
            textRes = R.string.map_network_error,
            isRetrying = isWaitingForNetwork,
            onRetry = {
                isWaitingForNetwork = true
                onRetry()
            },
        )
        return
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var isLoading by remember(reloadKey) { mutableStateOf(true) }
    var hasError by remember(reloadKey) { mutableStateOf(false) }
    val mapView = remember(context, reloadKey, initialPosition) { MapView(context) }
    var isMapReady by remember(mapView) { mutableStateOf(false) }
    val startMap =
        remember(mapView, initialPosition) {
            Runnable {
                mapView.start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() = Unit

                        override fun onMapError(error: Exception) {
                            isMapReady = false
                            isLoading = false
                            hasError = true
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(kakaoMap: KakaoMap) {
                            isMapReady = true
                            isLoading = false
                        }

                        override fun getPosition(): LatLng = initialPosition.toLatLng()

                        override fun getZoomLevel(): Int = DEFAULT_ZOOM_LEVEL
                    },
                )
            }
        }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> if (isMapReady) mapView.resume()
                    Lifecycle.Event.ON_PAUSE -> if (isMapReady) mapView.pause()
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (isMapReady) mapView.finish()
        }
    }

    Box(modifier = modifier) {
        key(mapView) {
            AndroidView(
                factory = {
                    mapView.apply {
                        doOnAttach { startMap.run() }
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        when {
            hasError -> MapErrorState(R.string.map_load_error, false, onRetry)
            isLoading -> MapLoadingOverlay()
        }
    }
}

@Composable
private fun MapLoadingOverlay() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.itday_logo),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        CircularProgressIndicator()
    }
}

@Composable
private fun MapErrorState(
    textRes: Int,
    isRetrying: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(textRes), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        ItDayButton(
            text =
                stringResource(
                    if (isRetrying) R.string.map_checking_network else R.string.map_retry,
                ),
            onClick = onRetry,
            enabled = !isRetrying,
            leadingIcon =
                if (isRetrying) {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                } else {
                    null
                },
        )
    }
}

@Composable
private fun MapStatusText(textRes: Int) {
    Text(
        text = stringResource(textRes),
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .wrapContentSize(Alignment.Center),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

data class MapCoordinate(
    val latitude: Double,
    val longitude: Double,
)

val DefaultMapCoordinate = MapCoordinate(latitude = 37.385, longitude = 126.645)

private fun MapCoordinate.toLatLng(): LatLng = LatLng.from(latitude, longitude)

private const val DEFAULT_ZOOM_LEVEL = 15
