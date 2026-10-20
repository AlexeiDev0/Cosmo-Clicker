package com.example.myapplication.ui.components

import com.example.myapplication.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DebrisResourceMappingTest {
    @Test
    fun allFortyEightDebrisIndicesHaveUniqueResources() {
        val resources = (1..48).map(::debrisDrawable)

        assertEquals(48, resources.toSet().size)
        resources.forEach { assertNotEquals(0, it) }
    }

    @Test
    fun invalidIndexUsesFirstDebrisAsSafeFallback() {
        assertEquals(R.drawable.minimal_debris_01_v7, debrisDrawable(0))
        assertEquals(R.drawable.minimal_debris_01_v7, debrisDrawable(49))
    }
}
