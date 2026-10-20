package com.example.myapplication

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.SystemClock
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Bundled effects and a quiet three-track playlist; no network requests. */
class SoundManager(context: Context) : AutoCloseable {
    private val lock = Any()
    private val appContext = context.applicationContext
    private var closed = false
    private var foreground = false
    private val pool = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
    ).build()
    private val loaded = mutableSetOf<Int>()
    private val sounds = mutableMapOf<Int, Int>()
    private val streams = ArrayDeque<Int>()
    private var lastCollectionAt = 0L
    private var backgroundPlayer: MediaPlayer? = null
    private val musicTracks = intArrayOf(R.raw.music_orbit_ambient_1, R.raw.music_orbit_ambient_2, R.raw.music_orbit_ambient_3)
    private val musicHandler = Handler(Looper.getMainLooper())
    private var incomingPlayer: MediaPlayer? = null
    private var fadePosition = 0f
    private var fadeStart = 0L
    private val musicVolume = .085f
    private val transitionMillis = 5000L
    private val musicTick = object : Runnable {
        override fun run() {
            synchronized(lock) {
                if (closed || !foreground) return
                try {
                    val current = backgroundPlayer ?: return
                    if (incomingPlayer == null && current.duration - current.currentPosition <= transitionMillis) {
                        val next = createMusicPlayer((musicTrackIndex + 1) % musicTracks.size)
                        if (next != null) {
                            incomingPlayer = next
                            fadeStart = SystemClock.elapsedRealtime()
                            fadePosition = 0f
                            next.setVolume(0f, 0f)
                            next.start()
                        }
                    }
                    incomingPlayer?.let { next ->
                        fadePosition = ((SystemClock.elapsedRealtime() - fadeStart).toFloat() / transitionMillis).coerceIn(0f, 1f)
                        val outgoingVolume = musicVolume * kotlin.math.cos(fadePosition * Math.PI.toFloat() / 2)
                        val incomingVolume = musicVolume * kotlin.math.sin(fadePosition * Math.PI.toFloat() / 2)
                        current.setVolume(outgoingVolume, outgoingVolume)
                        next.setVolume(incomingVolume, incomingVolume)
                        if (fadePosition >= 1f) {
                            current.release()
                            backgroundPlayer = next
                            incomingPlayer = null
                            musicTrackIndex = (musicTrackIndex + 1) % musicTracks.size
                        }
                    }
                } catch (_: RuntimeException) {
                    releaseMusic()
                }
                musicHandler.postDelayed(this, 50L)
            }
        }
    }
    private var musicTrackIndex = 0
    private var clickVariant = 0
    private var collectionVariant = 0
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        pool.setOnLoadCompleteListener { _, id, status ->
            synchronized(lock) { if (!closed && status == 0) loaded.add(id) }
        }
        listOf(R.raw.sfx_click_soft, R.raw.sfx_case_pulse, R.raw.sfx_case_reveal,
            R.raw.sfx_event_start, R.raw.sfx_event_success, R.raw.sfx_event_failure,
            R.raw.sfx_collect_soft, R.raw.sfx_drone_action, R.raw.sfx_prestige,
            R.raw.sfx_planet_travel).forEach { sounds[it] = pool.load(appContext, it, 1) }
    }

    fun playClick() { vibrate(12); play(R.raw.sfx_click_soft, .22f, rate = .96f + (clickVariant++ % 3) * .04f, priority = 0) }
    fun playButtonClick() = play(R.raw.sfx_click_soft, .16f, rate = 1.15f, priority = 0)
    fun playEventStart() { vibrate(40); play(R.raw.sfx_event_start) }
    fun playEventSuccess() { vibrate(60); play(R.raw.sfx_event_success) }
    fun playEventFailure() { vibrate(100); play(R.raw.sfx_event_failure, .35f) }
    fun playPlanetUnlock() = play(R.raw.sfx_planet_travel, .45f)
    fun playAchievementClaimed() = play(R.raw.sfx_event_success, .38f)
    fun playDroneAction() = play(R.raw.sfx_drone_action, .22f)
    fun playPrestige() = play(R.raw.sfx_prestige, .45f)
    fun playResourceCollected() {
        synchronized(lock) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastCollectionAt < 350L) return
            lastCollectionAt = now
            play(R.raw.sfx_collect_soft, .18f, rate = .96f + (collectionVariant++ % 4) * .035f, priority = 0)
        }
    }

    fun playCaseOpeningPulse(frame: Int, type: CaseType) {
        val boost = when (type) { CaseType.COMMON -> 0; CaseType.RARE -> 30; CaseType.LEGENDARY -> 70 }
        vibrate(if (frame >= 8) 90L else 12L + frame * 2L, (35 + frame * 18 + boost).coerceIn(1, 255))
        play(if (frame >= 8) R.raw.sfx_case_reveal else R.raw.sfx_case_pulse,
            if (frame >= 8) .5f else .2f, rate = (0.85f + frame * .06f).coerceAtMost(1.3f))
    }

    private fun vibrate(duration: Long, amplitude: Int = -1) {
        if (closed || !foreground) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(duration)
        }
    }

    private fun play(resource: Int, volume: Float = .4f, rate: Float = 1f, priority: Int = 1) {
        synchronized(lock) {
            if (closed || !foreground) return
            val id = sounds[resource] ?: return
            if (id !in loaded) return
            val quietVolume = volume * .75f
            val stream = pool.play(id, quietVolume, quietVolume, priority, 0, rate)
            if (stream != 0) {
                streams.addLast(stream)
                if (streams.size > 24) pool.stop(streams.removeFirst())
            }
        }
    }

    fun resumeBackgroundMusic() {
        synchronized(lock) {
            if (closed) return
            foreground = true
            try {
                val player = backgroundPlayer ?: createMusicPlayer(musicTrackIndex)?.also {
                    it.isLooping = false
                    it.setVolume(musicVolume, musicVolume)
                    backgroundPlayer = it
                }
                if (player != null && !player.isPlaying) player.start()
                incomingPlayer?.let {
                    fadeStart = SystemClock.elapsedRealtime() - (fadePosition * transitionMillis).toLong()
                    if (!it.isPlaying) it.start()
                }
                musicHandler.removeCallbacks(musicTick)
                musicHandler.post(musicTick)
            } catch (_: RuntimeException) {
                releaseMusic()
            }
        }
    }

    fun pauseBackgroundMusic() {
        synchronized(lock) {
            if (closed) return
            foreground = false
            musicHandler.removeCallbacks(musicTick)
            streams.forEach(pool::stop)
            streams.clear()
            vibrator?.cancel()
            try { backgroundPlayer?.takeIf { it.isPlaying }?.pause() } catch (_: IllegalStateException) { }
            try { incomingPlayer?.takeIf { it.isPlaying }?.pause() } catch (_: IllegalStateException) { }
        }
    }

    override fun close() {
        synchronized(lock) {
            if (closed) return
            closed = true
            foreground = false
            pool.release()
            loaded.clear()
            musicHandler.removeCallbacks(musicTick)
            releaseMusic()
            vibrator?.cancel()
        }
    }

    private fun createMusicPlayer(index: Int): MediaPlayer? = MediaPlayer.create(appContext, musicTracks[index])?.also { player ->
        player.setOnCompletionListener { completed ->
            synchronized(lock) {
                if (closed || backgroundPlayer !== completed || incomingPlayer != null) return@setOnCompletionListener
                completed.release()
                backgroundPlayer = null
                musicTrackIndex = (musicTrackIndex + 1) % musicTracks.size
                if (foreground) resumeBackgroundMusic()
            }
        }
    }

    private fun releaseMusic() {
        backgroundPlayer?.release()
        incomingPlayer?.release()
        backgroundPlayer = null
        incomingPlayer = null
        fadePosition = 0f
    }
}
