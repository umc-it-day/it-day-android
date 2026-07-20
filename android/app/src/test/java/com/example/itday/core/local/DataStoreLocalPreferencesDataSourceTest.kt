package com.example.itday.core.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DataStoreLocalPreferencesDataSourceTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `returns default values before preferences are saved`() =
        runTest {
            val dataSource = createDataSource(backgroundScope)

            assertFalse(dataSource.isOnboardingCompleted.first())
            assertFalse(dataSource.isGuestMode.first())
        }

    @Test
    fun `saves onboarding completed value`() =
        runTest {
            val dataSource = createDataSource(backgroundScope)

            dataSource.setOnboardingCompleted(completed = true)

            assertTrue(dataSource.isOnboardingCompleted.first())
        }

    @Test
    fun `saves guest mode value`() =
        runTest {
            val dataSource = createDataSource(backgroundScope)

            dataSource.setGuestMode(enabled = true)

            assertTrue(dataSource.isGuestMode.first())
        }

    @Test
    fun `clears user session preferences while keeping onboarding completed`() {
        val preferences =
            mutablePreferencesOf(
                LocalPreferenceKeys.IsOnboardingCompleted to true,
                LocalPreferenceKeys.IsGuestMode to true,
            )

        preferences.clearUserSessionPreferences()

        assertTrue(preferences[LocalPreferenceKeys.IsOnboardingCompleted] ?: false)
        assertFalse(preferences[LocalPreferenceKeys.IsGuestMode] ?: true)
    }

    private fun createDataSource(scope: CoroutineScope): LocalPreferencesDataSource {
        val file = File(temporaryFolder.newFolder(), "test.preferences_pb")
        val dataStore =
            PreferenceDataStoreFactory.createWithPath(
                scope = scope,
                produceFile = { file.absolutePath.toPath() },
            )
        return DataStoreLocalPreferencesDataSource(dataStore)
    }
}
