package com.example.myapplication.ui.components

import com.example.myapplication.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ColorfulArtworkMappingTest {
    @Test
    fun allNineteenUpgradesUseTheSameDistinctArtworkInAtlasAndStandaloneViews() {
        val legacy = listOf(
            R.drawable.upgrade_magnet_v2, R.drawable.upgrade_weld_torch_v2,
            R.drawable.upgrade_quantum_wrench_v2, R.drawable.upgrade_debris_harvester_v2,
            R.drawable.upgrade_signal_beacon_v2, R.drawable.upgrade_quantum_amplifier_v2,
            R.drawable.upgrade_neural_matrix_v2, R.drawable.upgrade_void_compressor_v2,
            R.drawable.upgrade_singularity_tap_v2, R.drawable.upgrade_antimatter_lens_v1,
            R.drawable.upgrade_pulsar_battery_v1, R.drawable.upgrade_graviton_press_v1,
            R.drawable.upgrade_nanite_swarm_v1, R.drawable.upgrade_dark_matter_forge_v1,
            R.drawable.upgrade_temporal_relay_v1, R.drawable.upgrade_stellar_resonator_v1,
            R.drawable.upgrade_entropy_engine_v1, R.drawable.upgrade_reality_anchor_v1,
            R.drawable.upgrade_omega_core_v1
        )
        legacy.forEachIndexed { index, resource ->
            val sheet = if (index < 9) R.drawable.shop_upgrades_minimal_sheet_v1
                else R.drawable.shop_upgrades_expansion_sheet_v1
            val cell = if (index < 9) index else index - 9
            assertEquals(flatArtworkResource(resource), generatedUiIcon(sheet, cell))
            assertNotEquals(resource, flatArtworkResource(resource))
        }
        assertEquals(19, legacy.map(::flatArtworkResource).toSet().size)
    }

    @Test
    fun allScreenBackgroundsResolveToDistinctReplacementResources() {
        val backgrounds = listOf(
            R.drawable.background_space_main_v4, R.drawable.background_space_start_v4,
            R.drawable.bg_shop_salvage_market_v3, R.drawable.bg_hangar_command_v3,
            R.drawable.bg_goals_starchart_v1, R.drawable.bg_achievements_archive_v1,
            R.drawable.bg_statistics_observatory_v3, R.drawable.bg_case_vault_v3,
            R.drawable.bg_event_operations_v3, R.drawable.bg_prestige_core_v3,
            R.drawable.bg_offline_reward_space_v1, R.drawable.bg_settings_control_v3
        )
        backgrounds.forEach { assertNotEquals(it, flatArtworkResource(it)) }
        assertEquals(12, backgrounds.map(::flatArtworkResource).toSet().size)
    }

    @Test
    fun uiReplacementClampsInvalidCellsAndLeavesEventAnimationSheetsIntact() {
        val sheet = R.drawable.shop_ui_minimal_sheet_v1
        assertEquals(generatedUiIcon(sheet, 0), generatedUiIcon(sheet, -100))
        assertEquals(generatedUiIcon(sheet, 15), generatedUiIcon(sheet, 100))
        assertNull(generatedUiIcon(R.drawable.event_pirate_ship_sheet_v1, 0))
    }
}
