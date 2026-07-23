package com.example.itday.ui.start

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayButtonSize
import com.example.itday.ui.component.ItDayButtonVariant
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayTheme

@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color.White).statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(72.dp))
            ItDayLogo(size = 56.dp)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "간편하게 혜택을\n누릴 준비가 되셨나요?",
                color = ItDayGray500,
                fontSize = 18.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.weight(1f))
            ItDayButton(
                text = "카카오로 시작하기",
                onClick = onKakaoLoginClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ItDayButtonVariant.Kakao,
                size = ItDayButtonSize.Medium,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "게스트로 둘러보기",
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = onGuestClick)
                        .padding(8.dp),
                color = ItDayGray500,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun LoginScreenPreview() {
    ItDayTheme(dynamicColor = false) {
        LoginScreen(
            onKakaoLoginClick = {},
            onGuestClick = {},
        )
    }
}
