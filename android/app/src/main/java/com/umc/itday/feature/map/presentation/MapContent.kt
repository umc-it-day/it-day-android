package com.umc.itday.feature.map.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.umc.itday.BuildConfig
import com.umc.itday.R

import kotlinx.coroutines.launch
import androidx.core.net.toUri


data class MapBenefitUiModel(
    val id: Long,
    val title: String,
    val description: String? = null,
    val telecom: String? = null,
    val telecomGrade: String? = null,
)

data class MapStoreUiModel(
    val id: String,
    val name: String,
    val position: MapCoordinate = DefaultMapCoordinate,
    val brandName: String? = null,
    val categoryName: String? = null,
    val brandImg: String? = null,
    val rating: Double = UNKNOWN_VALUE,
    val distanceMeters: Int = -1,
    val discountPercent: Int = -1,
    val benefitTitle: String = "",
    val detail: MapStoreDetailUiModel? = null,
)

data class MapStoreDetailUiModel(
    val benefit: String,
    val benefitDescription: String,
    val productSaving: String = "",
    val monthlySaving: String = "",
    val address: String = "",
    val businessHours: String = "",
    val phoneNumber: String = "",
    val placeUrl: String? = null,
    val benefits: List<MapBenefitUiModel> = emptyList(),
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
    isClusterFiltered: Boolean = false,
    onClearClusterFilter: () -> Unit = {},
    showLocationUnavailable: Boolean = false,
    showResearchButton: Boolean = false,
    mapReloadKey: Int = 0,
    myLocationTrigger: Long = 0L,
    onMyLocationClick: () -> Unit = {},
    onMapRetry: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onResearchClick: () -> Unit = {},
    onCameraMoveEnd: (MapCoordinate) -> Unit = {},
    onMarkerClick: (List<String>) -> Unit = {},
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
    LaunchedEffect(selectedStore?.id, isClusterFiltered) {
        if (selectedStore != null || isClusterFiltered) sheetState.expand()
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
                StoreSheet(
                    stores = stores,
                    sortOption = sortOption,
                    isClusterFiltered = isClusterFiltered,
                    onClearClusterFilter = onClearClusterFilter,
                    onSortClick = onSortClick,
                    onStoreClick = onStoreClick,
                    onDirectionsClick = directions,
                )
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
                onMarkerClick = onMarkerClick,
                onMapClick = {
                    onStoreClose()
                    onClearClusterFilter()
                    coroutineScope.launch { sheetState.partialExpand() }
                },
                reloadKey = mapReloadKey,
                onRetry = onMapRetry,
                onCameraMoveEnd = onCameraMoveEnd,
                myLocationTrigger = myLocationTrigger,
                modifier = Modifier.fillMaxSize(),
            )
            SearchBar(onClick = onSearchClick)
            if (showResearchButton && routePoints.isEmpty()) {
                ResearchButton(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 76.dp),
                    onClick = onResearchClick,
                )
            }
            MyLocationButton(
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 124.dp, end = 16.dp),
                onClick = onMyLocationClick,
            )
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
private fun ResearchButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, MapHandle, RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "↻",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MapPrimary,
        )
        Text(
            text = stringResource(R.string.map_research_here),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MapPrimary,
        )
    }
}

@Composable
private fun MyLocationButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, MapHandle, CircleShape)
                .clickable(onClick = onClick)
                .padding(11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_my_location),
            contentDescription = "내 위치",
            modifier = Modifier.size(24.dp),
        )
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
    isClusterFiltered: Boolean,
    onClearClusterFilter: () -> Unit,
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
        if (isClusterFiltered) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "이 위치의 매장 (${stores.size}개)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "전체 목록 ✕",
                    fontSize = 13.sp,
                    color = MapPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onClearClusterFilter() }.padding(4.dp),
                )
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(stringResource(R.string.map_sort_distance), sortOption == MapSortOption.DISTANCE) {
                    onSortClick(MapSortOption.DISTANCE)
                }
                FilterChip(stringResource(R.string.map_sort_discount), sortOption == MapSortOption.DISCOUNT) {
                    onSortClick(MapSortOption.DISCOUNT)
                }
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
        if (!store.brandImg.isNullOrBlank()) {
            AsyncImage(
                model = store.brandImg,
                contentDescription = store.name,
                placeholder = painterResource(R.drawable.map_store_placeholder),
                error = painterResource(R.drawable.map_store_placeholder),
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier
                        .size(imageSize.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, MapHandle, CircleShape)
                        .padding(4.dp),
            )
        } else {
            Image(
                painter = painterResource(R.drawable.map_store_placeholder),
                contentDescription = null,
                modifier = Modifier.size(imageSize.dp).clip(CircleShape).background(MapPlaceholder),
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = store.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val details = store.detailText()
            if (details.isNotEmpty()) {
                Text(
                    text = details,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
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
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                .heightIn(min = 520.dp)
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

        StoreInfoSection(store = store)
    }
}

@Composable
private fun StoreInfoSection(
    store: MapStoreUiModel,
    modifier: Modifier = Modifier,
) {
    val detail = store.detail ?: return
    val context = LocalContext.current
    val lat = store.position.latitude
    val lng = store.position.longitude
    val staticMapUrl = "https://dapi.kakao.com/v2/maps/staticmap?center=$lng,$lat&level=3&size=320x260&markers=type:default|pos:$lng,$lat"
    val kakaoMapWebUrl =
        store.detail.placeUrl?.takeIf { it.isNotBlank() }
            ?: "https://map.kakao.com/link/map/${Uri.encode(store.name)},$lat,$lng"

    val imageRequest =
        remember(staticMapUrl, BuildConfig.KAKAO_REST_API_KEY) {
            ImageRequest.Builder(context)
                .data(staticMapUrl)
                .apply {
                    val restKey = BuildConfig.KAKAO_REST_API_KEY
                    if (restKey.isNotBlank()) {
                        httpHeaders(
                            NetworkHeaders.Builder()
                                .set("Authorization", "KakaoAK $restKey")
                                .build(),
                        )
                    }
                }
                .crossfade(true)
                .build()
        }

    val openKakaoMap = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kakaoMapWebUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "지도를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.map_store_information),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color(0xFF1E293B),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (detail.address.isNotBlank()) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    clipboard?.setPrimaryClip(ClipData.newPlainText("Store Address", detail.address))
                                    Toast.makeText(context, "주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_store_pin),
                            contentDescription = "주소",
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = detail.address,
                            color = Color(0xFF475569),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (detail.businessHours.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_store_clock),
                            contentDescription = "영업시간",
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = detail.businessHours,
                            color = Color(0xFF475569),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (detail.phoneNumber.isNotBlank()) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    try {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${detail.phoneNumber}"))
                                        context.startActivity(dialIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "전화 앱을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_store_phone),
                            contentDescription = "전화번호",
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = detail.phoneNumber,
                            color = Color(0xFF475569),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }

            Box(
                modifier =
                    Modifier
                        .size(width = 110.dp, height = 96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MapPlaceholder)
                        .clickable(onClick = openKakaoMap),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = "${store.name} 위치 지도",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.map_store_placeholder),
                    error = painterResource(R.drawable.map_store_placeholder),
                    modifier = Modifier.fillMaxSize(),
                )
            }
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
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MapDiscountBackground)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("통신사 멤버십 혜택", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        if (detail.benefits.isNotEmpty()) {
            detail.benefits.take(5).forEach { benefit ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val telecomLabel = listOfNotNull(benefit.telecom, benefit.telecomGrade).joinToString(" ")
                    if (telecomLabel.isNotBlank()) {
                        Text(
                            text = telecomLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MapPrimary,
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                        )
                    }
                    Text(
                        text = benefit.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        } else {
            Text(detail.benefit, color = MapPrimary, fontWeight = FontWeight.Bold)
            if (detail.benefitDescription.isNotBlank()) {
                Text(detail.benefitDescription, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
private fun MapStoreUiModel.detailText(): String {
    val category = categoryName ?: ""
    val distance =
        when {
            distanceMeters < 0 -> ""
            distanceMeters < 1000 -> "${distanceMeters}m"
            else -> String.format("%.1fkm", distanceMeters / 1000.0)
        }
    return listOf(category, distance).filter { it.isNotBlank() }.joinToString(" • ")
}

@Composable
private fun MapStoreUiModel.discountText(): String =
    when {
        benefitTitle.isNotBlank() -> benefitTitle
        discountPercent > 0 -> stringResource(R.string.map_discount_percent, discountPercent)
        !detail?.benefit.isNullOrBlank() -> detail!!.benefit
        else -> stringResource(R.string.map_discount_unknown)
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


