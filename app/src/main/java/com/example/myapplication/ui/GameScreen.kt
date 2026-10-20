package com.example.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.example.myapplication.ui.components.Button
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.myapplication.ui.components.cosmicIconPainter as painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myapplication.FloatingTextData
import com.example.myapplication.GameEvent
import com.example.myapplication.GameEventType
import com.example.myapplication.GameViewModel
import com.example.myapplication.DistressChoice
import com.example.myapplication.EconomyBalance
import com.example.myapplication.StationChoice
import com.example.myapplication.TradeOffer
import com.example.myapplication.R
import com.example.myapplication.SoundManager
import com.example.myapplication.BuildConfig
import com.example.myapplication.RewardedAdsController
import com.example.myapplication.RewardedAdStatus
import com.example.myapplication.RewardedBonusEngine
import com.example.myapplication.ShopAdReward
import android.widget.Toast
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.utils.formatNum
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

@Composable
fun GameScreen(
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    soundEnabled: Boolean = true,
    onSoundEnabledChanged: (Boolean) -> Unit = {},
    reducedMotion: Boolean = false,
    onReducedMotionChanged: (Boolean) -> Unit = {},
    onPrivacyWithdraw: () -> Unit = {},
    rewardedAds: RewardedAdsController? = null,
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.gameState.collectAsState()
    val combo by viewModel.combo.collectAsState()
    val autoClickBlockSeconds by viewModel.autoClickBlockSeconds.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var sceneActive by remember { mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, _ ->
            sceneActive = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val context = LocalContext.current
    val adRewardSaveError by rememberUpdatedState(stringResource(R.string.ad_reward_save_error))
    val scope = rememberCoroutineScope()
    val audioContext = context.applicationContext
    val soundManager = remember(audioContext) { SoundManager(audioContext) }
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)
    val adStatus by (rewardedAds?.status ?: remember { kotlinx.coroutines.flow.MutableStateFlow(RewardedAdStatus.UNAVAILABLE) }).collectAsState()
    val adPrivacyRequired by (rewardedAds?.privacyRequired ?: remember { kotlinx.coroutines.flow.MutableStateFlow(false) }).collectAsState()
    val adRequestsAllowed by (rewardedAds?.requestsAllowed ?: remember { kotlinx.coroutines.flow.MutableStateFlow(false) }).collectAsState()
    var pendingAdReward by remember { mutableStateOf<(() -> Unit)?>(null) }
    val adShowing = adStatus == RewardedAdStatus.SHOWING
    val currentAdShowing by rememberUpdatedState(adShowing)
    LaunchedEffect(soundManager, soundEnabled, adShowing) {
        if (soundEnabled && !adShowing && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            soundManager.resumeBackgroundMusic()
        } else soundManager.pauseBackgroundMusic()
    }
    val floatingTextId = remember { AtomicLong(0L) }
    val floatingTexts = remember { mutableStateListOf<FloatingTextData?>().apply { repeat(MAX_FLOATING_TEXTS) { add(null) } } }
    var nextFloatingTextSlot by remember { mutableIntStateOf(0) }
    var lastFloatingTextMillis by remember { mutableLongStateOf(0L) }
    var isShopOpen by remember { mutableStateOf(false) }
    var isHangarOpen by remember { mutableStateOf(false) }
    var isAchievementsOpen by remember { mutableStateOf(false) }
    var isFusionOpen by remember { mutableStateOf(false) }
    var isQuestOpen by remember { mutableStateOf(false) }
    var isPrestigeShopOpen by remember { mutableStateOf(false) }
    var isGalaxyRouteOpen by rememberSaveable { mutableStateOf(false) }
    // Settings must survive Activity recreation when the app locale changes.
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showTradeMarket by remember { mutableStateOf(false) }
    LaunchedEffect(state.activeEvent?.startedAt) { showTradeMarket = false }
    var unlockedPlanetId by remember { mutableStateOf<String?>(null) }

    // Состояние стартового экрана
    var showStartScreen by rememberSaveable { mutableStateOf(true) }
    val tutorialPreferences = remember(context) {
        context.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
    }
    var tutorialCompleted by rememberSaveable {
        mutableStateOf(tutorialPreferences.getBoolean("tutorial_completed", false))
    }
    var startTransitionRunning by remember { mutableStateOf(false) }
    val showTutorial = !showStartScreen && !tutorialCompleted
    var showStartPrivacy by rememberSaveable { mutableStateOf(false) }
    val startScreenOffset = remember { Animatable(0f) }
    val startScreenAlpha = remember { Animatable(1f) }

    fun showRewardAd(reward: () -> Unit) {
        val shown = rewardedAds?.show(
            onStarted = { viewModel.beginRewardedAd(); soundManager.pauseBackgroundMusic() },
            onReward = reward,
            onFinished = {
                viewModel.finishRewardedAd()
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    viewModel.resumeSimulation()
                    if (currentSoundEnabled) soundManager.resumeBackgroundMusic()
                }
            }
        )
        if (shown == false && rewardedAds?.status?.value == RewardedAdStatus.LOADING) pendingAdReward = reward
    }

    fun requestRewardAd(reward: () -> Unit) {
        if (rewardedAds == null || pendingAdReward != null || adShowing || adStatus == RewardedAdStatus.LOADING) return
        if (adStatus == RewardedAdStatus.READY && adRequestsAllowed) showRewardAd(reward)
        else {
            pendingAdReward = reward
            rewardedAds.allowRequests()
        }
    }

    LaunchedEffect(adStatus, sceneActive, adRequestsAllowed) {
        if (adStatus == RewardedAdStatus.UNAVAILABLE || !adRequestsAllowed) pendingAdReward = null
        else if (adStatus == RewardedAdStatus.READY && sceneActive) {
            val reward = pendingAdReward
            pendingAdReward = null
            if (reward != null) showRewardAd(reward)
        }
    }

    BackHandler(
        enabled = showSettings ||
            (!showStartScreen && !showTutorial && state.lastOfflineReward > 0.0) ||
            isShopOpen ||
            isHangarOpen ||
            isAchievementsOpen ||
            isFusionOpen ||
            isPrestigeShopOpen ||
            isGalaxyRouteOpen ||
            isQuestOpen
    ) {
        when {
            showSettings -> showSettings = false
            !showStartScreen && !showTutorial && state.lastOfflineReward > 0.0 -> viewModel.clearOfflineReward()
            isShopOpen -> isShopOpen = false
            isHangarOpen -> isHangarOpen = false
            isAchievementsOpen -> isAchievementsOpen = false
            isFusionOpen -> isFusionOpen = false
            isPrestigeShopOpen -> isPrestigeShopOpen = false
            isGalaxyRouteOpen -> isGalaxyRouteOpen = false
            isQuestOpen -> isQuestOpen = false
        }
    }

    DisposableEffect(soundManager) {
        onDispose { soundManager.close() }
    }

    LaunchedEffect(state.activeEvent?.startedAt) {
        if (soundEnabled && state.activeEvent != null) soundManager.playEventStart()
    }

    LaunchedEffect(state.eventChainResult) {
        state.eventChainResult?.let {
            if (soundEnabled) {
                if (it.success) soundManager.playEventSuccess() else soundManager.playEventFailure()
            }
            delay(1_200L)
            viewModel.clearEventChainResult()
        }
    }

    var previousOwnedPlanets by remember { mutableStateOf(state.ownedPlanets) }
    LaunchedEffect(state.ownedPlanets) {
        if (state.ownedPlanets.size > previousOwnedPlanets.size) {
            if (soundEnabled) soundManager.playPlanetUnlock()
            unlockedPlanetId = (state.ownedPlanets - previousOwnedPlanets)
                .maxByOrNull(EconomyBalance::planetIndex)
        }
        previousOwnedPlanets = state.ownedPlanets
    }

    LaunchedEffect(unlockedPlanetId) {
        if (unlockedPlanetId != null) {
            delay(3_500L)
            unlockedPlanetId = null
        }
    }

    var previousClaimedAchievements by remember { mutableStateOf(state.claimedAchievementIds) }
    var previousCollectedDebris by remember { mutableLongStateOf(state.lifetimeStats.debrisCollected) }
    LaunchedEffect(state.lifetimeStats.debrisCollected) {
        if (soundEnabled && state.lifetimeStats.debrisCollected > previousCollectedDebris) soundManager.playResourceCollected()
        previousCollectedDebris = state.lifetimeStats.debrisCollected
    }
    var previousPrestiges by remember { mutableIntStateOf(state.lifetimeStats.prestiges) }
    LaunchedEffect(state.lifetimeStats.prestiges) {
        if (soundEnabled && state.lifetimeStats.prestiges > previousPrestiges) soundManager.playPrestige()
        previousPrestiges = state.lifetimeStats.prestiges
    }
    LaunchedEffect(state.claimedAchievementIds) {
        if (state.claimedAchievementIds.size > previousClaimedAchievements.size) {
            if (soundEnabled) soundManager.playAchievementClaimed()
        }
        previousClaimedAchievements = state.claimedAchievementIds
    }

    DisposableEffect(lifecycleOwner, viewModel, soundManager) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.resumeSimulation()
                    if (currentSoundEnabled && !currentAdShowing) soundManager.resumeBackgroundMusic()
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.pauseSimulation()
                    soundManager.pauseBackgroundMusic()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            viewModel.resumeSimulation()
            if (currentSoundEnabled && !currentAdShowing) soundManager.resumeBackgroundMusic()
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.pauseSimulation()
            soundManager.pauseBackgroundMusic()
        }
    }

    val fleetMap = viewModel.fleetById
    val nextPlanetIndex = EconomyBalance.nextPlanetIndex(state.ownedPlanets)
    val nextPlanetPrice = nextPlanetIndex?.let { viewModel.planets["p$it"]?.price }
    val nextPlanetImageRes = nextPlanetIndex?.let { viewModel.planets["p$it"]?.imageRes }

    // Логика выбора фона в зависимости от активного ивента
    val eventTint = when (state.activeEvent?.type) {
        GameEventType.STORM, GameEventType.BLACK_HOLE -> Color(0xFF5A3D8F)
        GameEventType.SOLAR_FLARE -> Color(0xFF9A512F)
        GameEventType.CYBER_VIRUS, GameEventType.PIRATE_RAID -> Color(0xFF71384C)
        else -> Color.Transparent
    }

    fun addFloatingText(text: String, x: Float, y: Float) {
        val now = System.currentTimeMillis()
        if (now - lastFloatingTextMillis < FLOATING_TEXT_THROTTLE_MS) return
        lastFloatingTextMillis = now
        val id = floatingTextId.incrementAndGet()
        val slot = nextFloatingTextSlot
        nextFloatingTextSlot = (nextFloatingTextSlot + 1) % MAX_FLOATING_TEXTS
        floatingTexts[slot] = FloatingTextData(id, text, x, y)
        scope.launch {
            delay(GameConstants.FloatingTextDuration)
            if (floatingTexts[slot]?.id == id) floatingTexts[slot] = null
        }
    }

    CompositionLocalProvider(LocalButtonSound provides {
        if (currentSoundEnabled) soundManager.playButtonClick()
    }) {
    val spaceMotion = if (!reducedMotion && sceneActive) rememberInfiniteTransition(label = "space_background") else null
    val galaxyPhase = spaceMotion?.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing)),
        label = "galaxy_phase"
    )?.value ?: 0f
    val starTwinklePhase = spaceMotion?.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2.0).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4_800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star_twinkle_phase"
    )?.value ?: 0f
    val cosmicParticlePhase = spaceMotion?.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cosmic_particle_phase"
    )?.value ?: 0f
    // A single clock drives every rotor. Previously each drone owned an infinite
    // transition, multiplying animation work as the active fleet grew.
    val sharedRotorPhase = spaceMotion?.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shared_drone_rotor_phase"
    )?.value ?: 0f

    Box(modifier = Modifier.fillMaxSize()) {
        // ДИНАМИЧЕСКИЙ ФОН
        Image(painterResource(R.drawable.background_cosmic_v5), null,
            Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

        if (!reducedMotion) CosmicParticleTrails(cosmicParticlePhase)
        val planetColors = com.example.myapplication.ui.theme.PlanetPalette.forPlanet(state.currentPlanetId)
        LivingGalaxy(galaxyPhase, reducedMotion, primary = planetColors.primary, secondary = planetColors.secondary)

        // Затемнение для читаемости элементов
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f))
                .background(eventTint.copy(alpha = if (eventTint == Color.Transparent) 0f else 0.10f))
        )

        // Звезды
        repeat(GameConstants.StarCount) { index ->
            Star(index = index, twinklePhase = starTwinklePhase, reduceMotion = reducedMotion)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                state = state,
                dps = viewModel.calculateDPS(),
                nextPlanetIndex = nextPlanetIndex,
                nextPlanetPrice = nextPlanetPrice,
                nextPlanetImageRes = nextPlanetImageRes,
                onAchievementsClick = { isAchievementsOpen = true },
                onPrestigeShopClick = { isPrestigeShopOpen = true },
                onRouteClick = { isGalaxyRouteOpen = true },
                onSettingsClick = { showSettings = true },
                reducedMotion = reducedMotion
            )
            
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val planetDiameter = minOf(maxWidth * .88f, maxHeight * .78f, 390.dp)
                state.activeEvent?.let { event ->
                    EventBanner(event, state.eventTapsLeft)
                }
                state.pendingEventChain?.let { pending ->
                    EventChainPendingBanner(
                        pending = pending,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

                if (combo > 1) {
                    ComboIndicator(
                        combo = combo,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = if (state.activeEvent != null || state.pendingEventChain != null) 76.dp else 8.dp)
                    )
                }

                PlanetButton(
                        planetId = state.currentPlanetId,
                        planetConfig = viewModel.planets[state.currentPlanetId] ?: viewModel.planets.values.first(),
                        modifier = Modifier.align(Alignment.Center),
                        reducedMotion = reducedMotion,
                        diameter = planetDiameter,
                        atmospherePhase = galaxyPhase
                    ) { x, y ->
                        val value = viewModel.onPlanetClick(x, y)
                        if (value > 0.0) {
                            if (soundEnabled) soundManager.playClick()
                            val planetWidthFraction = planetDiameter.value / maxWidth.value
                            val planetHeightFraction = planetDiameter.value / maxHeight.value
                            addFloatingText(
                                "+${formatNum(value)}",
                                (0.5f + (x - 0.5f) * planetWidthFraction).coerceIn(0f, 1f),
                                (0.5f + (y - 0.5f) * planetHeightFraction).coerceIn(0f, 1f)
                            )
                        }
                    }

                state.scavengeTargets.forEach { target ->
                    key(target.id) {
                        DebrisTarget(
                            target,
                            maxWidth,
                            maxHeight,
                            onClick = null,
                            planetId = state.currentPlanetId
                        )
                    }
                }

                state.drones.forEachIndexed { index, drone ->
                    key(drone.id) {
                        ScavengingDrone(drone, fleetMap, maxWidth, maxHeight, sharedRotorPhase, planetId = state.currentPlanetId)
                    }
                }

                state.activeEvent?.let { event ->
                    Image(
                        painter = painterResource(R.drawable.bg_event_operations_v3),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().zIndex(-1f),
                        contentScale = ContentScale.Crop
                    )
                    when (event.type) {
                        GameEventType.BLACK_HOLE -> {
                            BlackHoleComponent(
                                event,
                                state.eventTapsLeft,
                                state.stormSequence,
                                state.stormProgress,
                                state.stormRound,
                                maxWidth,
                                maxHeight,
                                viewModel::onBlackHoleNodeClick
                            )
                        }
                        GameEventType.PIRATE_RAID -> {
                            PirateAmbushComponent(event, state.stormSequence, state.stormProgress, state.stormRound, maxWidth, maxHeight, viewModel::onPirateTargetClick, viewModel::resolvePirateRaid)
                        }
                        GameEventType.STORM -> {
                            StormNodeChallenge(event, state.stormSequence, state.stormProgress, state.stormRound, maxWidth, maxHeight, viewModel::onStormNodeClick)
                        }
                        GameEventType.SOLAR_FLARE -> {
                            SolarFlareProtocol(event, state.stormSequence, state.stormProgress, state.stormRound, maxWidth, maxHeight, viewModel::onSolarChannelClick)
                        }
                        // The meteor shower is represented only by falling debris.
                        // There is no separate four-target interception mini-game.
                        GameEventType.METEOR_SHOWER -> Unit
                        GameEventType.DISTRESS_SIGNAL -> DistressSignalScanner(
                            event = event,
                            sequence = state.stormSequence,
                            progress = state.stormProgress,
                            phase = state.stormRound,
                            gameAreaWidth = maxWidth,
                            gameAreaHeight = maxHeight,
                            onNodeClick = viewModel::onDistressSignalNode,
                            onSalvage = { viewModel.respondToDistressSignal(DistressChoice.SALVAGE) },
                            onRescue = { viewModel.respondToDistressSignal(DistressChoice.RESCUE) }
                        )
                        GameEventType.ABANDONED_STATION -> AbandonedStationAccess(
                            event = event,
                            sequence = state.stormSequence,
                            progress = state.stormProgress,
                            phase = state.stormRound,
                            gameAreaWidth = maxWidth,
                            gameAreaHeight = maxHeight,
                            onRelayClick = viewModel::onStationRelayClick,
                            onSafeRoute = { viewModel.respondToAbandonedStation(StationChoice.SAFE_ROUTE) },
                            onReactorCore = { viewModel.respondToAbandonedStation(StationChoice.REACTOR_CORE) }
                        )
                        GameEventType.TRADING_SHIP -> TradingShipComponent(
                            event = event,
                            hullLeft = state.eventTapsLeft,
                            gameAreaWidth = maxWidth,
                            gameAreaHeight = maxHeight,
                            onOpenMarket = { showTradeMarket = true }
                        )
                        GameEventType.CYBER_VIRUS -> CyberVirusField(
                            event = event,
                            remaining = state.eventTapsLeft,
                            gameAreaWidth = maxWidth,
                            gameAreaHeight = maxHeight,
                            onResolved = viewModel::resolveCyberVirus
                        )
                    }
                }

                floatingTexts.filterNotNull().forEach { data ->
                    key(data.id) {
                        FloatingText(data, maxWidth, maxHeight)
                    }
                }
            }

        }

        if (isShopOpen) {
            ShopBar(
                viewModel = viewModel,
                state = state,
                onClose = { isShopOpen = false },
                modifier = Modifier.align(Alignment.BottomCenter),
                adRewards = {
                    if (BuildConfig.ADS_ENABLED && rewardedAds != null) ShopAdRewards(
                        state = state, amount = viewModel.shopAdDebrisReward(state), status = adStatus,
                        onWatch = { type, amount ->
                            val offerId = if (pendingAdReward == null && !adShowing && adStatus != RewardedAdStatus.LOADING)
                                viewModel.beginShopAdOffer(type, amount) else null
                            if (offerId != null) requestRewardAd {
                                if (!viewModel.claimShopAdReward(offerId))
                                    Toast.makeText(context, adRewardSaveError, Toast.LENGTH_LONG).show()
                            }
                        },
                        onOpenCase = { viewModel.openShopAdCase() }
                    )
                }
            )
        } else if (isHangarOpen) {
            DroneHangarPanel(
                viewModel = viewModel,
                state = state,
                onClose = { isHangarOpen = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else if (isAchievementsOpen) {
            AchievementsPanel(
                viewModel = viewModel,
                state = state,
                onClose = { isAchievementsOpen = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else if (isFusionOpen) {
            DroneFusionDialog(
                state = state,
                fleet = viewModel.fleetItems,
                onFuse = viewModel::fuseDrones,
                onClose = { isFusionOpen = false }
            )
        } else if (isPrestigeShopOpen) {
            PrestigeShopPanel(
                viewModel = viewModel,
                state = state,
                onClose = { isPrestigeShopOpen = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else if (isQuestOpen) {
            QuestPanel(
                state = state,
                onClaim = { viewModel.claimQuestReward(it) },
                onClaimDailyReward = viewModel::claimDailyReward,
                onClose = { isQuestOpen = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xB80A1322))
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameNavigationButton(
                    icon = R.drawable.ic_nav_quests_minimal,
                    label = R.string.goals,
                    description = R.string.goals,
                    onClick = { isQuestOpen = true },
                    modifier = Modifier.weight(1f)
                )
                GameNavigationButton(
                    icon = R.drawable.ic_nav_shop_minimal,
                    label = R.string.navigation_shop,
                    description = R.string.open_shop,
                    onClick = { isShopOpen = true },
                    modifier = Modifier.weight(1f)
                )
                GameNavigationButton(
                    icon = R.drawable.ic_nav_hangar_minimal,
                    label = R.string.navigation_hangar,
                    description = R.string.open_hangar,
                    onClick = { isHangarOpen = true },
                    modifier = Modifier.weight(1f)
                )
                GameNavigationButton(
                    icon = R.drawable.ic_nav_fusion,
                    label = R.string.fusion_title,
                    description = R.string.open_fusion,
                    onClick = { isFusionOpen = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (autoClickBlockSeconds > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 112.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .heightIn(min = 68.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF273540),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(shipActionPainter(1), null, Modifier.size(42.dp))
                        Text(
                            stringResource(R.string.autoclicker_detected, autoClickBlockSeconds),
                            modifier = Modifier.weight(1f).padding(start = 12.dp),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        unlockedPlanetId?.let { planetId ->
            val planet = viewModel.planets[planetId]
            if (planet != null) {
                PlanetUnlockBanner(
                    planetIndex = EconomyBalance.planetIndex(planetId),
                    planetName = planet.name,
                    incomeBonusPercent = ((EconomyBalance.planetIncomeMultiplier(planetId) - 1.0) * 100).toInt(),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 112.dp, start = 20.dp, end = 20.dp)
                )
            }
        }

        // ОВЕРЛЕЙ ОТКРЫТИЯ КЕЙСА
        CaseOpeningOverlay(
            isOpening = state.isOpeningCase,
            caseType = state.openingCaseType ?: com.example.myapplication.CaseType.COMMON,
            lastDroppedDrone = state.lastDroppedDroneId?.let { fleetMap[it] },
            remainingCases = state.pendingCaseOpenings + if (state.isOpeningCase) 1 else 0,
            bundleRewards = state.caseBundleRewards.mapNotNull { (id, count) -> fleetMap[id]?.let { it to count } },
            showBundleSummary = state.showCaseBundleSummary,
            onFinishOpening = { viewModel.finishOpeningCase() },
            onOpenAll = { viewModel.openAllPendingCases() },
            onClearReward = { viewModel.clearReward() },
            onClearBundleSummary = { viewModel.clearCaseBundleSummary() },
            onOpeningPulse = { frame ->
                if (soundEnabled) soundManager.playCaseOpeningPulse(frame, state.openingCaseType ?: com.example.myapplication.CaseType.COMMON)
            },
            reduceMotion = reducedMotion
        )

        if (showSettings) {
            SettingsScreen(
                adPrivacyRequired = adPrivacyRequired,
                onAdPrivacy = { rewardedAds?.showPrivacyOptions() },
                adRequestsAllowed = adRequestsAllowed,
                onStopAdRequests = { rewardedAds?.stopRequests() },
                onPrivacyWithdraw = onPrivacyWithdraw,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
                soundEnabled = soundEnabled,
                onSoundEnabledChanged = onSoundEnabledChanged,
                reducedMotion = reducedMotion,
                onReducedMotionChanged = onReducedMotionChanged,
                onBack = { showSettings = false }
            )
        }

        // СТАРТОВЫЙ ЭКРАН
        if (!showStartScreen && !showTutorial && state.lastOfflineReward > 0.0) {
            SpaceDialog(
                title = stringResource(R.string.offline_reward_title),
                onDismiss = { if (!adShowing) viewModel.clearOfflineReward() },
                modifier = Modifier.widthIn(max = 300.dp),
                backgroundRes = R.drawable.bg_offline_reward_space_v1,
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(AppColors.Primary.copy(alpha = .10f), CircleShape)
                                .border(2.dp, AppColors.Primary.copy(alpha = .62f), CircleShape)
                                .padding(5.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.offline_drone_reward_v1),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "+${formatNum(state.lastOfflineReward)}",
                            color = AppColors.Primary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            stringResource(R.string.debris),
                            color = AppColors.Secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stringResource(R.string.offline_time_away, formatOfflineDuration(state.lastOfflineSeconds)),
                            color = AppColors.TextMuted,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                },
                actions = {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (BuildConfig.ADS_ENABLED && rewardedAds != null && RewardedBonusEngine.isEligible(state)) {
                        val offerId = state.offlineBonusOfferId!!
                        Button(
                            onClick = {
                                requestRewardAd {
                                    if (!viewModel.claimOfflineAdBonus(offerId)) Toast.makeText(context,
                                        adRewardSaveError, Toast.LENGTH_LONG).show()
                                }
                            },
                            enabled = adStatus != RewardedAdStatus.LOADING && !adShowing,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Text(stringResource(when (adStatus) {
                                RewardedAdStatus.READY -> R.string.ad_offline_double
                                RewardedAdStatus.LOADING -> R.string.ad_loading
                                RewardedAdStatus.SHOWING -> R.string.ad_showing
                                else -> R.string.ad_retry
                            }), fontSize = 12.sp)
                        }
                        if (BuildConfig.TEST_ADS) Text(stringResource(R.string.ad_test_mode),
                            color = AppColors.TextMuted, fontSize = 11.sp)
                        if (adStatus == RewardedAdStatus.UNAVAILABLE) Text(stringResource(R.string.ad_unavailable),
                            color = AppColors.TextMuted, fontSize = 11.sp)
                        Spacer(Modifier.height(6.dp))
                    }
                    Button(
                        onClick = viewModel::clearOfflineReward,
                        enabled = !adShowing,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
                    ) {
                        Text(
                            stringResource(R.string.collect_reward),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    }
                }
            )
        }

        val merchant = state.activeEvent?.takeIf { it.type == GameEventType.TRADING_SHIP }
        if (showTradeMarket && merchant != null) {
            TradingShipMarket(
                event = merchant, totalDebris = state.totalDebris, phase = state.stormRound,
                sequence = state.stormSequence, progress = state.stormProgress, priceMultiplier = state.eventMultiplier,
                onChannelClick = viewModel::onTradeChannelClick,
                onBuy = { offer -> viewModel.buyTradeOffer(offer); showTradeMarket = false },
                onDismiss = { showTradeMarket = false },
                canBuy = { com.example.myapplication.EventEngine.canBuyTradeOffer(state, it) }
            )
        }
        if (isGalaxyRouteOpen) {
            GalaxyRouteDialog(viewModel, state) { isGalaxyRouteOpen = false }
        }

        if (showStartScreen) {
            val promptTransition = if (!reducedMotion) rememberInfiniteTransition(label = "start_prompt") else null
            val animatedPromptOffset = promptTransition?.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "start_prompt_offset"
            )?.value ?: 0f
            val animatedPromptAlpha = promptTransition?.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "start_prompt_alpha"
            )?.value ?: 1f
            val promptOffset = animatedPromptOffset
            val promptAlpha = animatedPromptAlpha

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = startScreenOffset.value
                        alpha = startScreenAlpha.value
                    }
                    .clickable(
                        enabled = !startTransitionRunning,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        startTransitionRunning = true
                        scope.launch {
                            if (soundEnabled) soundManager.playClick()
                            if (reducedMotion) {
                                showStartScreen = false
                                startTransitionRunning = false
                                return@launch
                            }
                            // Анимация ухода вверх и исчезновения
                            launch {
                                startScreenOffset.animateTo(
                                    targetValue = -1000f,
                                    animationSpec = tween(durationMillis = 800)
                                )
                            }
                            launch {
                                startScreenAlpha.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 800)
                                )
                            }
                            delay(800)
                            showStartScreen = false
                            startTransitionRunning = false
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_space_start_v4),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentScale = ContentScale.Crop
                )

                CosmicParticleTrails(cosmicParticlePhase)

                repeat(18) { index ->
                    Star(index = index + 100, twinklePhase = starTwinklePhase, reduceMotion = reducedMotion)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 44.dp)
                        .offset(y = promptOffset.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.launcher_legacy_v4),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.4.sp
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(76.dp).graphicsLayer { alpha = 0.78f + promptAlpha * 0.22f }, contentAlignment = Alignment.Center) {
                        Box(
                            Modifier.fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFF226B8C), Color(0xFF163E68), Color(0xFF17314D))))
                                .border(2.dp, Color(0xFF87E8F5), RoundedCornerShape(16.dp))
                        )
                        Text(
                            text = stringResource(R.string.tap_to_continue),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 48.dp),
                            maxLines = 2
                        )
                    }
                    PrivacyPolicyLink(onClick = { showStartPrivacy = true })
                }
            }
        }
        if (showStartPrivacy) {
            PrivacyPolicyDialog(onDismiss = { showStartPrivacy = false }, onWithdraw = {
                showStartPrivacy = false
                onPrivacyWithdraw()
            })
        }
        if (showTutorial) {
            TutorialDialog(onFinish = {
                tutorialPreferences.edit().putBoolean("tutorial_completed", true).apply()
                tutorialCompleted = true
            })
        }
    }
}

}

@Composable
fun QuestLauncherButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    com.example.myapplication.ui.components.IconButton(
        onClick = onClick,
        modifier = modifier.size(52.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_nav_quests_minimal),
            contentDescription = stringResource(R.string.quests),
            modifier = Modifier.padding(13.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun GameNavigationButton(
    icon: Int,
    label: Int,
    description: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = when (icon) {
        R.drawable.ic_nav_shop_minimal -> Color(0xFFE8AB65)
        R.drawable.ic_nav_quests_minimal -> Color(0xFF83CDA7)
        R.drawable.ic_nav_hangar_minimal -> Color(0xFFF0A278)
        R.drawable.ic_nav_fusion -> Color(0xFFC6A1FF)
        else -> Color(0xFF93B9EB)
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 64.dp, max = 76.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = accent),
        compact = true,
        generatedArtwork = false,
        contentPadding = PaddingValues(horizontal = 3.dp, vertical = 4.dp)
    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(description),
            modifier = Modifier.size(26.dp),
            tint = Color.Unspecified
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(label),
            color = Color.White.copy(alpha = 0.88f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 15.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
    }
}

private const val MAX_FLOATING_TEXTS = 6
private const val FLOATING_TEXT_THROTTLE_MS = 120L

@Composable
private fun MinimalCosmicBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF050B18), Color(0xFF080F22), Color(0xFF050A17)),
                startY = 0f,
                endY = size.height
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF254A76).copy(alpha = .25f), Color.Transparent),
                center = Offset(size.width * .92f, size.height * .22f),
                radius = size.maxDimension * .72f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF433275).copy(alpha = .17f), Color.Transparent),
                center = Offset(size.width * .08f, size.height * .76f),
                radius = size.maxDimension * .62f
            )
        )
        val orbitColor = Color(0xFF9DC7FF).copy(alpha = .045f)
        drawOval(
            color = orbitColor,
            topLeft = Offset(size.width * .03f, size.height * .34f),
            size = androidx.compose.ui.geometry.Size(size.width * .94f, size.height * .30f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
        )
        drawOval(
            color = orbitColor.copy(alpha = .58f),
            topLeft = Offset(-size.width * .22f, size.height * .25f),
            size = androidx.compose.ui.geometry.Size(size.width * 1.44f, size.height * .48f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun CosmicParticleTrails(phase: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val trails = listOf(
            Triple(0.00f, 0.12f, Color(0xFF8DEBFF)),
            Triple(0.38f, 0.46f, Color(0xFFD8B5FF)),
            Triple(0.73f, 0.72f, Color(0xFFFFE8A3))
        )
        trails.forEachIndexed { index, (delay, startY, color) ->
            val progress = ((phase - delay + 1f) % 1f) * 3f
            if (progress in 0f..1f) {
                val x = size.width * (-0.15f + progress * 1.30f)
                val y = size.height * (startY + progress * 0.18f)
                val length = size.minDimension * (0.10f + index * 0.018f)
                val alpha = (1f - kotlin.math.abs(progress - 0.5f) * 2f).coerceIn(0f, 1f)
                drawLine(
                    color = color.copy(alpha = alpha * 0.52f),
                    start = Offset(x - length, y - length * 0.28f),
                    end = Offset(x, y),
                    strokeWidth = 2.2f + index,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color.White.copy(alpha = alpha * 0.9f),
                    radius = 2.5f + index * 0.5f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun formatOfflineDuration(seconds: Long): String {
    val safe = seconds.coerceAtLeast(0L)
    val hours = safe / 3_600L
    val minutes = (safe % 3_600L) / 60L
    return if (hours > 0L) "${hours}h ${minutes}m" else "${minutes.coerceAtLeast(1L)}m"
}
