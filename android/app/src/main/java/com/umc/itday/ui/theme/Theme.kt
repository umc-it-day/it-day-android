package com.umc.itday.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
    lightColorScheme(
        primary = ItDayPrimary,
        secondary = ItDaySecondary,
        background = ItDayGray50,
        surface = ItDayWhite,
    )

@Suppress("UNUSED_PARAMETER")
@Composable
fun ItDayTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
