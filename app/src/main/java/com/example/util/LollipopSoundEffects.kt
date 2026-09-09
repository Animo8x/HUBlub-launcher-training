package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.SoundPool
import com.example.model.WaterSoundProfile
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, low-latency audio engine for authentic water effects and tactile Material clicks.
 *
 * Supports selectable sound profiles:
 * - SOFT_DROP: Soothing, gentle, non-intrusive natural water drip (relaxed, low harshness).
 * - SAMSUNG_CLASSIC: Iconic Samsung Galaxy S3/S4 Nature UX water bloop.
 * - GENTLE_BUBBLE: Soft, warm, rounded bubble pop.
 * - MUTED: Complete silence.
 *
 * Uses AudioAttributes.USAGE_MEDIA and CONTENT_TYPE_MUSIC routed to STREAM_MUSIC
 * so sounds play with clear volume and are never muted by Android system touch sound toggles.
 */
object LollipopSoundEffects {

    private var soundPool: SoundPool? = null
    private var audioManager: AudioManager? = null

    // Sound IDs
    private var softDropSoundId: Int = 0
    private var softDropLightSoundId: Int = 0
    private var samsungDropSoundId: Int = 0
    private var samsungDropLightSoundId: Int = 0
    private var gentleBubbleSoundId: Int = 0
    private var buttonClickSoundId: Int = 0
    private var softPopSoundId: Int = 0

    private var isInitialized = false
    private var isLoaded = false
    private var lastWaterDropTime = 0L
    private var lastDragDropTime = 0L
    private var soundVariationCounter = 0

    // Direct AudioTrack static fallback for instant 0ms playback
    private var fallbackWaterTrack: AudioTrack? = null

    private const val SAMPLE_RATE = 44100

    fun init(context: Context) {
        if (isInitialized) return
        try {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val sp = SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build()

            sp.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) {
                    isLoaded = true
                }
            }

            val cacheDir = context.cacheDir

            // 1. Soft Natural Water Drop (Quiet, pleasant, gentle)
            val softDropFile = File(cacheDir, "soft_nature_drip_v4.wav")
            val softDropLightFile = File(cacheDir, "soft_nature_drip_light_v4.wav")
            val softDropPcm = generateSoftNatureDrip(pitchFactor = 1.0, durationMs = 120)
            val softDropLightPcm = generateSoftNatureDrip(pitchFactor = 1.15, durationMs = 105)
            if (!softDropFile.exists() || softDropFile.length() < 100) writeWavFile(softDropFile, softDropPcm)
            if (!softDropLightFile.exists() || softDropLightFile.length() < 100) writeWavFile(softDropLightFile, softDropLightPcm)

            // 2. Samsung Galaxy Nature UX Classic Drop
            val samsungDropFile = File(cacheDir, "samsung_nature_water_drop_v4.wav")
            val samsungDropLightFile = File(cacheDir, "samsung_nature_water_light_v4.wav")
            val samsungDropPcm = generateSamsungWaterDrop(pitchFactor = 1.0, durationMs = 150)
            val samsungDropLightPcm = generateSamsungWaterDrop(pitchFactor = 1.14, durationMs = 135)
            if (!samsungDropFile.exists() || samsungDropFile.length() < 100) writeWavFile(samsungDropFile, samsungDropPcm)
            if (!samsungDropLightFile.exists() || samsungDropLightFile.length() < 100) writeWavFile(samsungDropLightFile, samsungDropLightPcm)

            // 3. Gentle Bubble Pop
            val gentleBubbleFile = File(cacheDir, "gentle_bubble_v4.wav")
            val gentleBubblePcm = generateGentleBubble(durationMs = 95)
            if (!gentleBubbleFile.exists() || gentleBubbleFile.length() < 100) writeWavFile(gentleBubbleFile, gentleBubblePcm)

            // 4. UI Tactile Click & Pop
            val clickFile = File(cacheDir, "lollipop_click_v4.wav")
            val popFile = File(cacheDir, "lollipop_pop_v4.wav")
            val clickPcm = generateTactileClick(1400.0, 26, 0.95f)
            val popPcm = generateSoftNatureDrip(pitchFactor = 1.35, durationMs = 90)
            if (!clickFile.exists() || clickFile.length() < 100) writeWavFile(clickFile, clickPcm)
            if (!popFile.exists() || popFile.length() < 100) writeWavFile(popFile, popPcm)

            // Load into SoundPool
            softDropSoundId = sp.load(softDropFile.absolutePath, 1)
            softDropLightSoundId = sp.load(softDropLightFile.absolutePath, 1)
            samsungDropSoundId = sp.load(samsungDropFile.absolutePath, 1)
            samsungDropLightSoundId = sp.load(samsungDropLightFile.absolutePath, 1)
            gentleBubbleSoundId = sp.load(gentleBubbleFile.absolutePath, 1)
            buttonClickSoundId = sp.load(clickFile.absolutePath, 1)
            softPopSoundId = sp.load(popFile.absolutePath, 1)

            // Setup instant static AudioTrack fallback for immediate zero-latency play
            try {
                val byteBufferSize = softDropPcm.size * 2
                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(byteBufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(softDropPcm, 0, softDropPcm.size)
                fallbackWaterTrack = track
            } catch (e: Exception) {}

            soundPool = sp
            isInitialized = true
        } catch (ignored: Exception) {}
    }

    /**
     * Synthesizes an ultra-gentle, calm, relaxing natural water drop.
     * Warm, organic curve with low frequency, very soft envelope, zero harsh high chirps.
     */
    private fun generateSoftNatureDrip(
        pitchFactor: Double = 1.0,
        durationMs: Int = 120
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val twoPi = 2.0 * PI
        var phase = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            // Warm downward glide 510Hz -> 380Hz
            val freq = (510.0 - (130.0 * (t / (durationMs / 1000.0)).coerceIn(0.0, 1.0))) * pitchFactor
            phase += twoPi * freq / SAMPLE_RATE

            // Smooth attack and soft exponential decay
            val attack = if (t < 0.008) t / 0.008 else 1.0
            val decay = exp(-t * 29.0)
            val sampleValue = sin(phase) * attack * decay * 0.62

            buffer[i] = (sampleValue * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Synthesizes a soft, rounded, soothing bubble pop sound.
     */
    private fun generateGentleBubble(durationMs: Int = 95): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val twoPi = 2.0 * PI
        var phase = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            // Upward gentle curve 290Hz -> 510Hz
            val freq = 290.0 + (220.0 * Math.sqrt(t / (durationMs / 1000.0)).coerceIn(0.0, 1.0))
            phase += twoPi * freq / SAMPLE_RATE

            val attack = if (t < 0.006) t / 0.006 else 1.0
            val decay = exp(-t * 36.0)
            val sampleValue = sin(phase) * attack * decay * 0.55

            buffer[i] = (sampleValue * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Synthesizes the iconic Samsung Galaxy S3 / S4 Nature UX water drop ("bloop / drip / plink").
     */
    private fun generateSamsungWaterDrop(
        pitchFactor: Double = 1.0,
        durationMs: Int = 150
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val twoPi = 2.0 * PI
        var phaseMain = 0.0
        var phaseHarmonic = 0.0
        var phaseEcho = 0.0
        var phaseThump = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            // 1. Air cavity thump
            val thumpFreq = 160.0 * pitchFactor
            phaseThump += twoPi * thumpFreq / SAMPLE_RATE
            val thumpEnv = if (t < 0.012) sin(PI * (t / 0.012)) * 0.28 else 0.0

            // 2. Main droplet sweep & chime
            val fStart = 460.0 * pitchFactor
            val fPeak = 2260.0 * pitchFactor
            val chirpDuration = 0.038

            val currentFreq = if (t < chirpDuration) {
                val sweepProg = Math.pow(t / chirpDuration, 0.58)
                fStart + (fPeak - fStart) * sweepProg
            } else {
                fPeak
            }

            phaseMain += twoPi * currentFreq / SAMPLE_RATE
            phaseHarmonic += twoPi * (currentFreq * 2.0) / SAMPLE_RATE

            val attack = if (t < 0.004) t / 0.004 else 1.0
            val decay = exp(-t * 26.0)
            val mainEnv = attack * decay

            // 3. Rebound micro-echo
            var echoEnv = 0.0
            if (t > 0.060 && t < 0.125) {
                val tEcho = t - 0.060
                val echoFreq = 1750.0 * pitchFactor
                phaseEcho += twoPi * echoFreq / SAMPLE_RATE
                val echoAttack = if (tEcho < 0.005) tEcho / 0.005 else 1.0
                val echoDecay = exp(-tEcho * 42.0)
                echoEnv = echoAttack * echoDecay * 0.25
            }

            val sampleValue = (
                sin(phaseMain) * 0.70 +
                sin(phaseHarmonic) * 0.18 +
                sin(phaseThump) * thumpEnv +
                sin(phaseEcho) * echoEnv
            ) * mainEnv * 0.90

            buffer[i] = (sampleValue * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateTactileClick(
        freq: Double,
        durationMs: Int,
        volume: Float
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val twoPi = 2.0 * PI
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            phase += twoPi * freq / SAMPLE_RATE

            val decayEnvelope = exp(-progress * 13.0)
            val sampleValue = sin(phase) * decayEnvelope * volume
            buffer[i] = (sampleValue * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun writeWavFile(file: File, pcmData: ShortArray) {
        val dataSize = pcmData.size * 2
        val totalSize = 36 + dataSize
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

        header.put('R'.code.toByte()).put('I'.code.toByte()).put('F'.code.toByte()).put('F'.code.toByte())
        header.putInt(totalSize)
        header.put('W'.code.toByte()).put('A'.code.toByte()).put('V'.code.toByte()).put('E'.code.toByte())

        header.put('f'.code.toByte()).put('m'.code.toByte()).put('t'.code.toByte()).put(' '.code.toByte())
        header.putInt(16)
        header.putShort(1)
        header.putShort(1)
        header.putInt(SAMPLE_RATE)
        header.putInt(SAMPLE_RATE * 2)
        header.putShort(2)
        header.putShort(16)

        header.put('d'.code.toByte()).put('a'.code.toByte()).put('t'.code.toByte()).put('a'.code.toByte())
        header.putInt(dataSize)

        FileOutputStream(file).use { fos ->
            fos.write(header.array())
            val pcmBytes = ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in pcmData) {
                pcmBytes.putShort(sample)
            }
            fos.write(pcmBytes.array())
        }
    }

    /**
     * Plays water drop sound based on selected profile and volume.
     */
    fun playWaterDrop(
        enabled: Boolean = true,
        profile: WaterSoundProfile = WaterSoundProfile.SOFT_DROP,
        volume: Float = 0.5f,
        pitchVariant: Int? = null
    ) {
        if (!enabled || profile == WaterSoundProfile.MUTED) return
        val now = System.currentTimeMillis()
        if (now - lastWaterDropTime < 60) return
        lastWaterDropTime = now

        val sp = soundPool
        val variant = pitchVariant ?: (soundVariationCounter++ % 2)

        val soundId = when (profile) {
            WaterSoundProfile.SOFT_DROP -> {
                if (variant == 1 && softDropLightSoundId != 0) softDropLightSoundId else softDropSoundId
            }
            WaterSoundProfile.SAMSUNG_CLASSIC -> {
                if (variant == 1 && samsungDropLightSoundId != 0) samsungDropLightSoundId else samsungDropSoundId
            }
            WaterSoundProfile.GENTLE_BUBBLE -> {
                gentleBubbleSoundId
            }
            WaterSoundProfile.MUTED -> 0
        }

        val effectiveVolume = volume.coerceIn(0.1f, 1.0f)

        if (sp != null && soundId != 0 && isLoaded) {
            sp.play(soundId, effectiveVolume, effectiveVolume, 1, 0, 1.0f)
        } else if (profile != WaterSoundProfile.MUTED) {
            try {
                fallbackWaterTrack?.let { track ->
                    track.setVolume(effectiveVolume)
                    track.stop()
                    track.reloadStaticData()
                    track.play()
                } ?: run {
                    audioManager?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                }
            } catch (e: Exception) {
                audioManager?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
            }
        }
    }

    /**
     * Plays light water ripple sound during dragging across the screen.
     */
    fun playDragWaterRipple(
        enabled: Boolean = true,
        profile: WaterSoundProfile = WaterSoundProfile.SOFT_DROP,
        volume: Float = 0.5f
    ) {
        if (!enabled || profile == WaterSoundProfile.MUTED) return
        val now = System.currentTimeMillis()
        if (now - lastDragDropTime < 110) return
        lastDragDropTime = now

        val dragVolume = (volume * 0.70f).coerceIn(0.1f, 0.9f)
        val soundId = when (profile) {
            WaterSoundProfile.SOFT_DROP -> if (softDropLightSoundId != 0) softDropLightSoundId else softDropSoundId
            WaterSoundProfile.SAMSUNG_CLASSIC -> if (samsungDropLightSoundId != 0) samsungDropLightSoundId else samsungDropSoundId
            WaterSoundProfile.GENTLE_BUBBLE -> gentleBubbleSoundId
            WaterSoundProfile.MUTED -> 0
        }

        val sp = soundPool
        if (sp != null && soundId != 0 && isLoaded) {
            sp.play(soundId, dragVolume, dragVolume, 1, 0, 1.08f)
        } else {
            playWaterDrop(enabled = true, profile = profile, volume = dragVolume, pitchVariant = 1)
        }
    }

    /**
     * Plays tactile Material click sound.
     */
    fun playButtonClick(enabled: Boolean = true) {
        if (!enabled) return
        val sp = soundPool
        val id = buttonClickSoundId
        if (sp != null && id != 0 && isLoaded) {
            sp.play(id, 0.85f, 0.85f, 1, 0, 1.0f)
        } else {
            audioManager?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
        }
    }

    /**
     * Plays crisp pop sound for transitions.
     */
    fun playSoftPop(enabled: Boolean = true) {
        if (!enabled) return
        val sp = soundPool
        val id = softPopSoundId
        if (sp != null && id != 0 && isLoaded) {
            sp.play(id, 0.80f, 0.80f, 1, 0, 1.0f)
        } else {
            audioManager?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
        }
    }
}
