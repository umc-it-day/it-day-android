package com.umc.itday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayMint
import com.umc.itday.ui.preview.ItDayComponentPreview

@Composable
fun ItDayStepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space12),
    ) {
        repeat(totalSteps) { index ->
            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .size(ItDayDimens.Space8)
                        .background(
                            color = if (index <= currentStep) ItDayMint else ItDayGray100,
                            shape = CircleShape,
                        ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDayStepIndicatorPreview() {
    ItDayComponentPreview {
        ItDayStepIndicator(currentStep = 1, totalSteps = 4)
    }
}
