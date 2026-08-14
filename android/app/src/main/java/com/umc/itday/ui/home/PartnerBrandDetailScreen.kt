package com.umc.itday.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.itday.R
import com.umc.itday.ui.theme.*

@Composable
fun PartnerBrandDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedCategory by remember { mutableStateOf("카페") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(ItDayWhite)) {
                IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                    Icon(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = "뒤로가기", modifier = Modifier.size(24.dp))
                }
                Text(
                    "제휴 매장",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
        },
        containerColor = ItDayWhite
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CategoryGrid(selectedCategory) { selectedCategory = it }
            Spacer(Modifier.height(24.dp))
            Divider(color = Color(0xFFF1F1F1), thickness = 8.dp)
            StoreListSection(selectedCategory)
        }
    }
}

@Composable
private fun CategoryGrid(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf(
        "카페" to "☕", "스터디" to "🎓", "편의점" to "🏪", "문화" to "🎫",
        "패스트푸드" to "🍔", "운동" to "💪", "음식점" to "🍲", "뷰티" to "💄"
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        categories.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { (name, emoji) ->
                    CategoryItem(name, emoji, name == selected) { onSelect(name) }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryItem(name: String, emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) Color(0xFFF6F7F9) else Color.Transparent,
            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFF1F1F1))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 28.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(name, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun StoreListSection(category: String) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(category, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Surface(color = Color(0xFFF1F1F1), shape = CircleShape) {
                Text("13", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 12.sp, color = ItDayGray500)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("할인율순", true)
            FilterChip("거리순", false)
            FilterChip("인기순", false)
        }
        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            StoreItem("스타벅스 영남대점", "15%", "4.8", "120m", R.drawable.logo_brand_starbucks)
            StoreItem("이디야 영남대점", "14%", "4.2", "150m", R.drawable.logo_brand_cu) // 임시 로고
            StoreItem("메가커피 영남대점", "10%", "3.3", "260m", R.drawable.logo_brand_gs25) // 임시 로고
        }
    }
}

@Composable
private fun FilterChip(text: String, isSelected: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) Color(0xFF191919) else Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E2E2))
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color(0xFF191919)
        )
    }
}

@Composable
private fun StoreItem(name: String, discount: String, rating: String, distance: String, logo: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(logo),
            contentDescription = null,
            modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFF6F7F9)),
            contentScale = ContentScale.Inside
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(discount, color = HomePrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("★ $rating · $distance 거리", color = ItDayGray500, fontSize = 13.sp)
        }
        Icon(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = ItDayGray300)
    }
}
