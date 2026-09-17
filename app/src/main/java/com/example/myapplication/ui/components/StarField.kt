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
fun DebrisTarget(target: ScavengeTarget, gameAreaWidth: Dp, gameAreaHeight: Dp, onClick: (() -> Unit)? = null) {
    val targetSize = if (target.isMeteor) 38.dp else (30 + target.rarity.ordinal * 2).dp

    Box(
        modifier = Modifier
            .offset(
                x = gameAreaWidth * target.x - (targetSize / 2),
                y = gameAreaHeight * target.y - (targetSize / 2)
            )
            .size(targetSize)
            .alpha(0.88f)
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
                painter = painterResource(debrisDrawable(target.imageIndex)),
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
    1 -> R.drawable.debris_01_v2
    2 -> R.drawable.debris_02_v2
    3 -> R.drawable.debris_03_v2
    4 -> R.drawable.debris_04_v2
    5 -> R.drawable.debris_05_v2
    6 -> R.drawable.debris_06_v2
    7 -> R.drawable.debris_07_v2
    8 -> R.drawable.debris_08_v2
    9 -> R.drawable.debris_09_v2
    10 -> R.drawable.debris_10_v2
    11 -> R.drawable.debris_11_v2
    12 -> R.drawable.debris_12_v2
    13 -> R.drawable.debris_13_v2
    14 -> R.drawable.debris_14_v2
    15 -> R.drawable.debris_15_v2
    16 -> R.drawable.debris_16_v2
    17 -> R.drawable.debris_17_v2
    18 -> R.drawable.debris_18_v2
    19 -> R.drawable.debris_19_v2
    20 -> R.drawable.debris_20_v2
    21 -> R.drawable.debris_21_v2
    22 -> R.drawable.debris_22_v2
    23 -> R.drawable.debris_23_v2
    24 -> R.drawable.debris_24_v2
    25 -> R.drawable.debris_25_v2
    26 -> R.drawable.debris_26_v2
    27 -> R.drawable.debris_27_v2
    28 -> R.drawable.debris_28_v2
    else -> R.drawable.debris_01_v2
}
