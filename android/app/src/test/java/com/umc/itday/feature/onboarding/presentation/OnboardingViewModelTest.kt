package com.umc.itday.feature.onboarding.presentation

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.network.NetworkClient
import com.umc.itday.feature.auth.data.model.ApiResponseDto
import com.umc.itday.feature.onboarding.data.model.TelecomGradeDto
import com.umc.itday.feature.onboarding.domain.model.OnboardingSubmission
import com.umc.itday.feature.onboarding.domain.model.OnboardingTerm
import com.umc.itday.feature.onboarding.domain.model.PreferredBrand
import com.umc.itday.feature.onboarding.domain.model.Telecom
import com.umc.itday.feature.onboarding.domain.model.TelecomGrade
import com.umc.itday.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeOnboardingApiRepository
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeOnboardingApiRepository()
        viewModel =
            OnboardingViewModel(
                repository = repository,
                debugLogger = {},
                infoLogger = {},
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 진입 시 약관과 통신사 목록을 조회한다`() = runTest {
        runCurrent()

        assertEquals(3, viewModel.uiState.value.terms.size)
        assertEquals(listOf(CarrierType.SKT, CarrierType.KT), viewModel.uiState.value.availableCarriers)
    }

    @Test
    fun `필수 약관에 동의하면 다음 단계로 이동할 수 있다`() = runTest {
        runCurrent()
        viewModel.setAgreement(AgreementType.Location, true)
        viewModel.setAgreement(AgreementType.Privacy, true)

        assertTrue(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `통신사를 선택하면 서버 등급과 membershipId를 반영한다`() = runTest {
        runCurrent()
        viewModel.selectCarrier(CarrierType.KT)
        runCurrent()
        viewModel.selectMembershipGrade(MembershipGradeType.GOLD)

        assertEquals(MembershipGradeType.GOLD, viewModel.uiState.value.selectedMembershipGrade)
        assertEquals(21L, viewModel.uiState.value.selectedMembershipId)
    }

    @Test
    fun `통신사 등급 응답에 설명이 없어도 파싱한다`() {
        val response =
            NetworkClient.json.decodeFromString<ApiResponseDto<List<TelecomGradeDto>>>(
                """
                {
                  "success": true,
                  "data": [
                    {
                      "membershipId": 21,
                      "telecomGrade": "골드 등급"
                    }
                  ]
                }
                """.trimIndent(),
            )

        assertEquals("골드 등급", response.data?.single()?.telecomGrade)
        assertEquals("", response.data?.single()?.gradeContent)
    }

    @Test
    fun `브랜드 3개와 서버 식별자를 포함해 온보딩을 제출한다`() = runTest {
        runCurrent()
        viewModel.setAgreement(AgreementType.Location, true)
        viewModel.setAgreement(AgreementType.Privacy, true)
        viewModel.next()
        viewModel.onLocationResult(true)
        viewModel.selectCarrier(CarrierType.KT)
        runCurrent()
        viewModel.next()
        viewModel.selectMembershipGrade(MembershipGradeType.GOLD)
        viewModel.next()
        runCurrent()

        repository.brands.forEach { viewModel.toggleBrand(it.id) }
        assertTrue(viewModel.uiState.value.canContinue)
        viewModel.next()
        runCurrent()

        val submission = repository.submission
        assertNotNull(submission)
        assertEquals(21L, submission?.membershipId)
        assertEquals(listOf(31L, 32L, 33L), submission?.preferredBrandIds)
        assertEquals(3, submission?.termAgreements?.size)
    }

    @Test
    fun `브랜드가 3개 미만이면 완료할 수 없다`() = runTest {
        runCurrent()
        viewModel.setAgreement(AgreementType.Location, true)
        viewModel.setAgreement(AgreementType.Privacy, true)
        viewModel.next()
        viewModel.onLocationResult(true)
        viewModel.selectCarrier(CarrierType.KT)
        runCurrent()
        viewModel.next()
        viewModel.selectMembershipGrade(MembershipGradeType.GOLD)
        viewModel.next()
        runCurrent()

        viewModel.toggleBrand(31L)
        viewModel.toggleBrand(32L)
        assertFalse(viewModel.uiState.value.canContinue)
    }
}

private class FakeOnboardingApiRepository : OnboardingRepository {
    val brands =
        listOf(
            PreferredBrand(31, "스타벅스", null, "CAFE"),
            PreferredBrand(32, "CU", null, "CONVENIENCE"),
            PreferredBrand(33, "GS25", null, "CONVENIENCE"),
        )
    var submission: OnboardingSubmission? = null

    override suspend fun getTerms() =
        ApiResult.Success(
            listOf(
                OnboardingTerm(1, "위치 정보 이용 동의", "위치 약관", true),
                OnboardingTerm(2, "개인정보 제공 동의", "개인정보 약관", true),
                OnboardingTerm(3, "알림 권한 동의", "알림 약관", false),
            ),
        )

    override suspend fun getTelecoms() =
        ApiResult.Success(listOf(Telecom("SKT", "SKT"), Telecom("KT", "KT")))

    override suspend fun getGrades(telecom: String) =
        ApiResult.Success(listOf(TelecomGrade(21, "골드 등급", "")))

    override suspend fun getBrands() = ApiResult.Success(brands)

    override suspend fun submitOnboarding(submission: OnboardingSubmission): ApiResult<Unit> {
        this.submission = submission
        return ApiResult.Success(Unit)
    }
}
