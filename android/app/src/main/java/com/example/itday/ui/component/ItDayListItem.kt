package com.example.itday.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.preview.ItDayComponentPreview

@Composable
fun ItDayListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    leadingContent: (@Composable (() -> Unit))? = null,
    trailingContent: (@Composable (() -> Unit))? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = ItDayDimens.ButtonHeight - ItDayDimens.Space4)
                .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
                .padding(vertical = ItDayDimens.Space8),
        horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null) {
            leadingContent()
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            if (description != null) {
                Text(
                    text = description,
                    color = ItDayGray500,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDayListItemPreview() {
    ItDayComponentPreview {
        ItDayListItem(
            title = "알림 설정",
            description = "신규 제휴 매장 및 이벤트 소식을 받습니다.",
            trailingContent = { Text(text = ">") },
        )
    }
}
