package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test

class ComboRulesTest {
    @Test fun continuousClicksReachFiftyAndStayCapped() {
        var level = ComboRules.nextLevel(0, 0L, false)
        repeat(80) { level = ComboRules.nextLevel(level, 250L, true) }
        assertEquals(50, level)
    }

    @Test fun pauseOrNewSessionStartsFreshChain() {
        assertEquals(1, ComboRules.nextLevel(40, ComboRules.WINDOW_MILLIS, true))
        assertEquals(1, ComboRules.nextLevel(40, 10L, false))
        assertEquals(1, ComboRules.nextLevel(40, -1L, true))
        assertEquals(20, ComboRules.nextLevel(19, 349L, true))
    }
}
