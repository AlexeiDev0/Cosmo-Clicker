package com.example.myapplication

data class LifetimeStats(
    val clicks: Long = 0L,
    val bestCombo: Int = 0,
    val debrisCollected: Long = 0L,
    val casesOpened: Int = 0,
    val eventsCompleted: Int = 0,
    val prestiges: Int = 0,
    val playTimeMillis: Long = 0L
)

data class AchievementDefinition(
    val id: String,
    val rewardDebris: Double = 0.0,
    val rewardPrestigePoints: Int = 0,
    val scaleDebrisReward: Boolean = true,
    val isUnlocked: (GameState) -> Boolean
)

object AchievementEngine {
    // Keep persisted achievement IDs stable while making banked-resource goals attainable.
    const val FIRST_WEALTH_TARGET = 10_000_000.0
    const val SECOND_WEALTH_TARGET = 100_000_000.0
    val timeMilestones = listOf(
        Triple("time_30m", 1_800_000L, 100.0),
        Triple("time_1h", 3_600_000L, 250.0),
        Triple("time_3h", 10_800_000L, 750.0),
        Triple("time_6h", 21_600_000L, 1_500.0),
        Triple("time_12h", 43_200_000L, 3_000.0),
        Triple("time_1d", 86_400_000L, 7_500.0),
        Triple("time_3d", 259_200_000L, 25_000.0),
        Triple("time_7d", 604_800_000L, 75_000.0),
        Triple("time_14d", 1_209_600_000L, 175_000.0),
        Triple("time_30d", 2_592_000_000L, 500_000.0)
    )
    val definitions = listOf(
        AchievementDefinition("salvage_10", rewardDebris = 100_000.0) { it.discoveredDebrisIds.size >= 10 },
        AchievementDefinition("salvage_15", rewardDebris = 300_000.0) { it.discoveredDebrisIds.size >= 15 },
        AchievementDefinition("salvage_20", rewardPrestigePoints = 2) { it.discoveredDebrisIds.size >= 20 },
        AchievementDefinition("salvage_28", rewardPrestigePoints = 5) { it.discoveredDebrisIds.size >= 28 },
        AchievementDefinition("click_100", rewardDebris = 5_000.0) { it.lifetimeStats.clicks >= 100 },
        AchievementDefinition("click_1000", rewardDebris = 35_000.0) { it.lifetimeStats.clicks >= 1_000 },
        AchievementDefinition("click_10000", rewardDebris = 250_000.0) { it.lifetimeStats.clicks >= 10_000 },
        AchievementDefinition("click_100000", rewardPrestigePoints = 2) { it.lifetimeStats.clicks >= 25_000 },
        AchievementDefinition("fleet_5", rewardDebris = 25_000.0) { it.fleetCounts.values.sum() >= 5 },
        AchievementDefinition("fleet_12", rewardPrestigePoints = 1) { it.fleetCounts.values.sum() >= 12 },
        AchievementDefinition("fleet_50", rewardPrestigePoints = 2) { it.fleetCounts.values.sum() >= 50 },
        AchievementDefinition("collection_15", rewardDebris = 750_000.0) { it.discoveredDroneIds.size >= 15 },
        AchievementDefinition("collection_29", rewardPrestigePoints = 3) { it.discoveredDroneIds.size >= 29 },
        AchievementDefinition("planets_5", rewardDebris = 100_000.0) { it.ownedPlanets.size >= 5 },
        AchievementDefinition("planets_10", rewardPrestigePoints = 1) { it.ownedPlanets.size >= 10 },
        AchievementDefinition("planets_20", rewardPrestigePoints = 3) { it.ownedPlanets.size >= 20 },
        AchievementDefinition("planets_24", rewardPrestigePoints = 5) { it.ownedPlanets.size >= 24 },
        AchievementDefinition("events_10", rewardDebris = 100_000.0) { it.lifetimeStats.eventsCompleted >= 10 },
        AchievementDefinition("events_50", rewardPrestigePoints = 2) { it.lifetimeStats.eventsCompleted >= 50 },
        AchievementDefinition("cases_25", rewardDebris = 300_000.0) { it.lifetimeStats.casesOpened >= 25 },
        AchievementDefinition("cases_100", rewardPrestigePoints = 2) { it.lifetimeStats.casesOpened >= 100 },
        AchievementDefinition("prestige_1", rewardPrestigePoints = 1) { it.lifetimeStats.prestiges >= 1 },
        AchievementDefinition("prestige_5", rewardPrestigePoints = 4) { it.lifetimeStats.prestiges >= 5 }
        ,AchievementDefinition("click_250k", rewardDebris = 5_000_000.0) { it.lifetimeStats.clicks >= 50_000 }
        ,AchievementDefinition("click_1m", rewardPrestigePoints = 4) { it.lifetimeStats.clicks >= 100_000 }
        ,AchievementDefinition("click_5m", rewardPrestigePoints = 10) { it.lifetimeStats.clicks >= 250_000 }
        ,AchievementDefinition("debris_10m", rewardDebris = 1_000_000.0) { it.lifetimeStats.debrisCollected >= 10_000_000 }
        ,AchievementDefinition("debris_100m", rewardPrestigePoints = 2) { it.lifetimeStats.debrisCollected >= 100_000_000 }
        ,AchievementDefinition("debris_1b", rewardPrestigePoints = 4) { it.lifetimeStats.debrisCollected >= 1_000_000_000 }
        ,AchievementDefinition("debris_10b", rewardPrestigePoints = 8) { it.lifetimeStats.debrisCollected >= 10_000_000_000 }
        ,AchievementDefinition("cases_250", rewardPrestigePoints = 3) { it.lifetimeStats.casesOpened >= 150 }
        ,AchievementDefinition("cases_500", rewardPrestigePoints = 5) { it.lifetimeStats.casesOpened >= 250 }
        ,AchievementDefinition("cases_1000", rewardPrestigePoints = 10) { it.lifetimeStats.casesOpened >= 400 }
        ,AchievementDefinition("events_100", rewardPrestigePoints = 3) { it.lifetimeStats.eventsCompleted >= 75 }
        ,AchievementDefinition("events_250", rewardPrestigePoints = 6) { it.lifetimeStats.eventsCompleted >= 100 }
        ,AchievementDefinition("events_500", rewardPrestigePoints = 10) { it.lifetimeStats.eventsCompleted >= 150 }
        ,AchievementDefinition("prestige_10", rewardPrestigePoints = 5) { it.lifetimeStats.prestiges >= 10 }
        ,AchievementDefinition("prestige_25", rewardPrestigePoints = 10) { it.lifetimeStats.prestiges >= 15 }
        ,AchievementDefinition("prestige_50", rewardPrestigePoints = 20) { it.lifetimeStats.prestiges >= 25 }
        ,AchievementDefinition("fleet_100", rewardPrestigePoints = 3) { it.fleetCounts.values.sum() >= 75 }
        ,AchievementDefinition("fleet_250", rewardPrestigePoints = 6) { it.fleetCounts.values.sum() >= 100 }
        ,AchievementDefinition("fleet_500", rewardPrestigePoints = 10) { it.fleetCounts.values.sum() >= 150 }
        ,AchievementDefinition("parts_25", rewardDebris = 2_000_000.0) { it.droneParts.values.sum() >= 25 }
        ,AchievementDefinition("parts_100", rewardPrestigePoints = 4) { it.droneParts.values.sum() >= 75 }
        ,AchievementDefinition("parts_300", rewardPrestigePoints = 8) { it.droneParts.values.sum() >= 150 }
        ,AchievementDefinition("station_5", rewardDebris = 5_000_000.0) { it.stationLevels.values.sum() >= 5 }
        ,AchievementDefinition("station_10", rewardPrestigePoints = 4) { it.stationLevels.values.sum() >= 10 }
        ,AchievementDefinition("station_20", rewardPrestigePoints = 10) { it.stationLevels.values.sum() >= 20 }
        ,AchievementDefinition("wealth_1t", rewardPrestigePoints = 5) { it.totalDebris >= FIRST_WEALTH_TARGET }
        ,AchievementDefinition("wealth_1q", rewardPrestigePoints = 12) { it.totalDebris >= SECOND_WEALTH_TARGET }
        ,AchievementDefinition("tech_all", rewardPrestigePoints = 5) { it.technologies.size >= Technology.entries.size }
        ,AchievementDefinition("achievements_40", rewardPrestigePoints = 15) { it.claimedAchievementIds.size >= 40 }
    ) + timeMilestones.map { (id, duration, reward) ->
        AchievementDefinition(id, rewardDebris = reward, scaleDebrisReward = false) { it.lifetimeStats.playTimeMillis >= duration }
    } + listOf(
        AchievementDefinition("rank_novice", rewardDebris = 250.0, scaleDebrisReward = false) { it.claimedAchievementIds.size >= 5 },
        AchievementDefinition("rank_professional", rewardDebris = 2_500.0, scaleDebrisReward = false) { it.claimedAchievementIds.size >= 15 },
        AchievementDefinition("rank_veteran", rewardDebris = 15_000.0, scaleDebrisReward = false) { it.claimedAchievementIds.size >= 30 },
        AchievementDefinition("rank_legend", rewardDebris = 75_000.0, scaleDebrisReward = false) { it.claimedAchievementIds.size >= 50 }
    )

    private val byId = definitions.associateBy(AchievementDefinition::id)

    fun evaluate(state: GameState): GameState {
        val newlyUnlocked = definitions.asSequence()
            .filter { it.id !in state.unlockedAchievementIds && it.isUnlocked(state) }
            .map(AchievementDefinition::id)
            .toSet()
        return if (newlyUnlocked.isEmpty()) state
        else state.copy(unlockedAchievementIds = state.unlockedAchievementIds + newlyUnlocked)
    }

    fun claim(state: GameState, achievementId: String): GameState? {
        val achievement = byId[achievementId] ?: return null
        if (achievementId !in state.unlockedAchievementIds ||
            achievementId in state.claimedAchievementIds
        ) return null
        return state.copy(
            totalDebris = state.totalDebris + debrisReward(achievement, state),
            prestigePoints = state.prestigePoints + achievement.rewardPrestigePoints,
            claimedAchievementIds = state.claimedAchievementIds + achievementId
        )
    }

    fun debrisReward(achievement: AchievementDefinition, state: GameState): Double =
        if (achievement.scaleDebrisReward) EconomyBalance.scaledReward(achievement.rewardDebris, state.currentPlanetId)
        else achievement.rewardDebris
}
