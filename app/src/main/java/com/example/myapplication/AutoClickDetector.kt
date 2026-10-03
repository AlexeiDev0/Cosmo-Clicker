package com.example.myapplication

import kotlin.math.abs

/** Conservative local heuristic: a repeated point alone never proves automation. */
class AutoClickDetector(initial: Snapshot = Snapshot()) {
    data class Result(val allowed: Boolean, val newlyDetected: Boolean = false, val remainingBlockMillis: Long = 0L)
    data class Snapshot(val blockedUntil: Long = 0L, val strikes: Int = 0, val lastDetectionAt: Long = -1L)
    private data class Click(val time: Long, val x: Float, val y: Float)

    private val clicks = ArrayDeque<Click>()
    private var blockedUntil = initial.blockedUntil.coerceAtLeast(0L)
    private var strikes = initial.strikes.coerceIn(0, 4)
    private var lastDetectionAt = initial.lastDetectionAt
    private var lastInputAt = -1L
    private var seriesStartedAt = -1L
    private var sampleCount = 0
    private var suspiciousWindows = 0

    @Synchronized
    fun registerClick(nowMillis: Long, x: Float = 0f, y: Float = 0f): Result {
        val remaining = remainingBlockMillis(nowMillis)
        if (remaining > 0L) return Result(false, remainingBlockMillis = remaining)
        if (nowMillis < 0L || !x.isFinite() || !y.isFinite() || x !in 0f..1f || y !in 0f..1f) return Result(false)
        // Batched callbacks and duplicate timestamps are dropped, never punished.
        if (nowMillis <= lastInputAt) return Result(false)
        if (lastDetectionAt >= 0L && nowMillis - lastDetectionAt >= STRIKE_DECAY_MILLIS) strikes = 0
        if (lastInputAt < 0L || nowMillis - lastInputAt > SERIES_GAP_MILLIS) clearSeries()
        lastInputAt = nowMillis
        if (seriesStartedAt < 0L) seriesStartedAt = nowMillis
        clicks.addLast(Click(nowMillis, x, y))
        while (clicks.size > MAX_HISTORY || nowMillis - clicks.first().time > ANALYSIS_WINDOW_MILLIS) clicks.removeFirst()
        sampleCount++

        // Detect sustained extreme input before rewarding it; retain a small burst allowance.
        val burst = clicks.count { nowMillis - it.time <= 1_000L }
        if (burst >= 26) return triggerBlock(nowMillis)
        val last24 = clicks.takeLast(24)
        if (last24.size == 24 && (last24.last().time - last24.first().time) / 23.0 <= 40.0) return triggerBlock(nowMillis)
        // No reward above 20 accepted callbacks per second, even with randomised rhythm.
        if (burst > 20) return Result(false)

        if (sampleCount % 12 != 0 || clicks.size < 12) return Result(true)
        val window = clicks.takeLast(12)
        val intervals = window.zipWithNext { a, b -> (b.time - a.time).toDouble() }
        val average = intervals.average()
        val deviation = intervals.sumOf { abs(it - average) } / intervals.size / average
        val buckets = intervals.map { (it / 8.0).toInt() }.distinct().size
        val origin = window.first()
        val stationary = window.count {
            val dx = it.x - origin.x
            val dy = it.y - origin.y
            dx * dx + dy * dy <= .035f * .035f
        } >= 10
        val exactRhythm = average <= 800.0 && deviation <= .04
        val lowJitter = average <= 180.0 && deviation <= .10 && buckets <= 4
        val suspicious = exactRhythm || (stationary && lowJitter)
        suspiciousWindows = if (suspicious) suspiciousWindows + 1 else 0
        if (suspiciousWindows >= 3 && nowMillis - seriesStartedAt >= 4_000L) return triggerBlock(nowMillis)
        return Result(true)
    }

    private fun clearSeries() {
        clicks.clear()
        sampleCount = 0
        suspiciousWindows = 0
        seriesStartedAt = -1L
    }

    private fun triggerBlock(nowMillis: Long): Result {
        strikes = (strikes + 1).coerceAtMost(4)
        val duration = (15_000L * (1L shl (strikes - 1))).coerceAtMost(MAX_BLOCK_MILLIS)
        blockedUntil = nowMillis + duration
        lastDetectionAt = nowMillis
        clearSeries()
        return Result(false, newlyDetected = true, remainingBlockMillis = duration)
    }

    @Synchronized
    fun remainingBlockMillis(nowMillis: Long): Long = (blockedUntil - nowMillis).coerceIn(0L, MAX_BLOCK_MILLIS)

    @Synchronized
    fun snapshot(): Snapshot = Snapshot(blockedUntil, strikes, lastDetectionAt)

    companion object {
        const val MAX_BLOCK_MILLIS = 120_000L
        private const val STRIKE_DECAY_MILLIS = 5 * 60_000L
        private const val ANALYSIS_WINDOW_MILLIS = 30_000L
        private const val SERIES_GAP_MILLIS = 2_000L
        private const val MAX_HISTORY = 128
    }
}
