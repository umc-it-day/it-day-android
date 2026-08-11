package com.example.itday.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itday.R
import com.example.itday.ui.component.ItDayBadge
import com.example.itday.ui.component.ItDayBadgeVariant
import com.example.itday.ui.component.ItDayCard
import com.example.itday.ui.home.component.HomeTopBar
import com.example.itday.ui.theme.*

@Composable
fun CarrierComparisonScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(ItDayWhite)) {
                IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                    Icon(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = "뒤로가기", modifier = Modifier.size(24.dp))
                }
            }
        },
        containerColor = Color(0xFFF6F8FF)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            CurrentMembershipSummary()
            Spacer(Modifier.height(32.dp))
            RecommendationHeader()
            Spacer(Modifier.height(24.dp))
            RecommendationList()
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CurrentMembershipSummary() {
    ItDayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = ItDayWhite
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_membership_vvip),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("내 멤버십", color = ItDayGray500, fontSize = 14.sp)
                Text("SKT VIP", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Surface(
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("기존", color = HomePrimary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun RecommendationHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text("통신사별 혜택 비교", color = HomePrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text("고객님께 꼭 맞는 통신사\n4곳을 추천해드려요", fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp)
        }
        // 캐릭터 이미지 공간 (추후 리소스 추가 시 반영)
        Box(modifier = Modifier.size(80.dp).background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(40.dp)))
    }
}

@Composable
private fun RecommendationList() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        RecommendationItem(isBest = true)
        RecommendationItem(isBest = false)
        RecommendationItem(isBest = false)
        RecommendationItem(isBest = false)
    }
}

@Composable
private fun RecommendationItem(isBest: Boolean) {
    Box {
        ItDayCard(
            modifier = Modifier.fillMaxWidth().padding(top = if (isBest) 12.dp else 0.dp),
            backgroundColor = ItDayWhite,
            border = if (isBest) BorderStroke(1.5.dp, HomePrimary) else null
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("멤버십 이름", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ItDayBadge(text = "#VIP혜택", variant = ItDayBadgeVariant.Blue)
                    ItDayBadge(text = "#구독혜택", variant = ItDayBadgeVariant.Blue)
                }
                Icon(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = ItDayGray300)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { BenefitMiniCard() }
            }
        }
        if (isBest) {
            Surface(
                color = HomePrimary,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.offset(x = 12.dp)
            ) {
                Text("추천 🔥", color = ItDayWhite, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BenefitMiniCard() {
    Surface(
        color = Color(0xFFF6F7F9),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(64.dp).height(72.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("CU", fontSize = 11.sp, color = ItDayGray500)
            Text("15%", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = HomePrimary)
        }
    }
}
