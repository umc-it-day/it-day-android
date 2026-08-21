package com.umc.itday.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ItDayLoadingOverlay(modifier: Modifier = Modifier, message: String? = null, backgroundColor: Color = Color.White) {
    Box(modifier.fillMaxSize().background(backgroundColor), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ItDayPulseIndicator(size = 48.dp)
            message?.let { Text(text = it, color = LoadingPrimary) }
        }
    }
}

@Composable
fun ItDayPulseIndicator(modifier: Modifier = Modifier, size: Dp = 24.dp, color: Color = LoadingPrimary) {
    val alpha by rememberPulseAlpha()
    Box(modifier.size(size).alpha(alpha).background(color, CircleShape))
}

private val LoadingPrimary = Color(0xFF00C896)

@Composable
private fun rememberPulseAlpha() = rememberInfiniteTransition(label = "PulseTransition").animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
    label = "AlphaAnimation",
)
