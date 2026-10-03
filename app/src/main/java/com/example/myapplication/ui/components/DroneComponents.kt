package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import com.example.myapplication.ui.components.cosmicIconPainter as painterResource
import androidx.compose.ui.res.painterResource as cargoPainterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.example.myapplication.DroneData
import com.example.myapplication.DroneState
import com.example.myapplication.FleetConfig
import com.example.myapplication.R
import com.example.myapplication.Rarity
import kotlin.math.sin

@Composable
fun FleetIcon(item: FleetConfig, iconSize: Dp, inFlight: Boolean = false) {
    val rarityColor = remember(item.rarity) { item.rarity.color }
    
    Box(
        modifier = Modifier
            .size(iconSize)
            .background(Brush.radialGradient(listOf(
                rarityColor.copy(alpha = 0.20f),
                rarityColor.copy(alpha = 0.06f),
                Color.Transparent
            )), CircleShape)
            .then(if (inFlight) Modifier else Modifier.border(0.5.dp, rarityColor.copy(alpha = 0.28f), RoundedCornerShape(12.dp))),
        contentAlignment = Alignment.Center
    ) {
        if (item.spriteIndex >= 0) {
            GeneratedSheetIcon(item.iconRes, item.spriteIndex, iconSize, columns = 6, rows = 5)
        } else {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun ScavengingDrone(
    drone: DroneData,
    fleetItems: Map<String, FleetConfig>,
    gameAreaWidth: Dp,
    gameAreaHeight: Dp,
    rotorPhase: Float = 0f,
    onDroneClick: (Long) -> Unit = {}
) {
    val fleetItem = fleetItems[drone.type]
    val reducedMotion = com.example.myapplication.ui.theme.LocalReducedMotion.current
    val displayX = if (reducedMotion) .12f + (drone.id % 7).toFloat() * .12f else drone.x
    val displayY = if (reducedMotion) .18f + (drone.id % 5).toFloat() * .14f else drone.y
    val isInfected = drone.state == DroneState.INFECTED
    
    val droneSize = remember(drone.type, fleetItem) {
        if (fleetItem != null) {
            when(fleetItem.rarity) {
                Rarity.LEGENDARY, Rarity.VOID -> 58.dp
                Rarity.EPIC -> 46.dp
                Rarity.RARE -> 42.dp
                else -> 38.dp
            }
        } else 38.dp
    }

    Box(
        modifier = Modifier
            .offset(
                x = gameAreaWidth * displayX - (droneSize / 2),
                y = gameAreaHeight * displayY - (droneSize / 2)
            )
            .size(droneSize)
            .let { 
                if (isInfected) it
                    .background(Color.Red.copy(alpha = 0.35f), CircleShape)
                    .border(2.dp, Color.Red, CircleShape)
                    .shadow(12.dp, CircleShape, spotColor = Color.Red)
                else it
            }
            .semantics {
                role = Role.Button
                contentDescription = fleetItem?.name ?: drone.type
            }
            .clickable { onDroneClick(drone.id) },
        contentAlignment = Alignment.Center
    ) {
        if (fleetItem != null) {
            val engineColor = if (isInfected) Color(0xFFFF6688) else Color(0xFF8BE8FF)
            Canvas(Modifier.fillMaxSize()) {
                val pulse = if (reducedMotion) .65f else .65f + .18f * sin(rotorPhase + drone.id.toFloat())
                for (x in listOf(.31f, .69f)) {
                    val exhaust = Offset(size.width * x, size.height * .82f)
                    drawOval(
                        Brush.radialGradient(listOf(engineColor.copy(alpha = pulse), Color.Transparent), exhaust, size.width * .17f),
                        Offset(exhaust.x - size.width * .13f, exhaust.y - size.height * .04f),
                        Size(size.width * .26f, size.height * .24f)
                    )
                    drawCircle(Color(0xFFE2FAFF), size.width * .025f, exhaust)
                }
            }
            FleetIcon(fleetItem, droneSize, inFlight = true)
        } else {
            Box(modifier = Modifier.size(droneSize).background(Color.Red, RoundedCornerShape(2.dp)))
        }
        
        if (drone.hasCargo) {
            Image(
                painter = drone.cargoDebrisId?.let { cargoPainterResource(debrisDrawable(it)) }
                    ?: painterResource(R.drawable.cargo_crate_space_v2),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(droneSize * 0.52f)
                    .align(Alignment.BottomCenter)
                    .offset(y = droneSize * 0.14f)
            )
        }

        if (isInfected) {
            Text("!", color = Color.White, fontSize = 14.sp, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}
