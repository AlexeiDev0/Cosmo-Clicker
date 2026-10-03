package com.example.myapplication

import android.content.Context
import android.provider.Settings

/** Saves only cooldown metadata, never coordinates or tap history. Excluded from backups. */
internal class AutoClickBlockRepository(context: Context) {
    private val prefs = context.getSharedPreferences("input_guard", Context.MODE_PRIVATE)
    private val bootCount = try {
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (_: SecurityException) { -1 }

    fun load(now: Long): AutoClickDetector.Snapshot {
        if (bootCount < 0 || prefs.getInt("boot", -1) != bootCount) return AutoClickDetector.Snapshot()
        val last = prefs.getLong("last", -1L)
        if (last !in 0L..now || now - last >= 300_000L) return AutoClickDetector.Snapshot()
        return AutoClickDetector.Snapshot(
            prefs.getLong("until", 0L).coerceIn(0L, now + AutoClickDetector.MAX_BLOCK_MILLIS),
            prefs.getInt("strikes", 0).coerceIn(0, 4), last
        )
    }

    /** Call on IO; serialized so an older save cannot replace a newer detection. */
    @Synchronized
    fun save(snapshot: AutoClickDetector.Snapshot): Boolean {
        if (prefs.getLong("last", -1L) > snapshot.lastDetectionAt && prefs.getInt("boot", -1) == bootCount) return true
        return prefs.edit().putInt("boot", bootCount).putLong("until", snapshot.blockedUntil)
            .putLong("last", snapshot.lastDetectionAt).putInt("strikes", snapshot.strikes).commit()
    }
}
