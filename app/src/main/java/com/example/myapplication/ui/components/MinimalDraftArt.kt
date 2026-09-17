package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import com.example.myapplication.R
import androidx.compose.ui.layout.ContentScale

/** Replaces the UI atlas cells with individually decoded, scalable semantic artwork. */
internal fun generatedUiIcon(drawable: Int, index: Int): Int? {
    val icons = when (drawable) {
        R.drawable.shop_ui_minimal_sheet_v1 -> listOf(
            R.drawable.flat_icon_repair, R.drawable.flat_icon_case,
            R.drawable.flat_icon_planet, R.drawable.flat_icon_settings,
            R.drawable.flat_icon_debris, R.drawable.flat_icon_lock,
            R.drawable.flat_icon_case, R.drawable.flat_icon_prestige,
            R.drawable.flat_icon_ai, R.drawable.flat_icon_fleet,
            R.drawable.flat_icon_speed, R.drawable.flat_icon_magnet,
            R.drawable.flat_icon_launch, R.drawable.flat_icon_recall,
            R.drawable.flat_icon_sell, R.drawable.flat_icon_close
        )
        R.drawable.shop_upgrades_minimal_sheet_v1 -> listOf(
            R.drawable.flat_icon_magnet, R.drawable.flat_icon_torch,
            R.drawable.flat_icon_repair, R.drawable.flat_icon_harvester,
            R.drawable.flat_icon_beacon, R.drawable.flat_icon_amplifier,
            R.drawable.flat_icon_ai, R.drawable.flat_icon_compressor,
            R.drawable.flat_icon_singularity
        )
        R.drawable.shop_upgrades_expansion_sheet_v1 -> listOf(
            R.drawable.flat_icon_lens, R.drawable.flat_icon_pulsar,
            R.drawable.flat_icon_press, R.drawable.flat_icon_nanites,
            R.drawable.flat_icon_forge, R.drawable.flat_icon_relay,
            R.drawable.flat_icon_resonator, R.drawable.flat_icon_entropy,
            R.drawable.flat_icon_anchor, R.drawable.flat_icon_omega
        )
        R.drawable.ui_meteor_repair_sheet_v1 -> listOf(
            R.drawable.flat_event_meteor, R.drawable.flat_icon_repair,
            R.drawable.flat_icon_warning, R.drawable.flat_icon_core,
            R.drawable.flat_icon_energy, R.drawable.flat_icon_launch
        )
        else -> return null
    }
    return icons[index.coerceIn(0, icons.lastIndex)]
}

/** Restrained icon treatment for shop controls; game artwork remains untouched. */
@Composable
fun MinimalShopIcon(seed: Int, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = size.minDimension
        drawRoundRect(color.copy(.10f), cornerRadius = CornerRadius(unit * .22f))
        drawRoundRect(color.copy(.65f), style = Stroke(unit * .045f), cornerRadius = CornerRadius(unit * .22f))
        when (abs(seed) % 3) {
            0 -> {
                drawCircle(color, unit * .18f, center)
                drawCircle(Color(0xFF09121E), unit * .075f, center)
            }
            1 -> {
                drawLine(color, Offset(unit * .25f, unit * .68f), Offset(unit * .5f, unit * .25f), unit * .1f, StrokeCap.Round)
                drawLine(color, Offset(unit * .5f, unit * .25f), Offset(unit * .75f, unit * .68f), unit * .1f, StrokeCap.Round)
            }
            else -> {
                drawRect(color, Offset(unit * .28f, unit * .3f), Size(unit * .44f, unit * .4f))
                drawLine(Color(0xFF09121E), Offset(unit * .38f, unit * .5f), Offset(unit * .62f, unit * .5f), unit * .055f)
            }
        }
    }
}

@Composable
fun GeneratedSheetIcon(
    drawable: Int,
    index: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    rows: Int = 3
) {
    val vectorIcon = generatedUiIcon(drawable, index)
    if (vectorIcon != null) {
        Image(
            painter = cosmicIconPainter(vectorIcon),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
        return
    }
    val resources = LocalResources.current
    val bitmap = remember(drawable) { ImageBitmap.imageResource(resources, drawable) }
    Canvas(modifier.size(size)) {
        val safeIndex = index.coerceIn(0, columns * rows - 1)
        val cellWidth = bitmap.width / columns
        val cellHeight = bitmap.height / rows
        drawImage(
            image = bitmap,
            srcOffset = IntOffset((safeIndex % columns) * cellWidth, (safeIndex / columns) * cellHeight),
            srcSize = IntSize(cellWidth, cellHeight),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt()),
            filterQuality = FilterQuality.High
        )
    }
}

@Composable
fun GeneratedSheetPanel(
    drawable: Int,
    index: Int,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    rows: Int = 3
) {
    val resources = LocalResources.current
    val bitmap = remember(drawable) { ImageBitmap.imageResource(resources, drawable) }
    Canvas(modifier) {
        val safeIndex = index.coerceIn(0, columns * rows - 1)
        val cellWidth = bitmap.width / columns
        val cellHeight = bitmap.height / rows
        drawImage(
            image = bitmap,
            srcOffset = IntOffset((safeIndex % columns) * cellWidth, (safeIndex / columns) * cellHeight),
            srcSize = IntSize(cellWidth, cellHeight),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
            filterQuality = FilterQuality.High
        )
    }
}
