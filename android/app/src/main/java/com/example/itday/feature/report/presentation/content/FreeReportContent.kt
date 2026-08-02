package com.example.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.feature.report.presentation.component.ReportCategoryItem
import com.example.itday.feature.report.presentation.component.ReportCategorySummaryCard
import com.example.itday.feature.report.presentation.component.ReportTopStoreItem
import com.example.itday.feature.report.presentation.component.ReportTopStoresSection
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayButtonSize
import com.example.itday.ui.component.ItDayCard
import com.example.itday.ui.preview.ItDayComponentPreview
import com.example.itday.ui.theme.ItDayBlue50
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100

@Composable
fun FreeReportContent(
    categories: List<ReportCategoryItem>,
    topStores: List<ReportTopStoreItem>,
    onShopClick: () -> Unit,
    onPointHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(ItDayGray100),
    ) {
        Column(modifier = Modifier.padding(horizontal = ItDayDimens.ScreenHorizontalPadding)) {
            ReportHeader()
            FreeReportHero(onShopClick = onShopClick)
            ReportCategorySummaryCard(
                title = stringResource(R.string.report_free_category_title),
                description = stringResource(R.string.report_free_category_description),
                categories = categories,
                modifier = Modifier.padding(top = ItDayDimens.Space16),
            )
            ItDayCard(
                modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space12),
                onClick = onPointHistoryClick,
                backgroundColor = ItDayBlue50,
            ) {
                Text(text = stringResource(R.string.report_free_point_title), fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.report_free_point_description))
            }
            ReportTopStoresSection(
                title = stringResource(
                    if (topStores.isEmpty()) R.string.report_free_top_empty_title else R.string.report_free_top_title,
                ),
                stores = topStores,
                emptyMessage = stringResource(R.string.report_free_top_empty),
                modifier = Modifier.padding(vertical = ItDayDimens.Space16),
            )
        }
    }
}

@Composable
private fun FreeReportHero(onShopClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = ItDayDimens.Space12)
                .clip(RoundedCornerShape(24.dp))
                .background(ItDayBlue50),
    ) {
        Column(modifier = Modifier.padding(ItDayDimens.Space16)) {
            Text(text = stringResource(R.string.report_free_tower), style = MaterialTheme.typography.bodySmall)
            Text(
                text = stringResource(R.string.report_free_floor),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            ItDayButton(
                text = stringResource(R.string.report_free_shop),
                onClick = onShopClick,
                size = ItDayButtonSize.Small,
            )
        }
        Image(
            painter = painterResource(R.drawable.report_capybara_stack),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd).size(190.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FreeReportContentPreview() {
    ItDayComponentPreview {
        FreeReportContent(
            categories = listOf(ReportCategoryItem("카페", 1), ReportCategoryItem("식당", 2)),
            topStores = emptyList(),
            onShopClick = {},
            onPointHistoryClick = {},
        )
    }
}
