package com.example.itday.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.preview.ItDayComponentPreview

@Composable
fun ItDayFlowTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(ItDayDimens.ButtonHeight + ItDayDimens.Space8)
                .padding(horizontal = ItDayDimens.Space8),
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color(0xFF191919),
                )
            }
        }
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                color = ItDayBlue,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = onActionClick)
                        .padding(ItDayDimens.Space8),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDayFlowTopBarPreview() {
    ItDayComponentPreview {
        ItDayFlowTopBar(
            title = "프로필 설정",
            onBackClick = {},
            actionText = "안내",
            onActionClick = {},
        )
    }
}
