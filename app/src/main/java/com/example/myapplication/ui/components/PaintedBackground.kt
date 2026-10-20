package com.example.myapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

/** Flat scene artwork keeps decorative geometry outside the central interface area. */
@Composable
internal fun minimalBackgroundPainter(resource: Int): Painter? {
    val scene = when (resource) {
        R.drawable.flat_bg_main -> R.drawable.bg_main_cohesive_v8
        R.drawable.flat_bg_start -> R.drawable.minimal_bg_start_v7
        R.drawable.flat_bg_shop -> R.drawable.minimal_bg_shop_v7
        R.drawable.flat_bg_hangar -> R.drawable.minimal_bg_hangar_v7
        R.drawable.flat_bg_goals -> R.drawable.minimal_bg_goals_v7
        R.drawable.flat_bg_achievements -> R.drawable.minimal_bg_achievements_v7
        R.drawable.flat_bg_statistics -> R.drawable.minimal_bg_statistics_v7
        R.drawable.flat_bg_cases -> R.drawable.minimal_bg_cases_v7
        R.drawable.flat_bg_events -> R.drawable.minimal_bg_events_v7
        R.drawable.flat_bg_prestige -> R.drawable.minimal_bg_prestige_v7
        R.drawable.flat_bg_offline -> R.drawable.minimal_bg_offline_v7
        R.drawable.flat_bg_settings -> R.drawable.minimal_bg_settings_v7
        else -> return null
    }
    return painterResource(scene)
}
