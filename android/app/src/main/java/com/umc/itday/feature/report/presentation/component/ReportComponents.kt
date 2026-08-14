package com.umc.itday.feature.report.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.itday.ui.component.ItDayCard
import com.umc.itday.ui.component.ItDaySectionHeader
import com.umc.itday.ui.preview.ItDayComponentPreview
import com.umc.itday.ui.theme.ItDayBlue
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayGray500

data class ReportCategoryItem(
    val label: String,
    val count: Int,
    val iconRes: Int? = null,
)

data class ReportTopStoreItem(
    val name: String,
    val totalCount: Int,
    val monthlyCount: Int,
)

@Composable
fun ReportCategorySummaryCard(
    title: String,
    description: String,
    categories: List<ReportCategoryItem>,
    modifier: Modifier = Modifier,
) {
    val highestCount = categories.maxOfOrNull { it.count } ?: 0
    ItDayCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = ItDayBlue,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = ItDayDimens.Space8),
            color = ItDayGray500,
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            categories.forEach { category ->
                val isMostUsed = highestCount > 0 && category.count == highestCount
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${category.count}회",
                        color = if (isMostUsed) Color.Black else ItDayGray500,
                        fontWeight = if (isMostUsed) FontWeight.Bold else FontWeight.Normal,
                    )
                    category.iconRes?.let { iconRes ->
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = category.label,
                            modifier = Modifier.size(48.dp).alpha(if (isMostUsed) 1f else 0.25f),
                        )
                    }
                    Text(
                        text = category.label,
                        color = if (isMostUsed) Color.Black else ItDayGray500,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isMostUsed) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
fun ReportTopStoresSection(
    title: String,
    stores: List<ReportTopStoreItem>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ItDaySectionHeader(title = title)
        ItDayCard(
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space8),
        ) {
            if (stores.isEmpty()) {
                Text(text = emptyMessage, fontWeight = FontWeight.Bold)
            } else {
                stores.forEachIndexed { index, store ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = ItDayDimens.Space4),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${index + 1}  ${store.name}",
                            color = ItDayBlue,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "총 ${store.totalCount}회 · 이번달 ${store.monthlyCount}회",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportAchievementRow(
    title: String,
    description: String,
    rewardText: String,
    progress: Float,
    progressText: String? = null,
    iconText: String? = null,
    rewardEnabled: Boolean = progress < 1f,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = ItDayDimens.Space12),
        horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        iconText?.let { Text(text = it, style = MaterialTheme.typography.titleLarge) }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = description, color = ItDayBlue, style = MaterialTheme.typography.bodySmall)
            if (progress > 0f) {
                Box(
                    modifier =
                        Modifier
                            .padding(top = ItDayDimens.Space8)
                            .width(182.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(ItDayGray100),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(4.dp)
                                .background(ItDayBlue),
                    )
                }
                progressText?.let {
                    Text(text = it, color = ItDayGray500, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Text(
            text = rewardText,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (rewardEnabled) ItDayBlue else ItDayGray100)
                    .padding(horizontal = ItDayDimens.Space12, vertical = ItDayDimens.Space8),
            color = if (rewardEnabled) Color.White else ItDayGray500,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportComponentsPreview() {
    ItDayComponentPreview {
        Column(verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space16)) {
            ReportCategorySummaryCard(
                title = "가장 많은 할인을 받은 카테고리",
                description = "최근 6개월간의 이용 내역을 분석했어요",
                categories = listOf(ReportCategoryItem("카페", 3), ReportCategoryItem("식당", 10)),
            )
            ReportTopStoresSection(
                title = "사용 TOP3 매장",
                stores = listOf(ReportTopStoreItem("스타벅스", 32, 5)),
                emptyMessage = "아직 사용 기록이 없습니다.",
            )
            ReportAchievementRow(
                title = "할인 총 15회 달성",
                description = "이번달 할인을 20번 이용하세요",
                rewardText = "P 400",
                progress = 0.75f,
            )
        }
    }
}
