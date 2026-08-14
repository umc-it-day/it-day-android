package com.example.itday.feature.settings.presentation

import com.example.itday.core.data.result.ApiResult
import com.example.itday.feature.auth.domain.model.LoginSession
import com.example.itday.feature.auth.domain.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(FakeAuthRepository())
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
}

private class FakeAuthRepository : AuthRepository {
    override suspend fun loginWithKakao(kakaoAccessToken: String): ApiResult<LoginSession> =
        ApiResult.Success(LoginSession(isNewUser = false))

    override suspend fun logout(): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun withdraw(): ApiResult<Unit> = ApiResult.Success(Unit)
}
