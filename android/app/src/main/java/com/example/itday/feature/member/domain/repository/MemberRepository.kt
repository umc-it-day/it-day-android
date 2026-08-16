package com.example.itday.feature.member.domain.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.member.domain.model.MemberBarcode
import com.example.itday.feature.member.domain.model.MemberLottery
import com.example.itday.feature.member.domain.model.MemberMembership
import com.example.itday.feature.member.domain.model.MemberProfile

interface MemberRepository {
    suspend fun getProfile(): ApiResult<MemberProfile>
    suspend fun updateName(name: String): ApiResult<Unit>
    suspend fun getMembership(): ApiResult<MemberMembership>
    suspend fun updateMembership(membershipId: Long): ApiResult<Unit>
    suspend fun getBarcode(): ApiResult<MemberBarcode>
    suspend fun registerBarcode(barcodeNum: String): ApiResult<String>
    suspend fun updateBarcode(barcodeNum: String): ApiResult<Unit>
    suspend fun recordBarcodeUsage(storeId: Long): ApiResult<String>
    suspend fun getLottery(): ApiResult<MemberLottery>
    suspend fun withdraw(): ApiResult<Unit>
}
