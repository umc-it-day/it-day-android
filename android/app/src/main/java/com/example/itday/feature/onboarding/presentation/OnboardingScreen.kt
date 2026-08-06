package com.example.itday.feature.onboarding.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.core.permission.AndroidPermissionManager
import com.example.itday.core.permission.AppPermission
import com.example.itday.core.permission.PermissionManager
import com.example.itday.ui.component.ItDayStepIndicator
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray300
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayTheme
import com.example.itday.ui.theme.ItDayWhite
import kotlinx.coroutines.flow.Flow

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    events: Flow<OnboardingUiEvent>,
    onAgreementChange: (AgreementType, Boolean) -> Unit,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onLocationResult: (Boolean) -> Unit,
    onCarrierSelect: (CarrierType) -> Unit,
    onMembershipGradeSelect: (MembershipGradeType) -> Unit,
    onBrandToggle: (String) -> Unit,
    onComplete: () -> Unit,
    permissionManager: PermissionManager,
    modifier: Modifier = Modifier,
) {
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            onLocationResult(permissionManager.isGranted(AppPermission.Location))
        }

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                OnboardingUiEvent.RequestLocationPermission -> {
                    if (permissionManager.isGranted(AppPermission.Location)) {
                        onLocationResult(true)
                    } else {
                        permissionLauncher.launch(
                            permissionManager.permissionsFor(AppPermission.Location).toTypedArray(),
                        )
                    }
                }
                OnboardingUiEvent.Complete -> onComplete()
            }
        }
    }

    Scaffold(
        containerColor = ItDayWhite,
        topBar = {
            OnboardingTopBar(
                step = state.step,
                onClose = onClose,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            AnimatedContent(
                targetState = state.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingStepTransition",
            ) { targetStep ->
                when (targetStep) {
                    OnboardingUiState.TERMS_STEP -> {
                        TermsContent(
                            state = state,
                            onAgreementChange = onAgreementChange,
                            onNext = onNext,
                        )
                    }
                    OnboardingUiState.LOCATION_PERM_STEP -> {
                        LocationPermissionStep(
                            showError = state.locationError,
                            onSettingsClick = onNext,
                        )
                    }
                    else -> {
                        SelectionSteps(
                            state = state,
                            onCarrierSelect = onCarrierSelect,
                            onMembershipGradeSelect = onMembershipGradeSelect,
                            onBrandToggle = onBrandToggle,
                            onNext = onNext,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingTopBar(
    step: Int,
    onClose: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (step == OnboardingUiState.TERMS_STEP) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color(0xFF191919),
                )
            }
        } else {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color(0xFF191919),
                )
            }
            ItDayStepIndicator(
                currentStep = step - 1,
                totalSteps = 4,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun TermsContent(
    state: OnboardingUiState,
    onAgreementChange: (AgreementType, Boolean) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "약관 동의",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191919),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "서비스 이용을 위해 동의가 필요합니다",
            fontSize = 14.sp,
            color = ItDayGray500,
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AgreementCard(
                title = "위치 정보 이용 동의 (필수)",
                subtitle = "주변 할인 매장 찾기",
                isChecked = state.locationAgreed,
                onCheckedChange = { onAgreementChange(AgreementType.Location, it) },
            )

            AgreementCard(
                title = "개인정보 제공 동의 (필수)",
                subtitle = "할인 혜택 제공",
                isChecked = state.privacyAgreed,
                onCheckedChange = { onAgreementChange(AgreementType.Privacy, it) },
            )

            AgreementCard(
                title = "알림 권한 (선택)",
                subtitle = "할인 정보 알림",
                isChecked = state.notificationAgreed,
                onCheckedChange = { onAgreementChange(AgreementType.Notification, it) },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = state.canContinue,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = ItDayPrimary,
                    disabledContainerColor = Color(0xFFC4D8FF),
                ),
        ) {
            Text(
                text = "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ItDayWhite,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AgreementCard(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!isChecked) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7F9)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isChecked) ItDayPrimary else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = ItDayWhite,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191919),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = ItDayGray500,
                    )
                }
            }

            Text(
                text = "읽기",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ItDayPrimary,
                modifier = Modifier.clickable { /* 약관 상세보기 모달 */ },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingTermsStepPreview() {
    ItDayTheme {
        OnboardingScreen(
            state = OnboardingUiState(step = 0, locationAgreed = true, privacyAgreed = true),
            events = kotlinx.coroutines.flow.emptyFlow(),
            onAgreementChange = { _, _ -> },
            onClose = {},
            onBack = {},
            onNext = {},
            onLocationResult = {},
            onCarrierSelect = {},
            onMembershipGradeSelect = {},
            onBrandToggle = {},
            onComplete = {},
            permissionManager = AndroidPermissionManager(LocalContext.current),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingLocationStepPreview() {
    ItDayTheme {
        OnboardingScreen(
            state = OnboardingUiState(step = 1),
            events = kotlinx.coroutines.flow.emptyFlow(),
            onAgreementChange = { _, _ -> },
            onClose = {},
            onBack = {},
            onNext = {},
            onLocationResult = {},
            onCarrierSelect = {},
            onMembershipGradeSelect = {},
            onBrandToggle = {},
            onComplete = {},
            permissionManager = AndroidPermissionManager(LocalContext.current),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingCarrierStepPreview() {
    ItDayTheme {
        OnboardingScreen(
            state = OnboardingUiState(step = 2),
            events = kotlinx.coroutines.flow.emptyFlow(),
            onAgreementChange = { _, _ -> },
            onClose = {},
            onBack = {},
            onNext = {},
            onLocationResult = {},
            onCarrierSelect = {},
            onMembershipGradeSelect = {},
            onBrandToggle = {},
            onComplete = {},
            permissionManager = AndroidPermissionManager(LocalContext.current),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingMembershipStepPreview() {
    ItDayTheme {
        OnboardingScreen(
            state = OnboardingUiState(step = 3),
            events = kotlinx.coroutines.flow.emptyFlow(),
            onAgreementChange = { _, _ -> },
            onClose = {},
            onBack = {},
            onNext = {},
            onLocationResult = {},
            onCarrierSelect = {},
            onMembershipGradeSelect = {},
            onBrandToggle = {},
            onComplete = {},
            permissionManager = AndroidPermissionManager(LocalContext.current),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingBrandStepPreview() {
    ItDayTheme {
        OnboardingScreen(
            state = OnboardingUiState(step = 4),
            events = kotlinx.coroutines.flow.emptyFlow(),
            onAgreementChange = { _, _ -> },
            onClose = {},
            onBack = {},
            onNext = {},
            onLocationResult = {},
            onCarrierSelect = {},
            onMembershipGradeSelect = {},
            onBrandToggle = {},
            onComplete = {},
            permissionManager = AndroidPermissionManager(LocalContext.current),
        )
    }
}
