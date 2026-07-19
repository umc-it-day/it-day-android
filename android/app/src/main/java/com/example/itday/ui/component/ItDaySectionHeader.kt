package com.example.itday.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.itday.ui.theme.ItDayTheme

@Composable
fun ItDaySectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable (() -> Unit))? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDaySectionHeaderPreview() {
    ItDayTheme {
        ItDaySectionHeader(title = "내 멤버십 혜택")
    }
}
