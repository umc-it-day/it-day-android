package com.example.itday.feature.barcode.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray300
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayWhite

@Composable
internal fun BarcodeTopBar(
    title: String? = null,
    rightActionText: String? = null,
    onBackClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color(0xFF191919),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (title != null) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (rightActionText != null) {
            Text(
                text = rightActionText,
                fontSize = 15.sp,
                color = Color(0xFF191919),
                modifier = Modifier.padding(end = 12.dp),
            )
        }
    }
}

@Composable
internal fun <T> ChipSelectorRow(
    title: String,
    items: List<T>,
    selectedItem: T,
    displayName: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = ItDayGray500,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSelected) ItDayGray100 else Color.Transparent,
                            ).clickable { onSelect(item) }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = displayName(item),
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ItDayPrimary else ItDayGray500,
                    )
                }
            }
        }
    }
}

@Composable
internal fun BarcodeInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int = 16,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "바코드 번호",
            fontSize = 14.sp,
            color = ItDayGray500,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ItDayGray100)
                    .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "16자리 입력",
                    fontSize = 16.sp,
                    color = ItDayGray300,
                    fontWeight = FontWeight.Medium,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(ItDayPrimary),
                textStyle =
                    androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191919),
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${value.length}/$maxLength",
            fontSize = 13.sp,
            color = if (value.length == maxLength) ItDayPrimary else ItDayGray300,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
internal fun RegisteredCardInfoBox(
    badgeText: String,
    cardNumber: String,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ItDayWhite)
                .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "등록된 번호",
                    fontSize = 14.sp,
                    color = ItDayGray500,
                )
                Text(
                    text = badgeText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ItDayPrimary,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = cardNumber,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
                letterSpacing = 1.sp,
            )
        }
    }
}

@Composable
internal fun BarcodePrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = ItDayPrimary,
                contentColor = ItDayWhite,
                disabledContainerColor = ItDayGray100,
                disabledContentColor = ItDayGray300,
            ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun BarcodeSecondaryTextButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = ItDayPrimary,
        )
    }
}

@Composable
internal fun IntroGraphicIllustration() {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val phoneWidth = 90.dp.toPx()
            val phoneHeight = 140.dp.toPx()
            val left = (size.width - phoneWidth) / 2
            val top = (size.height - phoneHeight) / 2

            drawRoundRect(
                color = Color(0xFF2C3539),
                topLeft = Offset(left, top),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
            )

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left + 6.dp.toPx(), top + 6.dp.toPx()),
                size = Size(phoneWidth - 12.dp.toPx(), phoneHeight - 12.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF32D74B)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
internal fun SuccessGraphicIllustration() {
    IntroGraphicIllustration()
}

@Composable
internal fun DuplicateGraphicIllustration() {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val phoneWidth = 90.dp.toPx()
            val phoneHeight = 140.dp.toPx()
            val left = (size.width - phoneWidth) / 2
            val top = (size.height - phoneHeight) / 2

            drawRoundRect(
                color = Color(0xFF2C3539),
                topLeft = Offset(left, top),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
            )

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left + 6.dp.toPx(), top + 6.dp.toPx()),
                size = Size(phoneWidth - 12.dp.toPx(), phoneHeight - 12.dp.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}
