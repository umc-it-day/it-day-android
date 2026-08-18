package com.umc.itday.feature.map.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.itday.R
import kotlinx.coroutines.launch

data class MapStoreUiModel(
    val id: String,
    val name: String,
    val position: MapCoordinate,
    val rating: Double = UNKNOWN_VALUE,
    val distanceMeters: Int = -1,
    val discountPercent: Int = -1,
    val detail: MapStoreDetailUiModel? = null,
)

data class MapStoreDetailUiModel(
    val benefit: String,
    val benefitDescription: String,
    val productSaving: String,
    val monthlySaving: String,
    val address: String,
    val businessHours: String,
    val phoneNumber: String,
)

data class MapMarkerUiModel(
    val id: String,
    val position: MapCoordinate,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MapContent(
    modifier: Modifier = Modifier,
    stores: List<MapStoreUiModel> = emptyList(),
    isOnline: Boolean = true,
    initialPosition: MapCoordinate = DefaultMapCoordinate,
    currentLocation: MapCoordinate? = null,
    markers: List<MapMarkerUiModel> = emptyList(),
    routePoints: List<MapCoordinate> = emptyList(),
    selectedStore: MapStoreUiModel? = null,
    showLocationUnavailable: Boolean = false,
    mapReloadKey: Int = 0,
    onMapRetry: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onStoreClick: (String) -> Unit = {},
    onDirectionsClick: (String) -> Unit = {},
    onDirectionsCancel: () -> Unit = {},
    onStoreClose: () -> Unit = {},
    sortOption: MapSortOption = MapSortOption.DISTANCE,
    onSortClick: (MapSortOption) -> Unit = {},
) {
    val sheetState =
        rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true,
        )
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(selectedStore?.id) {
        if (selectedStore != null) sheetState.expand()
    }
    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberBottomSheetScaffoldState(sheetState),
        sheetPeekHeight = 112.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = { SheetDragHandle() },
        sheetContent = {
            val directions: (String) -> Unit = { storeId ->
                if (currentLocation != null) {
                    onDirectionsClick(storeId)
                    coroutineScope.launch { sheetState.partialExpand() }
                }
            }
            if (selectedStore == null) {
                StoreSheet(stores, sortOption, onSortClick, onStoreClick, directions)
            } else {
                StoreDetailSheet(selectedStore, directions, onStoreClose)
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize().background(MapBackground)) {
            KakaoMapView(
                isOnline = isOnline,
                initialPosition = initialPosition,
                currentLocation = currentLocation,
                markers = markers,
                routePoints = routePoints,
                onMarkerClick = onStoreClick,
                onMapClick = {
                    onStoreClose()
                    coroutineScope.launch { sheetState.partialExpand() }
                },
                reloadKey = mapReloadKey,
                onRetry = onMapRetry,
                modifier = Modifier.fillMaxSize(),
            )
            SearchBar(onClick = onSearchClick)
            if (routePoints.isNotEmpty()) {
                RouteCancelButton(
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 84.dp, end = 20.dp),
                    onClick = onDirectionsCancel,
                )
            }
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
    }
}

@Composable
private fun RouteCancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.map_directions_cancel),
        modifier =
            modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MapPrimary)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        color = Color.White,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SheetDragHandle() {
    Box(
        modifier =
            Modifier
                .padding(top = 12.dp)
                .size(width = 40.dp, height = 4.dp)
                .clip(CircleShape)
                .background(MapHandle),
    )
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
    sortOption: MapSortOption,
    onSortClick: (MapSortOption) -> Unit,
    onStoreClick: (String) -> Unit,
    onDirectionsClick: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 420.dp)
                .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(stringResource(R.string.map_sort_distance), sortOption == MapSortOption.DISTANCE) {
                onSortClick(MapSortOption.DISTANCE)
            }
            FilterChip(stringResource(R.string.map_sort_discount), sortOption == MapSortOption.DISCOUNT) {
                onSortClick(MapSortOption.DISCOUNT)
            }
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
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) MaterialTheme.colorScheme.onSurface else Color.White
    val content = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    Text(
        text = text,
        modifier =
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(background)
                .border(1.dp, MapHandle, RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
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
        StoreIdentity(store = store, modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.map_directions),
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MapPrimary).clickable {
                onDirectionsClick(store.id)
            }.padding(horizontal = 12.dp, vertical = 9.dp),
            color = Color.White,
        )
    }
}

@Composable
private fun StoreIdentity(
    store: MapStoreUiModel,
    modifier: Modifier = Modifier,
    imageSize: Int = 56,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.map_store_placeholder),
            contentDescription = null,
            modifier = Modifier.size(imageSize.dp).clip(CircleShape).background(MapPlaceholder),
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(store.name, fontWeight = FontWeight.Bold)
            val details = store.detailText()
            if (details.isNotEmpty()) {
                Text(details, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = store.discountText(),
                modifier =
                    Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MapDiscountBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                color = MapPrimary,
            )
        }
    }
}

@Composable
private fun StoreDetailSheet(
    store: MapStoreUiModel,
    onDirectionsClick: (String) -> Unit,
    onClose: () -> Unit,
) {
    val detail = store.detail ?: return
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 620.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            StoreIdentity(store, Modifier.weight(1f), imageSize = 68)
            DetailAction("→", MapPrimary) { onDirectionsClick(store.id) }
            Spacer(Modifier.size(8.dp))
            DetailAction("×", MapHandle, onClose)
        }
        BenefitCard(detail)
        SavingCard(detail)
        Text(stringResource(R.string.map_store_information), fontWeight = FontWeight.Bold)
        Text("⌖  ${detail.address}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("◷  ${detail.businessHours}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("☎  ${detail.phoneNumber}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MapPlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.map_mini_map_placeholder))
        }
    }
}

@Composable
private fun DetailAction(
    text: String,
    background: Color,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(onClick = onClick)
                .wrapContentSize(Alignment.Center),
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun BenefitCard(detail: MapStoreDetailUiModel) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MapDiscountBackground).padding(18.dp)) {
        Text(stringResource(R.string.map_vip_benefit), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(detail.benefit, color = MapPrimary, fontWeight = FontWeight.Bold)
        Text(detail.benefitDescription, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SavingCard(detail: MapStoreDetailUiModel) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MapPlaceholder).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column {
            Text(stringResource(R.string.map_product_basis))
            Text(detail.productSaving, fontWeight = FontWeight.Bold)
        }
        Column {
            Text(stringResource(R.string.map_monthly_here))
            Text(detail.monthlySaving, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MapStoreUiModel.detailText(): String {
    val ratingText = rating.takeIf { it >= 0 }?.let { "★ $it" } ?: stringResource(R.string.map_value_unknown)
    val distanceText =
        distanceMeters.takeIf { it >= 0 }?.let { "${it}m 거리" }
            ?: stringResource(R.string.map_distance_unknown)
    return "$ratingText · $distanceText"
}

@Composable
private fun MapStoreUiModel.discountText(): String =
    if (discountPercent >= 0) {
        stringResource(R.string.map_discount_percent, discountPercent)
    } else {
        stringResource(R.string.map_discount_unknown)
    }

@Preview(showBackground = true)
@Composable
private fun MapContentPreview() {
    MapContent(stores = previewStoresAround(DefaultMapCoordinate))
}

private const val UNKNOWN_VALUE = -1.0

private val PreviewStoreDetail =
    MapStoreDetailUiModel(
        benefit = "15% 할인",
        benefitDescription = "전 메뉴 대상 · 최대 3,000원",
        productSaving = "약 690원 절약",
        monthlySaving = "최대 9,000원 절약",
        address = "부산 서구 송도해변로 171",
        businessHours = "매일 7:00 - 22:00",
        phoneNumber = "051-123-4567",
    )

internal fun previewStoresAround(center: MapCoordinate): List<MapStoreUiModel> =
    listOf(
        MapStoreUiModel(
            "preview-1",
            "임시 매장 1",
            MapCoordinate(center.latitude + 0.002, center.longitude - 0.001),
            detail = PreviewStoreDetail,
        ),
        MapStoreUiModel(
            "preview-2",
            "임시 매장 2",
            MapCoordinate(center.latitude - 0.001, center.longitude + 0.002),
            detail = PreviewStoreDetail,
        ),
        MapStoreUiModel(
            "preview-3",
            "임시 매장 3",
            MapCoordinate(center.latitude + 0.001, center.longitude + 0.003),
            detail = PreviewStoreDetail,
        ),
    )

private val MapBackground = Color(0xFFE5E8E7)
private val MapHandle = Color(0xFFD7D9DC)
private val MapPlaceholder = Color(0xFFF1F1F1)
private val MapPrimary = Color(0xFF637CF6)
private val MapDiscountBackground = Color(0xFFEEF2FF)
