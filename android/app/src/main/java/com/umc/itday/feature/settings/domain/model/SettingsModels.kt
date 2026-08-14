package com.umc.itday.feature.settings.domain.model

data class MembershipInfo(
    val telecomLabel: String,
    val telecomGrade: String,
)

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
)
