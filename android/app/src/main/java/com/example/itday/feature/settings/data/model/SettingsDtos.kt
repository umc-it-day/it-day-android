package com.example.itday.feature.settings.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MembershipInfoDto(
    val telecomLabel: String,
    val telecomGrade: String,
)

@Serializable
data class UserProfileDto(
    val name: String,
    val email: String,
    val phone: String,
)

@Serializable
data class UpdateNameRequestDto(
    val name: String,
)

@Serializable
data class UpdateMembershipRequestDto(
    val membershipId: Long,
)
