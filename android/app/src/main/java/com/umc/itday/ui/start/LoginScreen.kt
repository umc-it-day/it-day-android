package com.umc.itday.ui.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.itday.R
import com.umc.itday.ui.component.ItDayButton
import com.umc.itday.ui.component.ItDayButtonSize
import com.umc.itday.ui.component.ItDayButtonVariant
import com.umc.itday.ui.theme.ItDayBlue
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayTheme

@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.White,
                        0.48f to Color.White,
                        0.70f to Color(0xFFE7FFFF),
                        1f to Color.White,
                    ),
                ).statusBarsPadding(),
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = stringResource(R.string.login_title_first),
                color = Color.Black,
                fontSize = 24.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text =
                    buildAnnotatedString {
                        append(stringResource(R.string.login_title_second_prefix))
                        withStyle(SpanStyle(color = ItDayBlue)) {
                            append(stringResource(R.string.login_brand_name))
                        }
                        append(stringResource(R.string.login_title_second_suffix))
                    },
                color = Color.Black,
                fontSize = 24.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(42.dp))
            Image(
                painter = painterResource(R.drawable.img_login_wallet),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(320.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
            ItDayButton(
                text =
                    if (isLoading) {
                        stringResource(R.string.login_loading)
                    } else {
                        stringResource(R.string.login_kakao_start)
                    },
                onClick = onKakaoLoginClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ItDayButtonVariant.Kakao,
                size = ItDayButtonSize.Large,
                enabled = !isLoading,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.login_guest_browse),
                modifier = Modifier.clickable(onClick = onGuestClick).padding(8.dp),
                color = ItDayGray500,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(modifier = Modifier.height(48.dp))
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
