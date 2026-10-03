package com.example.myapplication

import android.graphics.BitmapFactory
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Before
import org.junit.Test

class MainActivityUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun freezeGameAnimations() {
        if (composeRule.onAllNodesWithTag("privacy_gate").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("privacy_agreement").performClick()
            composeRule.onNodeWithTag("privacy_accept").performClick()
            composeRule.waitUntil(5_000) {
                composeRule.onAllNodesWithTag("privacy_gate").fetchSemanticsNodes().isEmpty()
            }
        }
        composeRule.mainClock.autoAdvance = false
    }

    @Test
    fun applicationIdAndMainGameAreAvailable() {
        assertEquals(
            "com.orbitsalvagers.droneclicker",
            composeRule.activity.applicationContext.packageName
        )

        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("debris").assertIsDisplayed()
    }

    @Test
    fun settingsLanguageAndResetDialogsAreReachable() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()

        composeRule.onNodeWithText("Language").performClick()
        composeRule.onNodeWithText("English").assertIsDisplayed()
        composeRule.onNodeWithText("Русский").assertIsDisplayed()
        composeRule.onNodeWithText("Español").assertIsDisplayed()
        composeRule.onNodeWithText("English").performClick()

        composeRule.onNodeWithText("Reset game progress").performClick()
        composeRule.onNodeWithText("Reset all progress?").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onAllNodesWithText("Reset all progress?").assertCountEquals(0)
    }

    @Test
    fun achievementsAreReachableFromSettings() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Achievements").performClick()
        composeRule.onNodeWithText("Achievements").assertIsDisplayed()
    }

    @Test
    fun unaffordableShopActionIsVisibleBeforeEarningCurrency() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription("Open shop").performClick()
        composeRule.onNodeWithText("Cosmic Shop").assertIsDisplayed()
        composeRule.onAllNodesWithText("Buy")[0].assertIsDisplayed()
    }

    @Test
    fun hangarIsReachableAndShowsLiveFleetOverview() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription("Open drone hangar").performClick()
        composeRule.onNodeWithText("Drone Hangar").assertIsDisplayed()
        composeRule.onNodeWithText("Hangar capacity").assertIsDisplayed()
        composeRule.onNodeWithText("All").assertIsDisplayed()
        composeRule.onNodeWithText("Owned").assertIsDisplayed()
        composeRule.onNodeWithText("In flight").assertIsDisplayed()
    }

    @Test
    fun statisticsExposeCompletionAndJourneyMetrics() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription("Open statistics").performClick()
        composeRule.onNodeWithText("Progress statistics").assertIsDisplayed()
        composeRule.onNodeWithText("Journey progress").assertIsDisplayed()
        composeRule.onNodeWithText("Best combo").assertIsDisplayed()
    }

    @Test
    fun navigationArtworkStaysOptimizedAndTransparent() {
        val resources = composeRule.activity.resources
        val artwork = listOf(
            R.drawable.ic_nav_shop_generated_v1,
            R.drawable.ic_nav_hangar_generated_v1,
            R.drawable.ui_button_quest_v3,
            R.drawable.ui_button_statistics_v2,
            R.drawable.ui_button_achievements_v2,
            R.drawable.ui_button_settings_v3
        )

        artwork.forEach { drawable ->
            val bitmap = BitmapFactory.decodeResource(resources, drawable)
            assertTrue("Navigation artwork width is too large", bitmap.width <= 256)
            assertTrue("Button height is too large", bitmap.height <= 256)
            assertEquals("Navigation artwork corner must be transparent", 0, bitmap.getPixel(0, 0).ushr(24))
        }
    }

    private fun dismissStartScreenIfPresent() {
        val prompt = composeRule.onAllNodesWithText("Tap me to continue")
        if (prompt.fetchSemanticsNodes().isNotEmpty()) {
            prompt[0].performClick()
            composeRule.mainClock.advanceTimeBy(1_000)
        }
    }
}
