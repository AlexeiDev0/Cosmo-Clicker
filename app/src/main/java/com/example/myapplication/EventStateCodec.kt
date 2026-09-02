package com.example.myapplication

object EventStateCodec {
    fun encodeActive(state: GameState): String? {
        val event = state.activeEvent ?: return null
        return listOf(
            event.type.name,
            event.expiresAt,
            event.x,
            event.y,
            event.startedAt,
            event.reward,
            event.isElite,
            state.eventMultiplier,
            state.eventTapsLeft,
            state.infectedDroneId ?: "",
            state.stormSequence.joinToString(","),
            state.stormProgress,
            state.stormRound
        ).joinToString("|")
    }

    fun restoreActive(state: GameState, encoded: String?): GameState = runCatching {
        val values = encoded?.split('|') ?: return state
        require(values.size == 13)
        val event = GameEvent(
            type = GameEventType.valueOf(values[0]),
            expiresAt = values[1].toLong(),
            x = values[2].toFloat().also { require(it.isFinite()) },
            y = values[3].toFloat().also { require(it.isFinite()) },
            startedAt = values[4].toLong(),
            reward = values[5].toDouble().also { require(it.isFinite()) },
            isElite = values[6].toBooleanStrict()
        )
        state.copy(
            activeEvent = event,
            eventMultiplier = values[7].toDouble().also { require(it.isFinite()) },
            eventTapsLeft = values[8].toInt().coerceAtLeast(0),
            infectedDroneId = values[9].takeIf(String::isNotEmpty)?.toLong(),
            stormSequence = values[10].takeIf(String::isNotEmpty)
                ?.split(',')?.map(String::toInt).orEmpty(),
            stormProgress = values[11].toInt().coerceAtLeast(0),
            stormRound = values[12].toInt().coerceAtLeast(0)
        )
    }.getOrDefault(state)

    fun encodePending(pending: PendingEventChain?): String? = pending?.let {
        listOf(it.resolvesAt, it.success, it.reward, it.eventType.name, it.failurePenalty)
            .joinToString("|")
    }

    fun decodePending(encoded: String?): PendingEventChain? = runCatching {
        val values = encoded?.split('|') ?: return null
        require(values.size == 5)
        PendingEventChain(
            resolvesAt = values[0].toLong(),
            success = values[1].toBooleanStrict(),
            reward = values[2].toDouble().also { require(it.isFinite()) },
            eventType = GameEventType.valueOf(values[3]),
            failurePenalty = values[4].toDouble().also { require(it.isFinite()) }
        )
    }.getOrNull()
}
