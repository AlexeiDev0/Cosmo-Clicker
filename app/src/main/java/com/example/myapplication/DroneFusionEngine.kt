package com.example.myapplication

data class DroneFusionRecipe(val source: Rarity, val target: Rarity, val chancePercent: Int)

data class DroneFusionResult(val state: GameState, val rewardDroneId: String?)

/** Fusion consumes two healthy reserve drones. Rolls are supplied once by the caller. */
object DroneFusionEngine {
    val recipes = listOf(
        DroneFusionRecipe(Rarity.COMMON, Rarity.RARE, 25),
        DroneFusionRecipe(Rarity.UNCOMMON, Rarity.RARE, 20),
        DroneFusionRecipe(Rarity.RARE, Rarity.EPIC, 12),
        DroneFusionRecipe(Rarity.EPIC, Rarity.LEGENDARY, 5)
    )

    fun recipe(rarity: Rarity): DroneFusionRecipe? = recipes.find { it.source == rarity }

    fun availableCount(state: GameState, id: String): Int = (
        (state.fleetCounts[id] ?: 0).coerceAtLeast(0).toLong() -
            (state.activeFleetCounts[id] ?: 0).coerceAtLeast(0).toLong() -
            (state.damagedFleetCounts[id] ?: 0).coerceAtLeast(0).toLong()
        ).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

    fun rewards(fleet: List<FleetConfig>, recipe: DroneFusionRecipe): List<FleetConfig> =
        fleet.filter {
            it.rarity == recipe.target && it.id !in setOf("drone_40", "drone_41")
        }

    fun fuse(
        state: GameState,
        sources: List<String>,
        fleet: List<FleetConfig>,
        successRoll: Int,
        rewardIndex: Int
    ): DroneFusionResult? {
        if (sources.size != 2 || successRoll !in 0..99) return null
        val byId = fleet.associateBy { it.id }
        val first = byId[sources[0]] ?: return null
        val second = byId[sources[1]] ?: return null
        val recipe = recipe(first.rarity) ?: return null
        if (first.rarity != second.rarity) return null
        val candidates = rewards(fleet, recipe)
        if (rewardIndex !in candidates.indices) return null
        val spent = sources.groupingBy { it }.eachCount()
        if (spent.any { (id, count) -> availableCount(state, id) < count }) return null

        val counts = state.fleetCounts.toMutableMap()
        spent.forEach { (id, count) -> counts[id] = counts.getValue(id) - count }
        val reward = candidates[rewardIndex].takeIf { successRoll < recipe.chancePercent }
        if (reward != null) {
            val owned = counts[reward.id] ?: 0
            if (owned < 0 || owned == Int.MAX_VALUE) return null
            counts[reward.id] = owned + 1
        }
        val quests = if (reward == null) state.activeQuests else QuestEngine.advance(
            QuestEngine.advance(state.activeQuests, QuestType.OBTAIN_DRONE, droneId = reward.id),
            QuestType.OBTAIN_RARE_DRONE, droneRarity = reward.rarity
        )
        return DroneFusionResult(
            state.copy(
                fleetCounts = counts,
                discoveredDroneIds = state.discoveredDroneIds + sources + listOfNotNull(reward?.id),
                activeQuests = quests
            ),
            reward?.id
        )
    }
}
