package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimedQuestFactoryTest {
    private val factory = TimedQuestFactory { type, target -> "$type:$target" }

    @Test
    fun `daily quests have stable cycle ids and cadence`() {
        val quests = factory.daily(42, "p1")
        assertEquals(6, quests.size)
        assertTrue(quests.all { it.id.startsWith(TimedQuestFactory.DAILY_ID_PREFIX) })
        assertTrue(quests.all { it.id.endsWith("_42") })
        assertTrue(quests.all { it.cadence == QuestCadence.DAILY })
    }

    @Test
    fun `weekly quests preserve descriptions and hard difficulty`() {
        val quests = factory.weekly(7, "p1")
        assertEquals(4, quests.size)
        assertTrue(quests.all { it.id.startsWith(TimedQuestFactory.WEEKLY_ID_PREFIX) })
        assertEquals("CLICK_PLANET:2500", quests.first().description)
        assertTrue(quests.all { it.difficulty == QuestDifficulty.HARD })
    }

    @Test
    fun `debris targets scale with progress but stay achievable`() {
        val early = factory.weekly(7, "p1").last().target
        val late = factory.weekly(7, "p39").last().target

        assertTrue(late > early)
        assertTrue(late <= 30_000.0)
    }

    @Test
    fun `clock rollback cannot regenerate a completed quest cycle`() {
        assertTrue(
            !TimedQuestSchedule.shouldRefresh(
                storedKey = 2_026_245L,
                currentKey = 2_026_244L,
                hasQuests = false,
                completedAt = 1_000L,
                hasLegacyQuests = false
            )
        )
        assertEquals(2_026_245L, TimedQuestSchedule.retainedKey(2_026_245L, 2_026_244L))
    }

    @Test
    fun `quest cycle advances only when calendar key moves forward`() {
        assertTrue(TimedQuestSchedule.shouldRefresh(2_026_245L, 2_026_246L, true, -1L, false))
    }
}
