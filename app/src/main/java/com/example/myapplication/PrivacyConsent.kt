package com.example.myapplication

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Consent is local, versioned, and only granted after a successful disk write. */
class PrivacyConsent(context: Context) {
    private val preferences = context.getSharedPreferences("privacy_consent", Context.MODE_PRIVATE)

    fun isAccepted(): Boolean = preferences.getInt("policy_version", 0) == POLICY_VERSION &&
        preferences.getLong("accepted_at", 0L) > 0L

    suspend fun accept(): Boolean = withContext(Dispatchers.IO) {
        val saved = preferences.edit().putInt("policy_version", POLICY_VERSION)
            .putLong("accepted_at", System.currentTimeMillis()).commit()
        // SharedPreferences updates its memory even when commit fails.
        if (!saved) preferences.edit().clear().apply()
        saved
    }

    suspend fun withdraw(): Boolean = withContext(Dispatchers.IO) {
        val previousVersion = preferences.getInt("policy_version", 0)
        val previousDate = preferences.getLong("accepted_at", 0L)
        val saved = preferences.edit().clear().commit()
        if (!saved) preferences.edit().putInt("policy_version", previousVersion)
            .putLong("accepted_at", previousDate).apply()
        saved
    }

    companion object { const val POLICY_VERSION = 3 }
}
