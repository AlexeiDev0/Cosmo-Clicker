package com.example.myapplication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.ui.components.DroneFusionDialog
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DroneFusionUiTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val common = FleetConfig("drone_1", "", 250.0, GameResourceRegistry.drone(1), rarity = Rarity.COMMON)
    private val rare = FleetConfig("drone_19", "", 500.0, GameResourceRegistry.drone(19), rarity = Rarity.RARE)
    private val fleet = listOf(common, rare)

    private fun selectCommon() {
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasTestTag("fusion_select_${common.id}"))
        compose.onNodeWithTag("fusion_select_${common.id}").performClick()
    }

    @Test fun cancelDoesNotSpendDronesAndConfirmedFailureSpendsOnlyOnce() {
        val state = mutableStateOf(GameState(fleetCounts = mapOf(common.id to 2)))
        var attempts = 0
        compose.setContent {
            MyApplicationTheme {
                DroneFusionDialog(state.value, fleet, { sources ->
                    attempts++
                    DroneFusionEngine.fuse(state.value, sources, fleet, 99, 0)
                        ?.also { state.value = it.state }
                }, {})
            }
        }
        compose.onNodeWithTag("fusion_review").assertIsNotEnabled()
        selectCommon()
        compose.onNodeWithTag("fusion_review").assertIsNotEnabled()
        selectCommon()
        compose.onNodeWithTag("fusion_review").performClick()
        compose.onNodeWithText(context.getString(R.string.fusion_risk)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.cancel)).performClick()
        compose.runOnIdle {
            assertEquals(0, attempts)
            assertEquals(2, state.value.fleetCounts[common.id])
        }
        compose.onNodeWithTag("fusion_review").performClick()
        compose.onNodeWithTag("fusion_confirm").performClick()
        compose.onNodeWithText(context.getString(R.string.fusion_failure)).assertIsDisplayed()
        compose.onNodeWithTag("fusion_confirm").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, attempts)
            assertEquals(0, state.value.fleetCounts[common.id])
        }
    }

    @Test fun successfulFusionShowsRewardAndKeepsItInReserve() {
        val state = mutableStateOf(GameState(fleetCounts = mapOf(common.id to 2)))
        compose.setContent {
            MyApplicationTheme {
                DroneFusionDialog(state.value, fleet, { sources ->
                    DroneFusionEngine.fuse(state.value, sources, fleet, 0, 0)
                        ?.also { state.value = it.state }
                }, {})
            }
        }
        repeat(2) { selectCommon() }
        compose.onNodeWithTag("fusion_review").performClick()
        compose.onNodeWithTag("fusion_confirm").performClick()
        compose.onNodeWithText(context.getString(R.string.fusion_success)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.fusion_reward_reserve)).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, state.value.fleetCounts[rare.id])
            assertEquals(0, state.value.activeFleetCounts[rare.id] ?: 0)
        }
    }

    @Test
    fun gridHighlightsPairAndSlotsAllowRemovingDrones() {
        val previewFleet = (1..27).map { number ->
            FleetConfig("drone_$number", "", 250.0, GameResourceRegistry.drone(number), rarity = when {
                number <= 10 -> Rarity.COMMON
                number <= 18 -> Rarity.UNCOMMON
                number <= 24 -> Rarity.RARE
                else -> Rarity.EPIC
            })
        }
        val state = GameState(fleetCounts = previewFleet.associate { it.id to 3 })
        compose.setContent {
            MyApplicationTheme { DroneFusionDialog(state, previewFleet, { null }, {}) }
        }
        selectCommon()
        compose.onNodeWithTag("fusion_grid").performScrollToNode(hasTestTag("fusion_select_drone_2"))
        compose.onNodeWithTag("fusion_select_drone_2").performClick()
        compose.onNodeWithTag("fusion_slot_0").assertIsEnabled()
        compose.onNodeWithTag("fusion_slot_1").assertIsEnabled()
        compose.onNodeWithTag("fusion_review").assertIsEnabled()
        compose.onNodeWithTag("fusion_grid").performScrollToIndex(0)
        compose.onNodeWithTag("fusion_select_drone_1").assertIsSelected()
        compose.onNodeWithTag("fusion_slot_0").performClick()
        compose.onNodeWithTag("fusion_slot_1").assertIsNotEnabled()
        compose.onNodeWithTag("fusion_review").assertIsNotEnabled()
        compose.onNodeWithTag("fusion_grid").performScrollToNode(hasTestTag("fusion_select_drone_19"))
        compose.onNodeWithTag("fusion_select_drone_19").assertIsNotEnabled()
    }

    @Test fun fusionIsAppliedOnceBeforeAnimationAndResultIsRevealedAfterward() {
        val state = mutableStateOf(GameState(fleetCounts = mapOf(common.id to 2)))
        var attempts = 0
        compose.setContent {
            MyApplicationTheme {
                DroneFusionDialog(state.value, fleet, { sources ->
                    attempts++
                    DroneFusionEngine.fuse(state.value, sources, fleet, 0, 0)?.also { state.value = it.state }
                }, {})
            }
        }
        repeat(2) { selectCommon() }
        compose.onNodeWithTag("fusion_review").performClick()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("fusion_confirm").performClick()
        compose.mainClock.advanceTimeBy(32)
        compose.onNodeWithTag("fusion_animation").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, attempts)
            assertEquals(0, state.value.fleetCounts[common.id])
            assertEquals(1, state.value.fleetCounts[rare.id])
        }
        compose.mainClock.advanceTimeBy(1_500)
        compose.mainClock.autoAdvance = true
        compose.onNodeWithText(context.getString(R.string.fusion_success)).assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, attempts) }
    }

    @Test fun reducedMotionShowsResultWithoutFusionAnimation() {
        val state = GameState(fleetCounts = mapOf(common.id to 2))
        compose.setContent {
            MyApplicationTheme(reducedMotion = true) {
                DroneFusionDialog(state, fleet, { sources -> DroneFusionEngine.fuse(state, sources, fleet, 0, 0) }, {})
            }
        }
        repeat(2) { selectCommon() }
        compose.onNodeWithTag("fusion_review").performClick()
        compose.onNodeWithTag("fusion_confirm").performClick()
        compose.onNodeWithTag("fusion_animation").assertDoesNotExist()
        compose.onNodeWithText(context.getString(R.string.fusion_success)).assertIsDisplayed()
    }
}
