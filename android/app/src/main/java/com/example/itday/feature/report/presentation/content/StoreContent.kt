package com.example.itday.feature.report.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.ui.component.ItDayButton
import com.example.itday.ui.component.ItDayButtonSize
import com.example.itday.ui.component.ItDayButtonVariant
import com.example.itday.ui.component.ItDayFlowTopBar
import com.example.itday.ui.preview.ItDayComponentPreview
import com.example.itday.ui.theme.ItDayBlue
import com.example.itday.ui.theme.ItDayBlue50
import com.example.itday.ui.theme.ItDayDimens
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayWhite

data class StoreProductUiModel(val name: String? = null, val points: Int, val exchangeEnabled: Boolean = true)

@Composable
fun StoreContent(
    products: List<StoreProductUiModel> = StorePreviewProducts,
    onBackClick: () -> Unit = {},
    onExchangeClick: (Int) -> Unit = {},
    onMissionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }
    val categories = listOf(R.string.store_all, R.string.store_cafe, R.string.store_convenience, R.string.store_restaurant, R.string.store_bookstore)
    Column(modifier = modifier.fillMaxSize().background(ItDayGray100).verticalScroll(rememberScrollState())) {
        Column(modifier = Modifier.background(ItDayWhite)) {
            ItDayFlowTopBar(title = stringResource(R.string.store_title), onBackClick = onBackClick)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItDayDimens.Space24),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = stringResource(R.string.store_my_points), fontWeight = FontWeight.Bold)
                    Text(text = stringResource(R.string.store_point_value, 0), color = ItDayBlue, style = MaterialTheme.typography.headlineMedium)
                }
                Text(
                    text = stringResource(R.string.store_available_points, 0),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(ItDayBlue50).padding(horizontal = 10.dp, vertical = 4.dp),
                    color = ItDayBlue,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16)) {
                categories.forEachIndexed { index, title ->
                    Column(
                        modifier = Modifier.weight(1f).clickable { selectedCategoryIndex = index },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(title),
                            color = if (index == selectedCategoryIndex) Color.Black else ItDayGray500,
                            fontWeight = if (index == selectedCategoryIndex) FontWeight.Bold else FontWeight.Normal,
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = ItDayDimens.Space8),
                            thickness = if (index == selectedCategoryIndex) 2.dp else 0.dp,
                            color = Color.Black,
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.padding(ItDayDimens.Space24), verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space16)) {
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(ItDayWhite)) {
                products.forEachIndexed { index, product ->
                    StoreProductRow(product = product, onExchangeClick = { onExchangeClick(index) })
                    if (index < products.lastIndex) HorizontalDivider(color = ItDayGray100)
                }
            }
            StoreMissionCard(onMissionClick = onMissionClick)
        }
    }
}

@Composable
fun StoreProductSummary(
    points: Int,
    modifier: Modifier = Modifier,
    name: String? = null,
    imageContent: (@Composable () -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) { imageContent?.invoke() }
        Column(modifier = Modifier.padding(start = ItDayDimens.Space12)) {
            if (name != null) Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = stringResource(R.string.store_point_value, points), color = ItDayBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StoreProductRow(product: StoreProductUiModel, onExchangeClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        StoreProductSummary(
            points = product.points,
            name = product.name,
            modifier = Modifier.padding(end = 88.dp),
        )
        ItDayButton(
            text = stringResource(if (product.exchangeEnabled) R.string.store_exchange else R.string.store_exchanged),
            onClick = onExchangeClick,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = ItDayDimens.Space16),
            size = ItDayButtonSize.Small,
            enabled = product.exchangeEnabled,
        )
    }
}

@Composable
private fun StoreMissionCard(onMissionClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(StoreMissionGradient).padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.store_mission_deadline), color = ItDayWhite)
                Text(text = stringResource(R.string.store_mission_title), color = ItDayWhite, style = MaterialTheme.typography.titleLarge)
            }
            Image(painter = painterResource(R.drawable.mission_gift), contentDescription = null, modifier = Modifier.size(72.dp))
        }
        ItDayButton(
            text = stringResource(R.string.store_mission_check),
            onClick = onMissionClick,
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space16),
            variant = ItDayButtonVariant.Secondary,
            size = ItDayButtonSize.Medium,
        )
        Text(
            text = stringResource(R.string.store_mission_reward),
            modifier = Modifier.fillMaxWidth().padding(top = ItDayDimens.Space12),
            color = ItDayWhite,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StoreContentPreview() {
    ItDayComponentPreview { StoreContent() }
}

private val StorePreviewProducts = listOf(2000, 3000, 3000, 6000, 2000).mapIndexed { index, points -> StoreProductUiModel(points = points, exchangeEnabled = index != 0) }
private val StoreMissionGradient = Brush.horizontalGradient(listOf(Color(0xFF5F79FF), Color(0xFF9569F4)))
