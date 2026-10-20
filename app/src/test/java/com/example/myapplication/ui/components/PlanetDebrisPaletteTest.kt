package com.example.myapplication.ui.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

class PlanetDebrisPaletteTest {
    @Test fun earthPreservesOriginalColorsAndUnknownPlanetFallsBack() {
        assertEquals(Color.White, planetDebrisColor("p1"))
        assertNull(planetDebrisFilter("p1"))
        assertNull(planetDebrisFilter("unknown"))
    }

    @Test fun everyDestinationHasADistinctPalette() {
        val colors = (1..39).map { planetDebrisColor("p$it") }
        assertEquals(39, colors.toSet().size)
    }

    @Test fun magmaIsWarmAndIceIsCool() {
        val magma = planetDebrisColor("p9")
        assertTrue(magma.red > magma.green && magma.green > magma.blue)
        val ice = planetDebrisColor("p28")
        assertTrue(ice.blue > ice.red)
    }
}
