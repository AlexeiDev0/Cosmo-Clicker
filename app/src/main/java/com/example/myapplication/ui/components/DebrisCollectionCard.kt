package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    R.string.debris_name_28
)

@Composable
internal fun DebrisCollectionCard(state: GameState) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val discovered = state.discoveredDebrisIds
    val nextGoal = listOf(10, 15, 20, 28).firstOrNull { discovered.size < it }
    Column(Modifier.fillMaxWidth().background(Color(0xCC101E2E), RoundedCornerShape(16.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.debris_collection), color = Color.White, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.debris_collection_count, discovered.size, 28), color = AppColors.Secondary, fontSize = 12.sp)
        LinearProgressIndicator(progress = { discovered.size / 28f }, modifier = Modifier.fillMaxWidth(),
            color = AppColors.Primary, trackColor = Color.White.copy(alpha = .08f))
        Text(if (nextGoal == null) stringResource(R.string.debris_collection_complete)
            else stringResource(R.string.debris_collection_goal, nextGoal), color = AppColors.TextMuted, fontSize = 11.sp)
        Button(onClick = { expanded = !expanded }, style = CosmicButtonStyle.Secondary) {
            Text(stringResource(if (expanded) R.string.debris_collection_hide else R.string.debris_collection_show))
        }
        if (expanded) (1..28).toList().chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { id ->
                    val known = id in discovered
                    Column(Modifier.weight(1f).background(Color.White.copy(alpha = .04f), RoundedCornerShape(10.dp)).padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(cosmicIconPainter(if (known) debrisDrawable(id) else R.drawable.ic_space_lock),
                            contentDescription = null, modifier = Modifier.size(48.dp))
                        Text(stringResource(if (known) debrisNames[id - 1] else R.string.debris_unknown),
                            color = if (known) Color.White else AppColors.TextMuted, fontSize = 9.sp,
                            lineHeight = 11.sp, minLines = 3, maxLines = 3)
                    }
                }
            }
        }
    }
}
