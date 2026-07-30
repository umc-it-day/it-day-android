package com.example.itday.feature.onboarding.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayButtonSize
import com.example.itday.ui.component.ItDayButtonVariant
import com.example.itday.ui.component.ItDaySelectableCard
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayMint

@Composable
fun SelectionSteps(
    state: OnboardingUiState,
    onCarrierSelect: (String) -> Unit,
    onMembershipSelect: (String) -> Unit,
    onBrandToggle: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title =
        when (state.step) {
            CARRIER_STEP -> "통신사 선택"
            MEMBERSHIP_STEP -> "멤버십 등급 선택"
            else -> "좋아하는 브랜드를\n3개 선택해주세요"
        }
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = ItDayDimens.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space12),
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(description(state.step), style = MaterialTheme.typography.bodyMedium)
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space12),
        ) {
            when (state.step) {
                CARRIER_STEP -> Cards(CARRIERS, state.carrier, onCarrierSelect)
                MEMBERSHIP_STEP -> Cards(memberships(state.carrier), state.membership, onMembershipSelect)
                else -> BrandSections(state.preferredBrands, onBrandToggle)
            }
        }
        ItDayButton(
            text = "다음",
            enabled = state.canContinue,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.step == BRAND_STEP) {
            ItDayButton(
                text = "건너뛰기",
                variant = ItDayButtonVariant.Text,
                size = ItDayButtonSize.Small,
                onClick = onNext,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = ItDayDimens.Space12),
            )
        } else {
            Spacer(modifier = Modifier.padding(bottom = ItDayDimens.Space12))
        }
    }
}

@Composable
private fun Cards(options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    options.forEach { option ->
        ItDaySelectableCard(
            selected = option == selected,
            onClick = { onSelect(option) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(option, fontWeight = FontWeight.Bold)
                    if (options !== CARRIERS) {
                        Text("멤버십 혜택을 받을 수 있는 등급이에요.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (options !== CARRIERS) RadioButton(option == selected, { onSelect(option) })
            }
        }
    }
}

@Composable
private fun BrandSections(selected: Set<String>, onSelect: (String) -> Unit) {
    var cafeExpanded by rememberSaveable { mutableStateOf(true) }
    var convenienceExpanded by rememberSaveable { mutableStateOf(true) }

    BrandSection(
        title = "카페",
        brands = CAFE_BRANDS,
        selected = selected,
        expanded = cafeExpanded,
        onExpandedChange = { cafeExpanded = !cafeExpanded },
        onSelect = onSelect,
    )
    BrandSection(
        title = "편의점",
        brands = CONVENIENCE_BRANDS.toList(),
        selected = selected,
        expanded = convenienceExpanded,
        onExpandedChange = { convenienceExpanded = !convenienceExpanded },
        onSelect = onSelect,
    )
}

@Composable
private fun BrandSection(
    title: String,
    brands: List<String>,
    selected: Set<String>,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandedChange)
                .padding(vertical = ItDayDimens.Space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (expanded) "⌃" else "⌄",
            style = MaterialTheme.typography.titleMedium,
        )
    }
    if (expanded) {
        BrandGrid(brands, selected, onSelect)
    }
}

@Composable
private fun BrandGrid(
    brands: List<String>,
    selected: Set<String>,
    onSelect: (String) -> Unit,
) {
    brands.chunked(3).forEach { rowBrands ->
        Row(modifier = Modifier.fillMaxWidth()) {
            rowBrands.forEach { brand ->
                val icon = if (brand in CONVENIENCE_BRANDS) "🏪" else "☕"
                ItDaySelectableCard(
                    selected = brand in selected,
                    onClick = { onSelect(brand) },
                    modifier = Modifier.weight(1f).padding(ItDayDimens.Space4),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text("$icon\n$brand", style = MaterialTheme.typography.bodySmall)
                        if (brand in selected) {
                            Text(
                                text = "✓",
                                color = ItDayMint,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.TopEnd),
                            )
                        }
                    }
                }
            }
            repeat(3 - rowBrands.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

private fun description(step: Int) =
    when (step) {
        CARRIER_STEP -> "사용 중인 통신사를 선택해주세요"
        MEMBERSHIP_STEP -> "현재 멤버십 등급을 선택해주세요"
        else -> "선택한 브랜드를 기반으로 맞춤 혜택을 추천해드릴게요!"
    }

private fun memberships(carrier: String?) =
    when (carrier) {
        "SKT" -> listOf("VIP", "GOLD", "SILVER")
        "KT" -> listOf("VVIP", "VIP", "GOLD", "SILVER", "WHITE", "일반")
        else -> listOf("VVIP", "VIP", "DIAMOND", "GOLD", "일반")
    }

private val CARRIERS = listOf("SKT", "KT", "LG U+")
private val CONVENIENCE_BRANDS = setOf("CU", "GS25", "세븐일레븐", "이마트24", "미니스톱")
private val CAFE_BRANDS =
    listOf("스타벅스", "투썸", "메가커피", "컴포즈", "파스쿠찌", "이디야", "할리스", "폴 바셋")
internal const val CARRIER_STEP = 2
internal const val MEMBERSHIP_STEP = 3
internal const val BRAND_STEP = 4
