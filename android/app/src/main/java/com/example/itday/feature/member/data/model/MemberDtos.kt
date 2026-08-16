package com.example.itday.feature.member.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MemberBarcodeDataDto(
    val barcodeNum: String,
)

@Serializable
data class RegisterBarcodeRequestDto(
    val barcodeNum: String,
)

@Serializable
data class UpdateBarcodeRequestDto(
    val barcodeNum: String,
)

@Serializable
data class RecordBarcodeUsageRequestDto(
    val storeId: Long,
)

@Serializable
data class UpdateNameRequestDto(
    val name: String,
)

@Serializable
data class MemberMembershipDataDto(
    val telecomLabel: String,
    val telecomGrade: String,
)

@Serializable
data class UpdateMembershipRequestDto(
    val membershipId: Long,
)

@Serializable
data class MemberProfileDataDto(
    val name: String,
    val email: String,
    val phone: String,
)

@Serializable
data class MemberLotteryDataDto(
    val lotteryNum: String,
)
