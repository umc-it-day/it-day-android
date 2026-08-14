package com.umc.itday.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.umc.itday.ui.theme.ItDayDimens
import com.umc.itday.ui.preview.ItDayComponentPreview

@Composable
fun ItDayDialog(
    title: String,
    description: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmVariant: ItDayButtonVariant = ItDayButtonVariant.Primary,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(ItDayDimens.Space8)) {
                ItDayButton(
                    text = confirmText,
                    variant = confirmVariant,
                    size = ItDayButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                )
                ItDayButton(
                    text = dismissText,
                    variant = ItDayButtonVariant.Secondary,
                    size = ItDayButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                )
            }
        },
        dismissButton = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ItDayDialogPreview() {
    ItDayComponentPreview {
        ItDayDialog(
            title = "로그아웃 하시겠습니까?",
            description = "로그아웃하면 다시 로그인해야 서비스를 이용할 수 있습니다.",
            confirmText = "로그아웃",
            dismissText = "취소",
            confirmVariant = ItDayButtonVariant.Destructive,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
