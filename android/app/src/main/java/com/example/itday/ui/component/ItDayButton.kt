package com.example.itday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayRadius
import com.example.itday.ui.theme.ItDayTheme
import com.example.itday.ui.theme.ItDayWhite

enum class ItDayButtonVariant {
    Primary,
    Secondary,
    Destructive,
    Kakao,
    Text,
}

enum class ItDayButtonSize {
    Large,
    Medium,
    Small,
}

@Composable
fun ItDayButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ItDayButtonVariant = ItDayButtonVariant.Primary,
    size: ItDayButtonSize = ItDayButtonSize.Large,
    enabled: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
) {
    val colors = variant.colors(enabled)
    val height = size.height
    val horizontalPadding = size.horizontalPadding
    val shape =
        RoundedCornerShape(
            if (size == ItDayButtonSize.Small) {
                ItDayRadius.ButtonRadiusSmall
            } else {
                ItDayRadius.ButtonRadius
            },
        )

    Row(
        modifier =
            modifier
                .height(height)
                .defaultMinSize(minWidth = size.minWidth)
                .clip(shape)
                .background(colors.containerColor)
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(ItDayDimens.ButtonContentGap))
        }
        Text(
            text = text,
            color = colors.contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

private data class ItDayButtonColors(
    val containerColor: Color,
    val contentColor: Color,
)

private val ItDayButtonSize.height: Dp
    get() =
        when (this) {
            ItDayButtonSize.Large -> ItDayDimens.ButtonHeight
            ItDayButtonSize.Medium -> ItDayDimens.ButtonHeight - ItDayDimens.Space12
            ItDayButtonSize.Small -> ItDayDimens.ButtonHeight - ItDayDimens.Space24 - ItDayDimens.Space4
        }

private val ItDayButtonSize.horizontalPadding: Dp
    get() =
        when (this) {
            ItDayButtonSize.Large,
            ItDayButtonSize.Medium,
            -> ItDayDimens.ButtonHorizontalPadding
            ItDayButtonSize.Small -> ItDayDimens.Space12
        }

private val ItDayButtonSize.minWidth: Dp
    get() =
        when (this) {
            ItDayButtonSize.Large -> ItDayDimens.ButtonHeight + ItDayDimens.ButtonHeight
            ItDayButtonSize.Medium -> ItDayDimens.ButtonHeight + ItDayDimens.Space24
            ItDayButtonSize.Small -> ItDayDimens.ButtonHeight
        }

@Composable
private fun ItDayButtonVariant.colors(enabled: Boolean): ItDayButtonColors {
    val colorScheme = MaterialTheme.colorScheme

    if (!enabled) {
        return ItDayButtonColors(
            containerColor = ItDayGray100,
            contentColor = colorScheme.onSurfaceVariant,
        )
    }

    return when (this) {
        ItDayButtonVariant.Primary ->
            ItDayButtonColors(colorScheme.primary, ItDayWhite)
        ItDayButtonVariant.Secondary ->
            ItDayButtonColors(ItDayGray100, colorScheme.onSurfaceVariant)
        ItDayButtonVariant.Destructive ->
            ItDayButtonColors(colorScheme.error, colorScheme.onError)
        ItDayButtonVariant.Kakao ->
            ItDayButtonColors(colorScheme.tertiary, colorScheme.onTertiary)
        ItDayButtonVariant.Text ->
            ItDayButtonColors(Color.Transparent, ItDayBlue)
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDayButtonPreview() {
    ItDayTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space8)) {
            ItDayButton(text = "Next", onClick = {})
            ItDayButton(text = "Move", size = ItDayButtonSize.Small, onClick = {})
        }
    }
}
