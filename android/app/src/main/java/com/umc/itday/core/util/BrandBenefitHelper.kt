package com.umc.itday.core.util

object BrandBenefitHelper {
    fun getBenefitSummary(brandName: String, category: String? = null): String {
        val name = brandName.trim().lowercase()
        val cat = category?.trim()?.lowercase().orEmpty()

        return when {
            // 카페 & 디저트
            name.contains("스타벅스") || name.contains("starbucks") -> "사이즈업 또는 아메리카노 무료"
            name.contains("투썸") || name.contains("twosome") -> "음료 및 케이크 10% 할인"
            name.contains("이디야") || name.contains("ediya") -> "제조 음료 10% 할인"
            name.contains("메가") || name.contains("mega") -> "음료 결제 시 500원 할인"
            name.contains("컴포즈") || name.contains("compose") -> "음료 결제 시 500원 할인"
            name.contains("빽다방") || name.contains("paik") -> "음료 500원 즉시 할인"
            name.contains("공차") || name.contains("gong cha") -> "베스트 콤비네이션 10% 할인"
            name.contains("파스쿠찌") || name.contains("pascucci") -> "커피 및 음료 10% 할인"
            name.contains("할리스") || name.contains("hollys") -> "음료 10% 할인 또는 사이즈업"
            name.contains("설빙") -> "시그니처 빙수 1,000원 할인"

            // 편의점
            name.contains("cu") || name.contains("씨유") -> "1,000원당 100원 할인"
            name.contains("gs25") || name.contains("지에스") -> "도시락 및 커피 10% 할인"
            name.contains("세븐일레븐") || name.contains("7-eleven") -> "1,000원당 100원 할인"
            name.contains("이마트24") || name.contains("emart24") -> "행사 상품 추가 10% 할인"

            // 베이커리 & 패스트푸드 & 피자
            name.contains("파리바게뜨") || name.contains("paris baguette") -> "1,000원당 100원 할인"
            name.contains("뚜레쥬르") || name.contains("tous les jours") -> "1,000원당 150원 할인"
            name.contains("서브웨이") || name.contains("subway") -> "세트 메뉴 10% 할인"
            name.contains("도미노") || name.contains("domino") -> "방문포장 최대 30% 할인"
            name.contains("피자헛") || name.contains("pizza hut") -> "배달 및 포장 20% 할인"
            name.contains("미스터피자") -> "프리미엄 피자 15% 할인"
            name.contains("버거킹") || name.contains("burger king") -> "와퍼 콤보 1,000원 할인"
            name.contains("맥도날드") || name.contains("mcdonald") -> "버거 세트 10% 할인"
            name.contains("롯데리아") || name.contains("lotteria") -> "세트 메뉴 10% 할인"

            // 외식 / 패밀리 레스토랑
            name.contains("아웃백") || name.contains("outback") -> "결제 금액 최대 15% 할인"
            name.contains("빕스") || name.contains("vips") -> "샐러드바 및 스테이크 15% 할인"
            name.contains("매드포갈릭") -> "식사 금액 20% 제휴 할인"

            // 뷰티 & 쇼핑
            name.contains("올리브영") || name.contains("olive young") -> "매월 5,000원 할인 쿠폰"
            name.contains("다이소") || name.contains("daiso") -> "결제 금액 5% 포인트 적립"
            name.contains("이마트") || name.contains("emart") -> "7만원 이상 결제 시 5,000원 할인"
            name.contains("홈플러스") || name.contains("homeplus") -> "결제 금액 5,000원 할인"

            // 영화 & 문화 & 테마파크
            name.contains("cgv") -> "영화 4,000원 할인 + 콤보 할인"
            name.contains("롯데시네마") || name.contains("lotte cinema") -> "영화 관람권 4,000원 할인"
            name.contains("메가박스") || name.contains("megabox") -> "영화 관람권 4,000원 할인"
            name.contains("에버랜드") || name.contains("everland") -> "자유이용권 최대 40% 할인"
            name.contains("롯데월드") || name.contains("lotte world") -> "종합이용권 최대 40% 할인"

            // 배달 & 생활
            name.contains("배달의민족") || name.contains("baemin") -> "배달 주문 3,000원 할인 쿠폰"
            name.contains("요기요") || name.contains("yogiyo") -> "배달 주문 3,000원 할인 쿠폰"
            name.contains("쿠팡이츠") -> "포장/배달 3,000원 쿠폰"

            // 카테고리별 Fallback
            cat.contains("카페") || cat.contains("디저트") || cat.contains("coffee") -> "음료 10% 제휴 할인"
            cat.contains("편의점") -> "1,000원당 100원 할인"
            cat.contains("외식") || cat.contains("식당") || cat.contains("푸드") -> "식사 금액 10~15% 할인"
            cat.contains("베이커리") -> "베이커리 10% 할인"
            cat.contains("영화") || cat.contains("문화") -> "관람권 최대 4,000원 할인"
            cat.contains("쇼핑") || cat.contains("뷰티") -> "결제 금액 5~10% 할인"
            cat.contains("테마파크") || cat.contains("레저") -> "이용권 최대 30~40% 할인"

            else -> "결제 금액 10% 제휴 할인"
        }
    }

    fun getBrandLogoRes(brandName: String): Int? {
        val name = brandName.trim().lowercase()
        return when {
            name.contains("스타벅스") || name.contains("starbucks") -> com.umc.itday.R.drawable.logo_brand_starbucks
            name.contains("cu") || name.contains("씨유") -> com.umc.itday.R.drawable.logo_brand_cu
            name.contains("gs25") || name.contains("지에스") -> com.umc.itday.R.drawable.logo_brand_gs25
            name.contains("올리브영") || name.contains("olive") -> com.umc.itday.R.drawable.logo_brand_oliveyoung
            name.contains("서브웨이") || name.contains("subway") -> com.umc.itday.R.drawable.logo_brand_subway
            else -> null
        }
    }
}

