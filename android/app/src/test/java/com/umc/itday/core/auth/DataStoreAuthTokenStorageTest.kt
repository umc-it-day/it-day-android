package com.umc.itday.core.auth

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DataStoreAuthTokenStorageTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `returns null before tokens are saved`() =
        runTest {
            val storage = createStorage(backgroundScope)

            assertNull(storage.tokens.first())
        }

    @Test
    fun `saves access and refresh tokens together`() =
        runTest {
            val storage = createStorage(backgroundScope)
            val expected = AuthTokens(accessToken = "access", refreshToken = "refresh")

            storage.saveTokens(expected)

            assertEquals(expected, storage.getTokens())
        }

    private fun createStorage(scope: CoroutineScope): AuthTokenStorage {
        val file = File(temporaryFolder.newFolder(), "auth.preferences_pb")
        val dataStore =
            PreferenceDataStoreFactory.createWithPath(
                scope = scope,
                produceFile = { file.absolutePath.toPath() },
            )
        return DataStoreAuthTokenStorage(dataStore)
    }
}
