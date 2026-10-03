package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

/** Semantic palette for the shared stylized-cosmic visual language. */
object AppColors {
    val Primary = Color(0xFFAFE3CD)
    val Secondary = Color(0xFFA7DDF3)
    val Danger = Color(0xFFF3A1AC)
    val Warning = Color(0xFFF4D59D)

    val SpaceBlack = Color(0xFF030817)
    val BackgroundStart = Color(0xFF18233E)
    val BackgroundMid = Color(0xFF293653)
    val BackgroundEnd = Color(0xFF121B32)

    val CardBackground = Color(0xFF1C2942)
    val Surface = Color(0xFF263650)
    val SurfaceRaised = Color(0xFF344661)
    val Outline = Color(0xFF5C708C)
    val OutlineActive = Primary
    val TextMuted = Color(0xFFCBD7E6)
    val TextDisabled = Color(0xFF7F90AC)
    val CargoColor = Color(0xFFE7B679)

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
