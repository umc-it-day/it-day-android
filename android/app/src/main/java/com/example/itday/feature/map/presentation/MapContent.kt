package com.example.itday.feature.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.core.di.appContainer

data class MapStoreUiModel(
    val id: String,
    val name: String,
    val rating: Double = UNKNOWN_VALUE,
    val distanceMeters: Int = -1,
)

@Composable
fun MapScreen() {
    val networkMonitor = LocalContext.current.appContainer.networkMonitor
    val isOnline by
        networkMonitor.isOnline.collectAsState(
            initial = networkMonitor.isCurrentlyConnected(),
        )
    var reloadKey by remember { mutableIntStateOf(0) }
    var mapCenter by remember { mutableStateOf(DefaultMapCoordinate) }
    var isLocationUnavailable by remember { mutableStateOf(false) }

    CurrentLocationEffect(
        onLocationFound = { coordinate ->
            isLocationUnavailable = false
            mapCenter = coordinate
            reloadKey++
        },
        onLocationUnavailable = { isLocationUnavailable = true },
    )

    MapContent(
        isOnline = isOnline,
        initialPosition = mapCenter,
        showLocationUnavailable = isLocationUnavailable,
        mapReloadKey = reloadKey,
        onMapRetry = { reloadKey++ },
    )
}

@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    stores: List<MapStoreUiModel> = emptyList(),
    isOnline: Boolean = true,
    initialPosition: MapCoordinate = DefaultMapCoordinate,
    showLocationUnavailable: Boolean = false,
    mapReloadKey: Int = 0,
    onMapRetry: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onStoreClick: (String) -> Unit = {},
    onDirectionsClick: (String) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize().background(MapBackground)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            KakaoMapView(
                isOnline = isOnline,
                initialPosition = initialPosition,
                reloadKey = mapReloadKey,
                onRetry = onMapRetry,
                modifier = Modifier.fillMaxSize(),
            )
            SearchBar(onClick = onSearchClick)
            if (showLocationUnavailable) {
                Text(
                    text = stringResource(R.string.map_location_unavailable),
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        StoreSheet(
            stores = stores,
            onStoreClick = onStoreClick,
            onDirectionsClick = onDirectionsClick,
        )
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "⌕", style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.map_search_hint),
            modifier = Modifier.padding(start = 10.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StoreSheet(
    stores: List<MapStoreUiModel>,
    onStoreClick: (String) -> Unit,
    onDirectionsClick: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .padding(20.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MapHandle),
        )
        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(text = stringResource(R.string.map_sort_distance), selected = true)
            FilterChip(text = stringResource(R.string.map_sort_discount), selected = false)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (stores.isEmpty()) {
            Text(
                text = stringResource(R.string.map_store_empty),
                modifier = Modifier.padding(vertical = 20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            stores.forEach { store ->
                StoreRow(store, onStoreClick, onDirectionsClick)
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean) {
    val background = if (selected) MaterialTheme.colorScheme.onSurface else Color.White
    val content = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    Text(
        text = text,
        modifier =
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(background)
                .border(1.dp, MapHandle, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        color = content,
    )
}

@Composable
private fun StoreRow(
    store: MapStoreUiModel,
    onStoreClick: (String) -> Unit,
    onDirectionsClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onStoreClick(store.id) }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(MapPlaceholder))
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(store.name, fontWeight = FontWeight.Bold)
            val details = store.detailText()
            if (details.isNotEmpty()) {
                Text(details, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            text = stringResource(R.string.map_directions),
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MapPrimary).clickable {
                onDirectionsClick(store.id)
            }.padding(horizontal = 12.dp, vertical = 9.dp),
            color = Color.White,
        )
    }
}

private fun MapStoreUiModel.detailText(): String =
    listOfNotNull(
        rating.takeIf { it >= 0 }?.let { "★ $it" },
        distanceMeters.takeIf { it >= 0 }?.let { "${it}m" },
    ).joinToString(" · ")

@Preview(showBackground = true)
@Composable
private fun MapContentPreview() {
    MapContent(stores = listOf(MapStoreUiModel(id = "preview", name = "스타벅스 송도 해수욕장점")))
}

private const val UNKNOWN_VALUE = -1.0
private val MapBackground = Color(0xFFE5E8E7)
private val MapHandle = Color(0xFFD7D9DC)
private val MapPlaceholder = Color(0xFFF1F1F1)
private val MapPrimary = Color(0xFF637CF6)
