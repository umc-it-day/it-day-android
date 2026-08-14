package com.example.itday.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.itday.core.data.result.ApiResult
import com.example.itday.core.data.result.toUserMessage
import com.example.itday.feature.auth.domain.repository.AuthRepository
import com.example.itday.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<SettingsUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val profileResult = settingsRepository.getUserProfile()
            val membershipResult = settingsRepository.getMembershipInfo()

            _uiState.update { state ->
                var newState = state.copy(isLoading = false)
                
                if (profileResult is ApiResult.Success) {
                    newState = newState.copy(
                        profile = UserProfile(
                            userName = profileResult.data.name,
                            userEmail = profileResult.data.email
                        )
                    )
                } else if (profileResult is ApiResult.Failure) {
                    // 프로필 로딩 실패 처리
                    viewModelScope.launch {
                        _events.send(SettingsUiEvent.ShowMessage("프로필 정보를 불러오는데 실패했습니다."))
                    }
                }
                
                if (membershipResult is ApiResult.Success) {
                    newState = newState.copy(
                        membership = state.membership.copy(
                            carrier = membershipResult.data.telecomLabel,
                            grade = membershipResult.data.telecomGrade
                        )
                    )
                } else if (membershipResult is ApiResult.Failure) {
                    // 멤버십 로딩 실패 처리
                }
                newState
            }
        }
    }

    fun navigateToScreen(screen: SettingsScreenType) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun togglePromotionNotification(enabled: Boolean) {
        _uiState.update { it.copy(promotionNotification = enabled) }
    }

    fun toggleCharacterNotification(enabled: Boolean) {
        _uiState.update { it.copy(characterNotification = enabled) }
    }

    fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun showNameEditDialog() {
        _uiState.update { it.copy(showNameEditDialog = true, editingName = it.profile.userName) }
    }

    fun dismissNameEditDialog() {
        _uiState.update { it.copy(showNameEditDialog = false) }
    }

    fun updateEditingName(name: String) {
        _uiState.update { it.copy(editingName = name) }
    }

    fun confirmNameEdit() {
        val newName = _uiState.value.editingName
        if (newName.isNotBlank()) {
            updateName(newName)
            dismissNameEditDialog()
        }
    }

    fun openPrivacyPolicy() {
        viewModelScope.launch {
            _events.send(SettingsUiEvent.OpenExternalUrl("https://example.com/privacy"))
        }
    }

    fun openTermsOfService() {
        viewModelScope.launch {
            _events.send(SettingsUiEvent.OpenExternalUrl("https://example.com/terms"))
        }
    }

    fun updateName(newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = settingsRepository.updateName(newName)
            if (result is ApiResult.Success) {
                _uiState.update { it.copy(
                    profile = it.profile.copy(userName = newName),
                    isLoading = false
                ) }
                _events.send(SettingsUiEvent.ShowMessage("이름이 변경되었습니다."))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(SettingsUiEvent.ShowMessage("이름 변경에 실패했습니다. 다시 시도해주세요."))
            }
        }
    }

    fun updateMembership(membershipId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = settingsRepository.updateMembership(membershipId)
            if (result is ApiResult.Success) {
                _events.send(SettingsUiEvent.ShowMessage("멤버십 정보가 변경되었습니다."))
                loadSettings()
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(SettingsUiEvent.ShowMessage("멤버십 정보 변경에 실패했습니다."))
            }
        }
    }

    fun withdraw() {
        if (_uiState.value.isWithdrawing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isWithdrawing = true, errorMessage = null) }
            when (val result = authRepository.withdraw()) {
                is ApiResult.Success -> {
                    _events.send(SettingsUiEvent.UserWithdrawn)
                }
                is ApiResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isWithdrawing = false,
                            errorMessage = result.error.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    fun toggleFaqItem(id: Int) {
        _uiState.update { state ->
            val updatedList =
                state.faqList.map { item ->
                    if (item.id == id) {
                        item.copy(isExpanded = !item.isExpanded)
                    } else {
                        item
                    }
                }
            state.copy(faqList = updatedList)
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val authRepository: AuthRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(settingsRepository, authRepository) as T
    }
}
