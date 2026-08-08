package com.example.itday.feature.onboarding.presentation

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray300
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayMint
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayWhite

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
                    CarrierType.entries.forEach { carrier ->
                        SelectableOptionCard(
                            title = carrier.displayName,
                            isSelected = state.selectedCarrier == carrier,
                            onClick = { onCarrierSelect(carrier) },
                        )
                    }
                }
                OnboardingUiState.MEMBERSHIP_STEP -> {
                    state.selectedCarrier?.availableGrades?.forEach { gradeInfo ->
                        SelectableOptionCard(
                            title = gradeInfo.type.displayName,
                            iconResId = gradeInfo.iconResId,
                            isSelected = state.selectedMembershipGrade == gradeInfo.type,
                            onClick = { onMembershipGradeSelect(gradeInfo.type) },
                        )
                    }
                }
                else -> {
                    BrandSections(state.preferredBrands, onBrandToggle)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            enabled = state.canContinue,
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
    selectedBrands: Set<String>,
    onBrandToggle: (String) -> Unit,
) {
    var cafeExpanded by rememberSaveable { mutableStateOf(true) }
    var convenienceExpanded by rememberSaveable { mutableStateOf(true) }

    BrandCategorySection(
        title = "카페",
        brands = CAFE_BRANDS,
        selectedBrands = selectedBrands,
        expanded = cafeExpanded,
        onExpandedChange = { cafeExpanded = !cafeExpanded },
        onBrandToggle = onBrandToggle,
    )

    Spacer(modifier = Modifier.height(16.dp))

    BrandCategorySection(
        title = "편의점",
        brands = CONVENIENCE_BRANDS.toList(),
        selectedBrands = selectedBrands,
        expanded = convenienceExpanded,
        onExpandedChange = { convenienceExpanded = !convenienceExpanded },
        onBrandToggle = onBrandToggle,
    )
}

@Composable
private fun BrandCategorySection(
    title: String,
    brands: List<String>,
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
                    val isSelected = brand in selectedBrands
                    val icon = if (brand in CONVENIENCE_BRANDS) "🏪" else "☕"
                    val cardBg = if (isSelected) Color(0xFFEBF2FF) else Color(0xFFF6F7F9)
                    val cardBorder = if (isSelected) ItDayPrimary else Color.Transparent

                    Card(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onBrandToggle(brand) },
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
                                Text(text = icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = brand,
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

private val CONVENIENCE_BRANDS = setOf("CU", "GS25", "세븐일레븐", "이마트24", "미니스톱")
private val CAFE_BRANDS =
    listOf("스타벅스", "투썸", "메가커피", "컴포즈", "파스쿠찌", "이디야", "할리스", "폴 바셋")
