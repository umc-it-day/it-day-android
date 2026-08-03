@file:Suppress("MagicNumber")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.R

@Composable
internal fun ProBadge() {
    Row(
        modifier =
            Modifier
                .background(
                    brush =
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFEF59FF),
                                Color(0xFF2692F2),
                                Color(0xFF29D393),
                            ),
                        ),
                    shape = RoundedCornerShape(12.dp),
                ).padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.payment_pro_star),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = stringResource(R.string.payment_pro),
            color = Color.White,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
