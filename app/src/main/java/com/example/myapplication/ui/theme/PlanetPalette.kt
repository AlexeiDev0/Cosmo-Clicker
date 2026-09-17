package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

/** Generated from the actual painted sprites by Build-PaintedPlanetReview.ps1. */
data class PlanetColors(val primary: Color, val secondary: Color)

object PlanetPalette {
    private val colors = listOf(
        PlanetColors(Color(0xFFC3D592), Color(0xFFA2C493)),
        PlanetColors(Color(0xFFDAE9D5), Color(0xFFD1EDDA)),
        PlanetColors(Color(0xFF9686D7), Color(0xFF7F85D1)),
        PlanetColors(Color(0xFFCDE8D6), Color(0xFFAFE3DD)),
        PlanetColors(Color(0xFFA7E1FD), Color(0xFF7F8CC6)),
        PlanetColors(Color(0xFFCAF8F7), Color(0xFF81C7EE)),
        PlanetColors(Color(0xFFCDDBA2), Color(0xFFABCC9C)),
        PlanetColors(Color(0xFFA0C690), Color(0xFFCADC93)),
        PlanetColors(Color(0xFFFDFDAF), Color(0xFFA5A7E0)),
        PlanetColors(Color(0xFF97A3EB), Color(0xFFFCDCAF)),
        PlanetColors(Color(0xFFD6DFBB), Color(0xFFC7DEBE)),
        PlanetColors(Color(0xFFE9DAD2), Color(0xFF97ABD8)),
        PlanetColors(Color(0xFFC9DA99), Color(0xFFA1C491)),
        PlanetColors(Color(0xFFD6E8D0), Color(0xFFC4E4CE)),
        PlanetColors(Color(0xFF78C0EA), Color(0xFF96B0DD)),
        PlanetColors(Color(0xFFA2CD8E), Color(0xFF8AC59A)),
        PlanetColors(Color(0xFF998CCC), Color(0xFF848DD2)),
        PlanetColors(Color(0xFFE0E3B7), Color(0xFFF0E1B0)),
        PlanetColors(Color(0xFFE2E6D1), Color(0xFFE0EBDC)),
        PlanetColors(Color(0xFFB1CCDF), Color(0xFF8F9CC8)),
        PlanetColors(Color(0xFFCEDDC5), Color(0xFFB1DBD8)),
        PlanetColors(Color(0xFFC9CFA4), Color(0xFFD1E2C7)),
        PlanetColors(Color(0xFFD0EDD9), Color(0xFFBAE8E3)),
        PlanetColors(Color(0xFFC5E2E1), Color(0xFF89C1E5)),
        PlanetColors(Color(0xFFFDFEC8), Color(0xFFB2DFFE)),
        PlanetColors(Color(0xFFCDDD9B), Color(0xFFA3C894)),
        PlanetColors(Color(0xFFC8D8BC), Color(0xFFD1D8B7)),
        PlanetColors(Color(0xFF82A0CE), Color(0xFFD4EBE9)),
        PlanetColors(Color(0xFFBAD5AD), Color(0xFFB0D6BC)),
        PlanetColors(Color(0xFFEDE5D7), Color(0xFF8895BE)),
        PlanetColors(Color(0xFF9DCEED), Color(0xFF8A9DE0)),
        PlanetColors(Color(0xFFC6D599), Color(0xFFA7C597)),
        PlanetColors(Color(0xFFDBEBD5), Color(0xFFCFE8D8)),
        PlanetColors(Color(0xFF88C5E3), Color(0xFF7B91D1)),
        PlanetColors(Color(0xFFBFD182), Color(0xFF93B784)),
        PlanetColors(Color(0xFFBED8D4), Color(0xFFACD6EC)),
        PlanetColors(Color(0xFFFDFEAA), Color(0xFF9699F4)),
        PlanetColors(Color(0xFFD6D9BB), Color(0xFFE9E4CC)),
        PlanetColors(Color(0xFFB6D0A5), Color(0xFFC4F4D8)),
    )

    fun forPlanet(id: String): PlanetColors {
        val index = id.removePrefix("p").toIntOrNull()?.minus(1) ?: 0
        return colors[index.coerceIn(0, colors.lastIndex)]
    }
}
