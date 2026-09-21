package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EventStateCodecTest {
    @Test
    fun activeMultiStageEventRoundTripsWithProgress() {
        val state = GameState(
            activeEvent = GameEvent(GameEventType.CYBER_VIRUS, 9_000L, .2f, .7f, 1_000L, 450.0, true),
            eventMultiplier = 2.5,
            eventTapsLeft = 4,
            infectedDroneId = 42L,
            stormSequence = listOf(3, 1, 2),
            stormProgress = 2,
            stormRound = 3
        )

        val restored = EventStateCodec.restoreActive(GameState(), EventStateCodec.encodeActive(state))

        assertEquals(state.activeEvent, restored.activeEvent)
        assertEquals(state.eventMultiplier, restored.eventMultiplier, 0.0)
        assertEquals(state.eventTapsLeft, restored.eventTapsLeft)
        assertEquals(state.infectedDroneId, restored.infectedDroneId)
        assertEquals(state.stormSequence, restored.stormSequence)
        assertEquals(state.stormProgress, restored.stormProgress)
        assertEquals(state.stormRound, restored.stormRound)
    }

    @Test
    fun pendingEventChainRoundTripsAndCorruptDataIsIgnored() {
        val pending = PendingEventChain(8_000L, false, 300.0, GameEventType.ABANDONED_STATION, 75.0)

        assertEquals(pending, EventStateCodec.decodePending(EventStateCodec.encodePending(pending)))
        assertNull(EventStateCodec.decodePending("broken"))
    }
}
