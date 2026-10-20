package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.R
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.CosmicButtonStyle

private data class TutorialStep(val title: Int, val body: Int, val image: Int)

@Composable
fun TutorialDialog(onFinish: () -> Unit) {
    val steps = remember {
        listOf(
            TutorialStep(R.string.tutorial_collect_title, R.string.tutorial_collect_body, R.drawable.flat_planet_1),
            TutorialStep(R.string.tutorial_drones_title, R.string.tutorial_drones_body, R.drawable.toon_drone_12_v3),
            TutorialStep(R.string.tutorial_upgrade_title, R.string.tutorial_upgrade_body, R.drawable.flat_upgrade_magnet),
            TutorialStep(R.string.tutorial_explore_title, R.string.tutorial_explore_body, R.drawable.flat_nav_route)
        )
    }
    var stepIndex by rememberSaveable { mutableIntStateOf(0) }
    val step = steps[stepIndex.coerceIn(steps.indices)]
    Dialog(onDismissRequest = onFinish) {
        BackHandler {
            if (stepIndex > 0) stepIndex-- else onFinish()
        }
        Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFF18283F),
            modifier = Modifier.fillMaxWidth().testTag("tutorial_dialog")) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.tutorial_title), style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF93C9FF))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(steps.size) { index ->
                        Box(Modifier.size(8.dp).background(
                            if (index == stepIndex) Color(0xFF83E6EA) else Color(0xFF40516C), CircleShape))
                    }
                }
                Image(painterResource(step.image), null, Modifier.size(132.dp))
                Text(stringResource(step.title), style = MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.testTag("tutorial_step_$stepIndex"))
                Text(stringResource(step.body), style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCEDBED), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Button(onClick = {
                    if (stepIndex < steps.lastIndex) stepIndex++ else onFinish()
                }, modifier = Modifier.fillMaxWidth().testTag("tutorial_next"), generatedArtwork = false) {
                    Text(stringResource(if (stepIndex == steps.lastIndex) R.string.tutorial_finish else R.string.tutorial_next))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { stepIndex-- }, enabled = stepIndex > 0,
                        modifier = Modifier.weight(1f).testTag("tutorial_back"), style = CosmicButtonStyle.Secondary) {
                        Text(stringResource(R.string.tutorial_back))
                    }
                    Button(onClick = onFinish, modifier = Modifier.weight(1f).testTag("tutorial_skip"),
                        style = CosmicButtonStyle.Secondary) {
                        Text(stringResource(R.string.tutorial_skip))
                    }
                }
            }
        }
    }
}
