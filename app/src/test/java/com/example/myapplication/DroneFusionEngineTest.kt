package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test

class DroneFusionEngineTest {
    private fun drone(id: String, rarity: Rarity) = FleetConfig(id, id, 250.0, 0, rarity = rarity)
    private val common = drone("common", Rarity.COMMON)
    private val other = drone("other", Rarity.COMMON)
    private val uncommon = drone("uncommon", Rarity.UNCOMMON)
    private val rare = drone("rare", Rarity.RARE)
    private val rare2 = drone("rare2", Rarity.RARE)
    private val epic = drone("epic", Rarity.EPIC)
    private val legendary = drone("legendary", Rarity.LEGENDARY)
    private val fleet = listOf(common, other, uncommon, rare, rare2, epic, legendary)

    private fun fuse(state: GameState, sources: List<String> = listOf(common.id, common.id), roll: Int = 0, index: Int = 0) =
        DroneFusionEngine.fuse(state, sources, fleet, roll, index)

    @Test fun identicalDronesConsumeExactlyTwoAndAddOneRareToReserve() {
        val state = GameState(totalDebris = 123.0, fleetCounts = mapOf(common.id to 3))
        val result = fuse(state)!!
        assertEquals(rare.id, result.rewardDroneId)
        assertEquals(1, result.state.fleetCounts[common.id])
        assertEquals(1, result.state.fleetCounts[rare.id])
        assertEquals(state.activeFleetCounts, result.state.activeFleetCounts)
        assertEquals(123.0, result.state.totalDebris, 0.0)
        assertTrue(result.state.discoveredDroneIds.containsAll(listOf(common.id, rare.id)))
        assertEquals(3, state.fleetCounts[common.id])
    }

    @Test fun differentModelsOfSameRarityCanBeCombined() {
        val state = GameState(fleetCounts = mapOf(common.id to 1, other.id to 1))
        val result = fuse(state, listOf(common.id, other.id), index = 1)!!
        assertEquals(rare2.id, result.rewardDroneId)
        assertEquals(0, result.state.fleetCounts[common.id])
        assertEquals(0, result.state.fleetCounts[other.id])
        assertEquals(1, result.state.fleetCounts[rare2.id])
    }

    @Test fun failedFusionPermanentlyRemovesBothWithoutRewardOrQuestProgress() {
        val quest = Quest("obtain", QuestType.OBTAIN_RARE_DRONE, "", 1.0, 0.0)
        val state = GameState(fleetCounts = mapOf(common.id to 2), activeQuests = listOf(quest), discoveredDroneIds = setOf(common.id))
        val result = fuse(state, roll = 99)!!
        assertNull(result.rewardDroneId)
        assertEquals(0, result.state.fleetCounts[common.id])
        assertEquals(0, result.state.fleetCounts[rare.id] ?: 0)
        assertEquals(state.activeQuests, result.state.activeQuests)
        assertEquals(state.discoveredDroneIds, result.state.discoveredDroneIds)
        assertNull(fuse(result.state))
    }

    @Test fun eachTierUsesItsExactChanceBoundaryAndLowerOdds() {
        val chances = DroneFusionEngine.recipes.map { it.chancePercent }
        assertTrue(chances.zipWithNext().all { (lower, higher) -> lower > higher })
        DroneFusionEngine.recipes.forEach { recipe ->
            val source = fleet.first { it.rarity == recipe.source }
            val state = GameState(fleetCounts = mapOf(source.id to 2))
            val success = fuse(state, listOf(source.id, source.id), recipe.chancePercent - 1)!!
            assertEquals(recipe.target, fleet.first { it.id == success.rewardDroneId }.rarity)
            val failure = fuse(state, listOf(source.id, source.id), recipe.chancePercent)!!
            assertNull(failure.rewardDroneId)
            assertEquals(0, failure.state.fleetCounts[source.id])
        }
        assertEquals(Rarity.RARE, DroneFusionEngine.recipe(Rarity.COMMON)!!.target)
        assertEquals(Rarity.RARE, DroneFusionEngine.recipe(Rarity.UNCOMMON)!!.target)
        assertNull(DroneFusionEngine.recipe(Rarity.LEGENDARY))
        assertNull(DroneFusionEngine.recipe(Rarity.VOID))
    }

    @Test fun activeAndDamagedDronesCannotBeSpent() {
        val state = GameState(
            fleetCounts = mapOf(common.id to 4),
            activeFleetCounts = mapOf(common.id to 1),
            damagedFleetCounts = mapOf(common.id to 2)
        )
        assertEquals(1, DroneFusionEngine.availableCount(state, common.id))
        assertNull(fuse(state))
        val enough = state.copy(fleetCounts = mapOf(common.id to 5))
        val result = fuse(enough)!!.state
        assertEquals(3, result.fleetCounts[common.id])
        assertEquals(enough.activeFleetCounts, result.activeFleetCounts)
        assertEquals(enough.damagedFleetCounts, result.damagedFleetCounts)
        assertEquals(0, DroneFusionEngine.availableCount(result, common.id))
    }

    @Test fun availabilityIsRecheckedWhenSelectionBecomesStale() {
        val state = GameState(fleetCounts = mapOf(common.id to 2))
        assertNotNull(fuse(state))
        assertNull(fuse(state.copy(activeFleetCounts = mapOf(common.id to 1))))
        assertNull(fuse(state.copy(damagedFleetCounts = mapOf(common.id to 1))))
        assertNull(fuse(state.copy(fleetCounts = mapOf(common.id to 1))))
    }

    @Test fun invalidPairsAndRollsNeverMutateInventory() {
        val state = GameState(fleetCounts = fleet.associate { it.id to 2 })
        listOf(emptyList(), listOf(common.id), listOf(common.id, common.id, common.id),
            listOf(common.id, rare.id), listOf("missing", common.id),
            listOf(legendary.id, legendary.id)).forEach { assertNull(fuse(state, it)) }
        assertNull(fuse(state, roll = -1))
        assertNull(fuse(state, roll = 100))
        assertNull(fuse(state, index = -1))
        assertNull(fuse(state, index = 2))
        assertEquals(2, state.fleetCounts[common.id])
    }

    @Test fun emptyRewardPoolAndExclusiveEventRewardsAreRejected() {
        val recipe = DroneFusionEngine.recipe(Rarity.COMMON)!!
        val exclusive = drone("drone_40", Rarity.RARE)
        assertEquals(listOf(rare, rare2), DroneFusionEngine.rewards(fleet + exclusive, recipe))
        val state = GameState(fleetCounts = mapOf(common.id to 2))
        assertNull(DroneFusionEngine.fuse(state, listOf(common.id, common.id), listOf(common, exclusive), 0, 0))
    }

    @Test fun successAdvancesObtainMissionsButNeverCaseOpeningMissions() {
        val quests = listOf(
            Quest("specific", QuestType.OBTAIN_DRONE, "", 1.0, 0.0, targetDroneId = rare.id),
            Quest("rare", QuestType.OBTAIN_RARE_DRONE, "", 1.0, 0.0),
            Quest("case", QuestType.OPEN_CASE, "", 1.0, 0.0)
        )
        val state = GameState(fleetCounts = mapOf(common.id to 2), activeQuests = quests)
        val result = fuse(state)!!.state
        assertTrue(result.activeQuests.first { it.id == "specific" }.isCompleted)
        assertTrue(result.activeQuests.first { it.id == "rare" }.isCompleted)
        assertFalse(result.activeQuests.first { it.id == "case" }.isCompleted)
        assertEquals(state.casePurchasesByType, result.casePurchasesByType)
        assertEquals(state.sessionStats, result.sessionStats)
    }

    @Test fun malformedCountsCannotUnderflowOrOverflow() {
        assertEquals(0, DroneFusionEngine.availableCount(GameState(fleetCounts = mapOf(common.id to -1)), common.id))
        val reserved = GameState(
            fleetCounts = mapOf(common.id to Int.MAX_VALUE),
            activeFleetCounts = mapOf(common.id to Int.MAX_VALUE),
            damagedFleetCounts = mapOf(common.id to Int.MAX_VALUE)
        )
        assertEquals(0, DroneFusionEngine.availableCount(reserved, common.id))
        assertNull(fuse(reserved))
        val fullReward = GameState(fleetCounts = mapOf(common.id to 2, rare.id to Int.MAX_VALUE))
        assertNull(fuse(fullReward))
    }
}
