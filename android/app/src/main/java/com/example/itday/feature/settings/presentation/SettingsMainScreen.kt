package com.example.itday.feature.settings.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itday.core.di.appContainer
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayWhite

@Composable
fun SettingsMainRoute(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(context.appContainer.featureSettingsRepository)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsUiEvent.OpenExternalUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
                is SettingsUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                SettingsUiEvent.UserWithdrawn -> {
                    onLogoutClick()
                }
            }
        }
    }

    SettingsMainScreen(
        uiState = uiState,
        onNavigateScreen = viewModel::navigateToScreen,
        onPromotionToggle = viewModel::togglePromotionNotification,
        onCharacterToggle = viewModel::toggleCharacterNotification,
        onShowLogoutDialog = viewModel::showLogoutConfirmation,
        onDismissLogoutDialog = viewModel::dismissLogoutConfirmation,
        onConfirmLogout = {
            viewModel.dismissLogoutConfirmation()
            onLogoutClick()
        },
        onShowNameEditDialog = viewModel::showNameEditDialog,
        onDismissNameEditDialog = viewModel::dismissNameEditDialog,
        onEditingNameChange = viewModel::updateEditingName,
        onConfirmNameEdit = viewModel::confirmNameEdit,
        onToggleFaq = viewModel::toggleFaqItem,
        onPrivacyPolicyClick = viewModel::openPrivacyPolicy,
        onTermsOfServiceClick = viewModel::openTermsOfService,
        onWithdrawClick = viewModel::withdraw,
        modifier = modifier,
    )
}

@Composable
fun SettingsMainScreen(
    uiState: SettingsUiState,
    onNavigateScreen: (SettingsScreenType) -> Unit,
    onPromotionToggle: (Boolean) -> Unit,
    onCharacterToggle: (Boolean) -> Unit,
    onShowLogoutDialog: () -> Unit,
    onDismissLogoutDialog: () -> Unit,
    onConfirmLogout: () -> Unit,
    onShowNameEditDialog: () -> Unit,
    onDismissNameEditDialog: () -> Unit,
    onEditingNameChange: (String) -> Unit,
    onConfirmNameEdit: () -> Unit,
    onToggleFaq: (Int) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWrapper(modifier = modifier) {
        AnimatedContent(
            targetState = uiState.currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "SettingsScreenTransition",
        ) { screen ->
            when (screen) {
                SettingsScreenType.Main ->
                    SettingsMainContent(
                        uiState = uiState,
                        onNavigateScreen = onNavigateScreen,
                        onPromotionToggle = onPromotionToggle,
                        onCharacterToggle = onCharacterToggle,
                        onShowLogoutDialog = onShowLogoutDialog,
                        onPrivacyPolicyClick = onPrivacyPolicyClick,
                        onTermsOfServiceClick = onTermsOfServiceClick,
                        onNameClick = onShowNameEditDialog,
                    )

                SettingsScreenType.PrivacySecurity ->
                    PrivacySecurityContent(
                        onBackClick = { onNavigateScreen(SettingsScreenType.Main) },
                        onPrivacyPolicyClick = onPrivacyPolicyClick,
                        onTermsOfServiceClick = onTermsOfServiceClick,
                        onWithdrawClick = onWithdrawClick,
                    )

                SettingsScreenType.CustomerService ->
                    CustomerServiceContent(
                        faqList = uiState.faqList,
                        onToggleFaq = onToggleFaq,
                        onBackClick = { onNavigateScreen(SettingsScreenType.Main) },
                    )
            }
        }

        if (uiState.showLogoutDialog) {
            LogoutConfirmDialog(
                onLogout = onConfirmLogout,
                onDismiss = onDismissLogoutDialog,
            )
        }

        if (uiState.showNameEditDialog) {
            NameEditDialog(
                name = uiState.editingName,
                onNameChange = onEditingNameChange,
                onConfirm = onConfirmNameEdit,
                onDismiss = onDismissNameEditDialog,
            )
        }

        if (uiState.isLoading) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(ItDayWhite, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ItDayPrimary)
                }
            }
        }
    }
}

@Composable
private fun BoxWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        content()
    }
}

@Composable
private fun SettingsMainContent(
    uiState: SettingsUiState,
    onNavigateScreen: (SettingsScreenType) -> Unit,
    onPromotionToggle: (Boolean) -> Unit,
    onCharacterToggle: (Boolean) -> Unit,
    onShowLogoutDialog: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onNameClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayWhite,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "설정",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileHeaderSection(
                profile = uiState.profile,
                onNameClick = onNameClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            MembershipInfoCard(membership = uiState.membership)

            Spacer(modifier = Modifier.height(16.dp))

            NotificationCardSection(
                promotionEnabled = uiState.promotionNotification,
                characterEnabled = uiState.characterNotification,
                onPromotionToggle = onPromotionToggle,
                onCharacterToggle = onCharacterToggle,
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsMenuGroup(
                items =
                    listOf(
                        "개인정보 및 보안" to { onNavigateScreen(SettingsScreenType.PrivacySecurity) },
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsMenuGroup(
                title = "기타",
                items =
                    listOf(
                        "고객센터" to { onNavigateScreen(SettingsScreenType.CustomerService) },
                        "로그아웃" to onShowLogoutDialog,
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsMenuGroup(
                title = "정보",
                items =
                    listOf(
                        "앱 버전" to {},
                        "개인정보 처리방침" to onPrivacyPolicyClick,
                        "서비스 이용약관" to onTermsOfServiceClick,
                    ),
                trailingTexts = mapOf("앱 버전" to uiState.appVersion),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PrivacySecurityContent(
    onBackClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayWhite,
        topBar = {
            SettingsTopBar(
                title = "개인정보 및 보안",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ShieldGraphicIllustration()
                Spacer(modifier = Modifier.padding(start = 16.dp))
                Column {
                    Text(
                        text = "보안 상태",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191919),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "계정이 안전하게 보호되고 있습니다.",
                        fontSize = 13.sp,
                        color = ItDayGray500,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SettingsMenuGroup(
                title = "보안 및 설정",
                items =
                    listOf(
                        "개인정보 처리방침" to onPrivacyPolicyClick,
                        "서비스 이용약관" to onTermsOfServiceClick,
                    ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ItDayGray100),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onWithdrawClick)
                            .padding(20.dp),
                ) {
                    Text(
                        text = "탈퇴하기",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF3B30),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "모든 데이터가 삭제되며 복구할 수 없습니다.",
                fontSize = 12.sp,
                color = ItDayGray500,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun CustomerServiceContent(
    faqList: List<FaqItem>,
    onToggleFaq: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = ItDayWhite,
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "무엇을 도와드릴까요?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "It-Day 팀이 언제나 여러분을 도와드립니다.",
                fontSize = 14.sp,
                color = ItDayGray500,
            )

            Spacer(modifier = Modifier.height(32.dp))

            FaqListCard(
                faqList = faqList,
                onToggleFaq = onToggleFaq,
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun SettingsMainScreenPreview() {
    SettingsMainScreen(
        uiState = SettingsUiState(),
        onNavigateScreen = {},
        onPromotionToggle = {},
        onCharacterToggle = {},
        onShowLogoutDialog = {},
        onDismissLogoutDialog = {},
        onConfirmLogout = {},
        onShowNameEditDialog = {},
        onDismissNameEditDialog = {},
        onEditingNameChange = {},
        onConfirmNameEdit = {},
        onToggleFaq = {},
        onPrivacyPolicyClick = {},
        onTermsOfServiceClick = {},
        onWithdrawClick = {},
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PrivacySecurityContentPreview() {
    SettingsMainScreen(
        uiState = SettingsUiState(currentScreen = SettingsScreenType.PrivacySecurity),
        onNavigateScreen = {},
        onPromotionToggle = {},
        onCharacterToggle = {},
        onShowLogoutDialog = {},
        onDismissLogoutDialog = {},
        onConfirmLogout = {},
        onShowNameEditDialog = {},
        onDismissNameEditDialog = {},
        onEditingNameChange = {},
        onConfirmNameEdit = {},
        onToggleFaq = {},
        onPrivacyPolicyClick = {},
        onTermsOfServiceClick = {},
        onWithdrawClick = {},
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun CustomerServiceContentPreview() {
    SettingsMainScreen(
        uiState = SettingsUiState(currentScreen = SettingsScreenType.CustomerService),
        onNavigateScreen = {},
        onPromotionToggle = {},
        onCharacterToggle = {},
        onShowLogoutDialog = {},
        onDismissLogoutDialog = {},
        onConfirmLogout = {},
        onShowNameEditDialog = {},
        onDismissNameEditDialog = {},
        onEditingNameChange = {},
        onConfirmNameEdit = {},
        onToggleFaq = {},
        onPrivacyPolicyClick = {},
        onTermsOfServiceClick = {},
        onWithdrawClick = {},
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun LogoutConfirmDialogPreview() {
    LogoutConfirmDialog(
        onLogout = {},
        onDismiss = {},
    )
}
