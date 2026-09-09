package com.example.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.model.WaterEffectMode
import com.example.model.WaterSoundProfile
import com.example.util.LollipopSoundEffects
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/**
 * Trailing wet water trace segment left behind when a droplet slides or rolls on glass.
 */
private class WetTrailPoint(
    val x: Float,
    val y: Float,
    val radius: Float,
    val timestampNanos: Long
)

/**
 * Physical Realistic Water Droplet with Surface Tension, Gravity Tilt Physics, and Organic Growth.
 */
private class RealisticWaterDrop(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val birthNanos: Long,
    var baseRadiusPx: Float,
    var targetRadiusPx: Float,
    var isHeldDown: Boolean = true,
    var alpha: Float = 1.0f,
    var lastMoveNanos: Long = birthNanos
) {
    val trail = mutableListOf<WetTrailPoint>()
}

/**
 * Concentric wave packet for Galaxy Nature Ripple mode.
 */
private class NatureWaterRipple(
    val id: Long,
    val centerX: Float,
    val centerY: Float,
    val startTimeNanos: Long,
    val durationMs: Float,
    val maxRadiusPx: Float,
    val wavelengthPx: Float,
    val isDragWake: Boolean,
    val intensity: Float
)

/**
 * Ultra-Realistic Samsung Liquid Glass & Water Droplet Physics Engine.
 *
 * Recreates the iconic Samsung Galaxy TouchWiz "Water Droplet" & "Ripple" unlock and touch effects:
 * - REALISTIC WATER DROPLET (نمط قطرة الماء الحقيقي):
 *   - Looks like real crystal water on the smartphone glass screen from near and far.
 *   - PRESS & HOLD GROWTH: Holding finger down pools more water and organically swells the droplet.
 *   - GRAVITY & TILT SENSOR (مستشعر الميلان): Accelerometer guides physical rolling/sliding of drops
 *     towards whichever way the phone is tilted (Right, Left, Up, Down).
 *   - WET GLISTENING TRAIL: Rolling drops leave evaporating moist trails on the glass.
 *   - OPTICAL CAUSTICS: Specular top-left sunlight glints, bottom-right total internal reflection,
 *     and ambient contact shadows for true 3D liquid elevation.
 * - GALAXY NATURE RIPPLE: Multi-ring concentric wave propagation.
 * - CUSTOMIZABLE SOOTHING ACOUSTICS: Non-annoying soft drop, classic Samsung bloop, or bubble pop.
 */
@Composable
fun LollipopTouchRippleContainer(
    soundEnabled: Boolean = true,
    rippleEnabled: Boolean = true,
    waterEffectMode: WaterEffectMode = WaterEffectMode.WATER_DROPLET,
    soundProfile: WaterSoundProfile = WaterSoundProfile.SOFT_DROP,
    soundVolume: Float = 0.5f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Tilt gravity values (screen X & Y acceleration in m/s²)
    var gravityX by remember { mutableFloatStateOf(0f) }
    var gravityY by remember { mutableFloatStateOf(4.5f) } // Default slight downward pull

    // Register Accelerometer for device tilt gravity
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.values.size >= 2) {
                    // In portrait orientation:
                    // event.values[0] is X (positive when tilted left -> gravity pulls right)
                    // event.values[1] is Y (positive when upright -> gravity pulls down)
                    val rawGx = -event.values[0]
                    val rawGy = event.values[1]

                    // Smooth low-pass filter (90% previous, 10% new) to eliminate sensor jitter
                    gravityX = gravityX * 0.88f + rawGx * 0.12f
                    gravityY = gravityY * 0.88f + rawGy * 0.12f
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Active Water Drops and Ripples
    val activeDrops = remember { mutableStateListOf<RealisticWaterDrop>() }
    val activeRipples = remember { mutableStateListOf<NatureWaterRipple>() }
    var currentFrameNanos by remember { mutableLongStateOf(0L) }

    val initialDropRadiusPx = with(density) { 20.dp.toPx() }
    val maxHeldDropRadiusPx = with(density) { 62.dp.toPx() }
    val splashMaxRadiusPx = with(density) { 180.dp.toPx() }
    val wavelengthPx = with(density) { 24.dp.toPx() }

    val isEffectActive = rippleEnabled && waterEffectMode != WaterEffectMode.DISABLED

    // Physics Animation Frame Loop: Runs smoothly at 60/120 FPS when any drops or ripples exist
    LaunchedEffect(isEffectActive, activeDrops.isNotEmpty() || activeRipples.isNotEmpty()) {
        if (!isEffectActive) return@LaunchedEffect

        var lastNanos = System.nanoTime()

        while (activeDrops.isNotEmpty() || activeRipples.isNotEmpty()) {
            withFrameNanos { frameTimeNanos ->
                currentFrameNanos = frameTimeNanos
                val dtSec = ((frameTimeNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = frameTimeNanos

                // 1. UPDATE REALISTIC WATER DROPS
                val dropIterator = activeDrops.iterator()
                while (dropIterator.hasNext()) {
                    val drop = dropIterator.next()
                    val holdElapsedSec = (frameTimeNanos - drop.birthNanos) / 1_000_000_000f

                    // When holding down: the droplet organically grows larger!
                    if (drop.isHeldDown) {
                        val growthProgress = (holdElapsedSec / 1.8f).coerceIn(0f, 1f)
                        val targetR = initialDropRadiusPx + (maxHeldDropRadiusPx - initialDropRadiusPx) * growthProgress
                        drop.baseRadiusPx = drop.baseRadiusPx * 0.92f + targetR * 0.08f
                        drop.lastMoveNanos = frameTimeNanos
                    } else {
                        // After finger release: drop slowly lives on glass, responds to tilt, then evaporates
                        val idleElapsedSec = (frameTimeNanos - drop.lastMoveNanos) / 1_000_000_000f
                        if (idleElapsedSec > 4.5f) {
                            drop.alpha -= dtSec * 0.6f
                        }
                    }

                    // Physics: Apply tilt gravity acceleration
                    val gravityStrength = 85f * (drop.baseRadiusPx / initialDropRadiusPx)
                    drop.vx += gravityX * gravityStrength * dtSec
                    drop.vy += gravityY * gravityStrength * dtSec

                    // Fluid surface viscosity and glass friction
                    drop.vx *= 0.94f
                    drop.vy *= 0.94f

                    val speed = hypot(drop.vx, drop.vy)
                    if (speed > 1.5f) {
                        drop.x += drop.vx * dtSec * 60f
                        drop.y += drop.vy * dtSec * 60f

                        // Add wet trail point when moving
                        if (drop.trail.size > 24) {
                            drop.trail.removeAt(0)
                        }
                        drop.trail.add(
                            WetTrailPoint(
                                x = drop.x,
                                y = drop.y,
                                radius = drop.baseRadiusPx * 0.45f,
                                timestampNanos = frameTimeNanos
                            )
                        )
                        drop.lastMoveNanos = frameTimeNanos
                    }

                    // Evaporate old trail points (> 1.6s)
                    drop.trail.removeAll { (frameTimeNanos - it.timestampNanos) > 1_600_000_000L }

                    // Remove completely evaporated or off-screen drops
                    if (drop.alpha <= 0.02f) {
                        dropIterator.remove()
                    }
                }

                // 2. UPDATE CONCENTRIC WAVES
                val rippleIterator = activeRipples.iterator()
                while (rippleIterator.hasNext()) {
                    val ripple = rippleIterator.next()
                    val elapsedMs = (frameTimeNanos - ripple.startTimeNanos) / 1_000_000f
                    if (elapsedMs >= ripple.durationMs) {
                        rippleIterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(soundEnabled, rippleEnabled, waterEffectMode, soundProfile, soundVolume) {
                if (!rippleEnabled && !soundEnabled) return@pointerInput

                var nextDropId = 0L
                var currentHeldDrop: RealisticWaterDrop? = null
                var lastTouchPos = Offset.Zero
                var isFingerDown = false

                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val changes = event.changes

                        if (changes.isNotEmpty()) {
                            val change = changes[0]
                            val pos = change.position

                            when (event.type) {
                                PointerEventType.Press -> {
                                    isFingerDown = true
                                    lastTouchPos = pos

                                    // 1. Play chosen soothing sound profile
                                    if (soundEnabled) {
                                        LollipopSoundEffects.playWaterDrop(
                                            enabled = true,
                                            profile = soundProfile,
                                            volume = soundVolume
                                        )
                                    }

                                    // 2. Spawn Realistic Water Droplet
                                    if (rippleEnabled && (waterEffectMode == WaterEffectMode.WATER_DROPLET || waterEffectMode == WaterEffectMode.HYBRID_BOTH)) {
                                        val nowNanos = System.nanoTime()
                                        if (activeDrops.size > 8) {
                                            activeDrops.removeAt(0)
                                        }
                                        val newDrop = RealisticWaterDrop(
                                            id = nextDropId++,
                                            x = pos.x,
                                            y = pos.y,
                                            birthNanos = nowNanos,
                                            baseRadiusPx = initialDropRadiusPx,
                                            targetRadiusPx = initialDropRadiusPx,
                                            isHeldDown = true,
                                            alpha = 1.0f
                                        )
                                        activeDrops.add(newDrop)
                                        currentHeldDrop = newDrop
                                    }

                                    // 3. Spawn Concentric Wave Ripples if mode is GALAXY_RIPPLE or HYBRID_BOTH
                                    if (rippleEnabled && (waterEffectMode == WaterEffectMode.GALAXY_RIPPLE || waterEffectMode == WaterEffectMode.HYBRID_BOTH)) {
                                        val nowNanos = System.nanoTime()
                                        if (activeRipples.size > 8) {
                                            activeRipples.removeAt(0)
                                        }
                                        activeRipples.add(
                                            NatureWaterRipple(
                                                id = nextDropId++,
                                                centerX = pos.x,
                                                centerY = pos.y,
                                                startTimeNanos = nowNanos,
                                                durationMs = 750f,
                                                maxRadiusPx = splashMaxRadiusPx,
                                                wavelengthPx = wavelengthPx,
                                                isDragWake = false,
                                                intensity = 1.0f
                                            )
                                        )
                                    }
                                }

                                PointerEventType.Move -> {
                                    if (isFingerDown) {
                                        val dist = hypot(pos.x - lastTouchPos.x, pos.y - lastTouchPos.y)
                                        if (dist >= 12f) {
                                            lastTouchPos = pos

                                            // Drag water droplet under finger
                                            currentHeldDrop?.let { drop ->
                                                drop.vx = (pos.x - drop.x) * 4f
                                                drop.vy = (pos.y - drop.y) * 4f
                                                drop.x = pos.x
                                                drop.y = pos.y
                                            }

                                            // Light audio while sliding
                                            if (soundEnabled) {
                                                LollipopSoundEffects.playDragWaterRipple(
                                                    enabled = true,
                                                    profile = soundProfile,
                                                    volume = soundVolume
                                                )
                                            }

                                            // Ripples on drag if in ripple mode
                                            if (rippleEnabled && (waterEffectMode == WaterEffectMode.GALAXY_RIPPLE || waterEffectMode == WaterEffectMode.HYBRID_BOTH)) {
                                                val nowNanos = System.nanoTime()
                                                if (activeRipples.size > 8) {
                                                    activeRipples.removeAt(0)
                                                }
                                                activeRipples.add(
                                                    NatureWaterRipple(
                                                        id = nextDropId++,
                                                        centerX = pos.x,
                                                        centerY = pos.y,
                                                        startTimeNanos = nowNanos,
                                                        durationMs = 450f,
                                                        maxRadiusPx = 65f,
                                                        wavelengthPx = wavelengthPx * 0.75f,
                                                        isDragWake = true,
                                                        intensity = 0.65f
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                PointerEventType.Release -> {
                                    isFingerDown = false
                                    currentHeldDrop?.isHeldDown = false
                                    currentHeldDrop = null
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // 1. Application Layer (Completely unmodified)
        content()

        // 2. Liquid Optics Canvas: Realistic Water Droplets, Caustics, and Concentric Ripples
        if (isEffectActive && (activeDrops.isNotEmpty() || activeRipples.isNotEmpty())) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val nowNanos = if (currentFrameNanos > 0) currentFrameNanos else System.nanoTime()

                // =========================================================================
                // 1. CONCENTRIC GALAXY RIPPLE WAVES (Samsung Nature UX)
                // =========================================================================
                if (waterEffectMode == WaterEffectMode.GALAXY_RIPPLE || waterEffectMode == WaterEffectMode.HYBRID_BOTH) {
                    for (ripple in activeRipples) {
                        val elapsedMs = (nowNanos - ripple.startTimeNanos) / 1_000_000f
                        if (elapsedMs < 0f || elapsedMs > ripple.durationMs) continue

                        val progress = (elapsedMs / ripple.durationMs).coerceIn(0f, 1f)
                        val center = Offset(ripple.centerX, ripple.centerY)
                        val waveRadius = ripple.maxRadiusPx * (progress.pow(0.72f))
                        val baseAlpha = (1f - progress).pow(1.35f) * ripple.intensity

                        val ringCount = if (ripple.isDragWake) 2 else 3
                        val lambda = ripple.wavelengthPx

                        for (ringIndex in 0 until ringCount) {
                            val crestRadius = waveRadius - (ringIndex * lambda)
                            if (crestRadius <= 2f) continue

                            val ringDecay = (1f - (ringIndex * 0.25f)).coerceAtLeast(0.15f)
                            val ringAlpha = (baseAlpha * ringDecay).coerceIn(0f, 1f)
                            if (ringAlpha < 0.01f) continue

                            val strokeWidthPx = ((2.4f - ringIndex * 0.4f) * (1f - progress * 0.4f)).coerceAtLeast(1.0f).dp.toPx()

                            // Refraction Shadow
                            drawCircle(
                                color = Color(0xFF006064).copy(alpha = ringAlpha * 0.20f),
                                radius = crestRadius + (strokeWidthPx * 0.6f),
                                center = center,
                                style = Stroke(width = strokeWidthPx * 1.2f)
                            )
                            // Refractive Cyan Wave Body
                            drawCircle(
                                color = Color(0xFF4DD0E1).copy(alpha = ringAlpha * 0.35f),
                                radius = crestRadius,
                                center = center,
                                style = Stroke(width = strokeWidthPx)
                            )
                            // Specular Crest Glint
                            drawCircle(
                                color = Color(0xFFFFFFFF).copy(alpha = ringAlpha * 0.42f),
                                radius = crestRadius - (strokeWidthPx * 0.35f),
                                center = center,
                                style = Stroke(width = strokeWidthPx * 0.75f)
                            )
                        }
                    }
                }

                // =========================================================================
                // 2. ULTRA-REALISTIC LIQUID GLASS WATER DROPLETS (Samsung Water Droplet)
                // =========================================================================
                if (waterEffectMode == WaterEffectMode.WATER_DROPLET || waterEffectMode == WaterEffectMode.HYBRID_BOTH) {
                    for (drop in activeDrops) {
                        if (drop.alpha < 0.01f) continue

                        val center = Offset(drop.x, drop.y)
                        val holdTimeSec = (nowNanos - drop.birthNanos) / 1_000_000_000f

                        // Subtle fluid breathing oscillation of the droplet surface tension
                        val breathing = if (drop.isHeldDown) sin(holdTimeSec * 5.0f) * 1.5f else 0f
                        val r = (drop.baseRadiusPx + breathing).coerceAtLeast(10f)

                        // --- A. WET TRAIL (Moisture left behind as droplet slides) ---
                        for (trailPoint in drop.trail) {
                            val trailAgeSec = (nowNanos - trailPoint.timestampNanos) / 1_000_000_000f
                            val trailAlpha = ((1f - (trailAgeSec / 1.6f)) * 0.35f * drop.alpha).coerceIn(0f, 1f)
                            if (trailAlpha > 0.01f) {
                                drawCircle(
                                    color = Color(0x6080DEEA).copy(alpha = trailAlpha),
                                    radius = trailPoint.radius,
                                    center = Offset(trailPoint.x, trailPoint.y)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = trailAlpha * 0.6f),
                                    radius = trailPoint.radius * 0.5f,
                                    center = Offset(trailPoint.x - trailPoint.radius * 0.2f, trailPoint.y - trailPoint.radius * 0.2f)
                                )
                            }
                        }

                        // --- B. CONTACT SHADOW (Lifts the water droplet off the screen glass) ---
                        // Positioned slightly towards bottom-right (ambient sunlight angle)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x3D00121C).copy(alpha = drop.alpha * 0.35f),
                                    Color(0x1F00121C).copy(alpha = drop.alpha * 0.18f),
                                    Color.Transparent
                                ),
                                center = center + Offset(r * 0.15f, r * 0.22f),
                                radius = r * 1.28f
                            ),
                            radius = r * 1.28f,
                            center = center + Offset(r * 0.15f, r * 0.22f)
                        )

                        // --- C. WATER LENS BODY (Convex Meniscus with Liquid Refraction) ---
                        // Real water is crystal transparent at the center, with dense refraction at the meniscus edge
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x0AE0F7FA).copy(alpha = drop.alpha * 0.15f), // Clear center
                                    Color(0x2880DEEA).copy(alpha = drop.alpha * 0.35f), // Translucent water body
                                    Color(0x754DD0E1).copy(alpha = drop.alpha * 0.65f), // Concentrated cyan water rim
                                    Color(0x95006064).copy(alpha = drop.alpha * 0.75f)  // Surface tension boundary
                                ),
                                center = center,
                                radius = r
                            ),
                            radius = r,
                            center = center
                        )

                        // --- D. HIGH SURFACE TENSION RIM (Meniscus Edge Highlight) ---
                        drawCircle(
                            color = Color(0xFFE0F7FA).copy(alpha = drop.alpha * 0.70f),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.4f.dp.toPx())
                        )

                        // --- E. PRIMARY SPECULAR SUNLIGHT GLINT (Top-Left Curved Highlight) ---
                        // This sharp, bright glint makes it look 100% like real physical water!
                        val glintRadius = r * 0.28f
                        val glintCenter = center - Offset(r * 0.38f, r * 0.38f)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = drop.alpha * 0.95f),
                                    Color.White.copy(alpha = drop.alpha * 0.60f),
                                    Color.Transparent
                                ),
                                center = glintCenter,
                                radius = glintRadius
                            ),
                            radius = glintRadius,
                            center = glintCenter
                        )

                        // Primary glint pinpoint
                        drawCircle(
                            color = Color.White.copy(alpha = drop.alpha * 0.98f),
                            radius = (glintRadius * 0.45f).coerceAtLeast(1.8f),
                            center = glintCenter - Offset(1f, 1f)
                        )

                        // --- F. SECONDARY TOTAL INTERNAL REFLECTION (Bottom-Right Interior Glow) ---
                        // Light reflecting back from inside the curved water bead
                        val innerReflectionCenter = center + Offset(r * 0.34f, r * 0.34f)
                        val innerRadius = r * 0.26f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = drop.alpha * 0.50f),
                                    Color(0x8080DEEA).copy(alpha = drop.alpha * 0.30f),
                                    Color.Transparent
                                ),
                                center = innerReflectionCenter,
                                radius = innerRadius
                            ),
                            radius = innerRadius,
                            center = innerReflectionCenter
                        )

                        // --- G. MICRO SATELLITE WATER BEADS (When drop grows large) ---
                        if (r > 38f) {
                            val micro1 = center + Offset(-r * 0.95f, r * 0.65f)
                            drawCircle(
                                color = Color(0x7080DEEA).copy(alpha = drop.alpha * 0.50f),
                                radius = 3.5f,
                                center = micro1
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = drop.alpha * 0.85f),
                                radius = 1.2f,
                                center = micro1 - Offset(1f, 1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
