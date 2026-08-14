package com.umc.itday.feature.settings.data.mapper

import com.umc.itday.feature.settings.data.model.MembershipInfoDto
import com.umc.itday.feature.settings.data.model.UserProfileDto
import com.umc.itday.feature.settings.domain.model.MembershipInfo
import com.umc.itday.feature.settings.domain.model.UserProfile

fun MembershipInfoDto.toDomain(): MembershipInfo =
    MembershipInfo(
        telecomLabel = telecomLabel,
        telecomGrade = telecomGrade,
    )

fun UserProfileDto.toDomain(): UserProfile =
    UserProfile(
        name = name,
        email = email,
        phone = phone,
    )
