package com.example.myapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

/** Painted depth, destination colours and quiet orbital details around the margins. */
@Composable
internal fun paintedBackgroundPainter(resource: Int): Painter? {
    val accent = when (resource) {
        R.drawable.flat_bg_main -> Color(0xFFA7DDF3)
        R.drawable.flat_bg_start -> Color(0xFFF4D59D)
        R.drawable.flat_bg_shop, R.drawable.flat_bg_offline -> Color(0xFFE7B679)
        R.drawable.flat_bg_hangar -> Color(0xFFAFE3CD)
        R.drawable.flat_bg_goals, R.drawable.flat_bg_cases -> Color(0xFFB8A8DE)
        R.drawable.flat_bg_achievements -> Color(0xFFF4D59D)
        R.drawable.flat_bg_statistics -> Color(0xFF9ED7E7)
        R.drawable.flat_bg_events -> Color(0xFFF3A1AC)
        R.drawable.flat_bg_prestige -> Color(0xFFC2AFE9)
        R.drawable.flat_bg_settings -> Color(0xFFB4CDDA)
        else -> return null
    }
    val warmScene = resource in listOf(R.drawable.flat_bg_start, R.drawable.flat_bg_shop,
        R.drawable.flat_bg_achievements, R.drawable.flat_bg_offline)
    val orbitalScene = resource in listOf(R.drawable.flat_bg_main, R.drawable.flat_bg_goals,
        R.drawable.flat_bg_statistics, R.drawable.flat_bg_prestige)
    val scene = painterResource(R.drawable.background_nebula_painted_v1)
    return remember(scene, accent, warmScene, orbitalScene) {
        object : Painter() {
            override val intrinsicSize: Size = scene.intrinsicSize
            override fun DrawScope.onDraw() {
                with(scene) { draw(size) }
                drawRect(Brush.verticalGradient(listOf(
                    Color(0xFF0A1228).copy(alpha = .26f),
                    Color(0xFF0A1228).copy(alpha = .48f),
                    Color(0xFF0A1228).copy(alpha = .32f)
                )))
                val glowCenter = Offset(size.width * if (warmScene) .12f else .88f, size.height * .12f)
                drawRect(Brush.radialGradient(
                    listOf(accent.copy(alpha = .30f), accent.copy(alpha = .08f), Color.Transparent),
                    center = glowCenter, radius = size.width * .95f
                ))
                drawRect(Brush.radialGradient(
                    listOf(accent.copy(alpha = .13f), Color.Transparent),
                    center = Offset(size.width * .15f, size.height * .94f), radius = size.width * .75f
                ))
                if (orbitalScene) {
                    // Deliberately outside the central text and planet area.
                    repeat(2) { ring ->
                        drawOval(accent.copy(alpha = if (ring == 0) .18f else .08f),
                            topLeft = Offset(size.width * .52f, -size.height * (.10f + ring * .025f)),
                            size = Size(size.width * (.72f + ring * .14f), size.height * .30f),
                            style = Stroke(width = size.width / 420f))
                    }
                } else {
                    drawLine(accent.copy(alpha = .20f), Offset(0f, size.height * .16f),
                        Offset(size.width * .13f, size.height * .16f), size.width / 360f)
                    drawLine(accent.copy(alpha = .20f), Offset(size.width * .87f, size.height * .84f),
                        Offset(size.width, size.height * .84f), size.width / 360f)
                }
                repeat(18) { index ->
                    val x = if (index % 2 == 0) .035f + (index % 5) * .025f
                        else .86f + (index % 5) * .024f
                    val y = .08f + ((index * 37) % 83) / 100f
                    drawCircle(accent.copy(alpha = .22f + (index % 3) * .12f),
                        radius = size.width * if (index % 4 == 0) .0028f else .0016f,
                        center = Offset(size.width * x, size.height * y))
                }
            }
        }
    }
}
