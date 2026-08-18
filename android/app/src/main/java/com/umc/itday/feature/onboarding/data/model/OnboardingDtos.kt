package com.umc.itday.feature.onboarding.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TermDto(
    val termsId: Long,
    val title: String,
    val content: String,
    val isRequired: Boolean,
)

@Serializable
data class TelecomDto(
    val telecom: String = "",
    val label: String = telecom,
)

@Serializable
data class TelecomGradeDto(
    val membershipId: Long,
    val telecomGrade: String,
    val gradeContent: String = "",
)

@Serializable
data class BrandDto(
    val brandId: Long,
    val brandName: String,
    val brandImg: String? = null,
    val category: String = "",
)

@Serializable
data class TermAgreementDto(
    val termId: Long,
    val isAgree: Boolean,
)

@Serializable
data class OnboardingRequestDto(
    val termAgreements: List<TermAgreementDto>,
    val membershipId: Long,
    val preferredBrandIds: List<Long>,
)
