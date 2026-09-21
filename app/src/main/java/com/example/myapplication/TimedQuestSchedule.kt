package com.example.myapplication

import java.util.Calendar

object TimedQuestSchedule {
    fun dayKey(now: Long): Long = Calendar.getInstance().run {
        timeInMillis = now
        get(Calendar.YEAR) * 1_000L + get(Calendar.DAY_OF_YEAR)
    }

    fun weekKey(now: Long): Long = Calendar.getInstance().run {
        timeInMillis = now
        getWeekYear() * 100L + get(Calendar.WEEK_OF_YEAR)
    }

    fun shouldRefresh(
        storedKey: Long,
        currentKey: Long,
        hasQuests: Boolean,
        completedAt: Long,
        hasLegacyQuests: Boolean
    ): Boolean = storedKey < 0L || currentKey > storedKey ||
        (!hasQuests && completedAt < 0L) || hasLegacyQuests

    fun retainedKey(storedKey: Long, currentKey: Long): Long = maxOf(storedKey, currentKey)
}
