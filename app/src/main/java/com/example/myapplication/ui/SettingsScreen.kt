package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.LocalButtonSound
import com.example.myapplication.ui.components.CosmicButtonStyle
import com.example.myapplication.ui.components.CosmicButtonState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import com.example.myapplication.ui.components.cosmicIconPainter as painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.ui.theme.SpaceDesign
import com.example.myapplication.ui.components.SpaceDialog
import com.example.myapplication.ui.components.shipActionPainter

private data class LanguageOption(val tag: String?, val label: Int)

@Composable
fun SettingsScreen(
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    soundEnabled: Boolean,
    onSoundEnabledChanged: (Boolean) -> Unit,
    reducedMotion: Boolean,
    onReducedMotionChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onPrivacyWithdraw: () -> Unit = {},
    adPrivacyRequired: Boolean = false,
    onAdPrivacy: () -> Unit = {},
    adRequestsAllowed: Boolean = false,
    onStopAdRequests: () -> Unit = {}
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showMoreLanguages by remember { mutableStateOf(false) }
    val playButtonSound = LocalButtonSound.current
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val languages = listOf(
        LanguageOption(null, R.string.language_system),
        LanguageOption("en", R.string.language_english),
        LanguageOption("ru", R.string.language_russian),
        LanguageOption("es", R.string.language_spanish),
        LanguageOption("de", R.string.language_german),
        LanguageOption("fr", R.string.language_french),
        LanguageOption("pt", R.string.language_portuguese),
        LanguageOption("it", R.string.language_italian),
        LanguageOption("pl", R.string.language_polish),
        LanguageOption("tr", R.string.language_turkish),
        LanguageOption("uk", R.string.language_ukrainian),
        LanguageOption("nl", R.string.language_dutch),
        LanguageOption("sv", R.string.language_swedish)
    )
    val languageLabel = languages.firstOrNull { it.tag == selectedLanguage }?.label
        ?: R.string.language_system

    BackHandler {
        when {
            showPrivacyDialog -> showPrivacyDialog = false
            showLanguageDialog -> showLanguageDialog = false
            else -> onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.bg_settings_control_v3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppColors.BackgroundStart.copy(alpha = .36f),
                            AppColors.SpaceBlack.copy(alpha = .68f),
                            AppColors.BackgroundStart.copy(alpha = .48f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 720.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SpaceDesign.SheetRadius),
                color = AppColors.CardBackground.copy(alpha = .96f),
                border = null
            ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    Modifier.size(48.dp).clickable {
                        playButtonSound()
                        onBack()
                    },
                    RoundedCornerShape(13.dp),
                    color = AppColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = .25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(shipActionPainter(4), stringResource(R.string.ship_back), Modifier.size(44.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Image(painterResource(R.drawable.ic_nav_settings_minimal), null, Modifier.size(34.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.settings),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            } }

            Spacer(Modifier.height(18.dp))
            SettingsSectionTitle(stringResource(R.string.settings_preferences_section), "01")
            Spacer(Modifier.height(8.dp))

            SettingsImageButton(
                label = stringResource(R.string.language),
                value = stringResource(languageLabel),
                icon = R.drawable.ui_language_simple,
                onClick = {
                    showMoreLanguages = selectedLanguage != null && selectedLanguage != "en"
                    showLanguageDialog = true
                }
            )

            Spacer(Modifier.height(10.dp))
            SettingsToggleRow(
                title = stringResource(R.string.sound),
                description = stringResource(R.string.sound_description),
                icon = R.drawable.ui_sound_simple,
                checked = soundEnabled,
                onCheckedChange = onSoundEnabledChanged
            )

            Spacer(Modifier.height(10.dp))
            SettingsToggleRow(
                title = stringResource(R.string.reduced_motion),
                description = stringResource(R.string.reduced_motion_description),
                icon = R.drawable.ui_motion_simple,
                checked = reducedMotion,
                onCheckedChange = onReducedMotionChanged
            )

            Spacer(Modifier.height(18.dp))
            SettingsSectionTitle(stringResource(R.string.settings_about_section), "02", AppColors.Secondary)
            if (adPrivacyRequired) {
                TextButton(onClick = onAdPrivacy) { Text(stringResource(R.string.ad_privacy_options)) }
            }
            if (adRequestsAllowed) {
                TextButton(onClick = onStopAdRequests) { Text(stringResource(R.string.ad_stop_requests)) }
            }
            Spacer(Modifier.height(8.dp))

            SettingsCard {
                PrivacyPolicyLink(onClick = { showPrivacyDialog = true })
                SettingsRow(
                    stringResource(R.string.version),
                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    "V"
                )
                SettingsRow(stringResource(R.string.developer), "Alexei Fitlin", "A")
            }

        }
    }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { showPrivacyDialog = false },
            onWithdraw = {
                showPrivacyDialog = false
                onPrivacyWithdraw()
            }
        )
    }

    if (showLanguageDialog) {
        SpaceDialog(
            title = stringResource(R.string.language),
            onDismiss = { showLanguageDialog = false },
            modifier = Modifier.widthIn(max = 420.dp),
            backgroundRes = R.drawable.flat_bg_main,
            content = {
                Column(Modifier.animateContentSize().heightIn(max = 380.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                languages.filterIndexed { index, _ -> index < 2 || showMoreLanguages }.forEach { option ->
                    val selected = selectedLanguage == option.tag
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = {
                            showLanguageDialog = false
                            onLanguageSelected(option.tag)
                        },
                        shape = RoundedCornerShape(12.dp),
                        style = CosmicButtonStyle.Secondary,
                        state = if (selected) CosmicButtonState.Selected else CosmicButtonState.Normal,
                        generatedArtwork = false
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            CosmicSelectionIndicator(selected)
                            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                                Text(stringResource(option.label), color = if (selected) AppColors.Primary else AppColors.Secondary,
                                    fontSize = 16.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                                option.tag?.let { tag ->
                                    Text(tag.uppercase(java.util.Locale.ROOT), color = AppColors.TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    if (option.tag == "en") {
                        TextButton(
                            onClick = { playButtonSound(); showMoreLanguages = !showMoreLanguages },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painterResource(R.drawable.ui_chevron_right_v2),
                                stringResource(if (showMoreLanguages) R.string.languages_collapse else R.string.languages_expand),
                                Modifier.size(16.dp).rotate(if (showMoreLanguages) -90f else 90f)
                            )
                            Text(
                                stringResource(if (showMoreLanguages) R.string.languages_collapse else R.string.languages_expand),
                                Modifier.padding(start = 8.dp), color = AppColors.TextMuted, fontSize = 12.sp
                            )
                        }
                    }
                }
                }
            },
            actions = {}
        )
    }

}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    icon: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val playButtonSound = LocalButtonSound.current
    Surface(
        modifier = Modifier.fillMaxWidth().clickable {
            playButtonSound()
            onCheckedChange(!checked)
        },
        shape = RoundedCornerShape(SpaceDesign.CardRadius),
        color = AppColors.Surface.copy(alpha = .92f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (checked) AppColors.Primary.copy(alpha = .42f) else AppColors.Outline
        )
    ) {
        Row(Modifier.padding(horizontal = 15.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(description, color = AppColors.TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
            }
            CosmicSwitch(checked)
        }
    }
}

@Composable
private fun CosmicSwitch(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF0B1523), if (checked) Color(0xFF28554C) else Color(0xFF25364A))))
            .border(
                1.dp,
                if (checked) AppColors.Primary.copy(alpha = .72f) else AppColors.Outline,
                RoundedCornerShape(15.dp)
            )
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            Modifier
                .size(22.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFDCE7EE), Color(0xFF778CA3))),
                    androidx.compose.foundation.shape.CircleShape
                )
        )
    }
}

@Composable
private fun CosmicSelectionIndicator(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .border(
                1.dp,
                if (selected) AppColors.Primary else AppColors.Outline,
                androidx.compose.foundation.shape.CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                Modifier
                    .size(12.dp)
                    .background(AppColors.Primary, androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}

@Composable
private fun SettingsImageButton(label: String, value: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 74.dp),
        style = CosmicButtonStyle.Secondary,
        generatedArtwork = false,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(icon), null, Modifier.size(32.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(value, color = AppColors.TextMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            Image(painterResource(R.drawable.ui_chevron_right_v2), null, Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceDesign.CardRadius),
        color = AppColors.Surface.copy(alpha = .92f),
        border = null,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String, badge: String? = null, showChevron: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (badge != null) {
            SettingsBadge(badge, AppColors.Primary)
            Spacer(Modifier.width(11.dp))
        }
        Text(label, modifier = Modifier.weight(1f), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(value, color = AppColors.TextMuted, fontSize = 11.sp)
        if (showChevron) {
            Spacer(Modifier.width(7.dp))
            Image(painterResource(R.drawable.ui_chevron_right_v2), null, Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String, number: String, color: Color = AppColors.Primary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(number, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Text(title.uppercase(), color = AppColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
    }
}

@Composable
private fun SettingsBadge(text: String, color: Color) {
    Surface(
        modifier = Modifier.size(32.dp),
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = .11f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .24f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
