package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SkyStreak(val start: Float, val y: Float, val direction: Float, val size: Float)

/** Decorative only. One parent clock, bounded objects, no jobs or game-state writes. */
@Composable
fun LivingGalaxy(phase: Float, reducedMotion: Boolean, modifier: Modifier = Modifier,
    primary: Color = Color(0xFF75EBD5), secondary: Color = Color(0xFFFFAD8F)) {
    // Session-local visual variation, never persisted or connected to the economy.
    val streaks = remember {
        List(3) { i -> SkyStreak(
            .08f + i * .29f + Random.nextFloat() * .08f,
            .12f + Random.nextFloat() * .23f,
            if (Random.nextBoolean()) 1f else -1f,
            .7f + Random.nextFloat() * .6f
        ) }
    }
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val u = size.minDimension
        val cyan = primary
        val coral = secondary
        val violet = Color(0xFFA49AFA)
        repeat(if (reducedMotion) 6 else 16) { i ->
            val x = ((i * .618034f) % 1f) * w
            val y = (((i * .38197f) + if (reducedMotion) 0f else sin(phase * 6.283185f) * .012f) % 1f) * h
            drawCircle(if (i % 2 == 0) cyan.copy(.24f) else coral.copy(.23f), u * .0025f, Offset(x, y))
        }
        // A quiet satellite orbit stays visible as a stationary landmark in reduced motion.
        val angle = (if (reducedMotion) .8f else phase * 6.283185f)
        val x = w * .5f + cos(angle) * w * .34f
        val y = h * .46f + sin(angle) * h * .15f
        // Solar wings, unified capsule and one status light: readable at small size.
        drawRoundRect(Color(0xFF386C9E).copy(.65f), Offset(x - u * .042f, y - u * .009f), Size(u * .084f, u * .018f), CornerRadius(u * .003f))
        drawRoundRect(violet.copy(.75f), Offset(x - u * .012f, y - u * .017f), Size(u * .024f, u * .034f), CornerRadius(u * .008f))
        drawRoundRect(Color(0xFFD8DEFA).copy(.8f), Offset(x - u * .01f, y - u * .017f), Size(u * .02f, u * .012f), CornerRadius(u * .005f))
        drawCircle(cyan, u * .003f, Offset(x, y + u * .006f))
        if (!reducedMotion) {
            // One comet every minute, visible for only 4.2 seconds.
            val comet = ((phase + .19f) % 1f) / .07f
            if (comet < 1f) {
                val head = Offset(w * (-.15f + comet * 1.4f), h * (.13f + comet * .2f))
                val fade = (sin(comet * 3.141593f) * 1.5f).coerceIn(0f, 1f)
                val tail = head - Offset(u * .22f, u * .055f)
                drawLine(Brush.linearGradient(listOf(Color.Transparent, violet.copy(.4f * fade), cyan.copy(.65f * fade)), tail, head), tail, head, u * .015f, StrokeCap.Round)
                drawLine(coral.copy(.85f * fade), head - Offset(u * .12f, u * .03f), head, u * .005f, StrokeCap.Round)
                drawCircle(cyan.copy(.12f * fade), u * .022f, head)
                drawCircle(Color(0xFFFFE6AC), u * .008f, head)
            }
            // A short shooting star; no reward or touch target.
            streaks.forEach { star ->
                val streak = (phase - star.start) / .012f
                if (streak in 0f..1f) {
                    val fade = sin(streak * 3.141593f).coerceIn(0f, 1f)
                    val head = Offset(w * (.5f + star.direction * (streak - .5f) * .65f), h * (star.y + streak * .15f))
                    val tail = head - Offset(star.direction * u * .12f * star.size, u * .05f * star.size)
                    drawLine(Brush.linearGradient(listOf(Color.Transparent, Color(0xFFB4DCFF).copy(.55f * fade), Color.White.copy(fade)), tail, head), tail, head, u * .003f * star.size, StrokeCap.Round)
                    drawCircle(Color.White.copy(fade), u * .004f * star.size, head)
                }
            }
            // A distant fleet crosses once per 60-second cycle.
            repeat(3) { i ->
                val travel = (phase - .36f) / .45f
                val px = (travel * 1.3f - .15f - i * .045f) * w
                val py = h * (.69f + i * .016f)
                val ship = Path().apply {
                    moveTo(px, py)
                    lineTo(px - u * .026f, py + u * .007f)
                    lineTo(px - u * .035f, py)
                    lineTo(px - u * .026f, py - u * .007f)
                    close()
                }
                drawPath(ship, Color(0xFF667BA8).copy(.6f))
                drawLine(cyan.copy(.55f), Offset(px - u * .018f, py), Offset(px - u * .007f, py), u * .003f, StrokeCap.Round)
                drawCircle(coral.copy(.55f), u * .003f, Offset(px - u * .031f, py))
            }
        }
    }
}
