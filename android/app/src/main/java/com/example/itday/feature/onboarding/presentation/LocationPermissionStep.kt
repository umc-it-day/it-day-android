package com.example.itday.feature.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray300
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayTheme
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun LocationPermissionStep(
    showError: Boolean,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                buildAnnotatedString {
                    append("서비스 이용을 위해\n")
                    withStyle(SpanStyle(color = ItDayPrimary, fontWeight = FontWeight.Bold)) {
                        append("위치권한")
                    }
                    append("을 허용해주세요")
                },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191919),
            lineHeight = 32.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        // 위치 권한 안내 카디얼 박스
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7F9)),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "위치 권한 (필수)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191919),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                PermissionFeatureRow(
                    title = "매장 도착 시 멤버십 자동 실행",
                    description = "매장 근처에 도착하면 필요한 멤버십을\n자동으로 보여드려요",
                )

                Spacer(modifier = Modifier.height(16.dp))

                PermissionFeatureRow(
                    title = "할인 알림",
                    description = "주변 매장의 할인 혜택을 알림으로\n안내해드려요",
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "위치 권한을 '항상허용' 으로 바꿔주세요.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = ItDayPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSettingsClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ItDayPrimary),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = ItDayWhite,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "설정 열기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ItDayWhite,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PermissionFeatureRow(
    title: String,
    description: String,
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = ItDayPrimary,
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = ItDayGray500,
                lineHeight = 18.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationPermissionStepPreview() {
    ItDayTheme {
        LocationPermissionStep(showError = false, onSettingsClick = {})
    }
}
