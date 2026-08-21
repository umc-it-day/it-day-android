package com.umc.itday.feature.map.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnAttach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.umc.itday.R
import com.umc.itday.core.map.KakaoMapEnvironment
import com.umc.itday.ui.component.ItDayButton
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import kotlin.math.hypot

@Composable
fun KakaoMapView(
    isOnline: Boolean,
    initialPosition: MapCoordinate,
    currentLocation: MapCoordinate?,
    markers: List<MapMarkerUiModel>,
    routePoints: List<MapCoordinate>,
    onMarkerClick: (List<String>) -> Unit,
    onMapClick: () -> Unit,
    reloadKey: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onCameraMoveEnd: ((MapCoordinate) -> Unit)? = null,
    myLocationTrigger: Long = 0L,
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
    var hasError by remember(reloadKey) { mutableStateOf(false) }
    val mapView = remember(context, reloadKey) { MapView(context) }
    var isMapReady by remember(mapView) { mutableStateOf(false) }
    var readyMap by remember(mapView) { mutableStateOf<KakaoMap?>(null) }
    var hasMovedToInitialLocation by remember(mapView) { mutableStateOf(false) }
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnCameraMoveEnd by rememberUpdatedState(onCameraMoveEnd)
    val startMap =
        remember(mapView) {
            Runnable {
                mapView.start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() = Unit

                        override fun onMapError(error: Exception) {
                            isMapReady = false
                            hasError = true
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(kakaoMap: KakaoMap) {
                            readyMap = kakaoMap
                            kakaoMap.setOnMapClickListener { _, _, _, _ -> currentOnMapClick() }
                            kakaoMap.startMarkerClustering(
                                context = context,
                                currentLocation = currentLocation,
                                markers = markers,
                                onMarkerClick = { currentOnMarkerClick(it) },
                                onCameraMoveEnd = { currentOnCameraMoveEnd?.invoke(it) },
                            )
                            isMapReady = true
                        }

                        override fun getPosition(): LatLng = (currentLocation ?: initialPosition).toLatLng()

                        override fun getZoomLevel(): Int = DEFAULT_ZOOM_LEVEL
                    },
                )
            }
        }

    LaunchedEffect(readyMap, isMapReady, currentLocation) {
        if (isMapReady && !hasMovedToInitialLocation && currentLocation != null) {
            hasMovedToInitialLocation = true
            readyMap?.moveCamera(
                CameraUpdateFactory.newCenterPosition(currentLocation.toLatLng(), DEFAULT_ZOOM_LEVEL),
                CameraAnimation.from(300),
            )
        }
    }

    LaunchedEffect(myLocationTrigger) {
        if (myLocationTrigger > 0 && isMapReady && currentLocation != null) {
            readyMap?.moveCamera(
                CameraUpdateFactory.newCenterPosition(currentLocation.toLatLng(), DEFAULT_ZOOM_LEVEL),
                CameraAnimation.from(300),
            )
        }
    }

    LaunchedEffect(readyMap, markers, currentLocation) {
        readyMap?.let { map ->
            if (isMapReady) {
                map.startMarkerClustering(
                    context = context,
                    currentLocation = currentLocation,
                    markers = markers,
                    onMarkerClick = { currentOnMarkerClick(it) },
                    onCameraMoveEnd = { currentOnCameraMoveEnd?.invoke(it) },
                )
            }
        }
    }

    LaunchedEffect(readyMap, routePoints) {
        readyMap?.showRoute(routePoints)
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
        if (hasError) {
            MapErrorState(R.string.map_load_error, false, onRetry)
        }
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

private fun KakaoMap.startMarkerClustering(
    context: Context,
    currentLocation: MapCoordinate?,
    markers: List<MapMarkerUiModel>,
    onMarkerClick: (List<String>) -> Unit,
    onCameraMoveEnd: ((MapCoordinate) -> Unit)? = null,
) {
    val manager = labelManager ?: return
    val layer = manager.layer ?: return
    val iconCache = mutableMapOf<Int, Bitmap?>()
    val currentLocationIcon = context.markerBitmap(R.drawable.ic_home_location, 38)
    val markerIcon: (Int) -> Bitmap? = { count -> context.clusterMarkerBitmap(count) }

    fun renderMarkers() {
        layer.removeAll()
        currentLocationIcon?.let { icon ->
            val coordinate = currentLocation ?: return@let
            layer.addLabel(
                LabelOptions
                    .from(CURRENT_LOCATION_LABEL_ID, coordinate.toLatLng())
                    .setStyles(icon),
            )
        }
        cluster(markers).forEach { cluster ->
            val icon = iconCache.getOrPut(cluster.ids.size) { markerIcon(cluster.ids.size) } ?: return@forEach
            layer.addLabel(
                LabelOptions
                    .from(cluster.id, cluster.position.toLatLng())
                    .setStyles(icon)
                    .setClickable(true)
                    .setTag(cluster),
            )
        }
    }
    renderMarkers()
    setOnCameraMoveEndListener { _, cameraPosition, _ ->
        renderMarkers()
        val pos = cameraPosition.position
        if (!pos.latitude.isNaN() && !pos.longitude.isNaN()) {
            onCameraMoveEnd?.invoke(MapCoordinate(pos.latitude, pos.longitude))
        }
    }
    setOnLabelClickListener { _, _, label ->
        val cluster = label.tag as? MarkerCluster ?: return@setOnLabelClickListener false
        if (cluster.ids.size > 1) {
            val currentZoom = cameraPosition?.zoomLevel ?: DEFAULT_ZOOM_LEVEL
            moveCamera(
                CameraUpdateFactory.newCenterPosition(cluster.position.toLatLng(), (currentZoom + 2).coerceAtMost(21)),
                CameraAnimation.from(250),
            )
        }
        onMarkerClick(cluster.ids)
        true
    }
}


private fun Context.markerBitmap(resourceId: Int, sizeDp: Int): Bitmap? {
    val sizePx = (sizeDp * resources.displayMetrics.density).toInt()
    return ContextCompat.getDrawable(this, resourceId)?.toBitmap(sizePx, sizePx)
}

private fun Context.clusterMarkerBitmap(count: Int): Bitmap? {
    val density = resources.displayMetrics.density
    val width = (36 * density).toInt()
    val height = (42 * density).toInt()
    val bitmap =
        ContextCompat
            .getDrawable(this, R.drawable.ic_map_store_marker)
            ?.toBitmap(width, height)
            ?: return null
    return bitmap.copy(Bitmap.Config.ARGB_8888, true).also { result ->
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 16 * density
            typeface = Typeface.DEFAULT_BOLD
        }
        val centerY = 18 * density - (paint.ascent() + paint.descent()) / 2
        Canvas(result).drawText(count.toString(), width / 2f, centerY, paint)
    }
}

private fun KakaoMap.showRoute(points: List<MapCoordinate>) {
    val layer = routeLineManager?.layer ?: return
    layer.removeAll()
    if (points.size < 2) return
    val styles =
        RouteLineStylesSet.from(
            RouteLineStyles.from(RouteLineStyle.from(12f, AndroidColor.rgb(99, 124, 246))),
        )
    val segment = RouteLineSegment.from(points.map { it.toLatLng() }, styles.getStyles(0))
    layer.addRouteLine(RouteLineOptions.from(segment).setStylesSet(styles))
    val mapPoints = points.map { it.toLatLng() }.toTypedArray()
    val padding = (minOf(viewport.width(), viewport.height()) * ROUTE_CAMERA_PADDING_RATIO).toInt()
    moveCamera(
        CameraUpdateFactory.fitMapPoints(mapPoints, padding),
        CameraAnimation.from(ROUTE_CAMERA_ANIMATION_MS),
    )
}

private fun KakaoMap.cluster(markers: List<MapMarkerUiModel>): List<MarkerCluster> {
    if (markers.isEmpty()) return emptyList()

    val scale = mapDpScale
    val clusterRadiusPx = CLUSTER_RADIUS_DP * scale

    // 1. 모든 마커의 화면 좌표를 미리 단 한 번만 계산 (네이티브 호출 최소화)
    val markerWithPoints = markers.mapNotNull { marker ->
        val point = toScreenPoint(marker.position.toLatLng())
        // 비정상적인 좌표나 null인 경우 제외
        if (point == null) null
        else marker to point
    }

    val groups = mutableListOf<MutableList<Pair<MapMarkerUiModel, android.graphics.Point>>>()

    markerWithPoints.forEach { (marker, point) ->
        // 2. 이미 계산된 앵커 좌표를 사용하여 거리 계산 (O(N^2) 루프 내 네이티브 호출 제거)
        val group = groups.firstOrNull { existing ->
            val anchorPoint = existing.first().second
            val dx = (point.x - anchorPoint.x).toDouble()
            val dy = (point.y - anchorPoint.y).toDouble()
            hypot(dx, dy) <= clusterRadiusPx
        }

        if (group == null) {
            groups.add(mutableListOf(marker to point))
        } else {
            group.add(marker to point)
        }
    }

    return groups.map { group ->
        val groupMarkers = group.map { it.first }
        MarkerCluster(
            id = "cluster-${groupMarkers.first().id}-${groupMarkers.size}",
            ids = groupMarkers.map { it.id },
            position =
                MapCoordinate(
                    latitude = groupMarkers.map { it.position.latitude }.average(),
                    longitude = groupMarkers.map { it.position.longitude }.average(),
                ),
        )
    }
}

private data class MarkerCluster(
    val id: String,
    val ids: List<String>,
    val position: MapCoordinate,
)

private const val DEFAULT_ZOOM_LEVEL = 15
private const val CURRENT_LOCATION_LABEL_ID = "current-location"
private const val CLUSTER_RADIUS_DP = 72.0
private const val ROUTE_CAMERA_PADDING_RATIO = 0.22f
private const val ROUTE_CAMERA_ANIMATION_MS = 500
