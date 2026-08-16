package com.example.itday.feature.member.data.mapper

import com.example.itday.feature.member.data.model.MemberBarcodeDataDto
import com.example.itday.feature.member.data.model.MemberLotteryDataDto
import com.example.itday.feature.member.data.model.MemberMembershipDataDto
import com.example.itday.feature.member.data.model.MemberProfileDataDto
import com.example.itday.feature.member.domain.model.MemberBarcode
import com.example.itday.feature.member.domain.model.MemberLottery
import com.example.itday.feature.member.domain.model.MemberMembership
import com.example.itday.feature.member.domain.model.MemberProfile

fun MemberProfileDataDto.toDomain(): MemberProfile =
    MemberProfile(
        name = name,
        email = email,
        phone = phone,
    )

fun MemberMembershipDataDto.toDomain(): MemberMembership =
    MemberMembership(
        telecomLabel = telecomLabel,
        telecomGrade = telecomGrade,
    )

fun MemberBarcodeDataDto.toDomain(): MemberBarcode =
    MemberBarcode(
        barcodeNum = barcodeNum,
    )

fun MemberLotteryDataDto.toDomain(): MemberLottery =
    MemberLottery(
        lotteryNum = lotteryNum,
    )
