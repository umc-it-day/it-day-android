package com.example.itday.feature.settings.data.repository

import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.AppError
import com.example.itday.core.data.result.AuthErrorReason
import com.example.itday.feature.settings.data.api.SettingsApi
import com.example.itday.feature.settings.data.mapper.toDomain
import com.example.itday.feature.settings.data.model.UpdateMembershipRequestDto
import com.example.itday.feature.settings.data.model.UpdateNameRequestDto
import com.example.itday.feature.settings.domain.model.MembershipInfo
import com.example.itday.feature.settings.domain.model.UserProfile
import com.example.itday.feature.settings.domain.repository.SettingsRepository
import java.io.IOException
import retrofit2.HttpException

class SettingsRepositoryImpl(
    private val api: SettingsApi,
) : SettingsRepository {
    override suspend fun getMembershipInfo(): ApiResult<MembershipInfo> =
        runApiCall {
            val response = api.getMembershipInfo()
            val data = response.data
            if (response.success && data != null) {
                ApiResult.Success(data.toDomain())
            } else {
                ApiResult.Failure(
                    AppError.Server(
                        statusCode = 200,
                        message = response.message,
                    ),
                )
            }
        }

    override suspend fun getUserProfile(): ApiResult<UserProfile> =
        runApiCall {
            val response = api.getUserProfile()
            val data = response.data
            if (response.success && data != null) {
                ApiResult.Success(data.toDomain())
            } else {
                ApiResult.Failure(
                    AppError.Server(
                        statusCode = 200,
                        message = response.message,
                    ),
                )
            }
        }

    override suspend fun updateName(name: String): ApiResult<Unit> =
        runApiCall {
            val response = api.updateName(UpdateNameRequestDto(name))
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(
                    AppError.Server(
                        statusCode = 200,
                        message = response.message,
                    ),
                )
            }
        }

    override suspend fun updateMembership(membershipId: Long): ApiResult<Unit> =
        runApiCall {
            val response = api.updateMembership(UpdateMembershipRequestDto(membershipId))
            if (response.success) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Failure(
                    AppError.Server(
                        statusCode = 200,
                        message = response.message,
                    ),
                )
            }
        }

    private suspend fun <T> runApiCall(block: suspend () -> ApiResult<T>): ApiResult<T> =
        try {
            block()
        } catch (error: IOException) {
            ApiResult.Failure(AppError.Network(error))
        } catch (error: HttpException) {
            when (error.code()) {
                401, 403 -> ApiResult.Failure(AppError.Auth(AuthErrorReason.Unauthorized, error))
                else -> ApiResult.Failure(AppError.Server(error.code(), cause = error))
            }
        } catch (error: Exception) {
            ApiResult.Failure(AppError.Unknown(error))
        }
}
