package com.umc.itday.feature.onboarding.domain.model

data class OnboardingTerm(
    val id: Long,
    val title: String,
    val content: String,
    val isRequired: Boolean,
)

data class Telecom(
    val code: String,
    val label: String,
)

data class TelecomGrade(
    val membershipId: Long,
    val grade: String,
    val content: String,
)

data class PreferredBrand(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val category: String,
)

data class TermAgreement(
    val termId: Long,
    val isAgreed: Boolean,
)

data class OnboardingSubmission(
    val termAgreements: List<TermAgreement>,
    val membershipId: Long,
    val preferredBrandIds: List<Long>,
)
