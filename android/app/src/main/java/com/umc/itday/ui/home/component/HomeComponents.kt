@file:Suppress("TooManyFunctions")

package com.umc.itday.ui.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.umc.itday.R
import com.umc.itday.ui.component.ItDayBadge
import com.umc.itday.ui.component.ItDayBadgeVariant
import com.umc.itday.ui.component.ItDayButton
import com.umc.itday.ui.component.ItDayButtonSize
import com.umc.itday.ui.component.ItDayCard
import com.umc.itday.ui.component.ItDayMainTopBar
import com.umc.itday.ui.component.ItDaySectionHeader
import com.umc.itday.ui.home.HomeBenefitUiModel
import com.umc.itday.ui.home.HomeBrandDayUiModel
import com.umc.itday.ui.home.HomeLocationUiModel
import com.umc.itday.ui.home.HomeMembershipUiModel
import com.umc.itday.ui.home.HomePartnerBrandUiModel
import com.umc.itday.ui.theme.HomeBrandDay
import com.umc.itday.ui.theme.HomePrimary
import com.umc.itday.ui.theme.HomeProChallenge
import com.umc.itday.ui.theme.HomeProStore
import com.umc.itday.ui.theme.HomeSurface
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayGray300
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayWhite
import java.util.Locale

private const val BARCODE_UNIT_COUNT = 64f
private const val BARCODE_BAR_COUNT = 32
private const val BARCODE_WIDE_INTERVAL = 5
private const val BARCODE_WIDE_FACTOR = 1.8f
private const val BARCODE_X_FACTOR = 2f
private val ProGradientColors =
    listOf(
        Color(0xFFEF59FF),
        Color(0xFF2692F2),
        Color(0xFF29D393),
    )
private val ProBadgeRed = Color(0xFFFF514D)

@Composable
fun HomeTopBar(
    onCalendarClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ItDayMainTopBar(
        onAttendanceClick = onCalendarClick,
        onProfileClick = onProfileClick,
        modifier = modifier,
    )
}

@Composable
fun CurrentLocationRow(
    location: HomeLocationUiModel,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_home_location),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(ItDayDimens.Space8))
        Column(modifier = Modifier.weight(1f)) {
            Text(location.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(
                location.address,
                color = ItDayGray500,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        RefreshLocationButton(isRefreshing = isRefreshing, onClick = onRefresh)
    }
}

@Composable
private fun RefreshLocationButton(
    isRefreshing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(ItDayWhite)
                .border(
                    width = 1.dp,
                    color = ItDayGray300,
                    shape = CircleShape,
                ).clickable(enabled = !isRefreshing, onClick = onClick)
                .semantics { contentDescription = "현재 위치 새로고침" },
        contentAlignment = Alignment.Center,
    ) {
        if (isRefreshing) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
        } else {
            Image(
                painter = painterResource(R.drawable.ic_home_location_refresh),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun MembershipStatusCard(
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ItDayCard(modifier = modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(ItDayDimens.Space12))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(HomeSurface, RoundedCornerShape(16.dp))
                    .padding(ItDayDimens.Space16),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(description, color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(ItDayDimens.Space12))
                ItDayButton(
                    text = actionText,
                    onClick = onAction,
                    size = ItDayButtonSize.Small,
                )
            }
        }
    }
}

@Composable
fun MembershipBarcodeCard(
    membership: HomeMembershipUiModel,
    brands: List<HomePartnerBrandUiModel>,
    barcodeEnabled: Boolean,
    remainingTimeSeconds: Int?,
    onActivate: () -> Unit,
    onUse: () -> Unit,
    onBrandClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onBrandDetailClick: () -> Unit,
    onAddBrandClick: () -> Unit = onBrandDetailClick,
    onViewMapClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ItDayCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = HomeSurface,
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 32.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier =
                    if (!barcodeEnabled) {
                        Modifier.blur(12.dp, BlurredEdgeTreatment.Unbounded)
                    } else {
                        Modifier
                    },
            ) {
                Text(
                    "현재 위치에서 사용가능한 혜택이에요!",
                    color = HomePrimary,
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(Modifier.height(ItDayDimens.Space16))
                MembershipStoreHeader(
                    membership = membership,
                    brand = brands.firstOrNull { it.selected } ?: brands.firstOrNull(),
                    onClick = onBrandDetailClick,
                )
                Spacer(Modifier.height(28.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().clickable(enabled = barcodeEnabled, onClick = onUse),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MockBarcode(
                        value = membership.barcodeValue,
                        enabled = barcodeEnabled,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(ItDayDimens.Space12))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            if (barcodeEnabled) formatBarcodeNumber(membership.userBarcodeNumber) else "1234 5667 9012 3456",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(ItDayDimens.Space8))
                BarcodeMeta(
                    enabled = barcodeEnabled,
                    remainingTimeSeconds = remainingTimeSeconds,
                    onRefresh = onRefresh,
                )
            }
            if (!barcodeEnabled) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .background(HomeSurface.copy(alpha = 0.42f)),
                    contentAlignment = Alignment.Center,
                ) {
                    ItDayButton(
                        text = "눌러서 바코드 사용",
                        onClick = onActivate,
                        size = ItDayButtonSize.Small,
                    )
                }
            }
        }
        MembershipStoreFooter(
            brands = brands,
            onBrandClick = onBrandClick,
            onAddBrandClick = onAddBrandClick,
            onViewMapClick = onViewMapClick,
        )
    }
}

@Composable
private fun MembershipStoreFooter(
    brands: List<HomePartnerBrandUiModel>,
    onBrandClick: (String) -> Unit,
    onAddBrandClick: () -> Unit = {},
    onViewMapClick: () -> Unit = {},
) {
    Spacer(Modifier.height(28.dp))
    HorizontalDivider(color = ItDayGray300)
    Spacer(Modifier.height(ItDayDimens.Space16))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("이 매장이 아닌가요?", fontWeight = FontWeight.Bold)
        Text(
            "지도에서 보기",
            color = HomePrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onViewMapClick),
        )
    }
    Spacer(Modifier.height(20.dp))
    if (brands.isEmpty()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ItDayWhite)
                    .clickable(onClick = onAddBrandClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "등록된 제휴 브랜드가 없어요",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF191919),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "선호 브랜드를 추가하고 혜택을 확인해보세요",
                    color = ItDayGray500,
                    fontSize = 12.sp,
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEBF2FF),
                modifier = Modifier.clickable(onClick = onAddBrandClick),
            ) {
                Text(
                    text = "+ 추가하기",
                    color = HomePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    } else {
        PartnerBrandRow(
            brands = brands,
            onBrandClick = onBrandClick,
            onAddBrandClick = onAddBrandClick,
        )
    }
}


@Composable
private fun MembershipStoreHeader(
    membership: HomeMembershipUiModel,
    brand: HomePartnerBrandUiModel?,
    onClick: () -> Unit,
) {
    val storeName = brand?.displayName ?: membership.brandName
    val benefitDesc =
        brand?.let { com.umc.itday.core.util.BrandBenefitHelper.getBenefitSummary(it.displayName) }
            ?: membership.benefitText

    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (brand != null) {
            BrandMark(brand = brand)
            Spacer(Modifier.width(ItDayDimens.Space12))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(storeName, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(ItDayDimens.Space4))
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = ItDayGray500,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(benefitDesc, color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(ItDayDimens.Space8))
                ItDayBadge(text = "D-6", variant = ItDayBadgeVariant.Blue)
            }
        }
    }
}


@Composable
private fun BarcodeMeta(
    enabled: Boolean,
    remainingTimeSeconds: Int?,
    onRefresh: () -> Unit,
) {
    if (remainingTimeSeconds != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_home_location_refresh),
                contentDescription = "바코드 새로고침",
                modifier = Modifier.size(16.dp).clickable(onClick = onRefresh),
            )
            Spacer(Modifier.width(ItDayDimens.Space4))
            val minutes = remainingTimeSeconds / 60
            val seconds = remainingTimeSeconds % 60
            val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            Text(timeString, color = HomePrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GuestMembershipCard(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ItDayCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = HomeSurface,
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(295.dp), contentAlignment = Alignment.Center) {
            MockBarcode(
                value = "1234 5667 9012 3456",
                enabled = false,
                modifier = Modifier.fillMaxWidth().blur(14.dp, BlurredEdgeTreatment.Unbounded),
            )
            Box(Modifier.matchParentSize().background(HomeSurface.copy(alpha = 0.58f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.ic_home_guest_lock),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
                Spacer(Modifier.height(ItDayDimens.Space8))
                Text("30초만에 잇데이의 모든 기능 사용하기", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(ItDayDimens.Space8))
                Text("게스트 모드에서는 바코드 등록, 사용 기록 저장,", color = ItDayGray500)
                Text("캐릭터 성장을 이용할 수 없어요", color = ItDayGray500)
                Spacer(Modifier.height(ItDayDimens.Space16))
                ItDayButton("로그인 하기", onLogin, size = ItDayButtonSize.Small)
            }
        }
    }
}

@Composable
private fun MockBarcode(
    value: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val barcodeBitmap =
        remember(value) {
            com.umc.itday.core.util.BarcodeGenerator.generateBarcode(value)
        }

    if (barcodeBitmap != null) {
        Image(
            bitmap = barcodeBitmap,
            contentDescription = if (enabled) "멤버십 바코드 $value" else "비활성화된 멤버십 바코드",
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(72.dp),
            contentScale = ContentScale.FillBounds,
            alpha = if (enabled) 1f else 0.65f,
        )
    } else {
        Canvas(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .semantics {
                        contentDescription =
                            if (enabled) "멤버십 바코드 $value" else "비활성화된 멤버십 바코드"
                    },
        ) {
            val unit = size.width / BARCODE_UNIT_COUNT
            repeat(BARCODE_BAR_COUNT) { index ->
                val width = if (index % BARCODE_WIDE_INTERVAL == 0) unit * BARCODE_WIDE_FACTOR else unit
                val x = index * unit * BARCODE_X_FACTOR
                drawLine(
                    color = Color.Black.copy(alpha = if (enabled) 1f else 0.65f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = width,
                    cap = StrokeCap.Butt,
                )
            }
        }
    }
}

private fun formatBarcodeNumber(number: String): String {
    val clean = number.filter { it.isDigit() }
    return if (clean.length == 16) {
        clean.chunked(4).joinToString(" ")
    } else {
        number
    }
}


@Composable
fun PartnerBrandRow(
    brands: List<HomePartnerBrandUiModel>,
    onBrandClick: (String) -> Unit,
    onAddBrandClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (brands.isEmpty()) {
        EmptyHomeSection("표시할 제휴 브랜드가 없어요", modifier)
        return
    }
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(brands, key = { it.id }) { brand ->
            Column(
                modifier =
                    Modifier
                        .clickable { onBrandClick(brand.id) }
                        .padding(horizontal = ItDayDimens.Space4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BrandMark(brand)
                Spacer(Modifier.height(ItDayDimens.Space4))
                Text(
                    brand.displayName,
                    color = if (brand.selected) HomePrimary else ItDayGray500,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (brand.selected) FontWeight.Bold else FontWeight.Normal,
                )
                if (brand.selected) {
                    Spacer(Modifier.height(2.dp))
                    Box(Modifier.width(38.dp).height(2.dp).background(HomePrimary))
                }
            }
        }
        item {
            Column(
                modifier =
                    Modifier
                        .clickable(onClick = onAddBrandClick)
                        .padding(horizontal = ItDayDimens.Space4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ItDayWhite),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        color = HomePrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(ItDayDimens.Space4))
                Text(
                    "추가",
                    color = ItDayGray500,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}


@Composable
private fun BrandMark(brand: HomePartnerBrandUiModel) {
    Box(
        modifier =
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ItDayWhite),
        contentAlignment = Alignment.Center,
    ) {
        val resolvedLogoRes =
            com.umc.itday.core.util.BrandBenefitHelper.getBrandLogoRes(brand.displayName)
                ?: if (brand.logoRes != 0 && brand.logoRes != com.umc.itday.R.drawable.logo_brand_starbucks) brand.logoRes else null

        if (!brand.logoUrl.isNullOrBlank()) {
            AsyncImage(
                model = brand.logoUrl,
                contentDescription = brand.displayName,
                placeholder = painterResource(R.drawable.map_store_placeholder),
                error = painterResource(R.drawable.map_store_placeholder),
                fallback = painterResource(R.drawable.map_store_placeholder),
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Fit,
            )
        } else if (resolvedLogoRes != null) {
            Image(
                painter = painterResource(resolvedLogoRes),
                contentDescription = brand.displayName,
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Fit,
            )
        } else {
            Image(
                painter = painterResource(R.drawable.map_store_placeholder),
                contentDescription = "${brand.displayName} 이미지 로딩 실패",
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Fit,
            )
        }
    }
}



@Composable
fun CarrierComparisonBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ItDayCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundColor = HomeSurface,
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = ItDayDimens.Space16),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("통신사 바꾸면 더 받을 수도 있어요", fontWeight = FontWeight.Bold)
                Text("라이프스타일에 맞는 통신사 혜택을 추천해드려요", style = MaterialTheme.typography.bodySmall)
            }
            Image(
                painter = painterResource(R.drawable.img_home_carrier_comparison),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun MembershipBenefitSection(
    benefits: List<HomeBenefitUiModel>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAddBenefit: () -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ItDayCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = HomeSurface,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = ItDayDimens.Space24),
    ) {
        ItDaySectionHeader(
            title = "내 멤버십 혜택",
            trailingContent = {
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter =
                            painterResource(
                                if (expanded) R.drawable.ic_expand_more else R.drawable.ic_chevron_right,
                            ),
                        contentDescription = if (expanded) "멤버십 혜택 접기" else "멤버십 혜택 펼치기",
                        modifier = Modifier.size(24.dp),
                        tint = ItDayGray500,
                    )
                }
            },
        )
        Spacer(Modifier.height(ItDayDimens.Space12))
        HorizontalDivider(color = ItDayGray300)
        Spacer(Modifier.height(ItDayDimens.Space16))
        if (expanded) {
            MembershipBenefitContent(benefits, onAddBenefit, onViewAll)
        }
    }
}

@Composable
private fun MembershipBenefitContent(
    benefits: List<HomeBenefitUiModel>,
    onAddBenefit: () -> Unit,
    onViewAll: () -> Unit,
) {
    if (benefits.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space12)) {
            benefits.forEach { benefit -> MembershipBenefitItem(benefit) }
            Spacer(Modifier.height(ItDayDimens.Space4))
            ItDayButton("자세히 보기", onViewAll, modifier = Modifier.fillMaxWidth())
        }
        return
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).background(ItDayWhite, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_home_benefit_money_bag),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
            }
            Spacer(Modifier.width(ItDayDimens.Space16))
            Column {
                Text("선호 브랜드를 선택하면", fontWeight = FontWeight.Bold)
                Row {
                    Text("맞춤 혜택", color = HomePrimary, fontWeight = FontWeight.Bold)
                    Text("을 확인할 수 있어요", fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(ItDayDimens.Space16))
        ItDayButton("추가하기", onAddBenefit, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MembershipBenefitItem(
    benefit: HomeBenefitUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    when (benefit.rank) {
                        1 -> R.drawable.img_home_benefit_medal_1
                        2 -> R.drawable.img_home_benefit_medal_2
                        else -> R.drawable.img_home_benefit_medal_3
                    },
                ),
            contentDescription = "${benefit.rank}위",
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(ItDayDimens.Space8))
        Text(benefit.brandName, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        Text(benefit.benefitText, color = HomePrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MembershipBenefitHeader(
    onMyMembership: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("멤버십 혜택 모아보기", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Box(
            modifier =
                Modifier
                    .border(1.dp, ItDayGray300, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onMyMembership)
                    .padding(horizontal = ItDayDimens.Space12, vertical = ItDayDimens.Space8),
        ) {
            Text("내 멤버십", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
@Suppress("LongMethod")
fun BrandDaySection(
    brandDays: List<HomeBrandDayUiModel>,
    modifier: Modifier = Modifier,
) {
    ItDayCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = HomeSurface,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = ItDayDimens.Space24),
    ) {
        ItDaySectionHeader(
            title = "내 브랜드 데이",
            trailingContent = {
                Box(
                    modifier = Modifier.size(32.dp).background(ItDayWhite, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(brandDays.count { it.highlighted }.toString(), color = ItDayGray500)
                }
            },
        )
        Spacer(Modifier.height(ItDayDimens.Space12))
        if (brandDays.isEmpty()) {
            EmptyHomeSection("예정된 브랜드 데이가 없어요")
        } else {
            brandDays.filter { it.highlighted }.forEach { day ->
                if (day.highlighted) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(HomeBrandDay, RoundedCornerShape(16.dp))
                                .padding(ItDayDimens.Space16),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(52.dp).background(ItDayWhite, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo_brand_cu),
                                contentDescription = "CU",
                                modifier = Modifier.size(42.dp),
                                contentScale = ContentScale.Fit,
                            )
                        }
                        Spacer(Modifier.width(ItDayDimens.Space12))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(day.categoryText, color = ItDayWhite.copy(alpha = 0.72f))
                            Text(day.benefitText, color = ItDayWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            if (brandDays.any { it.highlighted } && brandDays.any { !it.highlighted }) {
                Spacer(Modifier.height(ItDayDimens.Space16))
                HorizontalDivider(color = ItDayGray300)
                Spacer(Modifier.height(ItDayDimens.Space16))
            }
            if (brandDays.any { !it.highlighted }) {
                Text("다가오는 브랜드 데이", color = ItDayGray500, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(ItDayDimens.Space8))
            }
            brandDays.filterNot { it.highlighted }.forEach { day ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrandDayDot()
                    Spacer(Modifier.width(ItDayDimens.Space12))
                    Text(
                        text = day.brandName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = day.scheduleText,
                        color = ItDayGray500,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun ItDayProSection(
    onStoreClick: () -> Unit,
    onChallengeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row {
            Text("잇데이 ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "PRO",
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(ProGradientColors),
                    ),
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(ItDayDimens.Space16))
        Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space16)) {
            ProCard(
                label = "상점",
                title = "모은 포인트로\n쿠폰 교환하기",
                symbolRes = R.drawable.img_home_pro_gift_box,
                color = HomeProStore,
                onClick = onStoreClick,
                badge = "오픈 예정",
                modifier = Modifier.weight(1f),
            )
            ProCard(
                label = "도전과제",
                title = "도전과제로 포인트\n얻기",
                symbolRes = R.drawable.img_home_pro_reward_star,
                color = HomeProChallenge,
                onClick = onChallengeClick,
                badge = "더 많은 보상",
                modifier = Modifier.weight(1f),
            )
        }

    }
}

@Composable
private fun ProCard(
    label: String,
    title: String,
    @DrawableRes symbolRes: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
) {
    Box(
        modifier = modifier.height(194.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
                    .clickable(onClick = onClick),
        ) {
            Column(Modifier.padding(ItDayDimens.Space16)) {
                Text(label, color = ItDayGray500)
                Spacer(Modifier.height(ItDayDimens.Space8))
                Text(title, fontWeight = FontWeight.Bold)
            }
            Image(
                painter = painterResource(symbolRes),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd).padding(ItDayDimens.Space16).size(48.dp),
            )
        }
        badge?.let {
            Text(
                it,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-12).dp)
                        .background(ProBadgeRed, CircleShape)
                        .padding(horizontal = ItDayDimens.Space8, vertical = ItDayDimens.Space4),
                color = ItDayWhite,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
fun HomeAdvertisement(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mint: Boolean = false,
) {
    ItDayCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        backgroundColor = if (mint) HomeProChallenge else HomeSurface,
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = ItDayDimens.Space16),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("광고", fontWeight = FontWeight.Bold)
                Text("광고 내용", color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
            }
            Box(Modifier.size(48.dp).background(ItDayWhite.copy(alpha = 0.7f)))
        }
    }
}

@Composable
fun MembershipUseDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        ItDayCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("멤버십을 사용하셨나요?", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(ItDayDimens.Space8))
                Text("사용 기록을 리포트에 반영합니다", color = ItDayGray500)
                Spacer(Modifier.height(ItDayDimens.Space24))
                Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space12)) {
                    ItDayButton(
                        "사용안함",
                        onDismiss,
                        modifier = Modifier.weight(1f),
                        variant = com.umc.itday.ui.component.ItDayButtonVariant.Secondary,
                        size = ItDayButtonSize.Small,
                    )
                    ItDayButton(
                        "사용함",
                        onConfirm,
                        modifier = Modifier.weight(1f),
                        size = ItDayButtonSize.Small,
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandDayDot(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(10.dp)) {
        drawCircle(color = HomePrimary)
    }
}

@Composable
private fun EmptyHomeSection(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = ItDayDimens.Space16),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
    }
}
