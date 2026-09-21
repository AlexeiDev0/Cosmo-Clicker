package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class QuestRewardTest {
    private fun quest(cases: Int = 0) = Quest("reward", QuestType.CLICK_PLANET, "", 1.0, 1.0,
        rewardDebris = 100.0, rewardCases = cases, isCompleted = true)

    @Test fun mixedRewardIsPaidExactlyOnceAndQueuesEveryCase() {
        val q = quest(3).copy(rewardPrestigePoints = 2)
        val state = GameState(activeQuests = listOf(q))
        val claimed = QuestEngine.claim(state, q.id, emptyList(), 123L)!!
        assertEquals(150.0, claimed.totalDebris, 0.0)
        assertEquals(2, claimed.prestigePoints)
        assertEquals(2, claimed.pendingCaseOpenings)
        assertTrue(claimed.isRewardCaseOpening)
        assertTrue(claimed.isOpeningCase)
        assertEquals(123L, claimed.dailyQuestsCompletedAt)
        assertNull(QuestEngine.claim(claimed, q.id, emptyList(), 124L))
    }

    @Test fun rewardDoesNotOverwritePaidOpeningOrUncollectedDrone() {
        val state = GameState(activeQuests = listOf(quest(2)))
        assertNull(QuestEngine.claim(state.copy(isOpeningCase = true), "reward", emptyList(), 1L))
        assertNull(QuestEngine.claim(state.copy(lastDroppedDroneId = "drone_1"), "reward", emptyList(), 1L))
        assertNull(QuestEngine.claim(state.copy(showCaseBundleSummary = true), "reward", emptyList(), 1L))
        assertNull(QuestEngine.claim(state.copy(activeQuests = listOf(quest().copy(isCompleted = false))), "reward", emptyList(), 1L))
    }

    @Test fun freeCasesDoNotRaiseShopPricesAndPaidCasesStillDo() {
        val state = GameState(casePurchasesByType = mapOf(CaseType.COMMON to 5))
        assertEquals(5, CaseController.purchaseCountsAfterOpening(state.copy(isRewardCaseOpening = true), CaseType.COMMON)[CaseType.COMMON])
        assertEquals(6, CaseController.purchaseCountsAfterOpening(state, CaseType.COMMON)[CaseType.COMMON])
    }

    @Test fun droneRewardUpdatesCollectionAndObtainQuest() {
        val drone = FleetConfig("drone_1", "", 250.0, 0)
        val obtain = Quest("obtain", QuestType.OBTAIN_DRONE, "", 1.0, 0.0, targetDroneId = drone.id)
        val state = GameState(activeQuests = listOf(quest().copy(rewardDroneId = drone.id), obtain))
        val claimed = QuestEngine.claim(state, "reward", listOf(drone), 1L)!!
        assertEquals(1, claimed.fleetCounts[drone.id])
        assertTrue(drone.id in claimed.discoveredDroneIds)
        assertTrue(claimed.activeQuests.single().isCompleted)
    }

    @Test fun oldQuestGrowsWithProgressAndPaysTheDisplayedQuote() {
        val q = quest().copy(rewardDebris = 8_000.0, rewardPlanetId = "p1")
        val state = GameState(ownedPlanets = setOf("p1", "p30"), activeQuests = listOf(q))
        val quote = EconomyBalance.questReward(state, q)
        assertTrue(quote >= EconomyBalance.planetPrice(31) * .05)
        assertTrue(quote > q.rewardDebris)
        assertEquals(quote, EconomyBalance.questReward(state.copy(currentPlanetId = "p30"), q), 0.0)
        assertEquals(state.totalDebris + quote, QuestEngine.claim(state, q.id, emptyList(), 1L)!!.totalDebris, 0.0)
        assertTrue(EconomyBalance.questReward(state, q.copy(rewardPlanetId = null)) > q.rewardDebris)
    }

    @Test fun rewardsStayFiniteAndGrowThroughAll39Worlds() {
        var previous = 0.0
        for (planet in 1..39) {
            val state = GameState(ownedPlanets = setOf("p1", "p$planet"))
            val daily = quest().copy(rewardPlanetId = "p1")
            val reward = EconomyBalance.questReward(state, daily)
            assertTrue(reward.isFinite() && reward >= previous)
            assertTrue(EconomyBalance.questReward(state, daily.copy(cadence = QuestCadence.WEEKLY, difficulty = QuestDifficulty.HARD)) >= reward)
            previous = reward
        }
    }

    @Test fun dailyRewardPreviewKeepsClaimedDayAndStreakSurvivesDst() {
        val oldZone = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))
            fun time(day: Int, hour: Int) = Calendar.getInstance().apply {
                clear(); set(2026, Calendar.MARCH, day, hour, 30)
            }.timeInMillis
            val before = time(28, 12)
            val after = time(30, 0)
            val state = DailyRewardEngine.claim(GameState(), before)!!
            val second = DailyRewardEngine.claim(state, time(29, 12))!!
            assertEquals(2, DailyRewardEngine.preview(second, time(29, 14)).day)
            assertEquals(3, DailyRewardEngine.preview(second, after).day)
        } finally { TimeZone.setDefault(oldZone) }
    }
}
