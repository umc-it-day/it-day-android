package com.example.itday.feature.onboarding.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDaySelectableCard
import com.example.itday.ui.theme.ItDayDimens

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
        when (state.step) {
            CARRIER_STEP -> Cards(CARRIERS, state.carrier, onCarrierSelect)
            MEMBERSHIP_STEP -> Cards(memberships(state.carrier), state.membership, onMembershipSelect)
            else -> BrandGrid(state.preferredBrands, onBrandToggle)
        }
        Spacer(modifier = Modifier.weight(1f))
        ItDayButton(
            text = if (state.step == BRAND_STEP) "${state.preferredBrands.size}개 선택하기" else "다음",
            enabled = state.canContinue,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().padding(bottom = ItDayDimens.Space24),
        )
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
private fun BrandGrid(selected: Set<String>, onSelect: (String) -> Unit) {
    BRANDS.chunked(3).forEach { brands ->
        Row(modifier = Modifier.fillMaxWidth()) {
            brands.forEach { brand ->
                val icon = if (brand in CONVENIENCE_BRANDS) "🏪" else "☕"
                ItDaySelectableCard(
                    selected = brand in selected,
                    onClick = { onSelect(brand) },
                    modifier = Modifier.weight(1f).padding(ItDayDimens.Space4),
                ) {
                    Text("$icon\n$brand", style = MaterialTheme.typography.bodySmall)
                }
            }
            repeat(3 - brands.size) { Spacer(Modifier.weight(1f)) }
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
private val BRANDS =
    listOf("스타벅스", "투썸", "메가커피", "컴포즈", "파스쿠찌", "이디야", "할리스", "폴 바셋") +
        CONVENIENCE_BRANDS
internal const val CARRIER_STEP = 2
internal const val MEMBERSHIP_STEP = 3
internal const val BRAND_STEP = 4
