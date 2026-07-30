package com.example.itday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayRadius
import com.example.itday.ui.preview.ItDayComponentPreview

@Composable
fun ItDayTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    trailingContent: (@Composable (() -> Unit))? = null,
) {
    val resolvedTextStyle =
        if (textStyle == TextStyle.Default) {
            MaterialTheme.typography.bodyMedium
        } else {
            textStyle
        }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        textStyle = resolvedTextStyle.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(ItDayBlue),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier =
                    Modifier
                        .height(ItDayDimens.ButtonHeight - ItDayDimens.Space4)
                        .fillMaxWidth()
                        .background(ItDayGray100, RoundedCornerShape(ItDayRadius.ButtonRadius))
                        .padding(horizontal = ItDayDimens.Space16),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = ItDayGray500,
                        style = resolvedTextStyle,
                    )
                }
                innerTextField()
                if (trailingContent != null) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        trailingContent()
                    }
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ItDayTextFieldPreview() {
    ItDayComponentPreview {
        ItDayTextField(
            value = "",
            onValueChange = {},
            placeholder = "16자리 입력",
        )
    }
}
