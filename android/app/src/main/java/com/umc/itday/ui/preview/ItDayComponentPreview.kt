package com.umc.itday.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayTheme
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun ItDayComponentPreview(content: @Composable () -> Unit) {
    ItDayTheme {
        Box(
            modifier =
                Modifier
                    .requiredSize(width = PHONE_WIDTH, height = PHONE_HEIGHT)
                    .background(ItDayWhite)
                    .padding(ItDayDimens.ScreenHorizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

private val PHONE_WIDTH = 360.dp
private val PHONE_HEIGHT = 800.dp
