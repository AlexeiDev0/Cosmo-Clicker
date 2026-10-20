package com.example.myapplication.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

/** Same salvage models; each destination changes their material colors. */
internal fun planetDebrisColor(planetId: String): Color = when (planetId) {
    "p2" -> Color(0xFFE7AD78)
    "p3" -> Color(0xFFB49FE8)
    "p4" -> Color(0xFF82DCEB)
    "p5" -> Color(0xFFE6CD92)
    "p6" -> Color(0xFF8CB1FF)
    "p7" -> Color(0xFFD9BAA1)
    "p8" -> Color(0xFF80DCA1)
    "p9" -> Color(0xFFFF713B)
    "p10" -> Color(0xFFDF8C79)
    "p11" -> Color(0xFFA5B5CA)
    "p12" -> Color(0xFFD6DEF0)
    "p13" -> Color(0xFF569DE4)
    "p14" -> Color(0xFF9FE1B7)
    "p15" -> Color(0xFFB1DCFF)
    "p16" -> Color(0xFFB2DD65)
    "p17" -> Color(0xFFE8A0CC)
    "p18" -> Color(0xFFD3DCEC)
    "p19" -> Color(0xFFB9A895)
    "p20" -> Color(0xFF9395BE)
    "p21" -> Color(0xFF9D84DC)
    "p22" -> Color(0xFF6EDFC8)
    "p23" -> Color(0xFFD3AFE7)
    "p24" -> Color(0xFF9A91D5)
    "p25" -> Color(0xFFF79853)
    "p26" -> Color(0xFF67CB8C)
    "p27" -> Color(0xFF89A6C5)
    "p28" -> Color(0xFFA5E4F5)
    "p29" -> Color(0xFFD8A8DF)
    "p30" -> Color(0xFF938CAA)
    "p31" -> Color(0xFFCFBA78)
    "p32" -> Color(0xFF4FBFCB)
    "p33" -> Color(0xFFF5D071)
    "p34" -> Color(0xFFEA8167)
    "p35" -> Color(0xFFB6CE83)
    "p36" -> Color(0xFFA7ABD9)
    "p37" -> Color(0xFFFF8750)
    "p38" -> Color(0xFF86A2BE)
    "p39" -> Color(0xFFE7D5AD)
    else -> Color.White
}

internal fun planetDebrisFilter(planetId: String): ColorFilter? {
    val color = planetDebrisColor(planetId)
    if (color == Color.White) return null
    // Recolor luminance rather than overlaying a flat tint; alpha and shading survive.
    return ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
        .299f * color.red, .587f * color.red, .114f * color.red, 0f, 0f,
        .299f * color.green, .587f * color.green, .114f * color.green, 0f, 0f,
        .299f * color.blue, .587f * color.blue, .114f * color.blue, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )))
}
