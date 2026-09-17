package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.PlanetPalette
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.ContentScale
import com.example.myapplication.ui.components.cosmicIconPainter as painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.PlanetConfig
import com.example.myapplication.ui.GameConstants
import kotlinx.coroutines.delay

@Composable
fun PlanetButton(
    planetId: String,
    planetConfig: PlanetConfig,
    modifier: Modifier,
    reducedMotion: Boolean = false,
    diameter: Dp = GameConstants.PlanetSize,
    atmospherePhase: Float = 0f,
    onClick: (Float, Float) -> Unit
) {
    var scaleVal by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scaleVal,
        animationSpec = tween(120, easing = FastOutSlowInEasing),
        label = "planet_press"
    )
    val isLocked = planetConfig.price < 0
    val containerSize = diameter

    Box(
        modifier = modifier
            .size(containerSize)
            .scale(animatedScale)
            .semantics {
                role = Role.Button
                if (isLocked) disabled()
            }
            .pointerInput(isLocked, reducedMotion, planetId) {
                detectTapGestures { position ->
                    if (!isLocked && size.width > 0 && size.height > 0) {
                        scaleVal = if (reducedMotion) 1f else 0.94f
                        onClick(
                            (position.x / size.width).coerceIn(0f, 1f),
                            (position.y / size.height).coerceIn(0f, 1f)
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(scaleVal) { if (scaleVal < 1f) { delay(90); scaleVal = 1f } }

        val palette = remember(planetId) { PlanetPalette.forPlanet(planetId) }
        Canvas(Modifier.fillMaxSize()) {
            val radius = size.minDimension * .49f
            drawCircle(
                brush = Brush.radialGradient(
                    0f to Color.Transparent,
                    .65f to palette.secondary.copy(alpha = if (isLocked) .025f else .07f),
                    .83f to palette.primary.copy(alpha = if (isLocked) .025f else .10f),
                    1f to Color.Transparent,
                    center = center,
                    radius = radius
                ),
                radius = radius
            )
        }

        // Gentle graphic motion; all forms stay inside the artwork viewport.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(androidx.compose.ui.graphics.RectangleShape)
        ) {
            if (planetConfig.spriteIndex >= 0) {
                val columns = 5
                val rows = 4
                val row = planetConfig.spriteIndex / columns
                val col = planetConfig.spriteIndex % columns

                Image(
                    painter = painterResource(id = planetConfig.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .requiredSize(containerSize * columns, containerSize * rows)
                        .offset(x = -containerSize * col, y = -containerSize * row)
                )
            } else {
                Image(
                    painter = painterResource(id = planetConfig.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(if (reducedMotion) 0f else kotlin.math.sin(atmospherePhase * 6.283185f) * 2f)
                        .let { if (isLocked) it.alpha(0.5f) else it }
                )
            }
        }
    }
}
