package com.example.myapplication

/** Shared progress and ordering for achievement groups and player titles. */
object MilestoneProgress {
    private val targets = mapOf(
        "salvage_10" to 10.0, "salvage_15" to 15.0, "salvage_20" to 20.0, "salvage_28" to 28.0,
        "click_100" to 100.0, "click_1000" to 1000.0, "click_10000" to 10000.0, "click_100000" to 25000.0,
        "click_250k" to 50000.0, "click_1m" to 100000.0, "click_5m" to 250000.0,
        "fleet_5" to 5.0, "fleet_12" to 12.0, "fleet_50" to 50.0, "fleet_100" to 75.0, "fleet_250" to 100.0, "fleet_500" to 150.0,
        "collection_15" to 15.0, "collection_29" to 29.0,
        "planets_5" to 5.0, "planets_10" to 10.0, "planets_20" to 20.0, "planets_24" to 24.0,
        "events_10" to 10.0, "events_50" to 50.0, "events_100" to 75.0, "events_250" to 100.0, "events_500" to 150.0,
        "cases_25" to 25.0, "cases_100" to 100.0, "cases_250" to 150.0, "cases_500" to 250.0, "cases_1000" to 400.0,
        "prestige_1" to 1.0, "prestige_5" to 5.0, "prestige_10" to 10.0, "prestige_25" to 15.0, "prestige_50" to 25.0,
        "debris_10m" to 1e7, "debris_100m" to 1e8, "debris_1b" to 1e9, "debris_10b" to 1e10,
        "parts_25" to 25.0, "parts_100" to 75.0, "parts_300" to 150.0,
        "station_5" to 5.0, "station_10" to 10.0, "station_20" to 20.0,
        "wealth_1t" to AchievementEngine.FIRST_WEALTH_TARGET, "wealth_1q" to AchievementEngine.SECOND_WEALTH_TARGET,
        "tech_all" to Technology.entries.size.toDouble(), "achievements_40" to 40.0,
        "rank_novice" to 5.0, "rank_professional" to 15.0, "rank_veteran" to 30.0, "rank_legend" to 50.0
    ) + AchievementEngine.timeMilestones.associate { it.first to it.second.toDouble() }

    fun fraction(id: String, state: GameState): Float {
        if (id in state.unlockedAchievementIds) return 1f
        val value: Double = when (id.substringBefore('_')) {
            "time" -> state.lifetimeStats.playTimeMillis.toDouble()
            "click" -> state.lifetimeStats.clicks.toDouble()
            "salvage" -> state.discoveredDebrisIds.size.toDouble()
            "fleet" -> state.fleetCounts.values.sumOf { it.toLong() }.toDouble()
            "collection" -> state.discoveredDroneIds.size.toDouble()
            "planets" -> state.ownedPlanets.size.toDouble()
            "events" -> state.lifetimeStats.eventsCompleted.toDouble()
            "cases" -> state.lifetimeStats.casesOpened.toDouble()
            "prestige" -> state.lifetimeStats.prestiges.toDouble()
            "debris" -> state.lifetimeStats.debrisCollected.toDouble()
            "parts" -> state.droneParts.values.sumOf { it.toLong() }.toDouble()
            "station" -> state.stationLevels.values.sum().toDouble()
            "wealth" -> state.totalDebris
            "tech" -> state.technologies.size.toDouble()
            "achievements", "rank" -> state.claimedAchievementIds.size.toDouble()
            else -> 0.0
        }
        return (value / (targets[id] ?: 1.0)).coerceIn(0.0, 1.0).toFloat()
    }

    fun difficulty(id: String): QuestDifficulty {
        val category = id.substringBefore('_')
        val peers = targets.filterKeys { it.substringBefore('_') == category }.entries.sortedBy { it.value }
        val index = peers.indexOfFirst { it.key == id }.coerceAtLeast(0)
        return QuestDifficulty.entries[(index * 3 / peers.size.coerceAtLeast(1)).coerceAtMost(2)]
    }

    fun ordered(state: GameState, difficulty: QuestDifficulty): List<AchievementDefinition> =
        AchievementEngine.definitions.filter { difficulty(it.id) == difficulty }.sortedWith(
            compareBy<AchievementDefinition> { it.id in state.claimedAchievementIds }
                .thenByDescending { fraction(it.id, state) }
                .thenBy { targets[it.id] ?: 0.0 }.thenBy { it.id }
        )

    /** A title depends on collected achievements, never on a purchasable currency. */
    fun rank(state: GameState): Int = when {
        state.claimedAchievementIds.size >= 50 -> 4
        state.claimedAchievementIds.size >= 30 -> 3
        state.claimedAchievementIds.size >= 15 -> 2
        state.claimedAchievementIds.size >= 5 -> 1
        else -> 0
    }
}
