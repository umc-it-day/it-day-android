package com.example.itday.feature.settings.presentation

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.auth.domain.model.LoginSession
import com.example.itday.feature.auth.domain.repository.AuthRepository
import com.example.itday.feature.member.domain.model.MemberBarcode
import com.example.itday.feature.member.domain.model.MemberLottery
import com.example.itday.feature.member.domain.model.MemberMembership
import com.example.itday.feature.member.domain.model.MemberProfile
import com.example.itday.feature.member.domain.repository.MemberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SettingsViewModel

    private val fakeAuthRepository =
        object : AuthRepository {
            override suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<LoginSession> =
                ApiResult.Success(LoginSession(isNewUser = false))

            override suspend fun logout(): ApiResult<Unit> = ApiResult.Success(Unit)

            override suspend fun withdraw(): ApiResult<Unit> = ApiResult.Success(Unit)
        }

    private val fakeMemberRepository =
        object : MemberRepository {
            override suspend fun getProfile(): ApiResult<MemberProfile> =
                ApiResult.Success(
                    MemberProfile(
                        name = "홍길동",
                        email = "hong@example.com",
                        phone = "010-1234-5678",
                    ),
                )

            override suspend fun updateName(name: String): ApiResult<Unit> = ApiResult.Success(Unit)

            override suspend fun getMembership(): ApiResult<MemberMembership> =
                ApiResult.Success(
                    MemberMembership(
                        telecomLabel = "KT",
                        telecomGrade = "GOLD",
                    ),
                )

            override suspend fun updateMembership(membershipId: Long): ApiResult<Unit> = ApiResult.Success(Unit)

            override suspend fun getBarcode(): ApiResult<MemberBarcode> =
                ApiResult.Success(MemberBarcode("1234567890123456"))

            override suspend fun registerBarcode(barcodeNum: String): ApiResult<String> = ApiResult.Success("성공")

            override suspend fun updateBarcode(barcodeNum: String): ApiResult<Unit> = ApiResult.Success(Unit)

            override suspend fun recordBarcodeUsage(storeId: Long): ApiResult<String> = ApiResult.Success("성공")

            override suspend fun getLottery(): ApiResult<MemberLottery> =
                ApiResult.Success(MemberLottery("LOTTO-1234"))

            override suspend fun withdraw(): ApiResult<Unit> = ApiResult.Success(Unit)
        }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(authRepository = fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }



    @Test
    fun `initial uiState has default values`() {
        val state = viewModel.uiState.value
        assertEquals(SettingsScreenType.Main, state.currentScreen)
        assertEquals("김예진", state.profile.userName)
        assertEquals("SKT", state.membership.carrier)
        assertTrue(state.promotionNotification)
        assertFalse(state.characterNotification)
        assertFalse(state.showLogoutDialog)
    }

    @Test
    fun `navigateToScreen changes currentScreen in state`() {
        viewModel.navigateToScreen(SettingsScreenType.PrivacySecurity)
        assertEquals(SettingsScreenType.PrivacySecurity, viewModel.uiState.value.currentScreen)

        viewModel.navigateToScreen(SettingsScreenType.CustomerService)
        assertEquals(SettingsScreenType.CustomerService, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun `togglePromotionNotification updates promotionNotification in state`() {
        viewModel.togglePromotionNotification(false)
        assertFalse(viewModel.uiState.value.promotionNotification)

        viewModel.togglePromotionNotification(true)
        assertTrue(viewModel.uiState.value.promotionNotification)
    }

    @Test
    fun `toggleCharacterNotification updates characterNotification in state`() {
        viewModel.toggleCharacterNotification(true)
        assertTrue(viewModel.uiState.value.characterNotification)
    }

    @Test
    fun `showLogoutConfirmation and dismissLogoutConfirmation update showLogoutDialog`() {
        viewModel.showLogoutConfirmation()
        assertTrue(viewModel.uiState.value.showLogoutDialog)

        viewModel.dismissLogoutConfirmation()
        assertFalse(viewModel.uiState.value.showLogoutDialog)
    }

    @Test
    fun `toggleFaqItem toggles isExpanded for specific FAQ item`() {
        val targetId = 1
        assertFalse(viewModel.uiState.value.faqList.first { it.id == targetId }.isExpanded)

        viewModel.toggleFaqItem(targetId)
        assertTrue(viewModel.uiState.value.faqList.first { it.id == targetId }.isExpanded)

        viewModel.toggleFaqItem(targetId)
        assertFalse(viewModel.uiState.value.faqList.first { it.id == targetId }.isExpanded)
    }

    @Test
    fun `loadUserData updates profile and membership when memberRepository succeeds`() =

        runTest {
            val memberViewModel =
                SettingsViewModel(
                    authRepository = fakeAuthRepository,
                    memberRepository = fakeMemberRepository,
                )
            testDispatcher.scheduler.advanceUntilIdle()

            val state = memberViewModel.uiState.value
            assertEquals("홍길동", state.profile.userName)
            assertEquals("hong@example.com", state.profile.userEmail)
            assertEquals("KT", state.membership.carrier)
            assertEquals("GOLD", state.membership.grade)
        }
}

