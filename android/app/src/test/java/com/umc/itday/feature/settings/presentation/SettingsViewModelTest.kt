package com.umc.itday.feature.settings.presentation

import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.feature.auth.domain.model.LoginSession
import com.umc.itday.feature.auth.domain.repository.AuthRepository
import com.umc.itday.feature.settings.domain.model.MembershipInfo
import com.umc.itday.feature.settings.domain.model.UserProfile
import com.umc.itday.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(FakeSettingsRepository(), FakeAuthRepository())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState has default values`() {
        val state = viewModel.uiState.value
        assertEquals(SettingsScreenType.Main, state.currentScreen)
        assertEquals("", state.profile.userName)
        assertEquals("", state.membership.carrier)
        assertTrue(state.promotionNotification)
        assertFalse(state.characterNotification)
        assertFalse(state.showLogoutDialog)
    }

    @Test
    fun `setGuestMode true sets guest user profile and membership without API call`() {
        viewModel.setGuestMode(true)
        val state = viewModel.uiState.value

        assertEquals("게스트", state.profile.userName)
        assertEquals("로그인이 필요합니다", state.profile.userEmail)
        assertEquals("미등록", state.membership.carrier)
        assertEquals("게스트", state.membership.grade)
        assertFalse(state.membership.isPro)
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
}

private class FakeSettingsRepository : SettingsRepository {
    override suspend fun getMembershipInfo(): ApiResult<MembershipInfo> =
        ApiResult.Success(MembershipInfo(telecomLabel = "SKT", telecomGrade = "VIP"))

    override suspend fun getUserProfile(): ApiResult<UserProfile> =
        ApiResult.Success(UserProfile(name = "김예진", email = "test@itday.com", phone = ""))

    override suspend fun updateName(name: String): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun updateMembership(membershipId: Long): ApiResult<Unit> = ApiResult.Success(Unit)
}

private class FakeAuthRepository : AuthRepository {
    override suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<LoginSession> =
        ApiResult.Success(LoginSession(isNewUser = false))

    override suspend fun logout(): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun withdraw(): ApiResult<Unit> = ApiResult.Success(Unit)
}
