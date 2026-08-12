package com.example.itday.feature.settings.domain.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.settings.domain.model.MembershipInfo
import com.example.itday.feature.settings.domain.model.UserProfile

interface SettingsRepository {
    suspend fun getMembershipInfo(): ApiResult<MembershipInfo>
    suspend fun getUserProfile(): ApiResult<UserProfile>
    suspend fun updateName(name: String): ApiResult<Unit>
    suspend fun updateMembership(membershipId: Long): ApiResult<Unit>
}
