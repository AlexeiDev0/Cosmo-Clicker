package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test

class FleetProgressionIncomeTest {
    private val fleet = mapOf("common" to FleetConfig("common", "", 0.0, 0))
    private val upgrades = listOf(ItemConfig("upgrade", "", 0.0, 999.0, 0))
    private val state = GameState(fleetCounts = mapOf("common" to 6), clickLevels = mapOf("upgrade" to 1))

    @Test fun thousandPerClickNoLongerLeavesSixDronesAtThirtyPerSecond() {
        val income = EconomyBalance.passiveIncome(state, fleet, 0L, upgrades)
        assertTrue(income >= 1_000.0 && income <= 2_000.0)
        assertTrue(income > EconomyBalance.passiveIncome(state, fleet, 0L) * 30.0)
    }

    @Test fun upgradesGrowProductivityButDoNotGenerateIncomeWithoutDrones() {
        val oneLevel = EconomyBalance.passiveIncome(state, fleet, 0L, upgrades)
        assertTrue(EconomyBalance.passiveIncome(state.copy(clickLevels = mapOf("upgrade" to 2)), fleet, 0L, upgrades) > oneLevel)
        assertEquals(0.0, EconomyBalance.passiveIncome(state.copy(fleetCounts = emptyMap()), fleet, 0L, upgrades), 0.0)
        assertEquals(30.0, EconomyBalance.passiveIncome(state.copy(clickLevels = emptyMap()), fleet, 0L, upgrades), 0.0)
    }

    @Test fun temporaryClickEffectsCannotPermanentlyIncreaseFleetProductivity() {
        assertEquals(EconomyBalance.fleetProgressionMultiplier(state, upgrades),
            EconomyBalance.fleetProgressionMultiplier(state.copy(eventMultiplier = 100.0,
                activeEffects = mapOf(SkillType.TRADE_CLICK_BOOST.id to Long.MAX_VALUE)), upgrades), 0.0)
    }

    @Test fun offlineIncomeReceivesTheSamePermanentUpgradeGrowth() {
        val base = OfflineProgressEngine.calculate(1_000L, 61_000L, state.fleetCounts, emptyMap())
        val scaled = OfflineProgressEngine.calculate(1_000L, 61_000L, state.fleetCounts, emptyMap(),
            EconomyBalance.fleetProgressionMultiplier(state, upgrades))
        assertTrue(scaled.reward > base.reward * 30.0)
        assertEquals(base.elapsedSeconds, scaled.elapsedSeconds)
    }

    @Test fun shopRewardKeepsUpWithTheNewFleetIncome() {
        val income = EconomyBalance.passiveIncome(state, fleet, 0L, upgrades)
        val reward = ShopAdRewardEngine.debrisReward(state, fleet, upgrades)
        assertTrue(reward >= income * 55.0 && reward <= income * 65.0)
    }
}
