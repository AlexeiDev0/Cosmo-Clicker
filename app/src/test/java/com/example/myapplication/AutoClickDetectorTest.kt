package com.example.myapplication

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class AutoClickDetectorTest {
    private fun detect(intervals: List<Long>, moving: Boolean = false): AutoClickDetector.Result {
        val detector = AutoClickDetector()
        var now = 0L
        repeat(120) { index ->
            now += intervals[index % intervals.size]
            val result = detector.registerClick(now, if (moving) (index % 9) / 10f else .5f, .5f)
            if (result.newlyDetected) return result
        }
        return AutoClickDetector.Result(true)
    }

    @Test fun sustainedRegularMacroIsBlocked() { assertTrue(detect(listOf(100L)).newlyDetected) }
    @Test fun fourClicksPerSecondMacroIsBlocked() { assertTrue(detect(listOf(250L)).newlyDetected) }
    @Test fun slowerMacroNeedsSeveralWindows() {
        val detector = AutoClickDetector()
        var detectedAt = 0
        repeat(45) { index ->
            if (detector.registerClick(index * 700L, .5f, .5f).newlyDetected) detectedAt = index + 1
        }
        assertTrue(detectedAt >= 36)
    }
    @Test fun lowJitterMovingMacroIsBlocked() {
        assertTrue(detect(listOf(96L, 104L, 100L), moving = true).newlyDetected)
    }
    @Test fun veryFastMovingMacroIsBlocked() {
        assertTrue(detect(listOf(30L), moving = true).newlyDetected)
    }
    @Test fun twentyRegularClicksDoNotCauseCooldown() {
        val detector = AutoClickDetector()
        repeat(20) { assertTrue(detector.registerClick(it * 100L, .5f, .5f).allowed) }
    }
    @Test fun rhythmicHumanWithNormalVariationRemainsAllowed() {
        val detector = AutoClickDetector()
        var now = 0L
        val intervals = listOf(230L, 267L, 244L, 281L, 219L, 258L, 238L, 273L)
        repeat(240) {
            now += intervals[it % intervals.size]
            assertTrue(detector.registerClick(now, .5f, .5f).allowed)
        }
    }
    @Test fun variedHumanPatternsRemainAllowedAtSamePoint() {
        val random = Random(42)
        repeat(20) {
            val detector = AutoClickDetector()
            var now = 0L
            repeat(300) {
                now += random.nextLong(75L, 400L)
                assertTrue(detector.registerClick(now, .5f, .5f).allowed)
            }
        }
    }
    @Test fun fastIrregularHumanBurstRemainsAllowed() {
        val detector = AutoClickDetector()
        var now = 0L
        val intervals = listOf(48L, 92L, 57L, 135L, 66L, 111L, 74L, 149L)
        repeat(20) {
            now += intervals[it % intervals.size]
            assertTrue(detector.registerClick(now, (it % 4) * .16f, (it % 5) * .13f).allowed)
        }
    }
    @Test fun staleAndSameMillisecondCallbacksAreDroppedWithoutPunishment() {
        val detector = AutoClickDetector()
        assertTrue(detector.registerClick(1_000L).allowed)
        assertFalse(detector.registerClick(1_000L).allowed)
        assertFalse(detector.registerClick(999L).allowed)
        assertEquals(0L, detector.remainingBlockMillis(1_000L))
        assertTrue(detector.registerClick(1_100L).allowed)
    }
    @Test fun invalidCoordinatesCannotPoisonHistory() {
        val detector = AutoClickDetector()
        assertFalse(detector.registerClick(100L, Float.NaN, .5f).allowed)
        assertFalse(detector.registerClick(200L, .5f, Float.POSITIVE_INFINITY).allowed)
        assertTrue(detector.registerClick(300L, .5f, .5f).allowed)
        assertEquals(0L, detector.remainingBlockMillis(300L))
    }
    @Test fun idleBreakClearsSuspiciousEvidence() {
        val detector = AutoClickDetector()
        var now = 0L
        repeat(10) {
            repeat(20) { now += 100L; assertTrue(detector.registerClick(now).allowed) }
            now += 3_000L
        }
    }
    @Test fun cooldownExpiresAndBlockedCallbacksDoNotExtendIt() {
        val detector = AutoClickDetector()
        var now = 0L
        var blocked = AutoClickDetector.Result(true)
        while (!blocked.newlyDetected) { now += 100L; blocked = detector.registerClick(now) }
        val until = now + blocked.remainingBlockMillis
        assertFalse(detector.registerClick(now + 1L).allowed)
        assertEquals(1L, detector.remainingBlockMillis(until - 1L))
        assertTrue(detector.registerClick(until + 1L).allowed)
    }
    @Test fun cooldownAndEscalationSurviveDetectorRecreation() {
        val detector = AutoClickDetector()
        var now = 0L
        var result = AutoClickDetector.Result(true)
        while (!result.newlyDetected) { now += 100L; result = detector.registerClick(now) }
        val restored = AutoClickDetector(detector.snapshot())
        assertFalse(restored.registerClick(now + 1L).allowed)
        now += result.remainingBlockMillis + 1L
        result = AutoClickDetector.Result(true)
        while (!result.newlyDetected) { now += 100L; result = restored.registerClick(now) }
        assertEquals(30_000L, result.remainingBlockMillis)
    }
    @Test fun fiveQuietMinutesResetEscalation() {
        val detector = AutoClickDetector()
        var now = 0L
        var result = AutoClickDetector.Result(true)
        while (!result.newlyDetected) { now += 100L; result = detector.registerClick(now) }
        now += 301_000L
        result = AutoClickDetector.Result(true)
        while (!result.newlyDetected) { now += 100L; result = detector.registerClick(now) }
        assertEquals(15_000L, result.remainingBlockMillis)
    }
    @Test fun randomisedFloodCannotEarnMoreThanTwentyTapsInASecond() {
        val detector = AutoClickDetector()
        var now = 0L
        var accepted = 0
        repeat(24) {
            now += if (it % 2 == 0) 1L else 79L
            if (detector.registerClick(now, (it % 8) / 10f, .5f).allowed) accepted++
        }
        assertTrue(accepted <= 20)
    }
    @Test fun repeatedDetectionsNeverExceedTwoMinutes() {
        val detector = AutoClickDetector()
        var now = 0L
        repeat(6) { cycle ->
            var result = AutoClickDetector.Result(true)
            while (!result.newlyDetected) { now += 100L; result = detector.registerClick(now) }
            assertTrue(result.remainingBlockMillis <= 120_000L)
            if (cycle >= 3) assertEquals(120_000L, result.remainingBlockMillis)
            now += result.remainingBlockMillis + 1L
        }
    }
}
