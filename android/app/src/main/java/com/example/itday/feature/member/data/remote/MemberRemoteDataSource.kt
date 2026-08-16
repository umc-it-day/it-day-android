package com.example.itday.feature.member.data.remote

import android.util.Log
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.core.data.result.AuthErrorReason
import com.example.itday.feature.member.data.mapper.toDomain
import com.example.itday.feature.member.data.model.RecordBarcodeUsageRequestDto
import com.example.itday.feature.member.data.model.RegisterBarcodeRequestDto
import com.example.itday.feature.member.data.model.UpdateBarcodeRequestDto
import com.example.itday.feature.member.data.model.UpdateMembershipRequestDto
import com.example.itday.feature.member.data.model.UpdateNameRequestDto
import com.example.itday.feature.member.domain.model.MemberBarcode
import com.example.itday.feature.member.domain.model.MemberLottery
import com.example.itday.feature.member.domain.model.MemberMembership
import com.example.itday.feature.member.domain.model.MemberProfile
import java.io.IOException
import retrofit2.HttpException

interface MemberRemoteDataSource {
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

class RetrofitMemberRemoteDataSource(
    private val api: MemberApi,
) : MemberRemoteDataSource {
    override suspend fun getProfile(): ApiResult<MemberProfile> =
        runApiCall {
            val response = api.getProfile()
            val data = response.data
            if (!response.success || data == null) {
                Log.e("MemberDataSource", "GetProfile failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            } else {
                ApiResult.Success(data.toDomain())
            }
        }

    override suspend fun updateName(name: String): ApiResult<Unit> =
        runApiCall {
            val response = api.updateName(UpdateNameRequestDto(name))
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                Log.e("MemberDataSource", "UpdateName failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            }
        }

    override suspend fun getMembership(): ApiResult<MemberMembership> =
        runApiCall {
            val response = api.getMembership()
            val data = response.data
            if (!response.success || data == null) {
                Log.e("MemberDataSource", "GetMembership failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            } else {
                ApiResult.Success(data.toDomain())
            }
        }

    override suspend fun updateMembership(membershipId: Long): ApiResult<Unit> =
        runApiCall {
            val response = api.updateMembership(UpdateMembershipRequestDto(membershipId))
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                Log.e("MemberDataSource", "UpdateMembership failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            }
        }

    override suspend fun getBarcode(): ApiResult<MemberBarcode> =
        runApiCall {
            val response = api.getBarcode()
            val data = response.data
            if (!response.success || data == null) {
                Log.e("MemberDataSource", "GetBarcode failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            } else {
                ApiResult.Success(data.toDomain())
            }
        }

    override suspend fun registerBarcode(barcodeNum: String): ApiResult<String> =
        runApiCall {
            val response = api.registerBarcode(RegisterBarcodeRequestDto(barcodeNum))
            val data = response.data
            if (!response.success || data == null) {
                Log.e("MemberDataSource", "RegisterBarcode failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            } else {
                ApiResult.Success(data)
            }
        }

    override suspend fun updateBarcode(barcodeNum: String): ApiResult<Unit> =
        runApiCall {
            val response = api.updateBarcode(UpdateBarcodeRequestDto(barcodeNum))
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                Log.e("MemberDataSource", "UpdateBarcode failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            }
        }

    override suspend fun recordBarcodeUsage(storeId: Long): ApiResult<String> =
        runApiCall {
            val response = api.recordBarcodeUsage(RecordBarcodeUsageRequestDto(storeId))
            val data = response.data
            if (!response.success || data == null) {
                Log.e("MemberDataSource", "RecordBarcodeUsage failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            } else {
                ApiResult.Success(data)
            }
        }

    override suspend fun getLottery(): ApiResult<MemberLottery> =
        runApiCall {
            val response = api.getLottery()
            val data = response.data
            if (!response.success || data == null) {
                Log.e("MemberDataSource", "GetLottery failed: ${response.message}")
                ApiResult.Failure(AppError.Server(statusCode = 200, message = response.message))
            } else {
                ApiResult.Success(data.toDomain())
            }
        }

    override suspend fun withdraw(): ApiResult<Unit> =
        runApiCall {
            val response = api.withdraw()
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                Log.e("MemberDataSource", "Withdraw failed: ${response.message}")
                ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized))
            }
        }

    private suspend fun <T> runApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> =
        try {
            block()
        } catch (error: IOException) {
            Log.e("MemberDataSource", "Network Error: ${error.message}", error)
            ApiResult.Failure(AppError.Network(error))
        } catch (error: HttpException) {
            Log.e("MemberDataSource", "HTTP Error: code=${error.code()}, message=${error.message()}", error)
            when (error.code()) {
                401, 403 -> ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized, error))
                else -> ApiResult.Failure(AppError.Server(error.code(), cause = error))
            }
        } catch (error: Exception) {
            Log.e("MemberDataSource", "Unknown Error: ${error.message}", error)
            ApiResult.Failure(AppError.Unknown(error))
        }
}
