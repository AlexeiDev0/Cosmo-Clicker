package com.example.myapplication.ui.theme

import androidx.compose.ui.unit.dp

/** Shared visual rhythm for game surfaces and controls. */
object SpaceDesign {
    // Sheet, card, and control are the only standard shape levels.
    val SheetRadius = 24.dp
    val CardRadius = 16.dp
    val ControlRadius = 12.dp

    val Space2 = 2.dp
    val Space4 = 4.dp
    val Space8 = 8.dp
    val Space12 = 12.dp
    val Space16 = 16.dp
    val Space24 = 24.dp
    val Space32 = 32.dp

    val SheetPadding = 18.dp
    val CardPadding = 13.dp
    val MinTouchSize = 48.dp
    val IconSmall = 20.dp
    val IconMedium = 24.dp
    val IconLarge = 32.dp

    val Hairline = 1.dp
    val SelectedOutline = 2.dp
    val ShortShadow = 8.dp
    val FocusGlow = 14.dp

    const val StrongSurfaceAlpha = .88f
    const val MutedSurfaceAlpha = .045f
    const val TrackAlpha = .09f
    const val DisabledAlpha = .52f
    const val LockedAlpha = .48f
    const val CompletedAlpha = .72f
}
