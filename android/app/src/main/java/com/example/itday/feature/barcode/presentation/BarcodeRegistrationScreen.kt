package com.example.itday.feature.barcode.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itday.core.di.appContainer

import com.example.itday.ui.theme.ItDayBlue50
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun BarcodeRegistrationRoute(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: BarcodeRegistrationViewModel =
        viewModel(
            factory = BarcodeRegistrationViewModel.factory(context.appContainer.memberRepository),
        )
    val uiState by viewModel.uiState.collectAsState()


    BarcodeRegistrationScreen(
        uiState = uiState,
        onCarrierSelect = viewModel::selectCarrier,
        onGradeSelect = viewModel::selectGrade,
        onBarcodeChange = viewModel::onBarcodeNumberChange,
        onNavigateStep = viewModel::navigateStep,
        onSubmit = viewModel::submitRegistration,
        onReenter = viewModel::resetFormToReentry,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
    )
}

@Composable
fun BarcodeRegistrationScreen(
    uiState: BarcodeRegistrationUiState,
    onCarrierSelect: (Carrier) -> Unit,
    onGradeSelect: (MembershipGrade) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onNavigateStep: (BarcodeStep) -> Unit,
    onSubmit: () -> Unit,
    onReenter: () -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
) {
    AnimatedContent(
        targetState = uiState.step,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "BarcodeStepTransition",
    ) { currentStep ->
        when (currentStep) {
            BarcodeStep.Intro ->
                BarcodeIntroContent(
                    onNext = { onNavigateStep(BarcodeStep.Form) },
                    onBackClick = onBackClick,
                )

            BarcodeStep.Form ->
                BarcodeFormContent(
                    uiState = uiState,
                    onCarrierSelect = onCarrierSelect,
                    onGradeSelect = onGradeSelect,
                    onBarcodeChange = onBarcodeChange,
                    onSubmit = onSubmit,
                    onBackClick = { onNavigateStep(BarcodeStep.Intro) },
                )

            BarcodeStep.Success ->
                BarcodeSuccessContent(
                    uiState = uiState,
                    onHomeClick = onHomeClick,
                )

            BarcodeStep.Duplicate ->
                BarcodeDuplicateContent(
                    uiState = uiState,
                    onHomeClick = onHomeClick,
                    onReenterClick = onReenter,
                )
        }
    }
}

@Composable
private fun BarcodeIntroContent(
    onNext: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayWhite,
        topBar = {
            BarcodeTopBar(
                rightActionText = "안내",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            IntroGraphicIllustration()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "바코드 등록",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ItDayPrimary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "내 위치에 맞는 혜택을\n1초 만에 받아보세요",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF191919),
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
            )

            Spacer(modifier = Modifier.weight(1.2f))

            BarcodePrimaryButton(
                text = "번호 입력하기",
                onClick = onNext,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BarcodeFormContent(
    uiState: BarcodeRegistrationUiState,
    onCarrierSelect: (Carrier) -> Unit,
    onGradeSelect: (MembershipGrade) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayWhite,
        topBar = {
            BarcodeTopBar(
                title = "바코드 입력",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ChipSelectorRow(
                title = "통신사",
                items = Carrier.values().toList(),
                selectedItem = uiState.selectedCarrier,
                displayName = { it.displayName },
                onSelect = onCarrierSelect,
            )

            Spacer(modifier = Modifier.height(32.dp))

            ChipSelectorRow(
                title = "멤버십 등급",
                items = MembershipGrade.values().toList(),
                selectedItem = uiState.selectedGrade,
                displayName = { it.displayName },
                onSelect = onGradeSelect,
            )

            Spacer(modifier = Modifier.height(32.dp))

            BarcodeInputSection(
                value = uiState.barcodeNumber,
                onValueChange = onBarcodeChange,
            )

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isValidLength) {
                BarcodePrimaryButton(
                    text = "등록하기",
                    enabled = true,
                    onClick = onSubmit,
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun BarcodeSuccessContent(
    uiState: BarcodeRegistrationUiState,
    onHomeClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayBlue50,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            SuccessGraphicIllustration()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "바코드 등록 완료",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF191919),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "잇데이와 함께\n캠퍼스 할인 혜택을 누려보세요",
                fontSize = 15.sp,
                color = ItDayGray500,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            RegisteredCardInfoBox(
                badgeText = uiState.registeredBadgeText,
                cardNumber = uiState.maskedBarcodeNumber,
            )

            Spacer(modifier = Modifier.weight(1.2f))

            BarcodePrimaryButton(
                text = "홈으로 이동하기",
                onClick = onHomeClick,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BarcodeDuplicateContent(
    uiState: BarcodeRegistrationUiState,
    onHomeClick: () -> Unit,
    onReenterClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayBlue50,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            DuplicateGraphicIllustration()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "이미 등록된 멤버십 입니다",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF191919),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "이 바코드 번호는 이미 지갑에\n등록되어 있어요",
                fontSize = 15.sp,
                color = ItDayGray500,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            RegisteredCardInfoBox(
                badgeText = uiState.registeredBadgeText,
                cardNumber = uiState.maskedBarcodeNumber,
            )

            Spacer(modifier = Modifier.weight(1.2f))

            BarcodePrimaryButton(
                text = "홈으로 이동하기",
                onClick = onHomeClick,
            )

            Spacer(modifier = Modifier.height(8.dp))

            BarcodeSecondaryTextButton(
                text = "다른 번호 입력하기",
                onClick = onReenterClick,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun BarcodeIntroPreview() {
    BarcodeRegistrationScreen(
        uiState = BarcodeRegistrationUiState(step = BarcodeStep.Intro),
        onCarrierSelect = {},
        onGradeSelect = {},
        onBarcodeChange = {},
        onNavigateStep = {},
        onSubmit = {},
        onReenter = {},
        onBackClick = {},
        onHomeClick = {},
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun BarcodeFormPreview() {
    BarcodeRegistrationScreen(
        uiState = BarcodeRegistrationUiState(
            step = BarcodeStep.Form,
            barcodeNumber = "1234567890123456",
        ),
        onCarrierSelect = {},
        onGradeSelect = {},
        onBarcodeChange = {},
        onNavigateStep = {},
        onSubmit = {},
        onReenter = {},
        onBackClick = {},
        onHomeClick = {},
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun BarcodeSuccessPreview() {
    BarcodeRegistrationScreen(
        uiState = BarcodeRegistrationUiState(
            step = BarcodeStep.Success,
            barcodeNumber = "1234567890123456",
        ),
        onCarrierSelect = {},
        onGradeSelect = {},
        onBarcodeChange = {},
        onNavigateStep = {},
        onSubmit = {},
        onReenter = {},
        onBackClick = {},
        onHomeClick = {},
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun BarcodeDuplicatePreview() {
    BarcodeRegistrationScreen(
        uiState = BarcodeRegistrationUiState(
            step = BarcodeStep.Duplicate,
            barcodeNumber = "9999999999999999",
        ),
        onCarrierSelect = {},
        onGradeSelect = {},
        onBarcodeChange = {},
        onNavigateStep = {},
        onSubmit = {},
        onReenter = {},
        onBackClick = {},
        onHomeClick = {},
    )
}
