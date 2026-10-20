package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementEngineTest {
    @Test
    fun wealthMilestonesUseAttainableBalanceTargets() {
        val first = AchievementEngine.definitions.single { it.id == "wealth_1t" }
        val second = AchievementEngine.definitions.single { it.id == "wealth_1q" }
        org.junit.Assert.assertFalse(first.isUnlocked(GameState(totalDebris = 9_999_999.0)))
        assertTrue(first.isUnlocked(GameState(totalDebris = 10_000_000.0)))
        org.junit.Assert.assertFalse(second.isUnlocked(GameState(totalDebris = 99_999_999.0)))
        assertTrue(second.isUnlocked(GameState(totalDebris = 100_000_000.0)))
    }

    @Test
    fun previouslyClaimedWealthMilestoneIsNotGrantedAgainAfterRebalance() {
        val state = GameState(totalDebris = 100_000_000.0,
            unlockedAchievementIds = setOf("wealth_1t", "wealth_1q"),
            claimedAchievementIds = setOf("wealth_1t", "wealth_1q"), prestigePoints = 17)
        val evaluated = AchievementEngine.evaluate(state)
        assertNull(AchievementEngine.claim(evaluated, "wealth_1t"))
        assertNull(AchievementEngine.claim(evaluated, "wealth_1q"))
        assertEquals(17, evaluated.prestigePoints)
    }

    @Test
    fun eligibleAchievementsUnlockTogether() {
        val state = GameState(
            ownedPlanets = (1..5).mapTo(mutableSetOf()) { "p$it" },
            fleetCounts = mapOf("drone" to 5),
            lifetimeStats = LifetimeStats(clicks = 100)
        )

        val result = AchievementEngine.evaluate(state)

        assertTrue("click_100" in result.unlockedAchievementIds)
        assertTrue("fleet_5" in result.unlockedAchievementIds)
        assertTrue("planets_5" in result.unlockedAchievementIds)
    }

    @Test
    fun debrisRewardScalesWithCurrentPlanetAndCanOnlyBeClaimedOnce() {
        val state = GameState(
            totalDebris = 100.0,
            currentPlanetId = "p3",
            unlockedAchievementIds = setOf("click_100")
        )

        val claimed = requireNotNull(AchievementEngine.claim(state, "click_100"))

        assertEquals(100.0 + EconomyBalance.scaledReward(5_000.0, "p3"), claimed.totalDebris, 0.0001)
        assertTrue("click_100" in claimed.claimedAchievementIds)
        assertNull(AchievementEngine.claim(claimed, "click_100"))
    }

    @Test
    fun lockedOrUnknownAchievementCannotBeClaimed() {
        assertNull(AchievementEngine.claim(GameState(), "click_100"))
        assertNull(AchievementEngine.claim(GameState(), "missing"))
    }

    @Test
    fun prestigePointRewardIsApplied() {
        val state = GameState(unlockedAchievementIds = setOf("prestige_1"))
        val result = requireNotNull(AchievementEngine.claim(state, "prestige_1"))
        assertEquals(1, result.prestigePoints)
    }
}
