package com.umc.itday.ui.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.itday.ui.theme.ItDayTheme
import kotlinx.coroutines.delay

private const val SPLASH_DISPLAY_MILLIS = 1_200L

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DISPLAY_MILLIS)
        onNavigateNext()
    }

    Box(
        modifier = modifier.fillMaxSize().background(splashGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ItDayLogo(size = 72.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "혜택을 챙기는\n가장 쉬운 방법",
                color = Color(0xFF191919),
                fontSize = 20.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private val splashGradient =
    Brush.verticalGradient(
        colorStops =
            arrayOf(
                0.00f to Color(0xFFFFF8F5),
                0.40f to Color(0xFFF2FBF8),
                0.70f to Color(0xFFC9F9F2),
                1.00f to Color(0xFFBDEBFF),
            ),
    )

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun SplashScreenPreview() {
    ItDayTheme(dynamicColor = false) {
        SplashScreen(onNavigateNext = {})
    }
}
