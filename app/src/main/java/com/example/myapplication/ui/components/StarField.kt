package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import com.example.myapplication.ui.components.cosmicIconPainter as painterResource
import androidx.compose.ui.res.stringResource
import com.example.myapplication.ui.components.cosmicIconPainter as debrisPainterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.myapplication.ScavengeTarget
import com.example.myapplication.R
import kotlin.random.Random
import kotlin.math.sin

@Composable
fun Star(index: Int, twinklePhase: Float, reduceMotion: Boolean = false) {
    val x = remember { Random.nextFloat() }
    val y = remember { Random.nextFloat() }
    val size = remember { Random.nextFloat() * 2 + 1 }
    // All stars share one animation clock from GameScreen instead of creating
    // a separate infinite transition for every star.
    val twinkle = ((sin(twinklePhase + index * 1.73f) + 1f) * .5f)
    val alpha = if (reduceMotion) 0.55f else 0.2f + twinkle * 0.8f
    val windowSize = LocalWindowInfo.current.containerSize
    Box(
        modifier = Modifier
            .offset { IntOffset((x * windowSize.width).toInt(), (y * windowSize.height).toInt()) }
            .size(size.dp)
            .background(
                listOf(Color(0xFFD3EEFF), Color(0xFFFFD9AD), Color(0xFFE4D4FF), Color(0xFFC5F5E2), Color(0xFFFFCEDC))[index % 5]
                    .copy(alpha = alpha), CircleShape)
    )
}

@Composable
fun DebrisTarget(target: ScavengeTarget, gameAreaWidth: Dp, gameAreaHeight: Dp, onClick: (() -> Unit)? = null, planetId: String = "p1") {
    val targetSize = if (target.isMeteor) 38.dp else (44 + target.rarity.ordinal * 4).dp

    Box(
        modifier = Modifier
            .offset(
                x = gameAreaWidth * target.x - (targetSize / 2),
                y = gameAreaHeight * target.y - (targetSize / 2)
            )
            .size(targetSize)
            .background(androidx.compose.ui.graphics.Brush.radialGradient(listOf(
                target.rarity.color.copy(alpha = .24f), Color.Transparent
            )), CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (target.isMeteor) {
            Image(
                painter = painterResource(R.drawable.event_meteor_minimal_v3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(targetSize - 2.dp)
                    .rotate(35f)
            )
        } else {
            Image(
                painter = debrisPainterResource(debrisDrawable(target.imageIndex)),
                colorFilter = remember(planetId, target.rarity) {
                    if (target.rarity.ordinal < com.example.myapplication.Rarity.RARE.ordinal)
                        planetDebrisFilter(planetId) else null
                },
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(targetSize - 4.dp)
                    .rotate(if (target.isFalling) 25f else ((target.id % 8) * 45).toFloat())
            )
        }
    }
}

internal fun debrisDrawable(index: Int): Int = when (index) {
    1 -> R.drawable.minimal_debris_01_v7
    2 -> R.drawable.minimal_debris_02_v7
    3 -> R.drawable.minimal_debris_03_v7
    4 -> R.drawable.minimal_debris_04_v7
    5 -> R.drawable.minimal_debris_05_v7
    6 -> R.drawable.minimal_debris_06_v7
    7 -> R.drawable.minimal_debris_07_v7
    8 -> R.drawable.minimal_debris_08_v7
    9 -> R.drawable.minimal_debris_09_v7
    10 -> R.drawable.minimal_debris_10_v7
    11 -> R.drawable.minimal_debris_11_v7
    12 -> R.drawable.minimal_debris_12_v7
    13 -> R.drawable.minimal_debris_13_v7
    14 -> R.drawable.minimal_debris_14_v7
    15 -> R.drawable.minimal_debris_15_v7
    16 -> R.drawable.minimal_debris_16_v7
    17 -> R.drawable.minimal_debris_17_v7
    18 -> R.drawable.minimal_debris_18_v7
    19 -> R.drawable.minimal_debris_19_v7
    20 -> R.drawable.minimal_debris_20_v7
    21 -> R.drawable.minimal_debris_21_v7
    22 -> R.drawable.minimal_debris_22_v7
    23 -> R.drawable.minimal_debris_23_v7
    24 -> R.drawable.minimal_debris_24_v7
    25 -> R.drawable.minimal_debris_25_v7
    26 -> R.drawable.minimal_debris_26_v7
    27 -> R.drawable.minimal_debris_27_v7
    28 -> R.drawable.minimal_debris_28_v7
    29 -> R.drawable.minimal_debris_29_v7
    30 -> R.drawable.minimal_debris_30_v7
    31 -> R.drawable.minimal_debris_31_v7
    32 -> R.drawable.minimal_debris_32_v7
    33 -> R.drawable.minimal_debris_33_v7
    34 -> R.drawable.minimal_debris_34_v7
    35 -> R.drawable.minimal_debris_35_v7
    36 -> R.drawable.minimal_debris_36_v7
    37 -> R.drawable.minimal_debris_37_v7
    38 -> R.drawable.minimal_debris_38_v7
    39 -> R.drawable.minimal_debris_39_v7
    40 -> R.drawable.minimal_debris_40_v7
    41 -> R.drawable.minimal_debris_41_v7
    42 -> R.drawable.minimal_debris_42_v7
    43 -> R.drawable.minimal_debris_43_v7
    44 -> R.drawable.minimal_debris_44_v7
    45 -> R.drawable.minimal_debris_45_v7
    46 -> R.drawable.minimal_debris_46_v7
    47 -> R.drawable.minimal_debris_47_v7
    48 -> R.drawable.minimal_debris_48_v7
    else -> R.drawable.minimal_debris_01_v7
}
