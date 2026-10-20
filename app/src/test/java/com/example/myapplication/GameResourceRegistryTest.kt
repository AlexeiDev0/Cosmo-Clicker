package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GameResourceRegistryTest {
    @Test
    fun everyDroneHasAnExplicitUniqueResource() {
        val resources = (1..GameResourceRegistry.DRONE_COUNT).map(GameResourceRegistry::drone)
        resources.forEach { assertNotEquals(R.drawable.upgrade_magnet, it) }
        assertEquals(GameResourceRegistry.DRONE_COUNT, resources.toSet().size)
    }

    @Test
    fun invalidDroneAndCaseNumbersUseSafeFallbacks() {
        assertEquals(R.drawable.upgrade_magnet, GameResourceRegistry.drone(0))
        assertEquals(R.drawable.upgrade_magnet, GameResourceRegistry.drone(GameResourceRegistry.DRONE_COUNT + 1))
        assertEquals(R.drawable.case_08, GameResourceRegistry.caseFrame(0))
        assertEquals(R.drawable.case_08, GameResourceRegistry.caseFrame(9))
    }
}
