package com.umc.itday.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.umc.itday.R
import com.umc.itday.ui.theme.ItDayDimens

@Composable
fun ItDayMainTopBar(
    onAttendanceClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_itday_symbol),
            contentDescription = "IT-Day",
            modifier = Modifier.size(40.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(ItDayDimens.Space8)) {
            MainTopBarButton("출석체크", R.drawable.ic_home_attendance, onAttendanceClick, false)
            MainTopBarButton("프로필", R.drawable.ic_home_profile, onProfileClick)
        }
    }
}

@Composable
private fun MainTopBarButton(
    description: String,
    iconRes: Int,
    onClick: () -> Unit,
    showPressedBackground: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier =
        if (showPressedBackground) {
            Modifier.clip(CircleShape).clickable(onClick = onClick)
        } else {
            Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        }
    Box(
        modifier = Modifier.size(40.dp).then(clickModifier).semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}
