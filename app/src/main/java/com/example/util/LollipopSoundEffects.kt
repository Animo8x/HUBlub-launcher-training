package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, low-latency audio engine for authentic Samsung Galaxy S3/S4 Nature UX
 * Water Droplet sounds and tactile Material clicks.
 *
 * Uses AudioAttributes.USAGE_MEDIA and CONTENT_TYPE_MUSIC routed to STREAM_MUSIC
 * so sounds play with crystal-clear volume and are never muted by Android system touch sound toggles.
 * Features both SoundPool and zero-latency AudioTrack fallback.
 */
object LollipopSoundEffects {

    private var soundPool: SoundPool? = null
    private var audioManager: AudioManager? = null
    private var waterDropSoundId: Int = 0
    private var waterDropLightSoundId: Int = 0
    private var waterDropDeepSoundId: Int = 0
    private var buttonClickSoundId: Int = 0
    private var softPopSoundId: Int = 0
    private var isInitialized = false
    private var isLoaded = false
    private var lastWaterDropTime = 0L
    private var lastDragDropTime = 0L
    private var soundVariationCounter = 0

    // Direct AudioTrack static fallback for instant 0ms playback without waiting for SoundPool async load
    private var fallbackWaterTrack: AudioTrack? = null

    private const val SAMPLE_RATE = 44100

    fun init(context: Context) {
        if (isInitialized) return
        try {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

            // Use USAGE_MEDIA so sound is routed through STREAM_MUSIC (Media Volume)
            // System touch sound settings will NOT mute this!
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

            // Authentic Samsung Nature UX Water Droplets (v3)
            val dropFile = File(cacheDir, "samsung_nature_water_drop_v3.wav")
            val dropLightFile = File(cacheDir, "samsung_nature_water_light_v3.wav")
            val dropDeepFile = File(cacheDir, "samsung_nature_water_deep_v3.wav")
            val clickFile = File(cacheDir, "lollipop_click_v3.wav")
            val popFile = File(cacheDir, "lollipop_pop_v3.wav")

            val mainDropPcm = generateSamsungWaterDrop(pitchFactor = 1.0, durationMs = 150)
            if (!dropFile.exists() || dropFile.length() < 100) {
                writeWavFile(dropFile, mainDropPcm)
            }

            if (!dropLightFile.exists() || dropLightFile.length() < 100) {
                val lightDropPcm = generateSamsungWaterDrop(pitchFactor = 1.14, durationMs = 135)
                writeWavFile(dropLightFile, lightDropPcm)
            }

            if (!dropDeepFile.exists() || dropDeepFile.length() < 100) {
                val deepDropPcm = generateSamsungWaterDrop(pitchFactor = 0.88, durationMs = 165)
                writeWavFile(dropDeepFile, deepDropPcm)
            }

            if (!clickFile.exists() || clickFile.length() < 100) {
                val clickPcm = generateTactileClick(1400.0, 26, 0.95f)
                writeWavFile(clickFile, clickPcm)
            }

            if (!popFile.exists() || popFile.length() < 100) {
                val popPcm = generateSamsungWaterDrop(pitchFactor = 1.25, durationMs = 120)
                writeWavFile(popFile, popPcm)
            }

            waterDropSoundId = sp.load(dropFile.absolutePath, 1)
            waterDropLightSoundId = sp.load(dropLightFile.absolutePath, 1)
            waterDropDeepSoundId = sp.load(dropDeepFile.absolutePath, 1)
            buttonClickSoundId = sp.load(clickFile.absolutePath, 1)
            softPopSoundId = sp.load(popFile.absolutePath, 1)

            // Setup instant static AudioTrack fallback for immediate play
            try {
                val byteBufferSize = mainDropPcm.size * 2
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
                track.write(mainDropPcm, 0, mainDropPcm.size)
                fallbackWaterTrack = track
            } catch (e: Exception) {}

            soundPool = sp
            isInitialized = true
        } catch (ignored: Exception) {}
    }

    /**
     * Synthesizes the iconic Samsung Galaxy S3 / S4 Nature UX water drop ("bloop / drip / plink").
     * Acoustic Profile:
     * 1. Air cavity thump: Initial 0-10ms resonant thump as the droplet penetrates the water surface.
     * 2. Rapid upward bubble chirp (8-40ms): Air cavity pinches off, frequency sweeps from 460Hz to ~2250Hz.
     * 3. Bell-like chime resonance (40-150ms): Rings at ~2220Hz with secondary harmonic overtone and exponential decay.
     * 4. Secondary micro-droplet echo (65-110ms): Rebound micro-drop sound.
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

            // 1. Air cavity thump (first 10ms)
            val thumpFreq = 160.0 * pitchFactor
            phaseThump += twoPi * thumpFreq / SAMPLE_RATE
            val thumpEnv = if (t < 0.012) sin(PI * (t / 0.012)) * 0.32 else 0.0

            // 2. Main droplet sweep & chime
            val fStart = 460.0 * pitchFactor
            val fPeak = 2260.0 * pitchFactor
            val chirpDuration = 0.038 // 38ms upward sweep

            val currentFreq = if (t < chirpDuration) {
                val sweepProg = Math.pow(t / chirpDuration, 0.58)
                fStart + (fPeak - fStart) * sweepProg
            } else {
                fPeak
            }

            phaseMain += twoPi * currentFreq / SAMPLE_RATE
            phaseHarmonic += twoPi * (currentFreq * 2.0) / SAMPLE_RATE

            // Main droplet envelope: instant attack and natural liquid decay
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
                echoEnv = echoAttack * echoDecay * 0.30
            }

            val sampleValue = (
                sin(phaseMain) * 0.72 +
                sin(phaseHarmonic) * 0.20 +
                sin(phaseThump) * thumpEnv +
                sin(phaseEcho) * echoEnv
            ) * mainEnv * 0.98

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

        // RIFF chunk
        header.put('R'.code.toByte()).put('I'.code.toByte()).put('F'.code.toByte()).put('F'.code.toByte())
        header.putInt(totalSize)
        header.put('W'.code.toByte()).put('A'.code.toByte()).put('V'.code.toByte()).put('E'.code.toByte())

        // fmt chunk
        header.put('f'.code.toByte()).put('m'.code.toByte()).put('t'.code.toByte()).put(' '.code.toByte())
        header.putInt(16)
        header.putShort(1)  // PCM
        header.putShort(1)  // Mono
        header.putInt(SAMPLE_RATE)
        header.putInt(SAMPLE_RATE * 2)
        header.putShort(2)
        header.putShort(16)

        // data chunk
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
     * Plays authentic Samsung Galaxy Nature UX water droplet sound.
     * Features alternating pitch variations (Standard, High, Deep) for organic realism.
     */
    fun playWaterDrop(enabled: Boolean = true, pitchVariant: Int? = null) {
        if (!enabled) return
        val now = System.currentTimeMillis()
        if (now - lastWaterDropTime < 65) return
        lastWaterDropTime = now

        val sp = soundPool
        val variant = pitchVariant ?: (soundVariationCounter++ % 3)
        val soundId = when (variant) {
            1 -> if (waterDropLightSoundId != 0) waterDropLightSoundId else waterDropSoundId
            2 -> if (waterDropDeepSoundId != 0) waterDropDeepSoundId else waterDropSoundId
            else -> waterDropSoundId
        }

        if (sp != null && soundId != 0 && isLoaded) {
            sp.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        } else {
            // Instant playback via AudioTrack fallback
            try {
                fallbackWaterTrack?.let { track ->
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
     * Plays light water ripple sound during dragging across the glass screen.
     */
    fun playDragWaterRipple(enabled: Boolean = true) {
        if (!enabled) return
        val now = System.currentTimeMillis()
        if (now - lastDragDropTime < 110) return
        lastDragDropTime = now

        val sp = soundPool
        val soundId = if (waterDropLightSoundId != 0) waterDropLightSoundId else waterDropSoundId
        if (sp != null && soundId != 0 && isLoaded) {
            sp.play(soundId, 0.75f, 0.75f, 1, 0, 1.08f)
        } else {
            playWaterDrop(enabled = true, pitchVariant = 1)
        }
    }

    /**
     * Plays tactile Material click sound with audible feedback.
     */
    fun playButtonClick(enabled: Boolean = true) {
        if (!enabled) return
        val sp = soundPool
        val id = buttonClickSoundId
        if (sp != null && id != 0 && isLoaded) {
            sp.play(id, 0.95f, 0.95f, 1, 0, 1.0f)
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
            sp.play(id, 0.90f, 0.90f, 1, 0, 1.0f)
        } else {
            audioManager?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
        }
    }
}
