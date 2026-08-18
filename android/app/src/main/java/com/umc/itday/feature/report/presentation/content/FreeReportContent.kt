package com.umc.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.itday.R
import com.umc.itday.feature.report.presentation.component.ReportCategoryItem
import com.umc.itday.feature.report.presentation.component.ReportCategorySummaryCard
import com.umc.itday.feature.report.presentation.component.ReportTopStoreItem
import com.umc.itday.feature.report.presentation.component.ReportTopStoresSection
import com.umc.itday.ui.component.ItDayButton
import com.umc.itday.ui.component.ItDayButtonSize
import com.umc.itday.ui.component.ItDayCard
import com.umc.itday.ui.preview.ItDayComponentPreview
import com.umc.itday.ui.theme.ItDayBlue50
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun FreeReportContent(
    categories: List<ReportCategoryItem>,
    topStores: List<ReportTopStoreItem>,
    onShopClick: () -> Unit,
    onPointHistoryClick: () -> Unit,
    onAttendanceClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    floor: Int = 3,
    pointBalance: Int = 0,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .background(ItDayWhite),
    ) {
        Box(modifier = Modifier.padding(horizontal = ItDayDimens.Space24)) {
            ReportHeader(onAttendanceClick = onAttendanceClick)
        }
        FreeReportHero(
            floor = floor,
            pointBalance = pointBalance,
            onShopClick = onShopClick,
        )
        ItDayCard(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ItDayDimens.Space24)
                    .padding(top = ItDayDimens.Space24),
            backgroundColor = ReportContainerColor,
        ) {
                ReportCategorySummaryCard(
                    title = stringResource(R.string.report_free_category_title),
                    description = stringResource(R.string.report_free_category_description),
                    categories = categories,
                )
                ItDayCard(
                    modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space12),
                    onClick = onPointHistoryClick,
                    backgroundColor = ItDayBlue50,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.report_free_point_title), fontWeight = FontWeight.Bold)
                            Text(text = stringResource(R.string.report_free_point_description))
                        }
                        Image(
                            painter = painterResource(R.drawable.report_point_bell),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
                ReportTopStoresSection(
                    title = stringResource(
                        if (topStores.isEmpty()) R.string.report_free_top_empty_title else R.string.report_free_top_title,
                    ),
                    stores = topStores,
                    emptyMessage = stringResource(R.string.report_free_top_empty),
                    modifier = Modifier.padding(top = ItDayDimens.Space16),
                )
        }
    }
}

@Composable
private fun FreeReportHero(
    floor: Int,
    pointBalance: Int,
    onShopClick: () -> Unit,
) {
    val currentFloor = floor.coerceIn(1, 3)
    val towerImageRes =
        when (currentFloor) {
            1 -> R.drawable.report_tower_floor_1
            2 -> R.drawable.report_tower_floor_2
            else -> R.drawable.report_tower_floor_3
        }
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(240.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.report_free_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Image(
            painter = painterResource(towerImageRes),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomCenter).width(150.dp).height(230.dp),
            contentScale = ContentScale.Fit,
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ItDayWhite, Color.Transparent),
                        ),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, ItDayWhite),
                        ),
                    ),
        )
        Column(modifier = Modifier.padding(horizontal = ItDayDimens.Space24, vertical = ItDayDimens.Space16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "P", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(R.string.report_free_point_balance, pointBalance),
                    modifier = Modifier.padding(start = ItDayDimens.Space8),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(text = stringResource(R.string.report_free_tower), style = MaterialTheme.typography.bodySmall)
            Text(
                text = stringResource(R.string.report_free_floor, currentFloor),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            ItDayButton(
                text = stringResource(R.string.report_free_shop),
                onClick = onShopClick,
                size = ItDayButtonSize.Small,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FreeReportContentPreview() {
    ItDayComponentPreview {
        FreeReportContent(
            categories =
                listOf(
                    ReportCategoryItem("카페", 1, R.drawable.report_category_cafe),
                    ReportCategoryItem("식당", 2, R.drawable.report_category_restaurant),
                ),
            topStores = emptyList(),
            onShopClick = {},
            onPointHistoryClick = {},
        )
    }
}

private val ReportContainerColor = Color(0xFFF2F2FF)
