package com.example.itday.feature.benefit.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.component.ItDayBadge
import com.example.itday.ui.component.ItDayBadgeVariant
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayButtonSize
import com.example.itday.ui.component.ItDayButtonVariant
import com.example.itday.ui.component.ItDayCard
import com.example.itday.ui.component.ItDayFlowTopBar
import com.example.itday.ui.component.ItDayListItem
import com.example.itday.ui.component.ItDaySegmentedControl
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray50
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayTheme

@Composable
fun BenefitBrowserScreen(
    benefits: List<BenefitUiModel>,
    selectedCarrier: CarrierUiModel,
    selectedCategory: String,
    onCarrierSelect: (CarrierUiModel) -> Unit,
    onCategorySelect: (String) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
) {
    val visibleBenefits = benefits.filteredBy(selectedCarrier, selectedCategory)

    Column(modifier = modifier.fillMaxSize()) {
        ItDayFlowTopBar(title = "제휴 혜택", onBackClick = onBackClick)
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = ItDayDimens.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space16),
        ) {
            ItDaySegmentedControl(
                items = CarrierUiModel.entries.map(CarrierUiModel::label),
                selectedIndex = CarrierUiModel.entries.indexOf(selectedCarrier),
                onSelectedIndexChange = { onCarrierSelect(CarrierUiModel.entries[it]) },
            )
            CategoryFilters(selectedCategory, onCategorySelect)
            Text(
                text = "${visibleBenefits.size}개의 혜택",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (visibleBenefits.isEmpty()) {
                EmptyBenefits(onResetFilters, Modifier.weight(1f))
            } else {
                BenefitList(visibleBenefits, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space8)) {
        items(benefitCategories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelect(category) },
                label = { Text(category) },
            )
        }
    }
}

@Composable
private fun BenefitList(
    benefits: List<BenefitUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = ItDayDimens.Space24),
        verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space12),
    ) {
        items(benefits, key = BenefitUiModel::id) { benefit ->
            ItDayCard(backgroundColor = ItDayGray50) {
                ItDayListItem(
                    title = benefit.storeName,
                    description = benefit.summary,
                    leadingContent = { ItDayBadge(benefit.carrier.label, variant = ItDayBadgeVariant.Blue) },
                    trailingContent = { ItDayBadge(benefit.category, variant = ItDayBadgeVariant.Neutral) },
                )
            }
        }
    }
}

@Composable
private fun EmptyBenefits(
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("조건에 맞는 혜택이 없어요", fontWeight = FontWeight.Bold)
        Text(
            text = "다른 통신사나 카테고리를 선택해 보세요.",
            color = ItDayGray500,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = ItDayDimens.Space8, bottom = ItDayDimens.Space16),
        )
        ItDayButton(
            text = "필터 초기화",
            onClick = onResetFilters,
            variant = ItDayButtonVariant.Secondary,
            size = ItDayButtonSize.Medium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BenefitBrowserScreenPreview() {
    ItDayTheme {
        BenefitBrowserScreen(
            benefits = benefitMockItems,
            selectedCarrier = CarrierUiModel.SKT,
            selectedCategory = ALL_CATEGORY,
            onCarrierSelect = {},
            onCategorySelect = {},
            onResetFilters = {},
        )
    }
}
