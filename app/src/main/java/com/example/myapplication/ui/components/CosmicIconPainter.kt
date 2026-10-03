package com.example.myapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

/** Shared artwork for scene, collections and rewards; legacy IDs remain stable. */
@Composable
fun cosmicIconPainter(id: Int): Painter {
    val resource = flatArtworkResource(id)
    paintedBackgroundPainter(resource)?.let { return it }
    refinedControlResource(resource)?.let { return painterResource(it) }
    shipArtworkPainter(id)?.let { return it }
    return painterResource(resource)
}

/** Small controls use full-size vectors for consistent weight and crisp edges. */
private fun refinedControlResource(id: Int): Int? = when (id) {
    R.drawable.flat_nav_shop, R.drawable.flat_icon_shop -> R.drawable.ui_refined_shop
    R.drawable.flat_nav_hangar, R.drawable.flat_icon_hangar -> R.drawable.ui_refined_hangar
    R.drawable.flat_nav_quests, R.drawable.flat_icon_quests -> R.drawable.ui_refined_quests
    R.drawable.flat_nav_stats, R.drawable.flat_icon_stats -> R.drawable.ui_refined_stats
    R.drawable.flat_nav_settings, R.drawable.flat_icon_settings -> R.drawable.ui_refined_settings
    R.drawable.flat_nav_prestige, R.drawable.flat_icon_prestige -> R.drawable.ui_refined_prestige
    R.drawable.flat_nav_achievements, R.drawable.flat_icon_achievements -> R.drawable.ui_refined_achievements
    R.drawable.flat_nav_route, R.drawable.flat_icon_route -> R.drawable.ui_refined_route
    R.drawable.ui_close_simple, R.drawable.flat_icon_close -> R.drawable.ui_refined_close
    R.drawable.ui_lock_simple, R.drawable.flat_icon_lock -> R.drawable.ui_refined_lock
    R.drawable.ui_reset_simple, R.drawable.flat_icon_reset -> R.drawable.ui_refined_reset
    R.drawable.ui_sound_simple, R.drawable.icon_settings_sound_v2, R.drawable.flat_icon_sound -> R.drawable.ui_refined_sound
    R.drawable.ui_language_simple, R.drawable.flat_icon_language -> R.drawable.ui_refined_language
    R.drawable.ui_motion_simple, R.drawable.flat_icon_motion -> R.drawable.ui_refined_motion
    else -> null
}

internal fun flatArtworkResource(id: Int): Int = when (id) {
        R.drawable.drone_01_v2 -> R.drawable.flat_mid_drone_1
        R.drawable.drone_02_v2 -> R.drawable.flat_mid_drone_2
        R.drawable.drone_03_v2 -> R.drawable.flat_mid_drone_3
        R.drawable.drone_04_v2 -> R.drawable.flat_mid_drone_4
        R.drawable.drone_05_v2 -> R.drawable.flat_mid_drone_5
        R.drawable.drone_06_v2 -> R.drawable.flat_mid_drone_6
        R.drawable.drone_07_v2 -> R.drawable.flat_mid_drone_7
        R.drawable.drone_08_v2 -> R.drawable.flat_mid_drone_8
        R.drawable.drone_09_v2 -> R.drawable.flat_mid_drone_9
        R.drawable.drone_10_v2 -> R.drawable.flat_mid_drone_10
        R.drawable.drone_11_v2 -> R.drawable.flat_mid_drone_11
        R.drawable.drone_12_v2 -> R.drawable.flat_mid_drone_12
        R.drawable.drone_13_v2 -> R.drawable.flat_mid_drone_13
        R.drawable.drone_14_v2 -> R.drawable.flat_mid_drone_14
        R.drawable.drone_15_v2 -> R.drawable.flat_mid_drone_15
        R.drawable.drone_16_v2 -> R.drawable.flat_mid_drone_16
        R.drawable.drone_17_v2 -> R.drawable.flat_mid_drone_17
        R.drawable.drone_18_v2 -> R.drawable.flat_mid_drone_18
        R.drawable.drone_19_v2 -> R.drawable.flat_mid_drone_19
        R.drawable.drone_20_v2 -> R.drawable.flat_mid_drone_20
        R.drawable.drone_21_v2 -> R.drawable.flat_mid_drone_21
        R.drawable.drone_22_v2 -> R.drawable.flat_mid_drone_22
        R.drawable.drone_23_v2 -> R.drawable.flat_mid_drone_23
        R.drawable.drone_24_v2 -> R.drawable.flat_mid_drone_24
        R.drawable.drone_25_v2 -> R.drawable.flat_mid_drone_25
        R.drawable.drone_26_v2 -> R.drawable.flat_mid_drone_26
        R.drawable.drone_27_v2 -> R.drawable.flat_mid_drone_27
        R.drawable.drone_28_v2 -> R.drawable.flat_mid_drone_28
        R.drawable.drone_29_v2 -> R.drawable.flat_mid_drone_29
        R.drawable.case_common_1 -> R.drawable.flat_mid_case_common_1
        R.drawable.case_common_2 -> R.drawable.flat_mid_case_common_2
        R.drawable.case_common_3 -> R.drawable.flat_mid_case_common_3
        R.drawable.case_common_4 -> R.drawable.flat_mid_case_common_4
        R.drawable.case_common_5 -> R.drawable.flat_mid_case_common_5
        R.drawable.case_common_6 -> R.drawable.flat_mid_case_common_6
        R.drawable.case_common_7 -> R.drawable.flat_mid_case_common_7
        R.drawable.case_common_8 -> R.drawable.flat_mid_case_common_8
        R.drawable.case_rare_1 -> R.drawable.flat_mid_case_rare_1
        R.drawable.case_rare_2 -> R.drawable.flat_mid_case_rare_2
        R.drawable.case_rare_3 -> R.drawable.flat_mid_case_rare_3
        R.drawable.case_rare_4 -> R.drawable.flat_mid_case_rare_4
        R.drawable.case_rare_5 -> R.drawable.flat_mid_case_rare_5
        R.drawable.case_rare_6 -> R.drawable.flat_mid_case_rare_6
        R.drawable.case_rare_7 -> R.drawable.flat_mid_case_rare_7
        R.drawable.case_rare_8 -> R.drawable.flat_mid_case_rare_8
        R.drawable.case_legendary_1 -> R.drawable.flat_mid_case_legendary_1
        R.drawable.case_legendary_2 -> R.drawable.flat_mid_case_legendary_2
        R.drawable.case_legendary_3 -> R.drawable.flat_mid_case_legendary_3
        R.drawable.case_legendary_4 -> R.drawable.flat_mid_case_legendary_4
        R.drawable.case_legendary_5 -> R.drawable.flat_mid_case_legendary_5
        R.drawable.case_legendary_6 -> R.drawable.flat_mid_case_legendary_6
        R.drawable.case_legendary_7 -> R.drawable.flat_mid_case_legendary_7
        R.drawable.case_legendary_8 -> R.drawable.flat_mid_case_legendary_8
        R.drawable.planet_1_v2, R.drawable.flat_planet_1 -> R.drawable.planet_1_painted_v3
        R.drawable.planet_2_v2, R.drawable.flat_planet_2 -> R.drawable.planet_2_painted_v3
        R.drawable.planet_3_v2, R.drawable.flat_planet_3 -> R.drawable.planet_3_painted_v3
        R.drawable.planet_4_v2, R.drawable.flat_planet_4 -> R.drawable.planet_4_painted_v3
        R.drawable.planet_5_v2, R.drawable.flat_planet_5 -> R.drawable.planet_5_painted_v3
        R.drawable.planet_6_v2, R.drawable.flat_planet_6 -> R.drawable.planet_6_painted_v3
        R.drawable.planet_7_v2, R.drawable.flat_planet_7 -> R.drawable.planet_7_painted_v3
        R.drawable.planet_8_v2, R.drawable.flat_planet_8 -> R.drawable.planet_8_painted_v3
        R.drawable.planet_9_v2, R.drawable.flat_planet_9 -> R.drawable.planet_9_painted_v3
        R.drawable.planet_10_v2, R.drawable.flat_planet_10 -> R.drawable.planet_10_painted_v3
        R.drawable.planet_11_v2, R.drawable.flat_planet_11 -> R.drawable.planet_11_painted_v3
        R.drawable.planet_12_v2, R.drawable.flat_planet_12 -> R.drawable.planet_12_painted_v3
        R.drawable.planet_13_v2, R.drawable.flat_planet_13 -> R.drawable.planet_13_painted_v3
        R.drawable.planet_14_v2, R.drawable.flat_planet_14 -> R.drawable.planet_14_painted_v3
        R.drawable.planet_15_v2, R.drawable.flat_planet_15 -> R.drawable.planet_15_painted_v3
        R.drawable.planet_16_v2, R.drawable.flat_planet_16 -> R.drawable.planet_16_painted_v3
        R.drawable.planet_17_v2, R.drawable.flat_planet_17 -> R.drawable.planet_17_painted_v3
        R.drawable.planet_18_v2, R.drawable.flat_planet_18 -> R.drawable.planet_18_painted_v3
        R.drawable.planet_19_v2, R.drawable.flat_planet_19 -> R.drawable.planet_19_painted_v3
        R.drawable.planet_20_v2, R.drawable.flat_planet_20 -> R.drawable.planet_20_painted_v3
        R.drawable.planet_21_v2, R.drawable.flat_planet_21 -> R.drawable.planet_21_painted_v3
        R.drawable.planet_22_v2, R.drawable.flat_planet_22 -> R.drawable.planet_22_painted_v3
        R.drawable.planet_23_v2, R.drawable.flat_planet_23 -> R.drawable.planet_23_painted_v3
        R.drawable.planet_24_v2, R.drawable.flat_planet_24 -> R.drawable.planet_24_painted_v3
        R.drawable.planet_25_v2, R.drawable.flat_planet_25 -> R.drawable.planet_25_painted_v3
        R.drawable.planet_26_v2, R.drawable.flat_planet_26 -> R.drawable.planet_26_painted_v3
        R.drawable.planet_27_v2, R.drawable.flat_planet_27 -> R.drawable.planet_27_painted_v3
        R.drawable.planet_28_v2, R.drawable.flat_planet_28 -> R.drawable.planet_28_painted_v3
        R.drawable.planet_29_v2, R.drawable.flat_planet_29 -> R.drawable.planet_29_painted_v3
        R.drawable.planet_30_v2, R.drawable.flat_planet_30 -> R.drawable.planet_30_painted_v3
        R.drawable.planet_31_v2, R.drawable.flat_planet_31 -> R.drawable.planet_31_painted_v3
        R.drawable.planet_32_v2, R.drawable.flat_planet_32 -> R.drawable.planet_32_painted_v3
        R.drawable.planet_33_v2, R.drawable.flat_planet_33 -> R.drawable.planet_33_painted_v3
        R.drawable.planet_34_v2, R.drawable.flat_planet_34 -> R.drawable.planet_34_painted_v3
        R.drawable.planet_35_v2, R.drawable.flat_planet_35 -> R.drawable.planet_35_painted_v3
        R.drawable.planet_36_v2, R.drawable.flat_planet_36 -> R.drawable.planet_36_painted_v3
        R.drawable.planet_37_v2, R.drawable.flat_planet_37 -> R.drawable.planet_37_painted_v3
        R.drawable.planet_38_v2, R.drawable.flat_planet_38 -> R.drawable.planet_38_painted_v3
        R.drawable.planet_39_v2, R.drawable.flat_planet_39 -> R.drawable.planet_39_painted_v3
        R.drawable.background_space_main_v4 -> R.drawable.flat_bg_main
        R.drawable.background_space_start_v4 -> R.drawable.flat_bg_start
        R.drawable.bg_shop_salvage_market_v3 -> R.drawable.flat_bg_shop
        R.drawable.bg_hangar_command_v3 -> R.drawable.flat_bg_hangar
        R.drawable.bg_goals_starchart_v1 -> R.drawable.flat_bg_goals
        R.drawable.bg_achievements_archive_v1 -> R.drawable.flat_bg_achievements
        R.drawable.bg_statistics_observatory_v3 -> R.drawable.flat_bg_statistics
        R.drawable.bg_case_vault_v3 -> R.drawable.flat_bg_cases
        R.drawable.bg_event_operations_v3 -> R.drawable.flat_bg_events
        R.drawable.bg_prestige_core_v3 -> R.drawable.flat_bg_prestige
        R.drawable.bg_offline_reward_space_v1 -> R.drawable.flat_bg_offline
        R.drawable.bg_settings_control_v3 -> R.drawable.flat_bg_settings
        R.drawable.event_black_hole_minimal_v3 -> R.drawable.flat_event_black_hole
        R.drawable.event_meteor_minimal_v3 -> R.drawable.flat_event_meteor
        R.drawable.event_trade_minimal_v3 -> R.drawable.flat_event_trade
        R.drawable.event_solar_minimal_v3 -> R.drawable.flat_event_solar
        R.drawable.event_cyber_minimal_v3 -> R.drawable.flat_event_cyber
        R.drawable.event_pirate_minimal_v3 -> R.drawable.flat_event_pirate
        R.drawable.event_station_minimal_v3 -> R.drawable.flat_event_station
        R.drawable.event_distress_minimal_v3 -> R.drawable.flat_event_distress
        R.drawable.event_storm_minimal_v3 -> R.drawable.flat_event_storm
        R.drawable.bg_hangar_fleet_command_v1 -> R.drawable.flat_bg_hangar
        R.drawable.bg_hangar_minimal_v1 -> R.drawable.flat_bg_hangar
        R.drawable.hangar_background -> R.drawable.flat_bg_hangar
        R.drawable.bg_shop_orbital_market_v1 -> R.drawable.flat_bg_shop
        R.drawable.shop_command_header -> R.drawable.flat_bg_shop
        R.drawable.shop_upgrade_showcase_v1 -> R.drawable.flat_bg_shop
        R.drawable.bg_statistics_observatory_v1 -> R.drawable.flat_bg_statistics
        R.drawable.bg_settings_space_v1 -> R.drawable.flat_bg_settings
        R.drawable.settings_header -> R.drawable.flat_bg_settings
        R.drawable.bg_prestige_shop_minimal_v2 -> R.drawable.flat_bg_prestige
        R.drawable.bg_case_vault_v1 -> R.drawable.flat_bg_cases
        R.drawable.bg_events_minimal_v2 -> R.drawable.flat_bg_events
        R.drawable.event_distress_background_v2 -> R.drawable.flat_event_distress
        R.drawable.event_reactor_core -> R.drawable.flat_event_station
        R.drawable.drone_20 -> R.drawable.flat_mid_drone_20
        R.drawable.drone_29 -> R.drawable.flat_mid_drone_29
        R.drawable.cargo_crate_space_v2 -> R.drawable.flat_mid_case_common_1
        R.drawable.offline_drone_reward_v1 -> R.drawable.flat_mid_drone_22
        R.drawable.drone_fleet_showcase_v1 -> R.drawable.flat_drone_29
        R.drawable.drone_collection_art_v2 -> R.drawable.flat_drone_29
        R.drawable.case_tier_showcase_v1 -> R.drawable.flat_case_legendary_1
        R.drawable.case_tier_showcase_v2 -> R.drawable.flat_case_legendary_1
        R.drawable.icon_game -> R.drawable.flat_icon_planet
        R.drawable.ui_new_badge_v2 -> R.drawable.flat_icon_new
        R.drawable.ui_autoclick_warning_v2 -> R.drawable.flat_icon_warning
        R.drawable.icon_prestige_galaxy_core_v2 -> R.drawable.flat_planet_39
        R.drawable.icon_prestige_minimal_v3 -> R.drawable.flat_planet_39
        R.drawable.ic_currency_debris_v2 -> R.drawable.ic_debris_minimal
        R.drawable.ic_drone_energy_cell_v2 -> R.drawable.ic_drone_income_minimal
        R.drawable.ic_prestige_hologram_v2 -> R.drawable.ic_prestige_core
        R.drawable.ic_nav_shop_minimal -> R.drawable.flat_nav_shop
        R.drawable.ic_nav_hangar_minimal -> R.drawable.flat_nav_hangar
        R.drawable.ic_nav_quests_minimal -> R.drawable.flat_nav_quests
        R.drawable.ic_nav_stats_minimal -> R.drawable.flat_nav_stats
        R.drawable.ic_nav_settings_minimal -> R.drawable.flat_nav_settings
        R.drawable.ic_nav_prestige_minimal -> R.drawable.flat_nav_prestige
        R.drawable.ic_achievement_medal -> R.drawable.flat_nav_achievements
        R.drawable.upgrade_magnet_v2 -> R.drawable.flat_icon_magnet
        R.drawable.upgrade_weld_torch_v2 -> R.drawable.flat_icon_torch
        R.drawable.upgrade_quantum_wrench_v2 -> R.drawable.flat_icon_repair
        R.drawable.upgrade_debris_harvester_v2 -> R.drawable.flat_icon_harvester
        R.drawable.upgrade_signal_beacon_v2 -> R.drawable.flat_icon_beacon
        R.drawable.upgrade_quantum_amplifier_v2 -> R.drawable.flat_icon_amplifier
        R.drawable.upgrade_neural_matrix_v2 -> R.drawable.flat_icon_ai
        R.drawable.upgrade_void_compressor_v2 -> R.drawable.flat_icon_compressor
        R.drawable.upgrade_singularity_tap_v2 -> R.drawable.flat_icon_singularity
        R.drawable.upgrade_antimatter_lens_v1 -> R.drawable.flat_icon_lens
        R.drawable.upgrade_pulsar_battery_v1 -> R.drawable.flat_icon_pulsar
        R.drawable.upgrade_graviton_press_v1 -> R.drawable.flat_icon_press
        R.drawable.upgrade_nanite_swarm_v1 -> R.drawable.flat_icon_nanites
        R.drawable.upgrade_dark_matter_forge_v1 -> R.drawable.flat_icon_forge
        R.drawable.upgrade_temporal_relay_v1 -> R.drawable.flat_icon_relay
        R.drawable.upgrade_stellar_resonator_v1 -> R.drawable.flat_icon_resonator
        R.drawable.upgrade_entropy_engine_v1 -> R.drawable.flat_icon_entropy
        R.drawable.upgrade_reality_anchor_v1 -> R.drawable.flat_icon_anchor
        R.drawable.upgrade_omega_core_v1 -> R.drawable.flat_icon_omega
        R.drawable.upgrade_flight_slots_v2 -> R.drawable.flat_icon_fleet
        R.drawable.upgrade_spawn_speed_v2 -> R.drawable.flat_icon_speed
        R.drawable.debris_01_v2 -> R.drawable.flat_debris_shard
        R.drawable.debris_02_v2 -> R.drawable.flat_debris_crystal
        R.drawable.debris_03_v2 -> R.drawable.flat_debris_ring
        R.drawable.debris_04_v2 -> R.drawable.flat_debris_cell
        R.drawable.debris_05_v2 -> R.drawable.flat_debris_orbit
        R.drawable.debris_06_v2 -> R.drawable.flat_debris_bolt
        R.drawable.debris_07_v2 -> R.drawable.flat_debris_shard
        R.drawable.debris_08_v2 -> R.drawable.flat_debris_crystal
        R.drawable.debris_09_v2 -> R.drawable.flat_debris_ring
        R.drawable.debris_10_v2 -> R.drawable.flat_debris_cell
        R.drawable.debris_11_v2 -> R.drawable.flat_debris_orbit
        R.drawable.debris_12_v2 -> R.drawable.flat_debris_bolt
        R.drawable.debris_13_v2 -> R.drawable.flat_debris_shard
        R.drawable.debris_14_v2 -> R.drawable.flat_debris_crystal
        R.drawable.debris_15_v2 -> R.drawable.flat_debris_ring
        R.drawable.debris_16_v2 -> R.drawable.flat_debris_cell
        R.drawable.debris_17_v2 -> R.drawable.flat_debris_orbit
        R.drawable.debris_18_v2 -> R.drawable.flat_debris_bolt
        R.drawable.debris_19_v2 -> R.drawable.flat_debris_shard
        R.drawable.debris_20_v2 -> R.drawable.flat_debris_crystal
        R.drawable.debris_21_v2 -> R.drawable.flat_debris_ring
        R.drawable.debris_22_v2 -> R.drawable.flat_debris_cell
        R.drawable.debris_23_v2 -> R.drawable.flat_debris_orbit
        R.drawable.debris_24_v2 -> R.drawable.flat_debris_bolt
        R.drawable.debris_25_v2 -> R.drawable.flat_debris_shard
        R.drawable.debris_26_v2 -> R.drawable.flat_debris_crystal
        R.drawable.debris_27_v2 -> R.drawable.flat_debris_ring
        R.drawable.debris_28_v2 -> R.drawable.flat_debris_cell
        else -> id
}
