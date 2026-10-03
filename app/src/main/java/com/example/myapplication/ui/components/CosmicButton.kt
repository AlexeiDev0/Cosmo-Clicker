package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.SpaceDesign

enum class CosmicButtonStyle { Primary, Secondary, Reward, Danger }
enum class CosmicButtonState { Normal, Selected, Active, Locked }

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
        enabled = enabled,
        compact = true,
        style = CosmicButtonStyle.Secondary,
        contentPadding = PaddingValues(0.dp),
        generatedArtwork = false
    ) { content() }
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceDesign.ControlRadius),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    style: CosmicButtonStyle = CosmicButtonStyle.Primary,
    state: CosmicButtonState = CosmicButtonState.Normal,
    compact: Boolean = false,
    generatedArtwork: Boolean = style != CosmicButtonStyle.Secondary,
    content: @Composable RowScope.() -> Unit
) {
    // Soft warm highlights and cool shadows echo the painted planet artwork.
    val requestedAccent = if (enabled) colors.containerColor else colors.disabledContainerColor
    val accent = when (style) {
        CosmicButtonStyle.Primary -> requestedAccent
        CosmicButtonStyle.Secondary -> Color(0xFF9AAFC4)
        CosmicButtonStyle.Reward -> AppColors.Reward
        CosmicButtonStyle.Danger -> AppColors.Danger
    }
    val effectiveAccent = when {
        !enabled -> AppColors.Disabled
        state == CosmicButtonState.Locked -> AppColors.Locked
        else -> accent
    }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val reduceMotion = com.example.myapplication.ui.theme.LocalReducedMotion.current
    val pressScale by animateFloatAsState(if (pressed && !reduceMotion) .965f else 1f, label = "cosmic_button_press")
    val emphasized = state == CosmicButtonState.Active || state == CosmicButtonState.Selected
    val richSurface = style != CosmicButtonStyle.Secondary || emphasized
    val depth = if (pressed) .16f else if (emphasized) .38f else .29f
    val surface = if (richSurface) Brush.verticalGradient(
        listOf(
            lerp(Color(0xFF40516C), effectiveAccent, depth),
            lerp(AppColors.Surface, effectiveAccent, depth * .72f),
            lerp(AppColors.CardBackground, effectiveAccent, depth * .42f)
        )
    ) else Brush.verticalGradient(listOf(AppColors.SurfaceRaised, AppColors.CardBackground))
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = if (compact) 48.dp else 96.dp,
                minHeight = 48.dp
            )
            .alpha(if (enabled) 1f else SpaceDesign.DisabledAlpha)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(if (enabled && (emphasized || richSurface)) 5.dp else 2.dp, shape, clip = false)
            .clip(shape)
            .background(surface)
            .then(
                if (border != null) Modifier.border(border.width, border.brush, shape)
                else Modifier.border(
                    width = 1.dp,
                    color = effectiveAccent.copy(alpha = if (pressed || emphasized) .8f else if (richSurface) .5f else .20f),
                    shape = shape
                )
            )
            .semantics { selected = emphasized }
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (generatedArtwork && !compact) {
            Image(
                painter = shipButtonPainter(style, pressed),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds,
                alpha = .32f
            )
        }
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) Color(0xFFF0F5F7) else AppColors.TextDisabled
        ) {
            Row(
                modifier = Modifier.padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}
