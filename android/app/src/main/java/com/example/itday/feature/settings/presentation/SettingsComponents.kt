package com.example.itday.feature.settings.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.itday.ui.theme.ItDayGray100
import com.example.itday.ui.theme.ItDayGray300
import com.example.itday.ui.theme.ItDayGray500
import com.example.itday.ui.theme.ItDayPrimary
import com.example.itday.ui.theme.ItDayWhite

@Composable
internal fun SettingsTopBar(
    title: String? = null,
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
        if (title != null) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
internal fun ProfileHeaderSection(
    profile: UserProfile,
    onProfileEditClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(ItDayGray100),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "프로필 사진",
                    tint = ItDayGray300,
                    modifier = Modifier.size(54.dp),
                )
            }
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(ItDayPrimary)
                        .clickable { onProfileEditClick() },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(com.example.itday.R.drawable.ic_settings_camera),
                    contentDescription = "사진 수정",
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onProfileEditClick() }
                    .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "이름", fontSize = 14.sp, color = ItDayGray500)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = profile.userName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191919),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ItDayGray300,
                )
            }
        }


        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "이메일", fontSize = 14.sp, color = ItDayGray500)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = profile.userEmail,
                    fontSize = 13.sp,
                    color = ItDayGray500,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ItDayGray300,
                )
            }
        }
    }
}

@Composable
internal fun MembershipInfoCard(membership: MembershipInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ItDayGray100),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = membership.carrier,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ItDayPrimary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = membership.grade,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF191919),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (membership.isPro) {
                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ItDayWhite)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    painter = painterResource(com.example.itday.R.drawable.ic_settings_pro_sparkle),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(13.dp),
                                )
                                Text(
                                    text = "PRO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ItDayPrimary,
                                )
                            }
                        }
                    }
                }
                CrownGraphicIllustration()
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ItDayGray300.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "바코드 번호", fontSize = 13.sp, color = ItDayGray500)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = membership.barcodeNumber,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "등록 정보", fontSize = 13.sp, color = ItDayGray500)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "통신사", fontSize = 13.sp, color = ItDayGray500)
                Text(
                    text = membership.carrier,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ItDayPrimary,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "멤버십 등급", fontSize = 13.sp, color = ItDayGray500)
                Text(
                    text = membership.grade,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ItDayPrimary,
                )
            }
        }
    }
}

@Composable
internal fun NotificationCardSection(
    promotionEnabled: Boolean,
    characterEnabled: Boolean,
    onPromotionToggle: (Boolean) -> Unit,
    onCharacterToggle: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ItDayGray100),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "알림",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191919),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "프로모션 알림",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191919),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "신규 제휴 매장 및 이벤트 소식을 받습니다.",
                        fontSize = 12.sp,
                        color = ItDayGray500,
                    )
                }
                Switch(
                    checked = promotionEnabled,
                    onCheckedChange = onPromotionToggle,
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = ItDayWhite,
                            checkedTrackColor = ItDayPrimary,
                            uncheckedThumbColor = ItDayWhite,
                            uncheckedTrackColor = ItDayGray300,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "캐릭터 성장 알림",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191919),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "캐릭터 레벨업 알림을 받습니다.",
                        fontSize = 12.sp,
                        color = ItDayGray500,
                    )
                }
                Switch(
                    checked = characterEnabled,
                    onCheckedChange = onCharacterToggle,
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = ItDayWhite,
                            checkedTrackColor = ItDayPrimary,
                            uncheckedThumbColor = ItDayWhite,
                            uncheckedTrackColor = ItDayGray300,
                        ),
                )
            }
        }
    }
}

@Composable
internal fun SettingsMenuGroup(
    title: String? = null,
    items: List<Pair<String, () -> Unit>>,
    trailingTexts: Map<String, String> = emptyMap(),
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ItDayGray100),
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191919),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
            items.forEach { (label, onClick) ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onClick() }
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        color = Color(0xFF191919),
                        fontWeight = FontWeight.Medium,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        trailingTexts[label]?.let { text ->
                            Text(text = text, fontSize = 13.sp, color = ItDayGray500)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = ItDayGray300,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun LogoutConfirmDialog(
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ItDayWhite),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "로그아웃 하시겠습니까?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A1010),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "로그아웃하면 다시 로그인해야\n서비스를 이용할 수 있습니다.",
                    fontSize = 14.sp,
                    color = ItDayGray500,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLogout,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF3B30),
                            contentColor = ItDayWhite,
                        ),
                ) {
                    Text(text = "로그아웃", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ItDayGray100,
                            contentColor = ItDayGray500,
                        ),
                ) {
                    Text(text = "취소", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
internal fun CrownGraphicIllustration() {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(com.example.itday.R.drawable.img_settings_crown),
        contentDescription = "왕관",
        modifier = Modifier.size(64.dp),
    )
}

@Composable
internal fun ShieldGraphicIllustration() {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(com.example.itday.R.drawable.img_settings_shield),
        contentDescription = "보안 방패",
        modifier = Modifier.size(64.dp),
    )
}

@Composable
internal fun FaqListCard(
    faqList: List<FaqItem>,
    onToggleFaq: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ItDayGray100),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF64B5F6)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "?", fontSize = 14.sp, color = ItDayWhite, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "자주 묻는 질문",
                    fontSize = 14.sp,
                    color = ItDayGray500,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            faqList.forEach { item ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onToggleFaq(item.id) }
                            .padding(vertical = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = item.question,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191919),
                            modifier = Modifier.weight(1f),
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = ItDayGray300,
                        )
                    }
                    if (item.isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.answer,
                            fontSize = 13.sp,
                            color = ItDayGray500,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "더보기",
                    fontSize = 13.sp,
                    color = ItDayGray500,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ItDayGray500,
                )
            }
        }
    }
}

@Composable
internal fun EditNameDialog(
    initialName: String,
    isUpdating: Boolean,
    errorMessage: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var nameText by remember { mutableStateOf(initialName) }

    Dialog(onDismissRequest = { if (!isUpdating) onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ItDayWhite),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "이름 수정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191919),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    singleLine = true,
                    placeholder = { Text(text = "이름을 입력하세요", fontSize = 14.sp, color = ItDayGray300) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorMessage != null,
                    enabled = !isUpdating,
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFFF3B30),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = ItDayGray100,
                                contentColor = Color(0xFF191919),
                            ),
                        enabled = !isUpdating,
                    ) {
                        Text(text = "취소", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onConfirm(nameText) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = ItDayPrimary,
                                contentColor = ItDayWhite,
                            ),
                        enabled = !isUpdating && nameText.isNotBlank(),
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = ItDayWhite,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(text = "저장", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}


