package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, low-latency audio feedback using Android SoundPool.
 * Sounds are pre-synthesized into tiny WAV files once in cache to ensure 0ms latency,
 * zero GC allocations during touches, and no hardware AudioTrack exhaustion.
 */
object LollipopSoundEffects {

    private var soundPool: SoundPool? = null
    private var waterDropSoundId: Int = 0
    private var buttonClickSoundId: Int = 0
    private var softPopSoundId: Int = 0
    private var isInitialized = false
    private var lastWaterDropTime = 0L

    private const val SAMPLE_RATE = 44100

    fun init(context: Context) {
        if (isInitialized) return
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val sp = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()

            val cacheDir = context.cacheDir
            val dropFile = File(cacheDir, "lollipop_drop.wav")
            val clickFile = File(cacheDir, "lollipop_click.wav")
            val popFile = File(cacheDir, "lollipop_pop.wav")

            if (!dropFile.exists()) {
                val dropPcm = generateWaterDropTone(880.0, 620.0, 32, 0.12f)
                writeWavFile(dropFile, dropPcm)
            }
            if (!clickFile.exists()) {
                val clickPcm = generateClickTone(1200.0, 16, 0.10f)
                writeWavFile(clickFile, clickPcm)
            }
            if (!popFile.exists()) {
                val popPcm = generateWaterDropTone(560.0, 380.0, 38, 0.14f)
                writeWavFile(popFile, popPcm)
            }

            waterDropSoundId = sp.load(dropFile.absolutePath, 1)
            buttonClickSoundId = sp.load(clickFile.absolutePath, 1)
            softPopSoundId = sp.load(popFile.absolutePath, 1)

            soundPool = sp
            isInitialized = true
        } catch (ignored: Exception) {}
    }

    private fun generateWaterDropTone(
        freqStart: Double,
        freqEnd: Double,
        durationMs: Int,
        volume: Float
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val twoPi = 2.0 * PI
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = freqStart + (freqEnd - freqStart) * progress
            phase += twoPi * currentFreq / SAMPLE_RATE

            val attackEnvelope = if (progress < 0.12) progress / 0.12 else 1.0
            val decayEnvelope = exp(-progress * 7.5)
            val envelope = attackEnvelope * decayEnvelope

            val sampleValue = sin(phase) * envelope * volume
            buffer[i] = (sampleValue * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateClickTone(
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

            val decayEnvelope = exp(-progress * 14.0)
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
        header.putInt(16) // Subchunk1Size for PCM
        header.putShort(1)  // AudioFormat 1 = PCM
        header.putShort(1)  // NumChannels 1 = Mono
        header.putInt(SAMPLE_RATE)
        header.putInt(SAMPLE_RATE * 2) // ByteRate = SampleRate * NumChannels * BitsPerSample/8
        header.putShort(2)  // BlockAlign = NumChannels * BitsPerSample/8
        header.putShort(16) // BitsPerSample

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
     * Plays gentle water droplet sound, throttled to prevent audio overlap during drags.
     */
    fun playWaterDrop(enabled: Boolean = true) {
        if (!enabled) return
        val now = System.currentTimeMillis()
        if (now - lastWaterDropTime < 85) return
        lastWaterDropTime = now

        val sp = soundPool ?: return
        val id = waterDropSoundId
        if (id != 0) {
            sp.play(id, 0.55f, 0.55f, 1, 0, 1.0f)
        }
    }

    /**
     * Plays tactile Material click sound.
     */
    fun playButtonClick(enabled: Boolean = true) {
        if (!enabled) return
        val sp = soundPool ?: return
        val id = buttonClickSoundId
        if (id != 0) {
            sp.play(id, 0.5f, 0.5f, 1, 0, 1.0f)
        }
    }

    /**
     * Plays soft pop sound for transitions.
     */
    fun playSoftPop(enabled: Boolean = true) {
        if (!enabled) return
        val sp = soundPool ?: return
        val id = softPopSoundId
        if (id != 0) {
            sp.play(id, 0.6f, 0.6f, 1, 0, 1.0f)
        }
    }
}
