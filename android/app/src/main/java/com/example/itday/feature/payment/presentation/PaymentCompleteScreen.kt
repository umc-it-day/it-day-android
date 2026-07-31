@file:Suppress("MagicNumber", "LongMethod")

package com.example.itday.feature.payment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.itday.R
import com.example.itday.ui.theme.ItDayTheme
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView

@Composable
fun PaymentCompleteScreen(
    uiState: PaymentUiState,
    onBack: () -> Unit,
    onConfettiShown: () -> Unit,
    onHome: () -> Unit,
    onChallenges: () -> Unit,
) {
    var confettiVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val confettiEventId = uiState.confettiEventId
        if (confettiEventId != null) {
            confettiVisible = true
            onConfettiShown()
            delay(CONFETTI_VISIBLE_MILLIS)
            confettiVisible = false
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(PaymentGradient)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PaymentBackButton(onClick = onBack, color = Color.White)
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(46.dp))
                Text(
                    text = stringResource(R.string.payment_complete_title),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = stringResource(R.string.payment_complete_subtitle),
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                )
                PaymentDetailRow(
                    label = stringResource(R.string.payment_amount_label),
                    value = uiState.amount + " / \uC6D4",
                    valueColor = Color(0xFF4A8EFF),
                )
                PaymentDetailRow(
                    label = stringResource(R.string.payment_next_date_label),
                    value = uiState.nextPaymentDate,
                )
                Spacer(modifier = Modifier.height(26.dp))
                BenefitCard()
                Spacer(modifier = Modifier.height(18.dp))
            }
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = onHome,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.payment_go_home),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                TextButton(onClick = onChallenges) {
                    Text(
                        text = stringResource(R.string.payment_view_challenges),
                        color = Color.White.copy(alpha = 0.78f),
                    )
                }
            }
        }
        if (confettiVisible) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = celebrationParties(),
            )
        }
    }
}

@Preview
@Composable
private fun PaymentCompletePreview() {
    ItDayTheme {
        PaymentCompleteScreen(
            uiState = PaymentUiState(step = PaymentStep.Complete),
            onBack = {},
            onConfettiShown = {},
            onHome = {},
            onChallenges = {},
        )
    }
}
