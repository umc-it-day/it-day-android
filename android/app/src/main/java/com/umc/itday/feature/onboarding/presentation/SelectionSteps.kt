package com.umc.itday.feature.onboarding.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.umc.itday.feature.onboarding.domain.model.PreferredBrand
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayGray300
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayMint
import com.umc.itday.ui.theme.ItDayPrimary
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun SelectionSteps(
    state: OnboardingUiState,
    onCarrierSelect: (CarrierType) -> Unit,
    onMembershipGradeSelect: (MembershipGradeType) -> Unit,
    onBrandToggle: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        val title =
            when (state.step) {
                OnboardingUiState.CARRIER_STEP -> "통신사 선택"
                OnboardingUiState.MEMBERSHIP_STEP -> "멤버십 등급 선택"
                else -> "선호 브랜드 선택"
            }

        val subtitle =
            when (state.step) {
                OnboardingUiState.CARRIER_STEP -> "사용 중인 통신사를 선택해주세요"
                OnboardingUiState.MEMBERSHIP_STEP -> "현재 멤버십 등급을 선택해주세요"
                else -> "좋아하는 브랜드를 3개 이상 선택해주세요"
            }

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191919),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = ItDayGray500,
            )

            if (state.step == OnboardingUiState.BRAND_STEP) {
                Text(
                    text = state.selectedBrandCountText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.canContinue) ItDayPrimary else Color(0xFFFF3B30),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (state.step) {
                OnboardingUiState.CARRIER_STEP -> {
                    state.availableCarriers.forEach { carrier ->
                        SelectableOptionCard(
                            title = carrier.displayName,
                            isSelected = state.selectedCarrier == carrier,
                            onClick = { onCarrierSelect(carrier) },
                        )
                    }
                }
                OnboardingUiState.MEMBERSHIP_STEP -> {
                    state.availableGrades.forEach { gradeInfo ->
                        SelectableOptionCard(
                            title = gradeInfo.type.displayName,
                            iconResId = gradeInfo.iconResId,
                            isSelected = state.selectedMembershipGrade == gradeInfo.type,
                            onClick = { onMembershipGradeSelect(gradeInfo.type) },
                        )
                    }
                }
                else -> {
                    BrandSections(
                        brands = state.availableBrands,
                        selectedBrands = state.preferredBrands,
                        onBrandToggle = onBrandToggle,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            enabled = state.canContinue && !state.isLoading && !state.isSubmitting,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = ItDayPrimary,
                    disabledContainerColor = Color(0xFFC4D8FF),
                ),
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ItDayWhite,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SelectableOptionCard(
    title: String,
    iconEmoji: String? = null,
    iconResId: Int? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (isSelected) Color(0xFFEBF2FF) else Color(0xFFF6F7F9)
    val borderColor = if (isSelected) ItDayPrimary else Color.Transparent

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(width = 1.5.dp, color = borderColor),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (iconResId != null) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                } else if (iconEmoji != null) {
                    Text(text = iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191919),
                )
            }

            if (isSelected) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ItDayPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = ItDayWhite,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandSections(
    brands: List<PreferredBrand>,
    selectedBrands: Set<String>,
    onBrandToggle: (String) -> Unit,
) {
    val groupedBrands = BrandCategory.entries.mapNotNull { category ->
        brands.filter { category.matches(it.category) }
            .takeIf(List<PreferredBrand>::isNotEmpty)
            ?.let { category to it }
    }

    groupedBrands.forEachIndexed { index, (category, categoryBrands) ->
        var expanded by rememberSaveable(category.title) { mutableStateOf(true) }

        if (index > 0) {
            Spacer(modifier = Modifier.height(16.dp))
        }
        BrandCategorySection(
            title = category.title,
            brands = categoryBrands,
            selectedBrands = selectedBrands,
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            onBrandToggle = onBrandToggle,
        )
    }
}

@Composable
private fun BrandCategorySection(
    title: String,
    brands: List<PreferredBrand>,
    selectedBrands: Set<String>,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onBrandToggle: (String) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onExpandedChange)
                .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191919),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (expanded) "▲" else "▼",
            fontSize = 12.sp,
            color = ItDayGray500,
        )
    }

    if (expanded) {
        Spacer(modifier = Modifier.height(8.dp))
        brands.chunked(3).forEach { rowBrands ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowBrands.forEach { brand ->
                    val isSelected = brand.name in selectedBrands
                    val cardBg = if (isSelected) Color(0xFFEBF2FF) else Color(0xFFF6F7F9)
                    val cardBorder = if (isSelected) ItDayPrimary else Color.Transparent

                    Card(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onBrandToggle(brand.name) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.5.dp, cardBorder),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                BrandLogo(brand = brand)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = brand.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF191919),
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ItDayPrimary,
                                    modifier = Modifier.size(16.dp).align(Alignment.TopEnd),
                                )
                            }
                        }
                    }
                }
                repeat(3 - rowBrands.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BrandLogo(brand: PreferredBrand) {
    val fallbackIcon = BrandCategory.from(brand.category).fallbackIcon
    val imageUrl = brand.imageUrl?.takeIf(String::isNotBlank)

    if (imageUrl == null) {
        Text(text = fallbackIcon, fontSize = 20.sp)
        return
    }

    AsyncImage(
        model = imageUrl,
        contentDescription = brand.name,
        modifier =
            Modifier
                .size(28.dp)
                .clip(CircleShape),
        contentScale = ContentScale.Fit,
        error = null,
        fallback = null,
    )
}

private enum class BrandCategory(
    val title: String,
    val fallbackIcon: String,
    private val keywords: Set<String>,
) {
    Cafe(
        title = "카페",
        fallbackIcon = "☕",
        keywords = setOf("cafe", "coffee", "dessert", "bakery", "카페", "커피", "디저트", "베이커리"),
    ),
    Convenience(
        title = "편의점",
        fallbackIcon = "🏪",
        keywords = setOf("convenience", "mart", "store", "편의점", "마트"),
    ),
    Food(
        title = "푸드",
        fallbackIcon = "🍽",
        keywords = setOf("food", "restaurant", "dining", "fastfood", "pizza", "chicken", "푸드", "외식", "음식", "식당", "피자", "치킨"),
    ),
    Culture(
        title = "문화",
        fallbackIcon = "🎬",
        keywords = setOf("culture", "movie", "cinema", "book", "travel", "leisure", "문화", "영화", "도서", "여행", "여가"),
    ),
    Shopping(
        title = "쇼핑",
        fallbackIcon = "🛍",
        keywords = setOf("shopping", "shop", "commerce", "fashion", "beauty", "쇼핑", "패션", "뷰티", "커머스"),
    ),
    Etc(
        title = "기타",
        fallbackIcon = "•",
        keywords = emptySet(),
    );

    fun matches(value: String): Boolean =
        if (this == Etc) {
            entries.filterNot { it == Etc }.none { it.matches(value) }
        } else {
            keywords.any { keyword -> value.contains(keyword, ignoreCase = true) }
        }

    companion object {
        fun from(value: String): BrandCategory =
            entries.firstOrNull { it != Etc && it.matches(value) } ?: Etc
    }
}
