package com.example.itday.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun ItDayTermsDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = content,
                    fontSize = 14.sp,
                    color = ItDayGray500,
                    lineHeight = 20.sp,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ItDayPrimary),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "확인", color = ItDayWhite)
            }
        },
        containerColor = ItDayWhite,
        shape = RoundedCornerShape(20.dp),
    )
}
