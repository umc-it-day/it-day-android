@file:Suppress("MagicNumber")

package com.umc.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.umc.itday.R

@Composable
fun PaymentOpeningScreen(
    onBack: () -> Unit,
    onOpenDirectly: () -> Unit,
) {
    PaymentProviderFrame(onBack = onBack) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = Color.White,
            strokeWidth = 2.dp,
        )
        Spacer(modifier = Modifier.height(34.dp))
        StatusMessage(
            title = stringResource(R.string.payment_opening_title),
            description = stringResource(R.string.payment_opening_description),
        )
        Spacer(modifier = Modifier.height(20.dp))
        StatusButton(
            text = stringResource(R.string.payment_open_directly),
            onClick = onOpenDirectly,
            modifier = Modifier.fillMaxWidth(0.4f),
        )
    }
}

@Composable
fun PaymentFailureScreen(
    showDemoCompletion: Boolean,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onDemoComplete: () -> Unit,
) {
    PaymentProviderFrame(onBack = onBack) {
        PaymentFailureMark()
        Spacer(modifier = Modifier.height(24.dp))
        StatusMessage(
            title = stringResource(R.string.payment_failure_title),
            description = stringResource(R.string.payment_failure_description),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusButton(
                text = stringResource(R.string.payment_return),
                onClick = onBack,
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFE8E8E8),
                contentColor = Color(0xFF636B75),
            )
            StatusButton(
                text = stringResource(R.string.payment_retry),
                onClick = onRetry,
                modifier = Modifier.weight(1f),
            )
        }
        if (showDemoCompletion) {
            Spacer(modifier = Modifier.height(12.dp))
            StatusButton(
                text = stringResource(R.string.payment_demo_complete),
                onClick = onDemoComplete,
                modifier = Modifier.fillMaxWidth(0.7f),
                containerColor = Color(0xFF283247),
            )
        }
    }
}

@Composable
private fun PaymentProviderFrame(
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .navigationBarsPadding(),
    ) {
        ProviderTopBar(onBack = onBack)
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content,
            )
            Text(
                text = stringResource(R.string.payment_help),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp),
                color = Color(0xFF657080),
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}

@Composable
private fun StatusMessage(
    title: String,
    description: String,
) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = description,
        color = Color(0xFF717A88),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun StatusButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    containerColor: Color = Color(0xFF4075F5),
    contentColor: Color = Color.White,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(9.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}
