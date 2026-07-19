package com.example.itday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayBlue50
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayMint
import com.example.itday.ui.theme.ItDayRadius
import com.example.itday.ui.theme.ItDayTheme

enum class ItDayBadgeVariant {
    Primary,
    Blue,
    Neutral,
}

@Composable
fun ItDayBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: ItDayBadgeVariant = ItDayBadgeVariant.Primary,
    leadingIcon: (@Composable (() -> Unit))? = null,
) {
    val colors = variant.colors()

    Row(
        modifier =
            modifier
                .height(ItDayDimens.Space24)
                .background(colors.containerColor, RoundedCornerShape(ItDayRadius.ButtonRadiusSmall))
                .padding(horizontal = ItDayDimens.Space8),
        horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
        }
        Text(
            text = text,
            color = colors.contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

private data class ItDayBadgeColors(
    val containerColor: Color,
    val contentColor: Color,
)

private fun ItDayBadgeVariant.colors(): ItDayBadgeColors =
    when (this) {
        ItDayBadgeVariant.Primary -> ItDayBadgeColors(ItDayGray100, ItDayMint)
        ItDayBadgeVariant.Blue -> ItDayBadgeColors(ItDayBlue50, ItDayBlue)
        ItDayBadgeVariant.Neutral -> ItDayBadgeColors(ItDayGray100, ItDayGray500)
    }

@Preview(showBackground = true)
@Composable
private fun ItDayBadgePreview() {
    ItDayTheme {
        ItDayBadge(text = "VIP 할인")
    }
}
