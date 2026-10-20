package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ControllerTest {
    @Test
    fun restartRecoversOnlyUnopenedPaidCases() {
        val waiting = GameState(openingCaseType = CaseType.RARE, pendingCaseOpenings = 2)
        val recovered = CaseController.restoreOpening(waiting)
        assertTrue(recovered.isOpeningCase)
        assertEquals(1, recovered.pendingCaseOpenings)
        assertEquals(recovered, CaseController.restoreOpening(recovered))
        val displayed = waiting.copy(lastDroppedDroneId = "drone_1")
        assertEquals(displayed, CaseController.restoreOpening(displayed))
        val finished = waiting.copy(pendingCaseOpenings = 0)
        assertEquals(finished, CaseController.restoreOpening(finished))
    }
    @Test
    fun casePurchaseRejectsInvalidValuesAndUnfinishedResults() {
        for (balance in listOf(Double.NaN, Double.POSITIVE_INFINITY, -1.0)) {
            assertNull(CaseController.startOpening(GameState(totalDebris = balance), CaseType.COMMON, 1))
        }
        val funded = GameState(totalDebris = 1_000_000.0)
        assertNull(CaseController.startOpening(funded, CaseType.COMMON, 0))
        assertNull(CaseController.startOpening(funded, CaseType.COMMON, -1))
        assertNull(CaseController.startOpening(funded.copy(showCaseBundleSummary = true), CaseType.COMMON, 1))
        assertNull(CaseController.startOpening(funded.copy(casePurchasesByType = mapOf(CaseType.COMMON to Int.MAX_VALUE)), CaseType.COMMON, 1))
        assertNull(CaseController.startOpening(funded.copy(totalDebris = Double.MAX_VALUE), CaseType.COMMON, 1))
    }

    @Test
    fun caseBundleDeductsExactDisplayedCostAndCannotBePurchasedTwice() {
        CaseType.entries.forEach { type ->
            val balance = 10_000_000.0
            val cost = CaseController.bundleCost(2, type, 3)
            val initial = GameState(totalDebris = balance, casePurchasesByType = mapOf(type to 2))
            val purchased = CaseController.startOpening(initial, type, 3)!!
            assertEquals(balance - cost, purchased.totalDebris, 0.0)
            assertNull(CaseController.startOpening(purchased, type, 3))
        }
    }
    @Test
    fun fleetControllerKeepsOwnershipAndCapacityConsistent() {
        val item = FleetConfig("drone_1", "", 250.0, 0)
        val state = GameState(
            fleetCounts = mapOf(item.id to 2),
            activeFleetCounts = mapOf(item.id to 1)
        )
        val recalled = FleetController.recall(state, item.id)!!
        assertEquals(0, recalled.activeFleetCounts[item.id])
        val deployed = FleetController.deploy(recalled, item)!!
        assertEquals(1, deployed.activeFleetCounts[item.id])
        val sold = FleetController.sell(deployed, item)!!
        assertEquals(1, sold.fleetCounts[item.id])
        assertEquals(1, sold.activeFleetCounts[item.id])
    }

    @Test
    fun caseControllerStartsOnlyAffordableOpening() {
        assertNull(CaseController.startOpening(GameState(totalDebris = 0.0), CaseType.COMMON, 1))
        val started = CaseController.startOpening(GameState(totalDebris = 100_000.0), CaseType.COMMON, 2)!!
        assertTrue(started.isOpeningCase)
        assertEquals(1, started.pendingCaseOpenings)
        assertFalse(started.showCaseBundleSummary)
    }

    @Test
    fun collectingCaseRewardTwiceAdvancesOnlyOnePendingCase() {
        val reward = GameState(
            openingCaseType = CaseType.COMMON,
            pendingCaseOpenings = 2,
            lastDroppedDroneId = "drone_1"
        )

        val advanced = CaseController.collectDisplayedReward(reward)!!

        assertEquals(1, advanced.pendingCaseOpenings)
        assertTrue(advanced.isOpeningCase)
        assertNull(CaseController.collectDisplayedReward(advanced))
    }

    @Test
    fun economyControllerPurchasesUpgrade() {
        val item = ItemConfig("magnet", "", 15.0, 1.0, 0)
        val state = GameState(totalDebris = 200.0)
        val purchased = EconomyController.buyClickUpgrade(state, item)!!
        assertEquals(1, purchased.clickLevels[item.id])
        assertEquals(50.0, purchased.totalDebris, 0.0)
    }

    @Test
    fun builtInAutoclickerUsesPremiumPriceCurve() {
        assertEquals(100_000.0, EconomyController.utilityUpgradeCost("autoclick", 0), 0.0)
        assertEquals(275_000.0, EconomyController.utilityUpgradeCost("autoclick", 1), 0.0)
    }
}
