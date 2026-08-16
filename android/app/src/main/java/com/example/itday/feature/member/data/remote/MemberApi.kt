package com.example.itday.feature.member.data.remote

import com.example.itday.feature.auth.data.model.ApiResponseDto
import com.example.itday.feature.member.data.model.MemberBarcodeDataDto
import com.example.itday.feature.member.data.model.MemberLotteryDataDto
import com.example.itday.feature.member.data.model.MemberMembershipDataDto
import com.example.itday.feature.member.data.model.MemberProfileDataDto
import com.example.itday.feature.member.data.model.RecordBarcodeUsageRequestDto
import com.example.itday.feature.member.data.model.RegisterBarcodeRequestDto
import com.example.itday.feature.member.data.model.UpdateBarcodeRequestDto
import com.example.itday.feature.member.data.model.UpdateMembershipRequestDto
import com.example.itday.feature.member.data.model.UpdateNameRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface MemberApi {
    @GET("api/members/me/profile")
    suspend fun getProfile(): ApiResponseDto<MemberProfileDataDto>

    @PATCH("api/members/me/name")
    suspend fun updateName(
        @Body request: UpdateNameRequestDto,
    ): ApiResponseDto<Unit?>

    @GET("api/members/me/membership")
    suspend fun getMembership(): ApiResponseDto<MemberMembershipDataDto>

    @PATCH("api/members/me/membership")
    suspend fun updateMembership(
        @Body request: UpdateMembershipRequestDto,
    ): ApiResponseDto<Unit?>

    @GET("api/members/me/barcode")
    suspend fun getBarcode(): ApiResponseDto<MemberBarcodeDataDto>

    @POST("api/members/me/barcode")
    suspend fun registerBarcode(
        @Body request: RegisterBarcodeRequestDto,
    ): ApiResponseDto<String>

    @PUT("api/members/me/barcode")
    suspend fun updateBarcode(
        @Body request: UpdateBarcodeRequestDto,
    ): ApiResponseDto<Unit?>

    @POST("api/members/me/barcode/usage")
    suspend fun recordBarcodeUsage(
        @Body request: RecordBarcodeUsageRequestDto,
    ): ApiResponseDto<String>

    @GET("api/members/me/lottery")
    suspend fun getLottery(): ApiResponseDto<MemberLotteryDataDto>

    @DELETE("api/members/me")
    suspend fun withdraw(): ApiResponseDto<Unit?>
}
