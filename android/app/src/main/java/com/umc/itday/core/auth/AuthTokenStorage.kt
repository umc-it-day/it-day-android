package com.umc.itday.core.auth

import kotlinx.coroutines.flow.Flow

interface AuthTokenStorage {
    val tokens: Flow<AuthTokens?>

    suspend fun getTokens(): AuthTokens?

    suspend fun saveTokens(tokens: AuthTokens)

    suspend fun clearTokens()
}
