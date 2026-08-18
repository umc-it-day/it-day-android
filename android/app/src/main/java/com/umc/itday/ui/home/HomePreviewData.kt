package com.umc.itday.ui.home

import com.umc.itday.R

object HomePreviewData {
    private val location =
        HomeLocationUiModel(
            name = "현재 위치",
            address = "경상북도 경산시 123",
        )

    private val membership =
        HomeMembershipUiModel(
            carrier = "SKT",
            grade = "VIP",
            brandName = "스타벅스 영남대점",
            benefitText = "10% 할인 또는 디저트 할인",
            barcodeValue = "1234 5667 9012 3456",
            pointText = "4,855 P",
        )

    private val brands =
        listOf(
            HomePartnerBrandUiModel(
                id = "starbucks",
                displayName = "스타벅스",
                logoRes = R.drawable.logo_brand_starbucks,
                selected = true,
            ),
            HomePartnerBrandUiModel("cu", "CU", R.drawable.logo_brand_cu),
            HomePartnerBrandUiModel("oliveyoung", "올리브영", R.drawable.logo_brand_oliveyoung),
            HomePartnerBrandUiModel("subway", "서브웨이", R.drawable.logo_brand_subway),
        )

    private val benefits =
        listOf(
            HomeBenefitUiModel(
                id = "starbucks",
                rank = 1,
                brandName = "스타벅스",
                benefitText = "10% 할인",
            ),
            HomeBenefitUiModel(
                id = "gs25",
                rank = 2,
                brandName = "GS25",
                benefitText = "5% 적립",
            ),
            HomeBenefitUiModel("cu", 3, "CU", "1+1 행사 알림"),
        )

    private val brandDays =
        listOf(
            HomeBrandDayUiModel(
                id = "cu-highlight",
                brandName = "CU",
                benefitText = "추가 5% 할인",
                scheduleText = "",
                highlighted = true,
                categoryText = "CU 편의점",
            ),
            HomeBrandDayUiModel("starbucks", "스타벅스", "", "D-2 · 이번달 5회"),
            HomeBrandDayUiModel("gs25", "GS25", "", "D-4 · 이번달 4회"),
            HomeBrandDayUiModel("cu", "CU", "", "D-8 · 이번달 2회"),
        )

    val barcodeEnabled =
        HomeUiState(
            membershipState = MembershipState.BarcodeEnabled,
            location = location,
            membership = membership,
            partnerBrands = brands,
            benefits = benefits,
            brandDays = brandDays,
        )

    val barcodeDisabled =
        barcodeEnabled.copy(
            membershipState = MembershipState.BarcodeDisabled,
            benefits = emptyList(),
        )

    val notRegistered =
        barcodeEnabled.copy(
            membershipState = MembershipState.NotRegistered,
            membership = null,
            benefits = emptyList(),
        )

    val guest =
        notRegistered.copy(
            membershipState = MembershipState.Guest,
            showProSection = false,
        )

    val dialogVisible = barcodeEnabled.copy(showMembershipDialog = true)

    val singleBenefit = barcodeEnabled.copy(benefits = benefits.take(1))

    val noBenefits = barcodeEnabled.copy(benefits = emptyList())

    val empty =
        barcodeEnabled.copy(
            partnerBrands = emptyList(),
            benefits = emptyList(),
            brandDays = emptyList(),
        )
}
