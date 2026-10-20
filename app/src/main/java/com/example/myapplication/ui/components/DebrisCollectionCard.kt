package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.GameState
import com.example.myapplication.R
import com.example.myapplication.DebrisCatalog
import com.example.myapplication.Rarity
import com.example.myapplication.ui.theme.AppColors

private val debrisNames = intArrayOf(
    R.string.debris_name_01,
    R.string.debris_name_02,
    R.string.debris_name_03,
    R.string.debris_name_04,
    R.string.debris_name_05,
    R.string.debris_name_06,
    R.string.debris_name_07,
    R.string.debris_name_08,
    R.string.debris_name_09,
    R.string.debris_name_10,
    R.string.debris_name_11,
    R.string.debris_name_12,
    R.string.debris_name_13,
    R.string.debris_name_14,
    R.string.debris_name_15,
    R.string.debris_name_16,
    R.string.debris_name_17,
    R.string.debris_name_18,
    R.string.debris_name_19,
    R.string.debris_name_20,
    R.string.debris_name_21,
    R.string.debris_name_22,
    R.string.debris_name_23,
    R.string.debris_name_24,
    R.string.debris_name_25,
    R.string.debris_name_26,
    R.string.debris_name_27,
    R.string.debris_name_28,
    R.string.debris_name_29,
    R.string.debris_name_30,
    R.string.debris_name_31,
    R.string.debris_name_32,
    R.string.debris_name_33,
    R.string.debris_name_34,
    R.string.debris_name_35,
    R.string.debris_name_36,
    R.string.debris_name_37,
    R.string.debris_name_38,
    R.string.debris_name_39,
    R.string.debris_name_40,
    R.string.debris_name_41,
    R.string.debris_name_42,
    R.string.debris_name_43,
    R.string.debris_name_44,
    R.string.debris_name_45,
    R.string.debris_name_46,
    R.string.debris_name_47,
    R.string.debris_name_48
)

@Composable
internal fun DebrisCollectionCard(state: GameState) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val discovered = state.discoveredDebrisIds
    val nextGoal = listOf(10, 15, 20, 28, 38, DebrisCatalog.TYPE_COUNT).firstOrNull { discovered.size < it }
    Column(Modifier.fillMaxWidth().background(Color(0xCC101E2E), RoundedCornerShape(16.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.debris_collection), color = Color.White, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.debris_collection_count, discovered.size, DebrisCatalog.TYPE_COUNT), color = AppColors.Secondary, fontSize = 12.sp)
        LinearProgressIndicator(progress = { discovered.size.toFloat() / DebrisCatalog.TYPE_COUNT }, modifier = Modifier.fillMaxWidth(),
            color = AppColors.Primary, trackColor = Color.White.copy(alpha = .08f))
        Text(if (nextGoal == null) stringResource(R.string.debris_collection_complete)
            else stringResource(R.string.debris_collection_goal, nextGoal), color = AppColors.TextMuted, fontSize = 11.sp)
        Button(onClick = { expanded = !expanded }, style = CosmicButtonStyle.Secondary) {
            Text(stringResource(if (expanded) R.string.debris_collection_hide else R.string.debris_collection_show))
        }
        if (expanded) listOf(Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC, Rarity.LEGENDARY).forEach { group ->
            Text(rarityLabel(group), color = group.color, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                style = androidx.compose.ui.text.TextStyle(shadow = androidx.compose.ui.graphics.Shadow(group.color.copy(alpha = .45f), blurRadius = 8f)))
            DebrisCatalog.idsFor(group).chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { id ->
                    val known = id in discovered
                    val rarity = DebrisCatalog.rarityOf(id)
                    Column(Modifier.weight(1f)
                        .background(rarity.color.copy(alpha = .08f), RoundedCornerShape(12.dp))
                        .border(1.dp, rarity.color.copy(alpha = if (known) .45f else .18f), RoundedCornerShape(12.dp))
                        .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(cosmicIconPainter(if (known) debrisDrawable(id) else R.drawable.minimal_collection_lock_v5),
                            contentDescription = null, modifier = Modifier.size(64.dp))
                        Text(stringResource(if (known) debrisNames[id - 1] else R.string.debris_unknown),
                            color = if (known) rarity.color else AppColors.TextMuted, fontSize = 12.sp,
                            lineHeight = 16.sp, minLines = 3, maxLines = 3)
                        Text(rarityLabel(rarity), color = rarity.color, fontSize = 11.sp,
                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            }
        }
    }
}
