package com.example.itday.ui.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.itday.core.local.LocalPreferencesDataSource
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

enum class SessionDestination {
    Login,
    Onboarding,
    Main,
}

class SessionViewModel(
    private val localPreferencesDataSource: LocalPreferencesDataSource,
) : ViewModel() {
    suspend fun resolveDestination(): SessionDestination =
        combine(
            localPreferencesDataSource.isLoggedIn,
            localPreferencesDataSource.isOnboardingCompleted,
            localPreferencesDataSource.isGuestMode,
        ) { isLoggedIn, isOnboardingCompleted, isGuestMode ->
            when {
                isGuestMode -> SessionDestination.Main
                !isLoggedIn -> SessionDestination.Login
                !isOnboardingCompleted -> SessionDestination.Onboarding
                else -> SessionDestination.Main
            }
        }.first()

    suspend fun enterGuestMode() {
        localPreferencesDataSource.setLoggedIn(false)
        localPreferencesDataSource.setGuestMode(true)
    }

    suspend fun completeOnboarding() {
        localPreferencesDataSource.setOnboardingCompleted(true)
    }

    class Factory(
        private val localPreferencesDataSource: LocalPreferencesDataSource,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SessionViewModel(localPreferencesDataSource) as T
    }
}
