package com.umc.itday.feature.onboarding.presentation

import com.umc.itday.R

enum class AgreementType {
    Location,
    Privacy,
    Notification,
}

enum class CarrierType(val displayName: String) {
    SKT("SKT"),
    KT("KT"),
    LGU_PLUS("LG U+");

    val availableGrades: List<MembershipGradeInfo>
        get() = when (this) {
            SKT -> listOf(
                MembershipGradeInfo(MembershipGradeType.VVIP, R.drawable.ic_membership_vvip),
                MembershipGradeInfo(MembershipGradeType.VIP, R.drawable.ic_membership_diamond),
                MembershipGradeInfo(MembershipGradeType.GOLD, R.drawable.ic_membership_gold),
                MembershipGradeInfo(MembershipGradeType.SILVER, R.drawable.ic_membership_silver),
                MembershipGradeInfo(MembershipGradeType.WHITE, R.drawable.white_circle),
                MembershipGradeInfo(MembershipGradeType.GENERAL, R.drawable.ic_membership_general),
            )
            KT -> listOf(
                MembershipGradeInfo(MembershipGradeType.VIP, R.drawable.ic_membership_vvip),
                MembershipGradeInfo(MembershipGradeType.GOLD, R.drawable.ic_membership_gold),
                MembershipGradeInfo(MembershipGradeType.SILVER, R.drawable.ic_membership_silver),
            )
            LGU_PLUS -> listOf(
                MembershipGradeInfo(MembershipGradeType.VVIP, R.drawable.ic_membership_vvip),
                MembershipGradeInfo(MembershipGradeType.VIP, R.drawable.ic_membership_vip_star),
                MembershipGradeInfo(MembershipGradeType.DIAMOND, R.drawable.ic_membership_diamond),
                MembershipGradeInfo(MembershipGradeType.GOLD, R.drawable.ic_membership_gold),
                MembershipGradeInfo(MembershipGradeType.GENERAL, R.drawable.ic_membership_general),
            )
        }
}

enum class MembershipGradeType(val displayName: String) {
    VVIP("VVIP"),
    VIP("VIP"),
    DIAMOND("DIAMOND"),
    GOLD("GOLD"),
    SILVER("SILVER"),
    WHITE("WHITE"),
    GENERAL("일반"),
}

data class MembershipGradeInfo(
    val type: MembershipGradeType,
    val iconResId: Int?,
)

data class OnboardingUiState(
    val step: Int = 0,
    val locationAgreed: Boolean = false,
    val privacyAgreed: Boolean = false,
    val notificationAgreed: Boolean = false,
    val locationError: Boolean = false,
    val selectedCarrier: CarrierType? = CarrierType.SKT,
    val selectedMembershipGrade: MembershipGradeType? = MembershipGradeType.VVIP,
    val preferredBrands: Set<String> = emptySet(),
    val showingTerms: AgreementType? = null,
) {
    val totalProgressSteps: Int = 4

    // 0: 약관동의 (필수 동의 완료 시), 1: 위치권한, 2: 통신사선택, 3: 멤버십등급선택, 4: 브랜드선택 (3개 이상 선택)
    val canContinue: Boolean
        get() =
            when (step) {
                TERMS_STEP -> locationAgreed && privacyAgreed
                LOCATION_PERM_STEP -> true
                CARRIER_STEP -> selectedCarrier != null
                MEMBERSHIP_STEP -> selectedMembershipGrade != null
                BRAND_STEP -> preferredBrands.size >= MIN_BRAND_COUNT
                else -> true
            }

    val selectedBrandCountText: String
        get() {
            val needed = MIN_BRAND_COUNT - preferredBrands.size
            return if (needed > 0) "${needed}개 선택 전" else "선택 완료"
        }

    companion object {
        const val TERMS_STEP = 0
        const val LOCATION_PERM_STEP = 1
        const val CARRIER_STEP = 2
        const val MEMBERSHIP_STEP = 3
        const val BRAND_STEP = 4
        const val MIN_BRAND_COUNT = 3
    }
}
