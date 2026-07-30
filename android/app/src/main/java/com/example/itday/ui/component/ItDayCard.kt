package com.example.itday.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayMint
import com.example.itday.ui.theme.ItDayRadius
import com.example.itday.ui.preview.ItDayComponentPreview
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun ItDayCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = ItDayWhite,
    border: BorderStroke? = null,
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = ItDayDimens.Space16,
            vertical = ItDayDimens.Space16,
        ),
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(ItDayRadius.CardRadius)
    val clickableModifier =
        if (onClick == null) {
            Modifier
        } else {
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
        }

    Column(
        modifier =
            modifier
                .clip(shape)
                .background(backgroundColor)
                .then(if (border == null) Modifier else Modifier.border(border, shape))
                .then(clickableModifier)
                .padding(contentPadding),
    ) {
        content()
    }
}

@Composable
fun ItDaySelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    ItDayCard(
        modifier = modifier,
        onClick = if (enabled) onClick else null,
        backgroundColor = if (selected) ItDayGray100 else ItDayWhite,
        border =
            BorderStroke(
                width = Dp.Hairline,
                color = if (selected) ItDayMint else ItDayGray100,
            ),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun ItDayCardPreview() {
    ItDayComponentPreview {
        ItDaySelectableCard(selected = true, onClick = {}) {
            androidx.compose.material3.Text(text = "VIP")
        }
    }
}
