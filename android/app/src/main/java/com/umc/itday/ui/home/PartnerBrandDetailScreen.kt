package com.umc.itday.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.umc.itday.R
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.di.appContainer
import com.umc.itday.feature.onboarding.domain.model.PreferredBrand
import com.umc.itday.ui.theme.HomePrimary
import com.umc.itday.ui.theme.ItDayGray100
import com.umc.itday.ui.theme.ItDayGray300
import com.umc.itday.ui.theme.ItDayGray500
import com.umc.itday.ui.theme.ItDayWhite

@Composable
fun PartnerBrandDetailScreen(
    onBack: () -> Unit,
    onBrandClick: (PreferredBrand) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("카페") }
    var selectedSort by remember { mutableStateOf("할인율순") }
    var allBrands by remember { mutableStateOf<List<PreferredBrand>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = context.appContainer.onboardingRepository.getBrands()
        if (result is ApiResult.Success) {
            allBrands = result.data
        }
        isLoading = false
    }

    val filteredBrands = remember(selectedCategory, allBrands) {
        allBrands.filter { matchesCategory(selectedCategory, it) }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(ItDayWhite)) {
                IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color(0xFF191919),
                        modifier = Modifier.size(24.dp),
                    )
                }

                Text(
                    "제휴 매장",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
        },
        containerColor = ItDayWhite,
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
        ) {
            CategoryGrid(selectedCategory) { selectedCategory = it }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF1F1F1), thickness = 8.dp)
            StoreListSection(
                category = selectedCategory,
                brands = filteredBrands,
                isLoading = isLoading,
                selectedSort = selectedSort,
                onSortChange = { selectedSort = it },
                onBrandClick = onBrandClick,
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    selected: String,
    onSelect: (String) -> Unit,
) {
    val categories =
        listOf(
            "카페" to "☕",
            "스터디" to "🎓",
            "편의점" to "🏪",
            "문화" to "🎫",
            "패스트푸드" to "🍔",
            "운동" to "💪",
            "음식점" to "🍲",
            "뷰티" to "💄",
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
private fun CategoryItem(
    name: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .padding(8.dp),
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) Color(0xFFF6F7F9) else Color.Transparent,
            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFF1F1F1)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 28.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            name,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun StoreListSection(
    category: String,
    brands: List<PreferredBrand>,
    isLoading: Boolean,
    selectedSort: String,
    onSortChange: (String) -> Unit,
    onBrandClick: (PreferredBrand) -> Unit = {},
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(category, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Surface(color = Color(0xFFF1F1F1), shape = CircleShape) {
                Text(
                    text = "${brands.size}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    color = ItDayGray500,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("할인율순", selectedSort == "할인율순") { onSortChange("할인율순") }
            FilterChip("거리순", selectedSort == "거리순") { onSortChange("거리순") }
            FilterChip("인기순", selectedSort == "인기순") { onSortChange("인기순") }
        }
        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = HomePrimary)
            }
        } else if (brands.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "해당 카테고리의 제휴 브랜드가 없습니다.",
                    color = ItDayGray500,
                    fontSize = 14.sp,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                brands.forEach { brand ->
                    BrandStoreItem(
                        brand = brand,
                        onClick = { onBrandClick(brand) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) Color(0xFF191919) else Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E2E2)),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color(0xFF191919),
        )
    }
}

@Composable
private fun BrandStoreItem(
    brand: PreferredBrand,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
    ) {
        val resolvedLogoRes = com.umc.itday.core.util.BrandBenefitHelper.getBrandLogoRes(brand.name)
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = ItDayGray100,
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (!brand.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = brand.imageUrl,
                        contentDescription = brand.name,
                        placeholder = painterResource(R.drawable.map_store_placeholder),
                        error = painterResource(resolvedLogoRes ?: R.drawable.map_store_placeholder),
                        fallback = painterResource(resolvedLogoRes ?: R.drawable.map_store_placeholder),
                        modifier = Modifier.size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit,
                    )
                } else if (resolvedLogoRes != null) {
                    Image(
                        painter = painterResource(resolvedLogoRes),
                        contentDescription = brand.name,
                        modifier = Modifier.size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.map_store_placeholder),
                        contentDescription = "${brand.name} 이미지 로딩 실패",
                        modifier = Modifier.size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }

        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = com.umc.itday.core.util.BrandBenefitHelper.getBenefitSummary(brand.name, brand.category),
                color = HomePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )

            Spacer(Modifier.height(2.dp))
            Text(
                text = brand.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF191919),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (brand.category.isNotBlank()) brand.category else "제휴 브랜드",
                color = ItDayGray500,
                fontSize = 12.sp,
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFEBF2FF),
            modifier = Modifier.clickable(onClick = onClick),
        ) {
            Text(
                text = "+ 추가",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = HomePrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}


private fun matchesCategory(category: String, brand: PreferredBrand): Boolean {
    val target = "${brand.category} ${brand.name}".lowercase()
    return when (category) {
        "카페" -> target.containsAny("cafe", "coffee", "dessert", "bakery", "카페", "커피", "디저트", "베이커리", "스타벅스", "투썸", "이디야", "메가", "컴포즈", "빽다방", "공차", "파스쿠찌", "할리스", "설빙")
        "편의점" -> target.containsAny("convenience", "mart", "store", "편의점", "마트", "cu", "gs25", "세븐일레븐", "이마트24", "미니스톱")
        "음식점" -> target.containsAny("food", "restaurant", "dining", "음식", "식당", "외식", "한식", "일식", "중식", "양식", "아웃백", "빕스", "애슐리", "매드포갈릭", "피자", "치킨")
        "패스트푸드" -> target.containsAny("fastfood", "burger", "pizza", "chicken", "버거", "피자", "치킨", "맥도날드", "버거킹", "롯데리아", "맘스터치", "kfc", "서브웨이", "도미노", "파파존스")
        "문화" -> target.containsAny("culture", "movie", "cinema", "book", "travel", "leisure", "문화", "영화", "도서", "여행", "여가", "cgv", "롯데시네마", "메가박스", "교보문고", "영풍문고", "롯데월드", "에버랜드")
        "뷰티" -> target.containsAny("beauty", "cosmetic", "뷰티", "화장품", "올리브영", "롭스", "랄라블라", "이니스프리", "미샤", "토니모리", "더페이스샵")
        "스터디" -> target.containsAny("study", "academy", "학원", "스터디", "독서실", "토익", "해커스", "파고다", "밀리의서재")
        "운동" -> target.containsAny("fitness", "gym", "sports", "헬스", "운동", "피트니스", "스포애니")
        else -> target.contains(category.lowercase())
    }
}

private fun String.containsAny(vararg keywords: String): Boolean =
    keywords.any { this.contains(it, ignoreCase = true) }
