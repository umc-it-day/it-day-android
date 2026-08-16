package com.example.itday.feature.member.domain.model

data class MemberProfile(
    val name: String,
    val email: String,
    val phone: String,
)

data class MemberMembership(
    val telecomLabel: String,
    val telecomGrade: String,
)

data class MemberBarcode(
    val barcodeNum: String,
)

data class MemberLottery(
    val lotteryNum: String,
)
