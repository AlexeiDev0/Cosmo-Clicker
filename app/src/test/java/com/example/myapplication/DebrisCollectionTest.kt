package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test

class DebrisCollectionTest {
    @Test fun genericCountersCannotCompleteUniqueTypeQuests() {
        val quests = listOf(Quest("types", QuestType.COLLECT_DEBRIS_TYPES, "", 10.0, 0.0))
        assertEquals(quests, QuestEngine.advance(quests, QuestType.COLLECT_DEBRIS_TYPES, 10.0))
        assertEquals(quests, QuestEngine.advance(quests, QuestType.COLLECT_DEBRIS, 10.0))
    }

    @Test fun completeCollectionUnlocksFinalMilestonesAndCountsInGalaxy() {
        val empty = GameState()
        val full = AchievementEngine.evaluate(empty.copy(discoveredDebrisIds = (1..28).toSet()))
        assertTrue(full.unlockedAchievementIds.containsAll(setOf("salvage_10", "salvage_15", "salvage_20", "salvage_28")))
        val before = GalacticCollectionEngine.progress(empty, totalDrones = 29)
        val after = GalacticCollectionEngine.progress(full, totalDrones = 29)
        assertEquals(28, after.debrisTypes)
        assertEquals(28, after.totalDebrisTypes)
        assertEquals(before.discoveredEntries + 28, after.discoveredEntries)
        val claimed = AchievementEngine.claim(full, "salvage_28")!!
        assertEquals(full.prestigePoints + 5, claimed.prestigePoints)
        assertNull(AchievementEngine.claim(claimed, "salvage_28"))
    }

    @Test fun prestigePreservesDiscoveriesButResetsTimedQuestProgress() {
        val before = GameState(ownedPlanets = setOf("p1", "p10"), discoveredDebrisIds = (1..15).toSet(),
            activeQuests = listOf(Quest("q", QuestType.COLLECT_DEBRIS_TYPES, "", 10.0, 5.0, collectedDebrisIds = (1..5).toSet())))
        val after = MetaProgressEngine.prestige(before)!!
        assertEquals(before.discoveredDebrisIds, after.discoveredDebrisIds)
        assertTrue(after.activeQuests.isEmpty())
    }

    @Test fun repeatedTypesDoNotAdvanceDailyOrWeeklyGoalsTwice() {
        val quests = listOf(
            Quest("daily", QuestType.COLLECT_DEBRIS_TYPES, "", 10.0, 0.0),
            Quest("weekly", QuestType.COLLECT_DEBRIS_TYPES, "", 15.0, 0.0, cadence = QuestCadence.WEEKLY)
        )
        val first = QuestEngine.collectTypes(quests, (1..10).toSet())
        val repeated = QuestEngine.collectTypes(first, (1..10).toSet())
        assertEquals(first, repeated)
        assertTrue(repeated[0].isCompleted)
        assertFalse(repeated[1].isCompleted)
        val completed = QuestEngine.collectTypes(repeated, (11..15).toSet())
        assertTrue(completed[1].isCompleted)
        assertEquals(15.0, completed[1].progress, 0.0)
    }

    @Test fun newCycleStartsFreshAndRejectsInvalidTypes() {
        val quest = TimedQuestFactory { _, _ -> "" }.daily(1, "p1").first { it.type == QuestType.COLLECT_DEBRIS_TYPES }
        val result = QuestEngine.collectTypes(listOf(quest), setOf(-1, 0, 29, 28)).single()
        assertEquals(setOf(28), result.collectedDebrisIds)
        assertEquals(1.0, result.progress, 0.0)
        assertEquals(0.0, TimedQuestFactory { _, _ -> "" }.daily(2, "p1").first { it.type == QuestType.COLLECT_DEBRIS_TYPES }.progress, 0.0)
    }

    @Test fun collectionAchievementsUnlockAtMilestonesAndPayOnlyOnce() {
        val state = AchievementEngine.evaluate(GameState(discoveredDebrisIds = (1..15).toSet()))
        assertTrue(state.unlockedAchievementIds.containsAll(setOf("salvage_10", "salvage_15")))
        assertFalse("salvage_20" in state.unlockedAchievementIds)
        val claimed = AchievementEngine.claim(state, "salvage_15")!!
        assertTrue(claimed.totalDebris > state.totalDebris)
        assertNull(AchievementEngine.claim(claimed, "salvage_15"))
    }

    @Test fun everyTypeCanSpawnWithoutChangingRarityOdds() {
        val found = mutableSetOf<Int>()
        for (rarity in Rarity.entries) for (index in 0..7) {
            val random = object : RandomProvider {
                override fun nextFloat() = 0f
                override fun nextInt(until: Int) = index.coerceAtMost(until - 1)
                override fun nextLong(until: Long) = 0L
                override fun nextLong(from: Long, until: Long) = from
            }
            found += DebrisEngine.imageIndex(rarity, random)
        }
        assertEquals((1..28).toSet(), found)
    }
}
