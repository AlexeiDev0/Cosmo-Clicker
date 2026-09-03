package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.SpaceDesign

enum class CosmicButtonStyle { Primary, Secondary, Reward, Danger }
enum class CosmicButtonState { Normal, Selected, Active, Locked }

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    style: CosmicButtonStyle = CosmicButtonStyle.Primary,
    state: CosmicButtonState = CosmicButtonState.Normal,
    compact: Boolean = false,
    generatedArtwork: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    // Draw the resting state with Compose primitives. The former full-size PNG frame
    // could be skipped by the renderer until the first pointer invalidation, leaving
    // an invisible but clickable button on some devices.
    val requestedAccent = if (enabled) colors.containerColor else colors.disabledContainerColor
    val requestedContent = if (enabled) colors.contentColor else colors.disabledContentColor
    val accent = when (style) {
        CosmicButtonStyle.Primary -> requestedAccent
        CosmicButtonStyle.Secondary -> AppColors.Secondary
        CosmicButtonStyle.Reward -> AppColors.Reward
        CosmicButtonStyle.Danger -> AppColors.Danger
    }
    val effectiveAccent = if (enabled) accent else AppColors.Disabled
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) .965f else 1f, label = "cosmic_button_press")
    val artworkRes = when {
        !enabled -> R.drawable.ui_button_disabled_v6
        state == CosmicButtonState.Locked -> R.drawable.ui_button_locked_v6
        pressed -> R.drawable.ui_button_pressed_v6
        state == CosmicButtonState.Active -> R.drawable.ui_button_active_v6
        state == CosmicButtonState.Selected -> R.drawable.ui_button_selected_v6
        else -> R.drawable.ui_button_normal_v6
    }
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = if (compact) 72.dp else 96.dp,
                minHeight = 48.dp
            )
            .alpha(if (enabled) 1f else SpaceDesign.DisabledAlpha)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        effectiveAccent.copy(alpha = if (enabled) .30f else .16f),
                        AppColors.Surface,
                        effectiveAccent.copy(alpha = if (enabled) .13f else .08f)
                    )
                )
            )
            .then(
                if (border != null) Modifier.border(border.width, border.brush, shape)
                else Modifier.border(
                    width = 1.dp,
                    color = effectiveAccent.copy(alpha = if (generatedArtwork) .58f else .38f),
                    shape = shape
                )
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (generatedArtwork) {
            Image(
                painter = painterResource(artworkRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                alpha = .72f
            )
        }
        CompositionLocalProvider(
            LocalContentColor provides if (style == CosmicButtonStyle.Primary) requestedContent else Color.White
        ) {
            Row(
                modifier = Modifier.padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}
