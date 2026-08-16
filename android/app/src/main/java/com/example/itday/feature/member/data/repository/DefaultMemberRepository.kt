package com.example.itday.feature.member.data.repository

import com.example.itday.core.auth.AuthTokenStorage
import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.member.data.remote.MemberRemoteDataSource
import com.example.itday.feature.member.domain.model.MemberBarcode
import com.example.itday.feature.member.domain.model.MemberLottery
import com.example.itday.feature.member.domain.model.MemberMembership
import com.example.itday.feature.member.domain.model.MemberProfile
import com.example.itday.feature.member.domain.repository.MemberRepository

class DefaultMemberRepository(
    private val remoteDataSource: MemberRemoteDataSource,
    private val tokenStorage: AuthTokenStorage? = null,
) : MemberRepository {
    override suspend fun getProfile(): ApiResult<MemberProfile> =
        remoteDataSource.getProfile()

    override suspend fun updateName(name: String): ApiResult<Unit> =
        remoteDataSource.updateName(name)

    override suspend fun getMembership(): ApiResult<MemberMembership> =
        remoteDataSource.getMembership()

    override suspend fun updateMembership(membershipId: Long): ApiResult<Unit> =
        remoteDataSource.updateMembership(membershipId)

    override suspend fun getBarcode(): ApiResult<MemberBarcode> =
        remoteDataSource.getBarcode()

    override suspend fun registerBarcode(barcodeNum: String): ApiResult<String> =
        remoteDataSource.registerBarcode(barcodeNum)

    override suspend fun updateBarcode(barcodeNum: String): ApiResult<Unit> =
        remoteDataSource.updateBarcode(barcodeNum)

    override suspend fun recordBarcodeUsage(storeId: Long): ApiResult<String> =
        remoteDataSource.recordBarcodeUsage(storeId)

    override suspend fun getLottery(): ApiResult<MemberLottery> =
        remoteDataSource.getLottery()

    override suspend fun withdraw(): ApiResult<Unit> =
        remoteDataSource.withdraw().also { result ->
            if (result is ApiResult.Success) {
                tokenStorage?.clearTokens()
            }
        }
}
