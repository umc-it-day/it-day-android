package com.umc.itday.ui.component

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.umc.itday.ui.preview.ItDayComponentPreview

data class ItDayBottomBarItem(
    val route: String,
    val label: String,
    val iconContent: @Composable () -> Unit,
)

@Composable
fun ItDayBottomBar(
    items: List<ItDayBottomBarItem>,
    currentRoute: String?,
    onItemClick: (ItDayBottomBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) },
                icon = item.iconContent,
                label = { Text(text = item.label) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItDayBottomBarPreview() {
    ItDayComponentPreview {
        ItDayBottomBar(
            items =
                listOf(
                    ItDayBottomBarItem("home", "Home") { Text("H") },
                    ItDayBottomBarItem("map", "Map") { Text("M") },
                    ItDayBottomBarItem("report", "Report") { Text("R") },
                    ItDayBottomBarItem("settings", "Settings") { Text("S") },
                ),
            currentRoute = "home",
            onItemClick = {},
        )
    }
}
