package com.example.myapplication

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Exercises the real Activity boundary so creating the ViewModel early is detected. */
class PrivacyConsentUiTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before fun clearConsent() {
        assertTrue(context.getSharedPreferences("privacy_consent", Context.MODE_PRIVATE).edit().clear().commit())
    }

    @Test fun declineDoesNotLoadOrModifyTheSave() {
        val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val before = prefs.all.toMap()
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithTag("privacy_gate").assertIsDisplayed()
            compose.onNodeWithTag("privacy_accept").assertIsNotEnabled()
            assertEquals(before, prefs.all)
            compose.onNodeWithTag("privacy_decline").performClick()
            compose.waitForIdle()
            assertFalse(PrivacyConsent(context).isAccepted())
            assertEquals(before, prefs.all)
        }
    }

    @Test fun acceptanceSurvivesRecreationAndCanBeWithdrawn() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            compose.onNodeWithTag("privacy_agreement").performClick()
            compose.onNodeWithTag("privacy_accept").performClick()
            compose.waitUntil(5_000) { PrivacyConsent(context).isAccepted() }
            scenario.recreate()
            compose.onNodeWithTag("privacy_gate").assertDoesNotExist()
        }
        assertTrue(runBlocking { PrivacyConsent(context).withdraw() })
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithTag("privacy_gate").assertIsDisplayed()
        }
    }
}
