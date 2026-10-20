package com.example.myapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.example.myapplication.FleetConfig
import com.example.myapplication.R

@Composable
internal fun localizedDroneName(drone: FleetConfig): String {
    val index = drone.id.removePrefix("drone_").toIntOrNull()?.minus(1) ?: -1
    return stringArrayResource(R.array.drone_display_names).getOrElse(index) { drone.name }
}
