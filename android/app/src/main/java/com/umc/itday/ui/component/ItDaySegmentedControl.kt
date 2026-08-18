package com.umc.itday.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.umc.itday.ui.theme.ItDayBlue
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayRadius
import com.umc.itday.ui.preview.ItDayComponentPreview
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun ItDaySegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(ItDayDimens.ButtonHeight - ItDayDimens.Space12)
                .fillMaxWidth()
                .clip(RoundedCornerShape(ItDayRadius.ButtonRadius))
                .background(ItDayGray100)
                .padding(ItDayDimens.Space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            androidx.compose.foundation.layout.Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(ItDayDimens.ButtonHeight - ItDayDimens.Space24)
                        .clip(RoundedCornerShape(ItDayRadius.ButtonRadius))
                        .background(if (selected) ItDayWhite else ItDayGray100)
                        .clickable { onSelectedIndexChange(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item,
                    color = if (selected) ItDayBlue else ItDayGray500,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDaySegmentedControlPreview() {
    ItDayComponentPreview {
        ItDaySegmentedControl(
            items = listOf("SKT", "KT", "LGU+"),
            selectedIndex = 0,
            onSelectedIndexChange = {},
        )
    }
}
