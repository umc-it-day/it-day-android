package com.umc.itday.ui.start

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.umc.itday.core.auth.KakaoLoginClient
import com.umc.itday.core.auth.KakaoLoginResult
import com.umc.itday.core.data.result.ApiResult
import com.umc.itday.core.data.result.toUserMessage
import com.umc.itday.core.local.LocalPreferencesDataSource
import com.umc.itday.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginEvent {
    data class Authenticated(
        val isNewUser: Boolean,
    ) : LoginEvent
}

class LoginViewModel(
    private val kakaoLoginClient: KakaoLoginClient,
    private val authRepository: AuthRepository,
    private val localPreferencesDataSource: LocalPreferencesDataSource,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun loginWithKakao(context: Context) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = kakaoLoginClient.login(context)) {
                is KakaoLoginResult.Success -> {
                    Log.d("LoginFlow", "Kakao Login Success: accessToken = ${result.accessToken}")
                    authenticateWithServer(result.accessToken)
                }
                KakaoLoginResult.Cancelled -> {
                    Log.d("LoginFlow", "Kakao Login Cancelled")
                    _uiState.value = LoginUiState()
                }
                is KakaoLoginResult.Failure -> {
                    Log.e("LoginFlow", "Kakao Login Failed: ${result.message}", result.cause)
                    _uiState.update {
                        LoginUiState(errorMessage = result.message)
                    }
                }
            }
        }
    }

    private suspend fun authenticateWithServer(kakaoAccessToken: String) {
        when (val result = authRepository.loginWithKakao(kakaoAccessToken)) {
            is ApiResult.Success -> {
                Log.d("LoginFlow", "Server Login Success: isNewUser = ${result.data.isNewUser}")
                localPreferencesDataSource.setGuestMode(false)
                localPreferencesDataSource.setOnboardingCompleted(!result.data.isNewUser)
                localPreferencesDataSource.setLoggedIn(true)
                _uiState.value = LoginUiState()
                _events.send(LoginEvent.Authenticated(result.data.isNewUser))
            }
            is ApiResult.Failure -> {
                Log.e("LoginFlow", "Server Login Failed: ${result.error}")
                _uiState.value = LoginUiState(errorMessage = result.error.toUserMessage())
            }
        }
    }

    class Factory(
        private val kakaoLoginClient: KakaoLoginClient,
        private val authRepository: AuthRepository,
        private val localPreferencesDataSource: LocalPreferencesDataSource,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(kakaoLoginClient, authRepository, localPreferencesDataSource) as T
    }
}
