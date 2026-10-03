package com.example.myapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.myapplication.R
import kotlin.math.roundToInt

private data class Region(val sheet: Int, val left: Float, val top: Float, val width: Float, val height: Float)

/** Displays atlas regions directly; original generated PNGs and alpha are preserved. */
@Composable
private fun regionPainter(region: Region): Painter {
    val image = ImageBitmap.imageResource(region.sheet)
    return remember(image, region) {
        val offset = IntOffset((image.width * region.left).roundToInt(), (image.height * region.top).roundToInt())
        val sourceSize = IntSize((image.width * region.width).roundToInt(), (image.height * region.height).roundToInt())
        object : Painter() {
            override val intrinsicSize = Size(sourceSize.width.toFloat(), sourceSize.height.toFloat())
            override fun DrawScope.onDraw() {
                drawImage(image, srcOffset = offset, srcSize = sourceSize,
                    dstSize = IntSize(size.width.roundToInt().coerceAtLeast(1), size.height.roundToInt().coerceAtLeast(1)))
            }
        }
    }
}

@Composable
internal fun shipArtworkPainter(id: Int): Painter? {
    val action = when (id) {
        R.drawable.ui_close_simple, R.drawable.flat_icon_close -> 0
        R.drawable.ui_lock_simple, R.drawable.flat_icon_lock -> 1
        R.drawable.ui_reset_simple, R.drawable.flat_icon_reset -> 11
        R.drawable.ui_sound_simple, R.drawable.icon_settings_sound_v2, R.drawable.flat_icon_sound -> 12
        R.drawable.flat_icon_repair -> 14
        else -> null
    }
    if (action != null) return shipActionPainter(action)
    val icon = when (id) {
        R.drawable.ic_nav_shop_minimal, R.drawable.flat_nav_shop -> 4
        R.drawable.ic_nav_hangar_minimal, R.drawable.flat_nav_hangar -> 1
        R.drawable.ic_nav_quests_minimal, R.drawable.flat_nav_quests -> 5
        R.drawable.ic_nav_settings_minimal, R.drawable.flat_nav_settings -> 8
        R.drawable.ic_nav_prestige_minimal, R.drawable.flat_nav_prestige -> 7
        R.drawable.ic_nav_stats_minimal, R.drawable.flat_nav_stats, R.drawable.flat_icon_stats -> 9
        R.drawable.flat_nav_route, R.drawable.flat_icon_route -> 0
        R.drawable.ic_achievement_medal, R.drawable.flat_nav_achievements -> 6
        R.drawable.ic_currency_debris_v2, R.drawable.ic_debris_minimal -> 3
        else -> return null
    }
    return regionPainter(Region(R.drawable.icons_atlas, (icon % 4) / 4f, (icon / 4) / 3f, .25f, 1f / 3f))
}

@Composable
fun shipActionPainter(index: Int): Painter {
    val cell = index.coerceIn(0, 15)
    return regionPainter(Region(R.drawable.ship_action_buttons_v1,
        .035f + (cell % 4) * .232f, listOf(.075f, .295f, .505f, .720f)[cell / 4], .225f, .215f))
}

@Composable
internal fun shipButtonPainter(style: CosmicButtonStyle, pressed: Boolean): Painter {
    val row = when (style) {
        CosmicButtonStyle.Primary, CosmicButtonStyle.Secondary -> 0
        CosmicButtonStyle.Reward -> 2
        CosmicButtonStyle.Danger -> 3
    }
    val top = listOf(.145f, .329f, .514f, .701f)[row]
    return regionPainter(Region(R.drawable.ship_button_panels_v1,
        if (pressed) .505f else .015f, top, .478f, .137f))
}
