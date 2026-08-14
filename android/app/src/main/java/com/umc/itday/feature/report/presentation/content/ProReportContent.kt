package com.umc.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.itday.R
import com.umc.itday.feature.report.presentation.component.ReportAchievementRow
import com.umc.itday.ui.component.ItDayCard
import com.umc.itday.ui.preview.ItDayComponentPreview
import com.umc.itday.ui.theme.ItDayBlue
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun ProReportContent(
    modifier: Modifier = Modifier,
    onAttendanceClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    discountUseCount: Int = 15,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(ItDayWhite),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.verticalGradient(
                            colorStops =
                                arrayOf(
                                    0f to ProMint,
                                    0.72f to ProMint,
                                    1f to ProMintFade,
                                ),
                        ),
                    )
                    .padding(horizontal = ItDayDimens.Space24, vertical = ItDayDimens.Space24),
        ) {
            ProHero(onAttendanceClick = onAttendanceClick, onProfileClick = onProfileClick)
            ProAchievementCard(
                currentCount = discountUseCount,
                modifier = Modifier.padding(top = ItDayDimens.Space4),
            )
        }
        ProNotice(modifier = Modifier.padding(ItDayDimens.Space24))
    }
}

@Composable
private fun ProHero(onAttendanceClick: () -> Unit, onProfileClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(290.dp)) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.report_pro_month), color = ItDayWhite)
            IconButton(onClick = onAttendanceClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar_outline),
                    contentDescription = stringResource(R.string.report_calendar),
                    tint = ItDayWhite,
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = stringResource(R.string.report_profile),
                    tint = ItDayWhite,
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.report_pro_badge),
            contentDescription = stringResource(R.string.report_pro_badge),
            modifier = Modifier.align(Alignment.TopStart).size(82.dp),
            contentScale = ContentScale.Fit,
        )
        Image(
            painter = painterResource(R.drawable.report_capybara_pro),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(180.dp),
            contentScale = ContentScale.Fit,
        )
        ItDayCard(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProStat("🗓️", stringResource(R.string.report_pro_monthly_use), 15)
                ProStat("💰", stringResource(R.string.report_pro_monthly_point), 15)
                ProStat("🚩", stringResource(R.string.report_pro_total_use), 15)
            }
        }
    }
}

@Composable
private fun ProStat(icon: String, label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon)
        Text(text = label, color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
        Text(text = value.toString(), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProAchievementCard(currentCount: Int, modifier: Modifier = Modifier) {
    val nextTarget = listOf(5, 10, 15, 20).firstOrNull { it > currentCount } ?: 20
    val remainingCount = (nextTarget - currentCount).coerceAtLeast(0)
    ItDayCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.report_pro_next_reward, remainingCount),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(ProBanner).padding(4.dp),
            color = ItDayBlue,
            style = MaterialTheme.typography.bodySmall,
        )
        Achievement("🏆", currentCount, 5, "+100", false)
        Achievement("🥈", currentCount, 10, "+100", false)
        Achievement("🥇", currentCount, 15, "P 400", currentCount >= 15)
        Achievement("🏅", currentCount, 20, "P 1,000", false)
    }
}

@Composable
private fun Achievement(icon: String, currentCount: Int, targetCount: Int, reward: String, enabled: Boolean) {
    val achievedCount = currentCount.coerceIn(0, targetCount)
    val progress = achievedCount.toFloat() / targetCount
    val status = if (achievedCount >= targetCount) R.string.report_pro_progress_complete else R.string.report_pro_progress_active
    ReportAchievementRow(
        title = stringResource(R.string.report_pro_achievement_title, targetCount),
        description = stringResource(R.string.report_pro_achievement_description, targetCount),
        rewardText = reward,
        progress = progress,
        progressText = stringResource(R.string.report_pro_progress, achievedCount, targetCount, stringResource(status)),
        iconText = icon,
        rewardEnabled = enabled,
    )
}

@Composable
private fun ProNotice(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(ItDayGray100).padding(16.dp)) {
        Text(text = stringResource(R.string.report_pro_notice), fontWeight = FontWeight.Bold)
        Text(text = stringResource(R.string.report_pro_notice_discount), color = ItDayGray500)
        Text(text = stringResource(R.string.report_pro_notice_expiry), color = ItDayGray500)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProReportContentPreview() {
    ItDayComponentPreview { ProReportContent() }
}

private val ProMint = Color(0xFF82DEC5)
private val ProMintFade = Color(0xFFDDF8F2)
private val ProBanner = Color(0xFFE1F1FF)
