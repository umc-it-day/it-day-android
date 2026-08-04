package com.example.itday.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashPlaceholderScreen(onNavigateNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DISPLAY_MILLIS)
        onNavigateNext()
    }

    NavigationPlaceholderScreen(
        title = "Splash",
        description = "앱 시작 화면",
    )
}

@Composable
fun LoginPlaceholderScreen(onLoginClick: () -> Unit) {
    NavigationPlaceholderScreen(
        title = "Login",
        description = "시작 및 로그인 화면 자리",
        buttonText = "로그인 완료",
        onButtonClick = onLoginClick,
    )
}

@Composable
fun OnboardingPlaceholderScreen(onStartClick: () -> Unit) {
    NavigationPlaceholderScreen(
        title = "Onboarding",
        description = "온보딩 플로우 화면 자리",
        buttonText = "홈으로 시작",
        onButtonClick = onStartClick,
    )
}

@Composable
fun HomePlaceholderScreen() {
    NavigationPlaceholderScreen(
        title = "Home",
        description = "홈 화면 자리",
    )
}

@Composable
fun MapPlaceholderScreen() {
    NavigationPlaceholderScreen(
        title = "Map",
        description = "지도 화면 자리",
    )
}

@Composable
fun BarcodePlaceholderScreen() {
    NavigationPlaceholderScreen(
        title = "Barcode",
        description = "바코드 플로우 화면 자리",
    )
}

@Composable
fun ReportPlaceholderScreen() {
    NavigationPlaceholderScreen(
        title = "Report",
        description = "리포트 화면 자리",
    )
}

@Composable
fun SettingsPlaceholderScreen(onPaymentClick: (() -> Unit)? = null) {
    NavigationPlaceholderScreen(
        title = "Settings",
        description = "설정 화면 자리",
        buttonText = if (onPaymentClick != null) "\uC720\uB8CC\uACB0\uC81C \uB370\uBAA8" else null,
        onButtonClick = onPaymentClick,
    )
}

@Composable
private fun NavigationPlaceholderScreen(
    title: String,
    description: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onButtonClick) {
                Text(text = buttonText)
            }
        }
    }
}

private const val SPLASH_DISPLAY_MILLIS = 1_000L
