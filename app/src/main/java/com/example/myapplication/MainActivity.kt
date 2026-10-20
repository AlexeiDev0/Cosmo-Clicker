package com.example.myapplication

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.example.myapplication.ui.GameScreen
import com.example.myapplication.ui.PrivacyScreen
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private var rewardedAds: RewardedAdsController? = null
    override fun attachBaseContext(newBase: Context) {
        val language = newBase.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .getString(LANGUAGE_KEY, null)
        if (language == null) {
            super.attachBaseContext(newBase)
            return
        }

        val configuration = Configuration(newBase.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(language))
        }
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val privacyConsent = PrivacyConsent(this)
        val ads = RewardedAdsController(this)
        rewardedAds = ads
        setContent {
            var privacyAccepted by remember { mutableStateOf(privacyConsent.isAccepted()) }
            var privacyBusy by remember { mutableStateOf(false) }
            var privacyFailed by remember { mutableStateOf(false) }
            val privacyScope = rememberCoroutineScope()
            var selectedLanguage by remember { mutableStateOf(getSelectedLanguage()) }
            var soundEnabled by remember { mutableStateOf(getBooleanSetting(SOUND_KEY, true)) }
            var reducedMotion by remember { mutableStateOf(getBooleanSetting(REDUCED_MOTION_KEY, false)) }
            val deviceConfiguration = LocalConfiguration.current
            val localizedConfiguration = remember(selectedLanguage, deviceConfiguration) {
                Configuration(resources.configuration).apply {
                    setLocale(selectedLanguage?.let(Locale::forLanguageTag)
                        ?: android.content.res.Resources.getSystem().configuration.locales[0])
                }
            }
            val localizedContext = remember(localizedConfiguration) {
                createConfigurationContext(localizedConfiguration)
            }
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfiguration,
                LocalResources provides localizedContext.resources
            ) {
                MyApplicationTheme(reducedMotion = reducedMotion) {
                    if (!privacyAccepted) {
                        PrivacyScreen(
                            onAccept = {
                                privacyBusy = true
                                privacyScope.launch {
                                    privacyAccepted = privacyConsent.accept()
                                    privacyFailed = !privacyAccepted
                                    privacyBusy = false
                                }
                            },
                            onDecline = { finish() },
                            busy = privacyBusy,
                            failed = privacyFailed
                        )
                    } else {
                    GameScreen(
                        rewardedAds = ads,
                        onPrivacyWithdraw = {
                            privacyScope.launch {
                                if (privacyConsent.withdraw()) {
                                    ads.stopRequests()
                                    privacyAccepted = false
                                    privacyFailed = false
                                } else {
                                    Toast.makeText(localizedContext,
                                        localizedContext.getString(R.string.privacy_save_error),
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        selectedLanguage = selectedLanguage,
                        onLanguageSelected = { language ->
                            saveSelectedLanguage(language)
                            selectedLanguage = language
                        },
                        soundEnabled = soundEnabled,
                        onSoundEnabledChanged = { enabled ->
                            saveBooleanSetting(SOUND_KEY, enabled)
                            soundEnabled = enabled
                        },
                        reducedMotion = reducedMotion,
                        onReducedMotionChanged = { enabled ->
                            saveBooleanSetting(REDUCED_MOTION_KEY, enabled)
                            reducedMotion = enabled
                        }
                    )
                    }
                }
            }
        }
    }

    private fun getSelectedLanguage(): String? =
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getString(LANGUAGE_KEY, null)

    private fun saveSelectedLanguage(language: String?) {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .edit {
                if (language == null) remove(LANGUAGE_KEY) else putString(LANGUAGE_KEY, language)
            }
    }

    override fun onDestroy() {
        rewardedAds?.close()
        rewardedAds = null
        super.onDestroy()
    }

    private fun getBooleanSetting(key: String, defaultValue: Boolean): Boolean =
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getBoolean(key, defaultValue)

    private fun saveBooleanSetting(key: String, value: Boolean) {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit { putBoolean(key, value) }
    }

    private companion object {
        const val PREFERENCES_NAME = "settings"
        const val LANGUAGE_KEY = "language"
        const val SOUND_KEY = "sound_enabled"
        const val REDUCED_MOTION_KEY = "reduced_motion"
    }
}
