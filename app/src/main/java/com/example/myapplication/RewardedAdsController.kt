package com.example.myapplication

import android.app.Activity
import android.os.SystemClock
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AgeRestrictedTreatment
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RewardedAdStatus { IDLE, LOADING, READY, SHOWING, UNAVAILABLE }

/** Activity-scoped. Only debug builds can request Google's demo ads without production UMP. */
class RewardedAdsController(private val activity: Activity) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val consent by lazy { UserMessagingPlatform.getConsentInformation(activity) }
    private val mutableStatus = MutableStateFlow(RewardedAdStatus.IDLE)
    val status = mutableStatus.asStateFlow()
    private val mutablePrivacyRequired = MutableStateFlow(false)
    val privacyRequired = mutablePrivacyRequired.asStateFlow()
    private val mutableRequestsAllowed = MutableStateFlow(false)
    val requestsAllowed = mutableRequestsAllowed.asStateFlow()
    private var ad: RewardedAd? = null
    private var loadedAt = 0L
    private var closed = false
    private var consentRequested = false
    private var initialized = false
    private var generation = 0
    private var privacyFormOpen = false
    private var loadTimeout: Job? = null
    private var finishShowing: (() -> Unit)? = null

    /** Permission is deliberately session-only and separate from the game's policy. */
    fun allowRequests() {
        if (!closed && BuildConfig.ADS_ENABLED) {
            mutableRequestsAllowed.value = true
            prepare()
        }
    }

    fun prepare() {
        if (closed || privacyFormOpen || !mutableRequestsAllowed.value || !BuildConfig.ADS_ENABLED || mutableStatus.value in
            setOf(RewardedAdStatus.LOADING, RewardedAdStatus.READY, RewardedAdStatus.SHOWING)) return
        mutableStatus.value = RewardedAdStatus.LOADING
        val request = ++generation
        if (BuildConfig.TEST_ADS) {
            // Demo App ID has no publisher's Privacy & messaging configuration.
            initializeAndLoad(request)
        } else if (consentRequested) {
            if (consent.canRequestAds()) initializeAndLoad(request)
            else requestConsent(request)
        } else requestConsent(request)
    }

    private fun requestConsent(request: Int) {
        consentRequested = true
        consent.requestConsentInfoUpdate(activity, ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(true).build(), {
            if (!valid(request)) return@requestConsentInfoUpdate
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                if (!valid(request)) return@loadAndShowConsentFormIfRequired
                updatePrivacy()
                if (consent.canRequestAds()) initializeAndLoad(request)
                else mutableStatus.value = RewardedAdStatus.UNAVAILABLE
            }
        }, {
            if (valid(request)) {
                updatePrivacy()
                if (consent.canRequestAds()) initializeAndLoad(request)
                else mutableStatus.value = RewardedAdStatus.UNAVAILABLE
            }
        })
    }

    private fun updatePrivacy() {
        mutablePrivacyRequired.value = consent.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    fun showPrivacyOptions() {
        if (closed || privacyFormOpen || !mutableRequestsAllowed.value || mutableStatus.value == RewardedAdStatus.SHOWING || !mutablePrivacyRequired.value) return
        generation++
        val request = generation
        loadTimeout?.cancel()
        privacyFormOpen = true
        ad = null
        mutableStatus.value = RewardedAdStatus.IDLE
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            privacyFormOpen = false
            if (valid(request)) { updatePrivacy(); prepare() }
        }
    }

    private fun initializeAndLoad(request: Int) {
        if (!valid(request)) return
        // Reading a consent form must never consume the SDK loading timeout.
        loadTimeout?.cancel()
        loadTimeout = scope.launch {
            delay(45_000)
            if (valid(request) && mutableStatus.value == RewardedAdStatus.LOADING) {
                generation++
                mutableStatus.value = RewardedAdStatus.UNAVAILABLE
            }
        }
        if (initialized) { load(request); return }
        // Audience flags are configured here before SDK initialization.
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder()
            // The declared audience is 13+, including minors. Until an age gate is implemented,
            // treat every request conservatively as below the age of consent.
            .setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G).build())
        scope.launch(Dispatchers.IO) {
            MobileAds.initialize(activity.applicationContext) {
                scope.launch {
                    initialized = true
                    if (valid(request)) load(request)
                }
            }
        }
    }

    private fun valid(request: Int) = !closed && mutableRequestsAllowed.value && generation == request && !activity.isDestroyed

    private fun load(request: Int) {
        RewardedAd.load(activity.applicationContext, BuildConfig.REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                override fun onAdLoaded(loaded: RewardedAd) {
                    if (!valid(request)) return
                    loadTimeout?.cancel()
                    ad = loaded
                    loadedAt = SystemClock.elapsedRealtime()
                    mutableStatus.value = RewardedAdStatus.READY
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    if (!valid(request)) return
                    loadTimeout?.cancel()
                    ad = null
                    mutableStatus.value = RewardedAdStatus.UNAVAILABLE
                }
            })
    }

    fun show(onStarted: () -> Unit, onReward: () -> Unit, onFinished: () -> Unit): Boolean {
        val loaded = ad ?: return false
        if (closed || !mutableRequestsAllowed.value || activity.isFinishing || activity.isDestroyed ||
            mutableStatus.value != RewardedAdStatus.READY) return false
        if (SystemClock.elapsedRealtime() - loadedAt >= 55 * 60_000L ||
            (!BuildConfig.TEST_ADS && !consent.canRequestAds())) {
            ad = null
            mutableStatus.value = RewardedAdStatus.IDLE
            prepare()
            return false
        }
        ad = null
        mutableStatus.value = RewardedAdStatus.SHOWING
        var rewarded = false
        finishShowing = onFinished
        fun finish() {
            val callback = finishShowing ?: return
            finishShowing = null
            if (!closed) mutableStatus.value = RewardedAdStatus.IDLE
            callback()
        }
        loaded.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { finish() }
            override fun onAdFailedToShowFullScreenContent(error: AdError) { finish() }
        }
        onStarted()
        try {
            loaded.show(activity) {
                if (!closed && !rewarded && finishShowing != null) {
                    rewarded = true
                    onReward()
                }
            }
        } catch (_: IllegalStateException) { finish() }
        return true
    }

    fun close() {
        closed = true
        generation++
        ad = null
        finishShowing?.invoke()
        finishShowing = null
        scope.cancel()
    }

    fun stopRequests() {
        mutableRequestsAllowed.value = false
        loadTimeout?.cancel()
        generation++
        ad = null
        mutableStatus.value = RewardedAdStatus.IDLE
        mutablePrivacyRequired.value = false
        consentRequested = false
    }
}
