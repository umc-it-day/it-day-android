package com.example.itday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayRadius
import com.example.itday.ui.preview.ItDayComponentPreview
import com.example.itday.ui.theme.ItDayWhite
import com.example.itday.ui.theme.KakaoContainer
import com.example.itday.ui.theme.KakaoLabel
import com.example.itday.ui.theme.KakaoSymbol

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
    val shape = RoundedCornerShape(variant.cornerRadius(size))

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
                ).padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val resolvedLeadingIcon =
            leadingIcon ?: if (variant == ItDayButtonVariant.Kakao) {
                { KakaoSymbol() }
            } else {
                null
            }
        if (resolvedLeadingIcon != null) {
            resolvedLeadingIcon()
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

private fun ItDayButtonVariant.cornerRadius(size: ItDayButtonSize): Dp =
    when {
        this == ItDayButtonVariant.Kakao -> ItDayRadius.KakaoButtonRadius
        size == ItDayButtonSize.Small -> ItDayRadius.ButtonRadiusSmall
        else -> ItDayRadius.ButtonRadius
    }

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

    if (this == ItDayButtonVariant.Kakao) {
        return ItDayButtonColors(KakaoContainer, KakaoLabel)
    }

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
        ItDayButtonVariant.Kakao -> error("Kakao colors are handled above")
        ItDayButtonVariant.Text ->
            ItDayButtonColors(Color.Transparent, ItDayBlue)
    }
}

/** Kakao Sync speech-bubble symbol. Its color is fixed by the Kakao design guide. */
@Composable
private fun KakaoSymbol() {
    Canvas(modifier = Modifier.width(18.dp).height(18.dp)) {
        drawKakaoSymbol()
    }
}

private fun DrawScope.drawKakaoSymbol() {
    val path =
        Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.08f)
            cubicTo(
                size.width * 0.20f,
                size.height * 0.08f,
                size.width * 0.04f,
                size.height * 0.28f,
                size.width * 0.04f,
                size.height * 0.51f,
            )
            cubicTo(
                size.width * 0.04f,
                size.height * 0.68f,
                size.width * 0.14f,
                size.height * 0.83f,
                size.width * 0.31f,
                size.height * 0.91f,
            )
            lineTo(size.width * 0.25f, size.height)
            lineTo(size.width * 0.43f, size.height * 0.94f)
            cubicTo(
                size.width * 0.45f,
                size.height * 0.94f,
                size.width * 0.48f,
                size.height * 0.95f,
                size.width * 0.5f,
                size.height * 0.95f,
            )
            cubicTo(
                size.width * 0.80f,
                size.height * 0.95f,
                size.width * 0.96f,
                size.height * 0.74f,
                size.width * 0.96f,
                size.height * 0.51f,
            )
            cubicTo(
                size.width * 0.96f,
                size.height * 0.28f,
                size.width * 0.80f,
                size.height * 0.08f,
                size.width * 0.5f,
                size.height * 0.08f,
            )
            close()
        }
    drawPath(path = path, color = KakaoSymbol)
}

@Preview(showBackground = true)
@Composable
private fun ItDayButtonPreview() {
    ItDayComponentPreview {
        Column(verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space12)) {
            ItDayButton(
                text = "Next",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
            ItDayButton(text = "Move", size = ItDayButtonSize.Small, onClick = {})
            ItDayButton(
                text = "카카오로 시작하기",
                variant = ItDayButtonVariant.Kakao,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
