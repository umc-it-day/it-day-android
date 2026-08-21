package com.umc.itday.feature.onboarding.data.repository

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.AppError
import com.umc.itday.core.network.safeApiCall
import com.umc.itday.BuildConfig
import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.onboarding.data.model.OnboardingRequestDto
import com.umc.itday.feature.onboarding.data.model.TermAgreementDto
import com.umc.itday.feature.onboarding.data.remote.OnboardingApi
import com.umc.itday.feature.onboarding.domain.model.OnboardingSubmission
import com.umc.itday.feature.onboarding.domain.model.OnboardingTerm
import com.umc.itday.feature.onboarding.domain.model.PreferredBrand
import com.umc.itday.feature.onboarding.domain.model.Telecom
import com.umc.itday.feature.onboarding.domain.model.TelecomGrade
import com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DefaultOnboardingRepository(
    private val api: OnboardingApi,
) : OnboardingRepository {
    override suspend fun getTerms(): ApiResult<List<OnboardingTerm>> =
        safeApiCall {
            api.getTerms().toResult { terms ->
                terms.map { OnboardingTerm(it.termsId, it.title, it.content, it.isRequired) }
            }
        }

    override suspend fun getTelecoms(): ApiResult<List<Telecom>> =
        safeApiCall {
            api.getTelecoms().toResult { telecoms -> telecoms.map { Telecom(it.telecom, it.label) } }
        }

    override suspend fun getGrades(telecom: String): ApiResult<List<TelecomGrade>> =
        safeApiCall {
            api.getGrades(telecom).toResult { data ->
                data.gradeItems().mapNotNull { item ->
                    val membershipId = item.longValue("membershipId", "membership_id", "id") ?: return@mapNotNull null
                    val grade =
                        item.stringValue(
                            "telecomGrade",
                            "telecom_grade",
                            "membershipGrade",
                            "membership_grade",
                            "grade",
                            "name",
                            "label",
                        ) ?: return@mapNotNull null
                    val content = item.stringValue("gradeContent", "grade_content", "content", "description").orEmpty()

                    TelecomGrade(membershipId, grade, content)
                }
            }
        }

    override suspend fun getBrands(): ApiResult<List<PreferredBrand>> =
        safeApiCall {
            api.getBrands().toResult { brands ->
                brands.map {
                    PreferredBrand(
                        id = it.brandId,
                        name = it.brandName,
                        imageUrl = it.brandImg.toAbsoluteUrlOrNull(),
                        category = it.category,
                    )
                }
            }
        }

    override suspend fun submitOnboarding(submission: OnboardingSubmission): ApiResult<Unit> =
        safeApiCall {
            val request =
                OnboardingRequestDto(
                    termAgreements = submission.termAgreements.map { TermAgreementDto(it.termId, it.isAgreed) },
                    membershipId = submission.membershipId,
                    preferredBrandIds = submission.preferredBrandIds,
                )
            val response = api.submitOnboarding(request)
            if (response.success) ApiResult.Success(Unit) else response.failure()
        }

    private fun <T, R> ApiResponseDto<T>.toResult(transform: (T) -> R): ApiResult<R> {
        val value = data
        return if (success && value != null) ApiResult.Success(transform(value)) else failure()
    }

    private fun ApiResponseDto<*>.failure(): ApiResult.Failure =
        ApiResult.Failure(AppError.Server(statusCode = 200, message = message))
}

class GuestOnboardingRepository(
    private val api: OnboardingApi,
) : OnboardingRepository {
    override suspend fun getTerms(): ApiResult<List<OnboardingTerm>> =
        ApiResult.Success(
            listOf(
                OnboardingTerm(1L, "위치 기반 서비스 이용 동의", "주변 혜택 안내를 위한 위치 이용에 동의합니다.", true),
                OnboardingTerm(2L, "개인정보 수집 및 이용 동의", "서비스 이용을 위한 최소한의 개인정보 처리에 동의합니다.", true),
                OnboardingTerm(3L, "알림 수신 동의", "혜택 및 이벤트 알림 수신에 동의합니다.", false),
            ),
        )

    override suspend fun getTelecoms(): ApiResult<List<Telecom>> =
        ApiResult.Success(
            listOf(
                Telecom("SKT", "SKT"),
                Telecom("KT", "KT"),
                Telecom("LGU", "LG U+"),
            ),
        )

    override suspend fun getGrades(telecom: String): ApiResult<List<TelecomGrade>> =
        ApiResult.Success(
            when (telecom.uppercase()) {
                "SKT" ->
                    listOf(
                        TelecomGrade(1001L, "VVIP", ""),
                        TelecomGrade(1002L, "VIP", ""),
                        TelecomGrade(1003L, "GOLD", ""),
                        TelecomGrade(1004L, "SILVER", ""),
                        TelecomGrade(1005L, "WHITE", ""),
                        TelecomGrade(1006L, "GENERAL", ""),
                    )
                "KT" ->
                    listOf(
                        TelecomGrade(2001L, "VIP", ""),
                        TelecomGrade(2002L, "GOLD", ""),
                        TelecomGrade(2003L, "SILVER", ""),
                    )
                else ->
                    listOf(
                        TelecomGrade(3001L, "VVIP", ""),
                        TelecomGrade(3002L, "VIP", ""),
                        TelecomGrade(3003L, "DIAMOND", ""),
                        TelecomGrade(3004L, "GOLD", ""),
                        TelecomGrade(3005L, "GENERAL", ""),
                    )
            },
        )

    override suspend fun getBrands(): ApiResult<List<PreferredBrand>> =
        DefaultOnboardingRepository(api).getBrands()

    override suspend fun submitOnboarding(submission: OnboardingSubmission): ApiResult<Unit> =
        ApiResult.Success(Unit)
}

private fun JsonElement.gradeItems(): List<JsonObject> =
    when (this) {
        is JsonArray -> mapNotNull { it as? JsonObject }
        is JsonObject -> {
            val nestedArray =
                firstArrayValue("grades", "telecomGrades", "membershipGrades", "memberships", "items", "content")
            when {
                nestedArray != null -> nestedArray.mapNotNull { it as? JsonObject }
                else -> listOf(this)
            }
        }
        else -> emptyList()
    }

private fun JsonObject.firstArrayValue(vararg keys: String): JsonArray? =
    keys.firstNotNullOfOrNull { key -> this[key] as? JsonArray }

private fun JsonObject.stringValue(vararg keys: String): String? =
    keys.firstNotNullOfOrNull { key ->
        (this[key] as? JsonPrimitive)
            ?.takeUnless { it.isString.not() && it.contentOrNull.isNullOrBlank() }
            ?.contentOrNull
            ?.takeIf(String::isNotBlank)
    }

private fun JsonObject.longValue(vararg keys: String): Long? =
    keys.firstNotNullOfOrNull { key ->
        this[key]?.jsonPrimitive?.contentOrNull?.toLongOrNull()
    }

private fun String?.toAbsoluteUrlOrNull(): String? {
    val value = this?.trim()?.takeIf(String::isNotBlank) ?: return null
    if (value.startsWith("http://", ignoreCase = true) || value.startsWith("https://", ignoreCase = true)) {
        return value
    }

    val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/')
    val path = value.trimStart('/')
    return "$baseUrl/$path"
}
