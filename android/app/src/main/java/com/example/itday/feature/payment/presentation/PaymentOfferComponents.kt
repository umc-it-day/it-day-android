@file:Suppress("MagicNumber")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.R

@Composable
internal fun RoyalCharacter() {
    Image(
        painter = painterResource(R.drawable.payment_king_kapibara),
        contentDescription = null,
        modifier = Modifier.size(94.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
internal fun TrialTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.payment_leaf_left),
            contentDescription = null,
            modifier = Modifier.size(width = 52.dp, height = 70.dp),
        )
        Text(
            text = stringResource(R.string.payment_trial_title),
            modifier = Modifier.width(188.dp),
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Image(
            painter = painterResource(R.drawable.payment_leaf_right),
            contentDescription = null,
            modifier = Modifier.size(width = 52.dp, height = 70.dp),
        )
    }
}

@Composable
internal fun PlanComparisonTable() {
    val rows =
        listOf(
            stringResource(R.string.payment_benefit_ad_free) to
                stringResource(R.string.payment_benefit_ad_free_description),
            stringResource(R.string.payment_benefit_challenge) to
                stringResource(R.string.payment_benefit_challenge_description),
            stringResource(R.string.payment_benefit_report) to
                stringResource(R.string.payment_benefit_report_description),
        )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(266.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .width(76.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF6681B4).copy(alpha = 0.58f), RoundedCornerShape(9.dp)),
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            PlanHeader()
            rows.forEachIndexed { index, row ->
                ComparisonRow(
                    title = row.first,
                    description = row.second,
                    showDivider = index != rows.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun PlanHeader() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.payment_free),
            modifier = Modifier.width(50.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
        Box(modifier = Modifier.width(76.dp), contentAlignment = Alignment.Center) {
            ProBadge()
        }
    }
}

@Composable
private fun ComparisonRow(
    title: String,
    description: String,
    showDivider: Boolean,
) {
    Column(modifier = Modifier.height(76.dp)) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = "\u2014",
                modifier = Modifier.width(50.dp),
                color = Color(0xFF80649E),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "\u2713",
                modifier = Modifier.width(76.dp),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            )
        }
        if (showDivider) {
            HorizontalDivider(color = Color.White.copy(alpha = 0.48f))
        }
    }
}
