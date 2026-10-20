package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test

class ProgressionExpansionTest {
    @Test fun dailyRewardsKeepGrowingBeyondSevenDaysAndAfterAMissedDay() {
        val start = 1_788_134_400_000L
        var state = GameState()
        var lastReward = 0.0
        repeat(31) { day ->
            val now = start + day * 86_400_000L
            val reward = DailyRewardEngine.preview(state, now)
            assertEquals(day + 1, reward.day)
            assertTrue(reward.debris > lastReward)
            state = DailyRewardEngine.claim(state, now)!!
            assertNull(DailyRewardEngine.claim(state, now))
            lastReward = reward.debris
        }
        val afterGap = DailyRewardEngine.preview(state, start + 40 * 86_400_000L)
        assertEquals(32, afterGap.day)
        assertTrue(afterGap.debris > lastReward)
    }

    @Test fun shortPlayTimeRewardsStaySmallOnLatePlanetsAndCanBeClaimedOnlyOnce() {
        val before = GameState(currentPlanetId = "p39", lifetimeStats = LifetimeStats(playTimeMillis = 1_799_999L))
        assertFalse("time_30m" in AchievementEngine.evaluate(before).unlockedAchievementIds)
        val earned = AchievementEngine.evaluate(before.copy(lifetimeStats = LifetimeStats(playTimeMillis = 1_800_000L)))
        val claimed = AchievementEngine.claim(earned, "time_30m")!!
        assertEquals(100.0, claimed.totalDebris - earned.totalDebris, .001)
        assertNull(AchievementEngine.claim(claimed, "time_30m"))
        assertEquals(1_800_000L, claimed.lifetimeStats.playTimeMillis)
    }

    @Test fun prestigePreservesTimeAndDailyRewardDay() {
        val state = GameState(ownedPlanets = setOf("p1", "p20"), dailyRewardStreak = 21,
            lifetimeStats = LifetimeStats(playTimeMillis = 43_200_000L))
        val prestige = MetaProgressEngine.prestige(state)!!
        assertEquals(21, prestige.dailyRewardStreak)
        assertEquals(43_200_000L, prestige.lifetimeStats.playTimeMillis)
    }

    @Test fun nearCompleteAchievementsComeBeforeDistantOnesAndClaimsGoLast() {
        val state = GameState(lifetimeStats = LifetimeStats(clicks = 90), fleetCounts = mapOf("drone_1" to 1),
            claimedAchievementIds = setOf("salvage_10"), unlockedAchievementIds = setOf("salvage_10"))
        val ordered = MilestoneProgress.ordered(state, QuestDifficulty.EASY).map { it.id }
        assertTrue(ordered.indexOf("click_100") < ordered.indexOf("fleet_5"))
        assertTrue(ordered.indexOf("fleet_5") < ordered.indexOf("salvage_10"))
    }

    private fun market() = GameState(totalDebris = 1_000_000.0,
        activeEvent = GameEvent(GameEventType.TRADING_SHIP, 30_000L, reward = 1_000.0), stormRound = 2)

    @Test fun droneBarterProtectsLastCopiesAndActiveDrones() {
        val onlyOne = market().copy(fleetCounts = mapOf("drone_1" to 1))
        assertEquals(onlyOne, EventEngine.buyTradeOffer(onlyOne, TradeOffer.DRONE_FOR_CASE, 1_000L))
        val allActive = market().copy(fleetCounts = mapOf("drone_1" to 2), activeFleetCounts = mapOf("drone_1" to 2))
        assertEquals(allActive, EventEngine.buyTradeOffer(allActive, TradeOffer.DRONE_FOR_CASE, 1_000L))
        val spare = allActive.copy(activeFleetCounts = mapOf("drone_1" to 1))
        val exchanged = EventEngine.buyTradeOffer(spare, TradeOffer.DRONE_FOR_CASE, 1_000L)
        assertEquals(1, exchanged.fleetCounts["drone_1"])
        assertEquals(1, exchanged.activeFleetCounts["drone_1"])
        assertEquals(spare.totalDebris, exchanged.totalDebris, .001)
        assertEquals(CaseType.RARE, exchanged.openingCaseType)
    }

    @Test fun partsBarterConsumesExactlyTwentyAndCannotRepeat() {
        val state = market().copy(droneParts = mapOf("drone_1" to 12, "drone_2" to 11))
        val result = EventEngine.buyTradeOffer(state, TradeOffer.PARTS_FOR_DRONE, 1_000L)
        assertEquals(3, result.droneParts.values.sum())
        assertEquals(1, result.fleetCounts["drone_41"])
        assertNull(result.activeEvent)
        assertEquals(result, EventEngine.buyTradeOffer(result, TradeOffer.PARTS_FOR_DRONE, 1_100L))
    }

    @Test fun exclusiveMerchantStockIsRareAndStableForEachVisit() {
        var exclusive = 0
        repeat(2000) { index ->
            val event = GameEvent(GameEventType.TRADING_SHIP, 30_000L, startedAt = index * 1_000L)
            val offers = EventEngine.tradeOffers(event)
            assertEquals(offers, EventEngine.tradeOffers(event))
            assertEquals(3, offers.toSet().size)
            if (offers.any { it == TradeOffer.MERCHANT_CROWN || it == TradeOffer.MERCHANT_MANTA }) exclusive++
        }
        assertTrue(exclusive in 50..200)
    }
}
