package com.example.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayButtonSize
import com.example.itday.ui.preview.ItDayComponentPreview
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray500

@Composable
fun GuestReportContent(
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(ItDayGray100)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ItDayDimens.ScreenHorizontalPadding),
    ) {
        ReportGuestHeader()
        Row(
            modifier = Modifier.padding(top = ItDayDimens.Space16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space8),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_lock_outline),
                contentDescription = null,
                tint = ItDayGray500,
            )
            Text(
                text = stringResource(R.string.report_guest_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = stringResource(R.string.report_guest_members_only),
            color = ItDayGray500,
            style = MaterialTheme.typography.bodySmall,
        )
        Image(
            painter = painterResource(R.drawable.report_guest_locked),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(300.dp).padding(top = ItDayDimens.Space24),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = stringResource(R.string.report_guest_preview),
            modifier = Modifier.padding(top = ItDayDimens.Space24, bottom = ItDayDimens.Space8),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        LockedReportPreview(onSignUpClick = onSignUpClick)
    }
}

@Composable
private fun ReportGuestHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.itday_logo_symbol),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(32.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space16)) {
            Text(text = "▣", color = ItDayGray500, style = MaterialTheme.typography.titleLarge)
            Text(text = "●", color = ItDayGray500, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun LockedReportPreview(onSignUpClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shadow(4.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(LockedPreviewColor),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(ItDayDimens.Space16),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.report_guest_monthly_usage), color = ItDayGray500)
            Text(text = "0회", color = ItDayBlue, style = MaterialTheme.typography.headlineMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = ItDayDimens.Space8))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "0회", color = ItDayGray500)
                    Text(text = "이용 예정", color = ItDayGray500)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "0회", color = ItDayGray500)
                    Text(text = "주간 평균", color = ItDayGray500)
                }
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_lock_outline),
            contentDescription = null,
            modifier = Modifier.size(52.dp).offset(y = (-28).dp),
            tint = Color.White,
        )
        ItDayButton(
            text = stringResource(R.string.report_guest_sign_up),
            onClick = onSignUpClick,
            size = ItDayButtonSize.Small,
            modifier = Modifier.align(Alignment.Center).offset(y = 30.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GuestReportContentPreview() {
    ItDayComponentPreview {
        GuestReportContent(onSignUpClick = {})
    }
}

private val LockedPreviewColor = Color(0xFFBDBDBD)
