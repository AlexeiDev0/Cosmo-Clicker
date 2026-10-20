package com.example.myapplication.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.DroneFusionEngine
import com.example.myapplication.DroneFusionResult
import com.example.myapplication.FleetConfig
import com.example.myapplication.GameState
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.LocalReducedMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val FusionPurple = Color(0xFFBD91FF)
private val FusionBackground = Color(0xFF11182B)

@Composable
fun DroneFusionDialog(
    state: GameState,
    fleet: List<FleetConfig>,
    onFuse: (List<String>) -> DroneFusionResult?,
    onClose: () -> Unit
) {
    var selected by remember { mutableStateOf<List<String>>(emptyList()) }
    var confirming by remember { mutableStateOf(false) }
    var resultShown by remember { mutableStateOf(false) }
    var rewardId by remember { mutableStateOf<String?>(null) }
    var animationSources by remember { mutableStateOf<List<String>>(emptyList()) }
    var animating by remember { mutableStateOf(false) }
    var selectionChanged by remember { mutableStateOf(false) }
    val reducedMotion = LocalReducedMotion.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(animating, reducedMotion) {
        if (animating) {
            progress.snapTo(0f)
            if (!reducedMotion) progress.animateTo(1f, tween(950, easing = FastOutSlowInEasing))
            animating = false
        }
    }
    val byId = remember(fleet) { fleet.associateBy { it.id } }
    val recipe = selected.firstOrNull()?.let(byId::get)?.rarity?.let(DroneFusionEngine::recipe)
    val collection = fleet.filter {
        (state.fleetCounts[it.id] ?: 0) > 0 && DroneFusionEngine.recipe(it.rarity) != null
    }.sortedWith(compareBy<FleetConfig> { DroneFusionEngine.availableCount(state, it.id) == 0 }
        .thenBy { it.rarity.ordinal }.thenBy { it.id })
    val valid = selected.size == 2 && recipe != null && selected.groupingBy { it }.eachCount().all { (id, count) ->
        byId[id]?.rarity == recipe.source && DroneFusionEngine.availableCount(state, id) >= count
    }
    val chanceText = if (recipe != null) {
        stringResource(R.string.fusion_chance, rarityLabel(recipe.target), recipe.chancePercent)
    } else stringResource(R.string.fusion_choose_pair)

    if (confirming) {
        AlertDialog(
            onDismissRequest = { confirming = false },
            title = { Text(stringResource(R.string.fusion_confirm_title)) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    selected.forEach { id ->
                        byId[id]?.let { drone ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Image(cosmicIconPainter(drone.iconRes), null, Modifier.size(40.dp))
                                Text(localizedDroneName(drone))
                            }
                        }
                    }
                    Text(chanceText, color = FusionPurple, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.fusion_risk), color = AppColors.Danger)
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag("fusion_confirm"),
                    enabled = valid,
                    style = CosmicButtonStyle.Danger,
                    onClick = {
                        confirming = false
                        // Apply and save immediately; animation never controls the transaction.
                        val pair = selected.toList()
                        val result = onFuse(pair)
                        selected = emptyList()
                        if (result != null) {
                            animationSources = pair
                            rewardId = result.rewardDroneId
                            resultShown = true
                            animating = !reducedMotion
                            selectionChanged = false
                        } else selectionChanged = true
                    }
                ) { Text(stringResource(R.string.fusion_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirming = false }) { Text(stringResource(R.string.cancel)) }
            },
            containerColor = FusionBackground
        )
        return
    }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(0.94f).fillMaxHeight(0.92f).testTag("fusion_workshop"),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, FusionPurple.copy(alpha = .35f)),
            colors = CardDefaults.cardColors(containerColor = FusionBackground)
        ) {
            Column(
                Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF292043), FusionBackground, Color(0xFF171D34))))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpaceSheetHeader(
                    title = stringResource(R.string.fusion_title),
                    subtitle = stringResource(R.string.fusion_workshop_subtitle),
                    onClose = onClose
                )
                if (resultShown) {
                    val reward = rewardId?.let(byId::get)
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        if (animating) {
                            FusionAnimation(animationSources.mapNotNull(byId::get), progress.value)
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !animating,
                            enter = if (reducedMotion) fadeIn(tween(0)) else fadeIn(tween(180)) + scaleIn(initialScale = .85f)
                        ) {
                            Column(
                                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    Modifier.size(150.dp).background(
                                        Brush.radialGradient(listOf(FusionPurple.copy(alpha = .3f), Color.Transparent)), CircleShape
                                    ), contentAlignment = Alignment.Center
                                ) {
                                    Image(cosmicIconPainter(reward?.iconRes ?: R.drawable.ic_nav_fusion), null, Modifier.size(108.dp))
                                }
                                Text(
                                    stringResource(if (reward != null) R.string.fusion_success else R.string.fusion_failure),
                                    color = if (reward != null) FusionPurple else AppColors.Danger,
                                    fontWeight = FontWeight.Bold, fontSize = 22.sp
                                )
                                if (reward != null) {
                                    Text(localizedDroneName(reward), color = Color.White)
                                    Text(rarityLabel(reward.rarity), color = reward.rarity.color)
                                    Text(stringResource(R.string.fusion_reward_reserve), color = AppColors.TextMuted, textAlign = TextAlign.Center)
                                } else {
                                    Text(stringResource(R.string.fusion_failure_details), color = AppColors.TextMuted, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                    Button(onClick = { resultShown = false }, enabled = !animating, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(if (animating) R.string.fusion_in_progress else R.string.fusion_again))
                    }
                } else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(2) { index ->
                            val drone = selected.getOrNull(index)?.let(byId::get)
                            FusionSlot(drone, index, Modifier.weight(1f)) {
                                selected = selected.filterIndexed { position, _ -> position != index }
                            }
                            if (index == 0) Text("+", color = FusionPurple, fontSize = 24.sp)
                        }
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = FusionPurple.copy(alpha = .12f)) {
                        Text(chanceText, Modifier.fillMaxWidth().padding(10.dp), color = FusionPurple,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.fusion_collection), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${selected.size} / 2", color = FusionPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    if (collection.isEmpty()) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.fusion_empty), color = AppColors.TextMuted, textAlign = TextAlign.Center)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(82.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth().testTag("fusion_grid"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(collection, key = { it.id }) { drone ->
                                val chosen = selected.count { it == drone.id }
                                val available = DroneFusionEngine.availableCount(state, drone.id)
                                val canAdd = selected.size < 2 && available > chosen && (recipe == null || drone.rarity == recipe.source)
                                FusionDroneTile(drone, available, chosen, canAdd || chosen > 0) {
                                    selectionChanged = false
                                    selected = if (canAdd) selected + drone.id else {
                                        val last = selected.indexOfLast { it == drone.id }
                                        selected.filterIndexed { index, _ -> index != last }
                                    }
                                }
                            }
                        }
                    }
                    if (selectionChanged) Text(stringResource(R.string.fusion_selection_changed), color = AppColors.Warning, fontSize = 12.sp)
                    Text(stringResource(R.string.fusion_risk_short), color = AppColors.Danger, fontSize = 12.sp)
                    Button(
                        onClick = { confirming = true }, enabled = valid,
                        modifier = Modifier.fillMaxWidth().testTag("fusion_review"),
                        colors = ButtonDefaults.buttonColors(containerColor = FusionPurple),
                        style = CosmicButtonStyle.Reward
                    ) { Text(stringResource(R.string.fusion_review)) }
                }
            }
        }
    }
}

@Composable
private fun FusionSlot(drone: FleetConfig?, index: Int, modifier: Modifier, onRemove: () -> Unit) {
    val description = if (drone == null) stringResource(R.string.fusion_slot_empty, index + 1)
        else stringResource(R.string.fusion_slot_remove, index + 1, localizedDroneName(drone))
    Column(
        modifier.height(108.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(FusionPurple.copy(alpha = if (drone != null) .2f else .06f), Color(0xFF131C30))))
            .border(if (drone != null) 2.dp else 1.dp, FusionPurple.copy(alpha = if (drone != null) .9f else .3f), RoundedCornerShape(16.dp))
            .clickable(enabled = drone != null, role = Role.Button, onClickLabel = description, onClick = onRemove)
            .testTag("fusion_slot_$index")
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        if (drone != null) {
            Image(cosmicIconPainter(drone.iconRes), description, Modifier.size(58.dp))
            Text(localizedDroneName(drone), color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        } else {
            Text("+", color = FusionPurple.copy(alpha = .6f), fontSize = 32.sp)
            Text(description, color = AppColors.TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun FusionDroneTile(drone: FleetConfig, available: Int, chosen: Int, enabled: Boolean, onClick: () -> Unit) {
    val reducedMotion = LocalReducedMotion.current
    val highlight by animateFloatAsState(if (chosen > 0) 1f else 0f, tween(if (reducedMotion) 0 else 150), label = "fusion_selection")
    Column(
        Modifier.aspectRatio(1f).clip(RoundedCornerShape(13.dp))
            .background(Brush.verticalGradient(listOf(
                FusionPurple.copy(alpha = .04f + .24f * highlight), Color(0xFF192339)
            )))
            .border(if (chosen > 0) 2.dp else 1.dp,
                if (chosen > 0) FusionPurple else AppColors.Outline.copy(alpha = .35f), RoundedCornerShape(13.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { selected = chosen > 0 }
            .testTag("fusion_select_${drone.id}")
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(cosmicIconPainter(drone.iconRes), localizedDroneName(drone),
                Modifier.fillMaxSize().padding(horizontal = 8.dp).graphicsLayer {
                    alpha = if (enabled || chosen > 0) 1f else .35f
                    scaleX = 1f + .06f * highlight
                    scaleY = scaleX
                })
            Text("×$available", Modifier.align(Alignment.TopEnd), color = AppColors.TextMuted, fontSize = 10.sp)
            if (chosen > 0) Surface(Modifier.align(Alignment.BottomEnd), shape = CircleShape, color = FusionPurple) {
                Text(chosen.toString(), Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = FusionBackground, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
        Text(localizedDroneName(drone), color = if (enabled) Color.White else AppColors.TextMuted,
            fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(rarityLabel(drone.rarity), color = drone.rarity.color, fontSize = 9.sp, maxLines = 1)
    }
}

@Composable
private fun FusionAnimation(drones: List<FleetConfig>, progress: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth().height(200.dp).testTag("fusion_animation"), contentAlignment = Alignment.Center) {
            val distance = maxWidth * .28f * (1f - progress)
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val glow = sin(PI * progress).toFloat().coerceIn(0f, 1f)
                drawCircle(FusionPurple.copy(alpha = .12f + glow * .28f), size.minDimension * (.12f + .24f * progress), center)
                drawCircle(FusionPurple.copy(alpha = glow), size.minDimension * (.18f + .25f * progress), center, style = Stroke(2.dp.toPx()))
                repeat(8) { index ->
                    val angle = index * PI / 4 + progress * PI
                    val radius = size.minDimension * (.12f + progress * .32f)
                    val spark = center + Offset(cos(angle).toFloat() * radius, sin(angle).toFloat() * radius)
                    drawCircle(Color.White.copy(alpha = glow), 2.dp.toPx(), spark)
                }
            }
            drones.forEachIndexed { index, drone ->
                Image(cosmicIconPainter(drone.iconRes), null, Modifier.size(74.dp)
                    .offset(x = distance * if (index == 0) -1f else 1f)
                    .graphicsLayer {
                        alpha = (1f - progress).coerceIn(0f, 1f)
                        scaleX = 1f - progress * .55f
                        scaleY = scaleX
                        rotationZ = (if (index == 0) 1f else -1f) * progress * 30f
                    })
            }
        }
        Text(stringResource(R.string.fusion_in_progress), color = FusionPurple, fontWeight = FontWeight.Bold)
    }
}
