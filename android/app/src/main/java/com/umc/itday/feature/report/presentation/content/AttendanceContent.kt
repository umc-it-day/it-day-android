package com.umc.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umc.itday.R
import com.umc.itday.ui.component.ItDayButton
import com.umc.itday.ui.component.ItDayFlowTopBar
import com.umc.itday.ui.preview.ItDayComponentPreview
import com.umc.itday.ui.theme.ItDayBlue
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayMint
import com.umc.itday.ui.theme.ItDayWhite
import java.util.Calendar

@Composable
fun AttendanceContent(
    monthlyPoints: Int = 0,
    consecutiveDays: Int = 0,
    sevenDaysBonus: Boolean = false,
    fifteenDaysBonus: Boolean = false,
    thirtyDaysBonus: Boolean = false,

    isAttendanceSubmitting: Boolean = false,
    isAttendanceCompleted: Boolean = false,
    attendanceSuccessMessage: String? = null,
    completedAttendanceDays: Set<Int> = emptySet(),
    latestEarnedPoint: Int = 0,
    onBackClick: () -> Unit = {},
    onPointClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {

    var selectedMonth by rememberSaveable { mutableIntStateOf(0) }
    var showCompletionDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(attendanceSuccessMessage) {
        if (attendanceSuccessMessage != null) {
            showCompletionDialog = true
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(AttendanceGradient)
                .verticalScroll(rememberScrollState())
                .padding(ItDayDimens.Space24),
    ) {
        ItDayFlowTopBar(title = stringResource(R.string.attendance_title), onBackClick = onBackClick)
        AttendanceHero(
            monthlyPoints = monthlyPoints,
            consecutiveDays = consecutiveDays,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ItDayWhite)
                    .padding(20.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = stringResource(R.string.attendance_bonus), fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(R.string.attendance_monthly_max),
                        color = ItDayGray500,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space16)) {
                    MonthText(R.string.attendance_this_month, selectedMonth == 0) { selectedMonth = 0 }
                    MonthText(R.string.attendance_last_month, selectedMonth == 1) { selectedMonth = 1 }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = ItDayDimens.Space16),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                BonusStamp(R.string.attendance_seven_days, sevenDaysBonus || consecutiveDays >= 7)
                BonusStamp(R.string.attendance_fifteen_days, fifteenDaysBonus || consecutiveDays >= 15)
                BonusStamp(R.string.attendance_perfect, thirtyDaysBonus || consecutiveDays >= 30)
            }
            HorizontalDivider(color = ItDayGray100)
            AttendanceCalendar(completedDays = completedAttendanceDays)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = ItDayDimens.Space16)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ItDayGray100)
                        .clickable(onClick = onPointClick)
                        .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.attendance_check_points),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    painter = painterResource(R.drawable.point_coin),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Text(text = ">", color = ItDayGray500)
            }
        }
        val buttonText =
            when {
                isAttendanceSubmitting -> "출석체크 중..."
                isAttendanceCompleted -> "오늘 출석 완료"
                else -> stringResource(R.string.attendance_action)
            }
        ItDayButton(
            text = buttonText,
            enabled = !isAttendanceSubmitting && !isAttendanceCompleted,
            onClick = {
                onAttendanceClick()
            },
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space24),
        )

    }
    if (showCompletionDialog) {
        AttendanceCompletionDialog(
            earnedPoint = latestEarnedPoint,
            onHomeClick = {
                showCompletionDialog = false
                onHomeClick()
            },
            onDismiss = { showCompletionDialog = false },
        )
    }
}

@Composable
private fun AttendanceCompletionDialog(
    earnedPoint: Int,
    onHomeClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .padding(horizontal = ItDayDimens.Space16),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(560.dp)
                        .background(
                            Brush.verticalGradient(
                                colorStops =
                                    arrayOf(
                                        0f to Color(0xFF55575A),
                                        0.3f to Color(0xFFEAF2FF),
                                        0.7f to Color(0xFFEAF2FF),
                                        1f to Color(0xFF55575A),
                                    ),
                            ),
                        )
                        .padding(horizontal = ItDayDimens.Space16),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.attendance_completion_label),
                    color = ItDayGray500,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(R.string.attendance_completion_title),
                    modifier = Modifier.padding(top = ItDayDimens.Space4),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (earnedPoint > 0) {
                    Text(
                        text = "+${earnedPoint}P 적립",
                        modifier = Modifier.padding(top = ItDayDimens.Space8),
                        color = ItDayBlue,
                        fontWeight = FontWeight.Bold,
                    )
                }
                ItDayButton(
                    text = stringResource(R.string.attendance_go_home),
                    onClick = onHomeClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                )
                Text(
                    text = stringResource(R.string.attendance_review),
                    modifier = Modifier.padding(top = ItDayDimens.Space16).clickable(onClick = onDismiss),
                    color = ItDayGray500,
                )
            }
        }
    }
}

@Composable
private fun AttendanceHero(
    monthlyPoints: Int,
    consecutiveDays: Int,
) {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val maxDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysLeft = maxOf(0, maxDays - today)

    Box(modifier = Modifier.fillMaxWidth().size(220.dp)) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.attendance_month_points, monthlyPoints),
                    color = ItDayBlue,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "D-$daysLeft",
                    modifier =
                        Modifier
                            .padding(start = ItDayDimens.Space4)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AttendanceSky)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = ItDayWhite,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(text = stringResource(R.string.attendance_month_earned), fontWeight = FontWeight.Bold)
        }
        Image(
            painter = painterResource(R.drawable.attendance_capybara),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(190.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = "다음 보너스까지 ${maxOf(1, 7 - (consecutiveDays % 7))}일 남음",
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ItDayWhite)
                    .padding(10.dp),
            fontWeight = FontWeight.Bold,
        )
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ItDayWhite)
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "🔥", style = MaterialTheme.typography.titleLarge)
            Text(text = "$consecutiveDays", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = stringResource(R.string.attendance_streak), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun MonthText(textRes: Int, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = stringResource(textRes),
        modifier = Modifier.clickable(onClick = onClick),
        color = if (selected) ItDayBlue else ItDayGray500,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
    )
}

@Composable
private fun BonusStamp(labelRes: Int, completed: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(labelRes), color = ItDayGray500, style = MaterialTheme.typography.bodySmall)
        Image(
            painter = painterResource(if (completed) R.drawable.attendance_stamp_completed else R.drawable.attendance_stamp_locked),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
        )
        if (completed) {
            Row(
                modifier =
                    Modifier
                        .padding(top = ItDayDimens.Space4)
                        .clip(RoundedCornerShape(18.dp))
                        .background(ItDayMint)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.attendance_reward_check),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "+30P",
                    modifier = Modifier.padding(start = ItDayDimens.Space4),
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Text(text = "100P", color = ItDayGray500, modifier = Modifier.padding(top = ItDayDimens.Space4))
        }
    }
}

@Composable
private fun AttendanceCalendar(completedDays: Set<Int>) {
    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_MONTH)
    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // 이번 달 1일의 요일 오프셋 구하기 (일요일: 0, 월요일: 1, ..., 토요일: 6)
    val firstDayCalendar = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val firstDayOffset = firstDayCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        listOf("일", "월", "화", "수", "목", "금", "토").forEach {
            Text(
                text = it,
                color = if (it == "일") Color(0xFFFF5252) else if (it == "토") ItDayBlue else ItDayGray500,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(36.dp),
            )
        }
    }

    val totalSlots = buildList {
        repeat(firstDayOffset) { add(null) }
        for (day in 1..maxDays) { add(day) }
        while (size % 7 != 0) { add(null) }
    }

    totalSlots.chunked(7).forEach { week ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space8),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            week.forEach { day ->
                if (day != null) {
                    AttendanceDay(
                        day = day,
                        completed = day in completedDays,
                        isToday = day == today,
                    )
                } else {
                    Box(modifier = Modifier.size(36.dp))
                }
            }
        }
    }
}

@Composable
private fun AttendanceDay(day: Int, completed: Boolean, isToday: Boolean) {
    val background =
        when {
            completed -> ItDayBlue
            isToday -> Color(0xFFEBF3FF)
            else -> Color.Transparent
        }
    val textColor =
        when {
            completed -> ItDayWhite
            isToday -> ItDayBlue
            else -> Color(0xFF333333)
        }

    Box(
        modifier =
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(background)
                .then(
                    if (isToday && !completed) Modifier.border(1.5.dp, ItDayBlue, CircleShape)
                    else Modifier
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isToday || completed) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun AttendanceContentPreview() {
    ItDayComponentPreview { AttendanceContent() }
}

private val AttendanceGradient = Brush.verticalGradient(listOf(Color(0xFFEDFBF8), Color(0xFFE9ECFF), ItDayWhite))
private val AttendanceSky = Color(0xFF72C7EE)
