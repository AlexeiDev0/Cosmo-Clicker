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
import androidx.compose.ui.test.performScrollTo
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
        composeRule.onNodeWithContentDescription(label(R.string.settings)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.debris)).assertIsDisplayed()
    }

    @Test
    fun settingsLanguageAndPrivacyRemainAvailableWithoutRemovedActions() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.settings)).performClick()
        composeRule.onNodeWithText(label(R.string.settings)).assertIsDisplayed()

        composeRule.onNodeWithText(label(R.string.language)).performClick()
        composeRule.onNodeWithText("English").assertIsDisplayed()
        composeRule.onNodeWithText("Русский").assertIsDisplayed()
        composeRule.onNodeWithText("Español").assertIsDisplayed()
        val language = composeRule.activity.getSharedPreferences("settings", 0).getString("language", null)
        val option = when (language) { "en" -> R.string.language_english; "ru" -> R.string.language_russian;
            "es" -> R.string.language_spanish; else -> R.string.language_system }
        composeRule.onNodeWithText(label(option)).performClick()
        composeRule.onNodeWithTag("privacy_policy_link").assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.reset_game)).assertDoesNotExist()
        composeRule.onNodeWithText(label(R.string.achievements)).assertDoesNotExist()
        composeRule.onNodeWithText(label(R.string.telegram)).assertDoesNotExist()
        composeRule.onNodeWithText(label(R.string.tutorial_replay)).assertDoesNotExist()
    }

    @Test
    fun achievementsAreReachableFromMainHeader() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.open_achievements)).performClick()
        composeRule.onAllNodesWithText(label(R.string.achievements))[0].assertIsDisplayed()
    }

    @Test
    fun languageChangesRefreshVisibleResourcesAndCanBeRestored() {
        val original = composeRule.activity.getSharedPreferences("settings", 0).getString("language", null)
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.settings)).performClick()
        try {
            listOf("en", "ru", "es").forEach { language ->
                chooseLanguage(language)
                composeRule.mainClock.advanceTimeBy(100)
                composeRule.onNodeWithText(label(R.string.settings)).assertIsDisplayed()
                composeRule.onNodeWithText(label(R.string.language)).assertIsDisplayed()
                composeRule.onNodeWithTag("privacy_policy_link").assertIsDisplayed()
            }
        } finally {
            chooseLanguage(original)
        }
    }

    @Test
    fun eachCaseInformationShowsOnlyItsOwnDescription() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.open_shop)).performClick()
        composeRule.onNodeWithText(label(R.string.shop_tab_cases)).performClick()
        val cases = listOf(
            R.string.common_case to R.string.case_common_description,
            R.string.rare_case to R.string.case_rare_description,
            R.string.legendary_case to R.string.case_legendary_description
        )
        cases.forEach { (title, description) ->
            composeRule.onNodeWithContentDescription(label(R.string.case_information).format(label(title)))
                .performScrollTo().performClick()
            composeRule.onNodeWithText(label(description)).assertIsDisplayed()
            cases.filter { it.second != description }.forEach { (_, otherDescription) ->
                composeRule.onNodeWithText(label(otherDescription)).assertDoesNotExist()
            }
            composeRule.onNodeWithText(label(R.string.close)).performClick()
        }
    }

    private fun chooseLanguage(language: String?) {
        composeRule.onNodeWithText(label(R.string.language)).performClick()
        val option = when (language) {
            "en" -> R.string.language_english
            "ru" -> R.string.language_russian
            "es" -> R.string.language_spanish
            else -> R.string.language_system
        }
        composeRule.onNodeWithText(label(option)).performClick()
    }

    @Test
    fun unaffordableShopActionIsVisibleBeforeEarningCurrency() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.open_shop)).performClick()
        composeRule.onNodeWithText(label(R.string.case_shop_title)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.upgrade_plasma_magnet)).assertIsDisplayed()
    }

    @Test
    fun hangarIsReachableAndShowsLiveFleetOverview() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.open_hangar)).performClick()
        composeRule.onNodeWithText(label(R.string.drone_hangar_title)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.hangar_overview)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.hangar_filter_all)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.hangar_filter_owned)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.hangar_filter_active)).assertIsDisplayed()
    }

    @Test
    fun fusionReplacesStatisticsAndExplainsRiskBeforeSelectingDrones() {
        dismissStartScreenIfPresent()
        composeRule.onNodeWithContentDescription(label(R.string.open_statistics)).assertDoesNotExist()
        composeRule.onNodeWithContentDescription(label(R.string.open_fusion)).performClick()
        composeRule.onNodeWithText(label(R.string.fusion_title)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.fusion_risk_short)).assertIsDisplayed()
        composeRule.onNodeWithText(label(R.string.fusion_review)).assertIsDisplayed()
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

    private fun label(resource: Int): String {
        val activity = composeRule.activity
        val language = activity.getSharedPreferences("settings", 0).getString("language", null)
        val config = android.content.res.Configuration(activity.resources.configuration).apply {
            setLocale(language?.let(java.util.Locale::forLanguageTag)
                ?: android.content.res.Resources.getSystem().configuration.locales[0])
        }
        return activity.createConfigurationContext(config).getString(resource)
    }

    private fun dismissStartScreenIfPresent() {
        val prompt = composeRule.onAllNodesWithText(label(R.string.tap_to_continue))
        if (prompt.fetchSemanticsNodes().isNotEmpty()) {
            prompt[0].performClick()
            composeRule.mainClock.advanceTimeBy(1_000)
        }
        if (composeRule.onAllNodesWithTag("tutorial_dialog").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("tutorial_skip").performScrollTo().performClick()
            composeRule.mainClock.advanceTimeBy(100)
        }
        val offline = composeRule.onAllNodesWithText(label(R.string.collect_reward))
        if (offline.fetchSemanticsNodes().isNotEmpty()) {
            offline[0].performClick()
            composeRule.mainClock.advanceTimeBy(100)
        }
    }
}
