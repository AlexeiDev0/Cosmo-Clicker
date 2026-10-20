package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.myapplication.ui.components.cosmicIconPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration
import com.example.myapplication.R

@Composable
fun PrivacyScreen(onAccept: () -> Unit, onDecline: () -> Unit, busy: Boolean, failed: Boolean) {
    var showFullPolicy by remember { mutableStateOf(false) }
    BackHandler { if (!busy) onDecline() }
    Surface(Modifier.fillMaxSize().testTag("privacy_gate")) {
        Box(Modifier.fillMaxSize()) {
            Image(cosmicIconPainter(R.drawable.background_cosmic_v5), null,
                Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Color(0xFF10192E).copy(alpha = .82f)))
            Card(Modifier.align(Alignment.Center).safeDrawingPadding().padding(24.dp).widthIn(max = 480.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xF015243C))) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.privacy_short_title), style = MaterialTheme.typography.headlineSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                PrivacyPolicyLink(onClick = { showFullPolicy = true })
                if (failed) Text(stringResource(R.string.privacy_save_error), color = MaterialTheme.colorScheme.error)
                Button(onClick = onAccept, enabled = !busy,
                    modifier = Modifier.fillMaxWidth().testTag("privacy_accept")) {
                    Text(stringResource(R.string.privacy_short_accept))
                }
                OutlinedButton(onClick = onDecline, enabled = !busy,
                    modifier = Modifier.fillMaxWidth().testTag("privacy_decline")) {
                    Text(stringResource(R.string.privacy_short_decline))
                }
            }
            }
        }
    }
    if (showFullPolicy) PrivacyPolicyDialog(onDismiss = { showFullPolicy = false })
}

@Composable
fun PrivacyPolicyLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier.testTag("privacy_policy_link")) {
        Text(stringResource(R.string.privacy_title), color = Color(0xFF62ABFF),
            textDecoration = TextDecoration.Underline)
    }
}

@Composable
private fun PrivacyPolicyContent() {
    val uriHandler = LocalUriHandler.current
    Text(stringResource(R.string.privacy_policy_body), style = MaterialTheme.typography.bodyMedium)
    TextButton(onClick = { uriHandler.openUri("mailto:alex.fitlin@gmail.com") }) {
        Text("alex.fitlin@gmail.com", color = Color(0xFF62ABFF))
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit, onWithdraw: (() -> Unit)? = null) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.privacy_title)) },
        text = { Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
            PrivacyPolicyContent()
            if (onWithdraw != null) Text(stringResource(R.string.privacy_withdraw_description), Modifier.padding(top = 16.dp))
        } },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } },
        dismissButton = { if (onWithdraw != null) TextButton(onClick = onWithdraw) { Text(stringResource(R.string.privacy_withdraw)) } }
    )
}
