package com.example.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.ui.component.ItDayFlowTopBar
import com.example.itday.ui.preview.ItDayComponentPreview
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayBlue50
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayWhite

enum class PointHistoryType { Earned, Used, Expired }

data class PointHistoryUiModel(val date: String, val title: String, val detail: String, val points: Int, val type: PointHistoryType)

@Composable
fun PointHistoryContent(
    histories: List<PointHistoryUiModel> = PointHistoryPreviewItems,
    onBackClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onSubscribeClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var newestFirst by rememberSaveable { mutableStateOf(true) }
    val tabs = listOf(R.string.point_tab_all, R.string.point_tab_used, R.string.point_tab_expired)
    val filtered = histories.filter { selectedTab == 0 || it.type == if (selectedTab == 1) PointHistoryType.Used else PointHistoryType.Expired }
    val visibleHistories = if (newestFirst) filtered.sortedByDescending { it.date } else filtered.sortedBy { it.date }

    Column(modifier = modifier.fillMaxSize().background(ItDayWhite).verticalScroll(rememberScrollState())) {
        ItDayFlowTopBar(title = stringResource(R.string.point_title), onBackClick = onBackClick)
        Column(modifier = Modifier.padding(horizontal = ItDayDimens.Space24)) {
            Text(text = stringResource(R.string.point_my_points), fontWeight = FontWeight.Bold)
            Text(text = stringResource(R.string.point_value, 0), color = ItDayBlue, style = MaterialTheme.typography.headlineMedium)
            Text(text = stringResource(R.string.point_expiring), color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space24).clip(RoundedCornerShape(16.dp)).background(ItDayBlue50).clickable(onClick = onAttendanceClick).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "✓", modifier = Modifier.clip(RoundedCornerShape(3.dp)).background(ItDayBlue).padding(horizontal = 5.dp), color = ItDayWhite)
                Text(text = stringResource(R.string.point_attendance_guide), modifier = Modifier.weight(1f).padding(start = ItDayDimens.Space12), fontWeight = FontWeight.Bold)
                Text(text = ">", color = ItDayGray500)
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16)) {
            tabs.forEachIndexed { index, tab ->
                Column(
                    modifier = Modifier.weight(1f).clickable { selectedTab = index },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = stringResource(tab), color = if (selectedTab == index) Color.Black else ItDayGray500, fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(top = ItDayDimens.Space12), thickness = if (selectedTab == index) 2.dp else 0.dp, color = Color.Black)
                }
            }
        }
        Column(modifier = Modifier.padding(horizontal = ItDayDimens.Space24, vertical = ItDayDimens.Space16)) {
            Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space8)) {
                SortChip(text = stringResource(R.string.point_sort_newest), selected = newestFirst) { newestFirst = true }
                SortChip(text = stringResource(R.string.point_sort_oldest), selected = !newestFirst) { newestFirst = false }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16).clip(RoundedCornerShape(20.dp)).background(ItDayGray100).clickable(onClick = onSubscribeClick).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.point_subscribe_title), fontWeight = FontWeight.Bold)
                    Text(text = stringResource(R.string.point_subscribe_description), color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
                }
                Image(painter = painterResource(R.drawable.pro_subscription), contentDescription = null, modifier = Modifier.size(44.dp))
            }
            visibleHistories.forEach { PointHistoryRow(it) }
        }
    }
}

@Composable
private fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(if (selected) Color.Black else ItDayWhite).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp),
        color = if (selected) ItDayWhite else Color.Black,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun PointHistoryRow(item: PointHistoryUiModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = ItDayDimens.Space16)) {
        Text(text = item.date, color = ItDayGray500, modifier = Modifier.padding(end = ItDayDimens.Space16))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, fontWeight = FontWeight.Bold)
            Text(text = item.detail, color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
        }
        Text(text = stringResource(R.string.point_value, item.points), color = if (item.type == PointHistoryType.Earned) ItDayBlue else ItDayGray500, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun PointHistoryContentPreview() {
    ItDayComponentPreview { PointHistoryContent() }
}

private val PointHistoryPreviewItems =
    listOf(
        PointHistoryUiModel("2026.04.01", "일주일 연속 출석 보상", "사용기간 : 26.01.25 - 2026.12.25", 300, PointHistoryType.Earned),
        PointHistoryUiModel("2026.02.02", "아이스 아메리카노 교환", "남은 포인트 : 0P", 3000, PointHistoryType.Used),
        PointHistoryUiModel("2026.01.25", "출석 보상", "사용기간 : 26.01.25 - 2026.12.25", 300, PointHistoryType.Expired),
    )
