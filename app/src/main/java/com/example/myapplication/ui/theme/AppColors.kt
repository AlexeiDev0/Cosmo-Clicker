package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

/** Semantic palette for the shared stylized-cosmic visual language. */
object AppColors {
    val Primary = Color(0xFF55E6BC)
    val Secondary = Color(0xFF72E4FF)
    val Danger = Color(0xFFFF6B74)
    val Warning = Color(0xFFFFCA62)

    val SpaceBlack = Color(0xFF030817)
    val BackgroundStart = Color(0xFF071A35)
    val BackgroundMid = Color(0xFF0A2A4D)
    val BackgroundEnd = Color(0xFF051126)

    val CardBackground = Color(0xFF0C1422)
    val Surface = Color(0xFF111C2C)
    val SurfaceRaised = Color(0xFF172334)
    val Outline = Color(0xFF293748)
    val OutlineActive = Primary
    val TextMuted = Color(0xFF91A0B2)
    val TextDisabled = Color(0xFF667487)
    val CargoColor = Color(0xFFB7773E)

    val RarityCommon = Color(0xFFB8C5D1)
    val RarityRare = Color(0xFF55D9E8)
    val RarityLegendary = Color(0xFFB49CFF)
    val RarityLegendaryWarm = Warning

    val Locked = Color(0xFF596779)
    val Disabled = Color(0xFF778394)
    val Completed = Color(0xFF69CFAE)
    val Reward = Warning

    val EventSolar = Color(0xFFFF9A52)
    val EventAnomaly = RarityLegendary
    val EventSignal = Secondary
    val EventHostile = Danger

    val WhiteAlpha20 = Color.White.copy(alpha = 0.2f)
    val WhiteAlpha10 = Color.White.copy(alpha = 0.1f)
    val WhiteAlpha05 = Color.White.copy(alpha = 0.05f)
}
