package com.example.myapplication

object QuestEngine {
    fun collectTypes(quests: List<Quest>, ids: Set<Int>): List<Quest> = quests.map { quest ->
        if (quest.type != QuestType.COLLECT_DEBRIS_TYPES || quest.isCompleted || quest.isClaimed) quest
        else {
            val collected = quest.collectedDebrisIds + ids.filter { it in 1..28 }
            val progress = collected.size.toDouble().coerceAtMost(quest.target)
            quest.copy(collectedDebrisIds = collected, progress = progress, isCompleted = progress >= quest.target)
        }
    }

    fun claim(state: GameState, questId: String, fleet: List<FleetConfig>, now: Long): GameState? {
        val quest = state.activeQuests.find { it.id == questId } ?: return null
        if (!quest.isCompleted || quest.isClaimed) return null
        // Keep the reward claimable until the current opening has been collected.
        if (quest.rewardCases > 0 && (state.isOpeningCase || state.lastDroppedDroneId != null ||
                    state.pendingCaseOpenings > 0 || state.showCaseBundleSummary)) return null
        val drone = quest.rewardDroneId?.let { id -> fleet.find { it.id == id } }
        if (quest.rewardDroneId != null && drone == null) return null
        var remaining = state.activeQuests.filterNot { it.id == questId }
        var counts = state.fleetCounts
        var discoveries = state.discoveredDroneIds
        var debris = state.totalDebris + EconomyBalance.questReward(state, quest)
        if (drone != null) {
            if (counts.values.sumOf { it.toLong() } < EconomyBalance.MAX_DRONES) {
                counts = counts + (drone.id to ((counts[drone.id] ?: 0) + 1))
                discoveries = discoveries + drone.id
                remaining = advance(advance(remaining, QuestType.OBTAIN_DRONE, droneId = drone.id),
                    QuestType.OBTAIN_RARE_DRONE, droneRarity = drone.rarity)
            } else debris += 50_000.0
        }
        return state.copy(
            totalDebris = debris,
            prestigePoints = state.prestigePoints + quest.rewardPrestigePoints,
            fleetCounts = counts,
            discoveredDroneIds = discoveries,
            activeQuests = remaining,
            completedQuestIds = state.completedQuestIds + questId,
            dailyQuestsCompletedAt = if (quest.cadence == QuestCadence.DAILY &&
                remaining.none { it.cadence == QuestCadence.DAILY } && state.dailyQuestsCompletedAt < 0) now else state.dailyQuestsCompletedAt,
            weeklyQuestsCompletedAt = if (quest.cadence == QuestCadence.WEEKLY &&
                remaining.none { it.cadence == QuestCadence.WEEKLY } && state.weeklyQuestsCompletedAt < 0) now else state.weeklyQuestsCompletedAt,
            isOpeningCase = quest.rewardCases > 0 || state.isOpeningCase,
            openingCaseType = if (quest.rewardCases > 0) CaseType.COMMON else state.openingCaseType,
            isRewardCaseOpening = if (quest.rewardCases > 0) true else state.isRewardCaseOpening,
            pendingCaseOpenings = if (quest.rewardCases > 0) quest.rewardCases - 1 else state.pendingCaseOpenings,
            caseBundleRewards = if (quest.rewardCases > 0) emptyMap() else state.caseBundleRewards
        )
    }

    /** Clears run-specific quest state. Timed quests are recreated on the next refresh. */
    fun reset(state: GameState): GameState = state.copy(
        activeQuests = emptyList(),
        completedQuestIds = emptySet(),
        questBonuses = emptyMap(),
        dailyQuestDay = -1L,
        weeklyQuestWeek = -1L,
        dailyQuestsCompletedAt = -1L,
        weeklyQuestsCompletedAt = -1L
    )

    fun advance(
        quests: List<Quest>,
        type: QuestType,
        amount: Double = 1.0,
        droneId: String? = null,
        droneRarity: Rarity? = null
    ): List<Quest> =
        quests.map { quest ->
            // Unique types can only advance with actual item identities through collectTypes.
            if (type == QuestType.COLLECT_DEBRIS_TYPES) return@map quest
            if (quest.type != type || quest.isCompleted || quest.isClaimed) return@map quest
            if (type == QuestType.OBTAIN_DRONE && quest.targetDroneId != droneId) return@map quest
            if (type == QuestType.OBTAIN_RARE_DRONE && (droneRarity == null || droneRarity.ordinal < Rarity.RARE.ordinal)) return@map quest
            val progress = (quest.progress + amount).coerceAtMost(quest.target)
            quest.copy(progress = progress, isCompleted = progress >= quest.target)
        }
}
