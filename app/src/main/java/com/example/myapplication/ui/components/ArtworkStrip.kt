package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/** Reuses actual game sprites so promotional art stays consistent with the collection. */
@Composable
fun ArtworkStrip(drawables: List<Int>, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        drawables.forEach { drawable ->
            Image(painterResource(drawable), null, Modifier.weight(1f).fillMaxHeight(), contentScale = ContentScale.Fit)
        }
    }
}
