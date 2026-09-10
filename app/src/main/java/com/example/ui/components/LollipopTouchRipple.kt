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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.model.WaterEffectMode
import com.example.model.WaterSoundProfile
import com.example.util.LollipopSoundEffects
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

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
 * Transient center impact droplet that forms immediately upon tapping, shines,
 * and smoothly evaporates as concentric waves expand.
 */
private class ImpactCenterDroplet(
    val id: Long,
    val centerX: Float,
    val centerY: Float,
    val startTimeNanos: Long,
    val durationMs: Float = 680f,
    val initialRadiusPx: Float
)

/**
 * Persistent 3D liquid water bead with surface tension, growth on press & hold,
 * and optional slow, realistic tilt gravity gliding.
 */
private class RealisticWaterDrop(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val birthNanos: Long,
    var baseRadiusPx: Float,
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
 * Electric lightning bolt arc for ELECTRIC_AQUA mode.
 */
private class ElectricBolt(
    val id: Long,
    val startTimeNanos: Long,
    val durationMs: Float = 380f,
    val points: List<Offset>,
    val color: Color
)

/**
 * Dazzling twinkling 4-point star for STARLIGHT_SPARKLE mode.
 */
private class StarlightParticle(
    val id: Long,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val startTimeNanos: Long,
    val durationMs: Float = 550f,
    val sizePx: Float,
    val rotationDeg: Float,
    val color: Color
)

/**
 * Minimalist Zen spring dew pearl for ZEN_SPRING mode.
 */
private class ZenDewPearl(
    val id: Long,
    var x: Float,
    var y: Float,
    val startTimeNanos: Long,
    val durationMs: Float = 850f,
    val radiusPx: Float,
    val floatAngle: Float
)

/**
 * Helper function that renders an ultra-realistic, clearly visible 3D water droplet:
 * - Semi-translucent visible aqua body (not overly faint or invisible)
 * - Soft offset contact shadow giving 3D elevation off the screen
 * - Dark ground occlusion rim anchoring the droplet
 * - Concentrated bottom-right caustic glow
 * - High-contrast surface tension meniscus highlight ring
 * - Top-left curved specular sunlight crescent highlight + pinpoint sun reflection
 * - Secondary bottom-right ambient rim glint
 */
private fun DrawScope.draw3DWaterDroplet(
    centerX: Float,
    centerY: Float,
    radius: Float,
    alpha: Float,
    wobblePhase: Float = 0f
) {
    if (alpha <= 0.01f || radius <= 2f) return

    val center = Offset(centerX, centerY)
    val r = radius + (if (wobblePhase != 0f) sin(wobblePhase) * (radius * 0.04f) else 0f)

    // 1. SOFT CONTACT DROP SHADOW (Elevates droplet in 3D above the wallpaper)
    val shadowOffset = Offset(r * 0.22f, r * 0.28f)
    val shadowRadius = r * 1.35f
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0x66011420).copy(alpha = alpha * 0.55f),
                0.62f to Color(0x28011420).copy(alpha = alpha * 0.25f),
                1.0f to Color.Transparent
            ),
            center = center + shadowOffset,
            radius = shadowRadius
        ),
        radius = shadowRadius,
        center = center + shadowOffset
    )

    // 2. TIGHT GROUND OCCLUSION SHADOW (Anchors the droplet base firmly to the glass)
    drawArc(
        color = Color(0x7500151E).copy(alpha = alpha * 0.65f),
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(center.x - r, center.y - r + r * 0.08f),
        size = Size(r * 2f, r * 2f),
        style = Stroke(width = (r * 0.09f).coerceIn(1.8f, 3.8f))
    )

    // 3. FOCUSED OPTICAL CAUSTIC GLOW (Sunlight concentrated into bottom-right crescent)
    val causticCenter = center + Offset(r * 0.32f, r * 0.32f)
    val causticRadius = r * 0.54f
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFFFFFFFF).copy(alpha = alpha * 0.85f),
                0.40f to Color(0xB080DEEA).copy(alpha = alpha * 0.65f),
                1.0f to Color.Transparent
            ),
            center = causticCenter,
            radius = causticRadius
        ),
        radius = causticRadius,
        center = causticCenter
    )

    // 4. RICH SEMI-TRANSLUCENT WATER LENS BODY
    // Crystal clear yet vibrantly visible aqua/cyan liquid dome
    val lensCenter = center - Offset(r * 0.12f, r * 0.12f)
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0x4580DEEA).copy(alpha = alpha * 0.60f), // Semi-translucent clear aqua
                0.58f to Color(0x7000BCD4).copy(alpha = alpha * 0.75f), // Rich turquoise water body
                0.85f to Color(0x9E00838F).copy(alpha = alpha * 0.88f), // Deep refractive ring
                1.0f to Color(0xD0004D40).copy(alpha = alpha * 0.95f)  // Total internal reflection meniscus
            ),
            center = lensCenter,
            radius = r
        ),
        radius = r,
        center = center
    )

    // 5. CRISP SURFACE TENSION MENISCUS HIGHLIGHT RING
    drawCircle(
        color = Color(0xDDFFFFFF).copy(alpha = alpha * 0.85f),
        radius = r,
        center = center,
        style = Stroke(width = (r * 0.06f).coerceIn(1.5f, 3.0f))
    )

    // 6. PRIMARY SPECULAR SUNLIGHT GLINT (Curved crescent arc along the top-left dome)
    val glintAuraCenter = center - Offset(r * 0.38f, r * 0.38f)
    val glintAuraRadius = r * 0.36f
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color.White.copy(alpha = alpha * 0.95f),
                0.55f to Color.White.copy(alpha = alpha * 0.45f),
                1.0f to Color.Transparent
            ),
            center = glintAuraCenter,
            radius = glintAuraRadius
        ),
        radius = glintAuraRadius,
        center = glintAuraCenter
    )

    // Sharp curved specular highlight arc
    val glintArcSize = r * 1.35f
    drawArc(
        color = Color.White.copy(alpha = alpha * 0.98f),
        startAngle = 195f,
        sweepAngle = 70f,
        useCenter = false,
        topLeft = Offset(center.x - glintArcSize * 0.5f - r * 0.08f, center.y - glintArcSize * 0.5f - r * 0.08f),
        size = Size(glintArcSize, glintArcSize),
        style = Stroke(width = (r * 0.13f).coerceIn(2.5f, 5.5f))
    )

    // Pinpoint sunlight spark
    val pinRadius = (r * 0.11f).coerceIn(2.0f, 4.2f)
    drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = pinRadius,
        center = glintAuraCenter - Offset(1f, 1f)
    )

    // 7. SECONDARY AMBIENT HORIZON REFLECTION (Bottom-Right rim)
    val secondaryArcSize = r * 1.65f
    drawArc(
        color = Color(0x99FFFFFF).copy(alpha = alpha * 0.45f),
        startAngle = 25f,
        sweepAngle = 55f,
        useCenter = false,
        topLeft = Offset(center.x - secondaryArcSize * 0.5f, center.y - secondaryArcSize * 0.5f),
        size = Size(secondaryArcSize, secondaryArcSize),
        style = Stroke(width = (r * 0.06f).coerceIn(1.2f, 2.2f))
    )
}

/**
 * Draws a 4-point diamond star sparkle with rotation and glow.
 */
private fun DrawScope.drawStarlight(
    centerX: Float,
    centerY: Float,
    size: Float,
    alpha: Float,
    rotationDeg: Float,
    color: Color
) {
    if (alpha <= 0.01f || size <= 1f) return
    val center = Offset(centerX, centerY)

    // Soft outer glow aura
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha * 0.55f), Color.Transparent),
            center = center,
            radius = size * 1.2f
        ),
        radius = size * 1.2f,
        center = center
    )

    rotate(degrees = rotationDeg, pivot = center) {
        val path = Path().apply {
            moveTo(centerX, centerY - size)
            quadraticTo(centerX, centerY, centerX + size, centerY)
            quadraticTo(centerX, centerY, centerX, centerY + size)
            quadraticTo(centerX, centerY, centerX - size, centerY)
            quadraticTo(centerX, centerY, centerX, centerY - size)
            close()
        }
        drawPath(path = path, color = color.copy(alpha = alpha * 0.90f))
        // Center white brilliance
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = size * 0.22f,
            center = center
        )
    }
}

/**
 * Ultra-Realistic Samsung Liquid Glass & Water Physics Engine (v4.1).
 *
 * Supports:
 * - GALAXY_RIPPLE (Default old classic Samsung Galaxy S3/S4 Nature UX ripple)
 * - WATER_DROPLET (Realistic 3D liquid glass water droplet with visible semi-translucent body)
 * - HYBRID_BOTH (Water droplet + concentric ripples)
 * - ELECTRIC_AQUA (Electric water plasma sparks & branching lightning arcs)
 * - STARLIGHT_SPARKLE (Twinkling starburst glitter & diamond dust)
 * - ZEN_SPRING (Minimalist Zen spring single ripple with floating pearl dew)
 */
@Composable
fun LollipopTouchRippleContainer(
    soundEnabled: Boolean = true,
    rippleEnabled: Boolean = true,
    waterEffectMode: WaterEffectMode = WaterEffectMode.GALAXY_RIPPLE,
    waterTiltGravityEnabled: Boolean = true,
    soundProfile: WaterSoundProfile = WaterSoundProfile.SOFT_DROP,
    soundVolume: Float = 0.5f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Tilt gravity values (screen X & Y acceleration in m/s²)
    var gravityX by remember { mutableFloatStateOf(0f) }
    var gravityY by remember { mutableFloatStateOf(4.5f) }

    // Register Accelerometer only if tilt gravity is enabled
    DisposableEffect(waterTiltGravityEnabled) {
        if (!waterTiltGravityEnabled) {
            gravityX = 0f
            gravityY = 0f
            return@DisposableEffect onDispose {}
        }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.values.size >= 2) {
                    val rawGx = -event.values[0]
                    val rawGy = event.values[1]
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

    // Active Water Drops, Impact Droplets, Ripples, and Special Particles
    val activeImpactDrops = remember { mutableStateListOf<ImpactCenterDroplet>() }
    val activeDrops = remember { mutableStateListOf<RealisticWaterDrop>() }
    val activeRipples = remember { mutableStateListOf<NatureWaterRipple>() }
    val activeElectricBolts = remember { mutableStateListOf<ElectricBolt>() }
    val activeStarlights = remember { mutableStateListOf<StarlightParticle>() }
    val activeZenPearls = remember { mutableStateListOf<ZenDewPearl>() }
    var currentFrameNanos by remember { mutableLongStateOf(0L) }

    val initialDropRadiusPx = with(density) { 22.dp.toPx() }
    val maxHeldDropRadiusPx = with(density) { 56.dp.toPx() }
    val splashMaxRadiusPx = with(density) { 170.dp.toPx() }
    val wavelengthPx = with(density) { 24.dp.toPx() }

    val isEffectActive = rippleEnabled && waterEffectMode != WaterEffectMode.DISABLED

    val hasActiveEntities = activeImpactDrops.isNotEmpty() ||
            activeDrops.isNotEmpty() ||
            activeRipples.isNotEmpty() ||
            activeElectricBolts.isNotEmpty() ||
            activeStarlights.isNotEmpty() ||
            activeZenPearls.isNotEmpty()

    // Physics Animation Frame Loop: Runs at 60/120 FPS
    LaunchedEffect(isEffectActive, hasActiveEntities) {
        if (!isEffectActive) return@LaunchedEffect

        var lastNanos = System.nanoTime()

        while (activeImpactDrops.isNotEmpty() ||
            activeDrops.isNotEmpty() ||
            activeRipples.isNotEmpty() ||
            activeElectricBolts.isNotEmpty() ||
            activeStarlights.isNotEmpty() ||
            activeZenPearls.isNotEmpty()
        ) {
            withFrameNanos { frameTimeNanos ->
                currentFrameNanos = frameTimeNanos
                val dtSec = ((frameTimeNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = frameTimeNanos

                // 1. UPDATE IMPACT CENTER DROPLETS (Evaporates in ~680ms)
                val impactIterator = activeImpactDrops.iterator()
                while (impactIterator.hasNext()) {
                    val impact = impactIterator.next()
                    val elapsedMs = (frameTimeNanos - impact.startTimeNanos) / 1_000_000f
                    if (elapsedMs >= impact.durationMs) {
                        impactIterator.remove()
                    }
                }

                // 2. UPDATE REALISTIC WATER DROPS
                val dropIterator = activeDrops.iterator()
                while (dropIterator.hasNext()) {
                    val drop = dropIterator.next()
                    val holdElapsedSec = (frameTimeNanos - drop.birthNanos) / 1_000_000_000f

                    if (drop.isHeldDown) {
                        // Organically swell while finger is held down
                        val growthProgress = (holdElapsedSec / 1.6f).coerceIn(0f, 1f)
                        val targetR = initialDropRadiusPx + (maxHeldDropRadiusPx - initialDropRadiusPx) * growthProgress
                        drop.baseRadiusPx = drop.baseRadiusPx * 0.90f + targetR * 0.10f
                        drop.lastMoveNanos = frameTimeNanos
                    } else {
                        // Released droplet
                        if (waterTiltGravityEnabled) {
                            // Slow, viscous tilt motion
                            val idleElapsedSec = (frameTimeNanos - drop.lastMoveNanos) / 1_000_000_000f
                            if (idleElapsedSec > 3.8f) {
                                drop.alpha -= dtSec * 0.50f
                            }

                            val tiltMag = hypot(gravityX, gravityY)
                            if (tiltMag > 2.4f) {
                                val normGx = gravityX / tiltMag
                                val normGy = gravityY / tiltMag
                                val excessTilt = (tiltMag - 2.4f).coerceAtMost(5.5f)
                                val accel = excessTilt * 14.0f * (drop.baseRadiusPx / initialDropRadiusPx)
                                drop.vx += normGx * accel * dtSec
                                drop.vy += normGy * accel * dtSec
                            }

                            drop.vx *= 0.88f
                            drop.vy *= 0.88f

                            val currentSpeed = hypot(drop.vx, drop.vy)
                            val maxGlideSpeed = with(density) { 34.dp.toPx() }
                            if (currentSpeed > maxGlideSpeed) {
                                drop.vx = (drop.vx / currentSpeed) * maxGlideSpeed
                                drop.vy = (drop.vy / currentSpeed) * maxGlideSpeed
                            }

                            if (currentSpeed > 1.2f) {
                                drop.x += drop.vx * dtSec * 60f
                                drop.y += drop.vy * dtSec * 60f
                                drop.lastMoveNanos = frameTimeNanos

                                if (drop.trail.isEmpty() || hypot(drop.x - drop.trail.last().x, drop.y - drop.trail.last().y) > 14f) {
                                    if (drop.trail.size > 20) {
                                        drop.trail.removeAt(0)
                                    }
                                    drop.trail.add(
                                        WetTrailPoint(
                                            x = drop.x,
                                            y = drop.y,
                                            radius = drop.baseRadiusPx * 0.42f,
                                            timestampNanos = frameTimeNanos
                                        )
                                    )
                                }
                            }
                        } else {
                            // TILT MOTION OFF: Force velocity to zero, stay completely still in place, and fade out gradually!
                            drop.vx = 0f
                            drop.vy = 0f
                            // Evaporates in place smoothly without shifting downwards
                            drop.alpha -= dtSec * 0.42f
                        }
                    }

                    // Evaporate old trail points (> 1.4s)
                    drop.trail.removeAll { (frameTimeNanos - it.timestampNanos) > 1_400_000_000L }

                    // Remove evaporated drops
                    if (drop.alpha <= 0.02f) {
                        dropIterator.remove()
                    }
                }

                // 3. UPDATE CONCENTRIC NATURE WAVES
                val rippleIterator = activeRipples.iterator()
                while (rippleIterator.hasNext()) {
                    val ripple = rippleIterator.next()
                    val elapsedMs = (frameTimeNanos - ripple.startTimeNanos) / 1_000_000f
                    if (elapsedMs >= ripple.durationMs) {
                        rippleIterator.remove()
                    }
                }

                // 4. UPDATE ELECTRIC BOLTS
                val boltIterator = activeElectricBolts.iterator()
                while (boltIterator.hasNext()) {
                    val bolt = boltIterator.next()
                    val elapsedMs = (frameTimeNanos - bolt.startTimeNanos) / 1_000_000f
                    if (elapsedMs >= bolt.durationMs) {
                        boltIterator.remove()
                    }
                }

                // 5. UPDATE STARLIGHT PARTICLES
                val starIterator = activeStarlights.iterator()
                while (starIterator.hasNext()) {
                    val star = starIterator.next()
                    val elapsedMs = (frameTimeNanos - star.startTimeNanos) / 1_000_000f
                    if (elapsedMs >= star.durationMs) {
                        starIterator.remove()
                    } else {
                        star.x += star.vx * dtSec * 60f
                        star.y += star.vy * dtSec * 60f
                    }
                }

                // 6. UPDATE ZEN PEARL DEW
                val zenIterator = activeZenPearls.iterator()
                while (zenIterator.hasNext()) {
                    val pearl = zenIterator.next()
                    val elapsedMs = (frameTimeNanos - pearl.startTimeNanos) / 1_000_000f
                    if (elapsedMs >= pearl.durationMs) {
                        zenIterator.remove()
                    } else {
                        // Gentle organic float
                        val floatProgress = elapsedMs / pearl.durationMs
                        pearl.x += cos(pearl.floatAngle) * 0.15f
                        pearl.y += sin(pearl.floatAngle) * 0.15f - (floatProgress * 0.12f)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(soundEnabled, rippleEnabled, waterEffectMode, waterTiltGravityEnabled, soundProfile, soundVolume) {
                if (!rippleEnabled && !soundEnabled) return@pointerInput

                var nextId = 0L
                var currentHeldDrop: RealisticWaterDrop? = null
                var pressStartTimeNanos = 0L
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
                                    val nowNanos = System.nanoTime()
                                    pressStartTimeNanos = nowNanos

                                    // Play sound
                                    if (soundEnabled) {
                                        LollipopSoundEffects.playWaterDrop(
                                            enabled = true,
                                            profile = soundProfile,
                                            volume = soundVolume
                                        )
                                    }

                                    if (rippleEnabled) {
                                        when (waterEffectMode) {
                                            WaterEffectMode.GALAXY_RIPPLE -> {
                                                // Classic pure Samsung Galaxy S3/S4 TouchWiz Ripples
                                                if (activeRipples.size > 8) activeRipples.removeAt(0)
                                                activeRipples.add(
                                                    NatureWaterRipple(
                                                        id = nextId++,
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

                                            WaterEffectMode.WATER_DROPLET -> {
                                                // Realistic 3D Water Droplet
                                                if (activeImpactDrops.size > 6) activeImpactDrops.removeAt(0)
                                                activeImpactDrops.add(
                                                    ImpactCenterDroplet(
                                                        id = nextId++,
                                                        centerX = pos.x,
                                                        centerY = pos.y,
                                                        startTimeNanos = nowNanos,
                                                        durationMs = 680f,
                                                        initialRadiusPx = initialDropRadiusPx
                                                    )
                                                )
                                                if (activeDrops.size > 6) activeDrops.removeAt(0)
                                                val newDrop = RealisticWaterDrop(
                                                    id = nextId++,
                                                    x = pos.x,
                                                    y = pos.y,
                                                    birthNanos = nowNanos,
                                                    baseRadiusPx = initialDropRadiusPx,
                                                    isHeldDown = true,
                                                    alpha = 1.0f
                                                )
                                                activeDrops.add(newDrop)
                                                currentHeldDrop = newDrop
                                            }

                                            WaterEffectMode.HYBRID_BOTH -> {
                                                if (activeRipples.size > 8) activeRipples.removeAt(0)
                                                activeRipples.add(
                                                    NatureWaterRipple(
                                                        id = nextId++,
                                                        centerX = pos.x,
                                                        centerY = pos.y,
                                                        startTimeNanos = nowNanos,
                                                        durationMs = 720f,
                                                        maxRadiusPx = splashMaxRadiusPx,
                                                        wavelengthPx = wavelengthPx,
                                                        isDragWake = false,
                                                        intensity = 1.0f
                                                    )
                                                )
                                                if (activeImpactDrops.size > 6) activeImpactDrops.removeAt(0)
                                                activeImpactDrops.add(
                                                    ImpactCenterDroplet(
                                                        id = nextId++,
                                                        centerX = pos.x,
                                                        centerY = pos.y,
                                                        startTimeNanos = nowNanos,
                                                        durationMs = 680f,
                                                        initialRadiusPx = initialDropRadiusPx
                                                    )
                                                )
                                                if (activeDrops.size > 6) activeDrops.removeAt(0)
                                                val newDrop = RealisticWaterDrop(
                                                    id = nextId++,
                                                    x = pos.x,
                                                    y = pos.y,
                                                    birthNanos = nowNanos,
                                                    baseRadiusPx = initialDropRadiusPx,
                                                    isHeldDown = true,
                                                    alpha = 1.0f
                                                )
                                                activeDrops.add(newDrop)
                                                currentHeldDrop = newDrop
                                            }

                                            WaterEffectMode.ELECTRIC_AQUA -> {
                                                // Electric Water Plasma & Lightning Arcs
                                                if (activeRipples.size > 8) activeRipples.removeAt(0)
                                                activeRipples.add(
                                                    NatureWaterRipple(
                                                        id = nextId++,
                                                        centerX = pos.x,
                                                        centerY = pos.y,
                                                        startTimeNanos = nowNanos,
                                                        durationMs = 500f,
                                                        maxRadiusPx = splashMaxRadiusPx * 0.85f,
                                                        wavelengthPx = wavelengthPx * 0.80f,
                                                        isDragWake = false,
                                                        intensity = 1.2f
                                                    )
                                                )
                                                // Generate 5 jagged lightning branches radiating out
                                                for (i in 0 until 5) {
                                                    val branchAngle = (i * 72f + Random.nextFloat() * 30f) * (Math.PI / 180f)
                                                    val branchLength = 40f + Random.nextFloat() * 55f
                                                    val pts = mutableListOf<Offset>()
                                                    pts.add(pos)
                                                    var curX = pos.x
                                                    var curY = pos.y
                                                    val steps = 4
                                                    for (s in 1..steps) {
                                                        val t = s.toFloat() / steps
                                                        val segX = pos.x + cos(branchAngle).toFloat() * branchLength * t + (Random.nextFloat() - 0.5f) * 16f
                                                        val segY = pos.y + sin(branchAngle).toFloat() * branchLength * t + (Random.nextFloat() - 0.5f) * 16f
                                                        pts.add(Offset(segX, segY))
                                                        curX = segX
                                                        curY = segY
                                                    }
                                                    if (activeElectricBolts.size > 12) activeElectricBolts.removeAt(0)
                                                    activeElectricBolts.add(
                                                        ElectricBolt(
                                                            id = nextId++,
                                                            startTimeNanos = nowNanos,
                                                            durationMs = 380f,
                                                            points = pts,
                                                            color = if (i % 2 == 0) Color(0xFF00E5FF) else Color(0xFF80D8FF)
                                                        )
                                                    )
                                                }
                                            }

                                            WaterEffectMode.STARLIGHT_SPARKLE -> {
                                                // Dazzling Starlight Twinkles & Diamond Dust
                                                for (i in 0 until 7) {
                                                    val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                                                    val speed = 0.5f + Random.nextFloat() * 2.2f
                                                    if (activeStarlights.size > 24) activeStarlights.removeAt(0)
                                                    activeStarlights.add(
                                                        StarlightParticle(
                                                            id = nextId++,
                                                            x = pos.x + (Random.nextFloat() - 0.5f) * 12f,
                                                            y = pos.y + (Random.nextFloat() - 0.5f) * 12f,
                                                            vx = cos(angle) * speed,
                                                            vy = sin(angle) * speed,
                                                            startTimeNanos = nowNanos,
                                                            durationMs = 500f + Random.nextFloat() * 200f,
                                                            sizePx = 12f + Random.nextFloat() * 14f,
                                                            rotationDeg = Random.nextFloat() * 360f,
                                                            color = when (i % 3) {
                                                                0 -> Color(0xFFFFFFFF)
                                                                1 -> Color(0xFF80DEEA)
                                                                else -> Color(0xFFFFD54F)
                                                            }
                                                        )
                                                    )
                                                }
                                            }

                                            WaterEffectMode.ZEN_SPRING -> {
                                                // Minimalist Zen spring single ripple and pearl dews
                                                if (activeRipples.size > 6) activeRipples.removeAt(0)
                                                activeRipples.add(
                                                    NatureWaterRipple(
                                                        id = nextId++,
                                                        centerX = pos.x,
                                                        centerY = pos.y,
                                                        startTimeNanos = nowNanos,
                                                        durationMs = 820f,
                                                        maxRadiusPx = splashMaxRadiusPx * 0.90f,
                                                        wavelengthPx = wavelengthPx * 1.2f,
                                                        isDragWake = false,
                                                        intensity = 0.75f
                                                    )
                                                )
                                                for (i in 0 until 3) {
                                                    val pearlAngle = (i * 120f + Random.nextFloat() * 30f) * (Math.PI / 180f).toFloat()
                                                    val dist = 14f + Random.nextFloat() * 22f
                                                    if (activeZenPearls.size > 10) activeZenPearls.removeAt(0)
                                                    activeZenPearls.add(
                                                        ZenDewPearl(
                                                            id = nextId++,
                                                            x = pos.x + cos(pearlAngle) * dist,
                                                            y = pos.y + sin(pearlAngle) * dist,
                                                            startTimeNanos = nowNanos,
                                                            durationMs = 800f + Random.nextFloat() * 200f,
                                                            radiusPx = 5.5f + Random.nextFloat() * 4.0f,
                                                            floatAngle = pearlAngle
                                                        )
                                                    )
                                                }
                                            }

                                            WaterEffectMode.DISABLED -> {}
                                        }
                                    }
                                }

                                PointerEventType.Move -> {
                                    if (isFingerDown) {
                                        val dist = hypot(pos.x - lastTouchPos.x, pos.y - lastTouchPos.y)
                                        if (dist >= 10f) {
                                            lastTouchPos = pos

                                            currentHeldDrop?.let { drop ->
                                                drop.vx = (pos.x - drop.x) * 3f
                                                drop.vy = (pos.y - drop.y) * 3f
                                                drop.x = pos.x
                                                drop.y = pos.y
                                                drop.lastMoveNanos = System.nanoTime()
                                            }

                                            if (soundEnabled) {
                                                LollipopSoundEffects.playDragWaterRipple(
                                                    enabled = true,
                                                    profile = soundProfile,
                                                    volume = soundVolume
                                                )
                                            }

                                            if (rippleEnabled) {
                                                val nowNanos = System.nanoTime()
                                                if (waterEffectMode == WaterEffectMode.GALAXY_RIPPLE ||
                                                    waterEffectMode == WaterEffectMode.HYBRID_BOTH ||
                                                    waterEffectMode == WaterEffectMode.ELECTRIC_AQUA ||
                                                    waterEffectMode == WaterEffectMode.ZEN_SPRING) {
                                                    if (activeRipples.size > 8) activeRipples.removeAt(0)
                                                    activeRipples.add(
                                                        NatureWaterRipple(
                                                            id = nextId++,
                                                            centerX = pos.x,
                                                            centerY = pos.y,
                                                            startTimeNanos = nowNanos,
                                                            durationMs = 400f,
                                                            maxRadiusPx = 52f,
                                                            wavelengthPx = wavelengthPx * 0.75f,
                                                            isDragWake = true,
                                                            intensity = 0.55f
                                                        )
                                                    )
                                                }

                                                if (waterEffectMode == WaterEffectMode.STARLIGHT_SPARKLE) {
                                                    if (activeStarlights.size > 24) activeStarlights.removeAt(0)
                                                    activeStarlights.add(
                                                        StarlightParticle(
                                                            id = nextId++,
                                                            x = pos.x,
                                                            y = pos.y,
                                                            vx = (Random.nextFloat() - 0.5f) * 1.5f,
                                                            vy = (Random.nextFloat() - 0.5f) * 1.5f,
                                                            startTimeNanos = nowNanos,
                                                            durationMs = 450f,
                                                            sizePx = 10f + Random.nextFloat() * 8f,
                                                            rotationDeg = Random.nextFloat() * 360f,
                                                            color = Color(0xFF80DEEA)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                PointerEventType.Release -> {
                                    isFingerDown = false
                                    val holdDurationMs = (System.nanoTime() - pressStartTimeNanos) / 1_000_000f

                                    if (holdDurationMs < 180f) {
                                        currentHeldDrop?.let { drop ->
                                            drop.alpha = 0.2f
                                        }
                                    }

                                    currentHeldDrop?.isHeldDown = false
                                    currentHeldDrop?.lastMoveNanos = System.nanoTime()
                                    currentHeldDrop = null
                                }
                            }
                        }
                    }
                }
            }
    ) {
        content()

        if (isEffectActive && hasActiveEntities) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val nowNanos = if (currentFrameNanos > 0) currentFrameNanos else System.nanoTime()

                // A. CONCENTRIC GALAXY NATURE WAVES
                if (waterEffectMode == WaterEffectMode.GALAXY_RIPPLE ||
                    waterEffectMode == WaterEffectMode.HYBRID_BOTH ||
                    waterEffectMode == WaterEffectMode.ELECTRIC_AQUA ||
                    waterEffectMode == WaterEffectMode.ZEN_SPRING) {

                    for (ripple in activeRipples) {
                        val elapsedMs = (nowNanos - ripple.startTimeNanos) / 1_000_000f
                        if (elapsedMs < 0f || elapsedMs > ripple.durationMs) continue

                        val progress = (elapsedMs / ripple.durationMs).coerceIn(0f, 1f)
                        val center = Offset(ripple.centerX, ripple.centerY)
                        val waveRadius = ripple.maxRadiusPx * (progress.pow(0.72f))
                        val baseAlpha = (1f - progress).pow(1.30f) * ripple.intensity

                        val ringCount = if (waterEffectMode == WaterEffectMode.ZEN_SPRING) 1 else if (ripple.isDragWake) 2 else 3
                        val lambda = ripple.wavelengthPx

                        for (ringIndex in 0 until ringCount) {
                            val crestRadius = waveRadius - (ringIndex * lambda)
                            if (crestRadius <= 2f) continue

                            val ringDecay = (1f - (ringIndex * 0.25f)).coerceAtLeast(0.15f)
                            val ringAlpha = (baseAlpha * ringDecay).coerceIn(0f, 1f)
                            if (ringAlpha < 0.01f) continue

                            val strokeWidthPx = ((2.4f - ringIndex * 0.4f) * (1f - progress * 0.4f)).coerceAtLeast(1.0f).dp.toPx()

                            // Refraction Shadow
                            val shadowColor = if (waterEffectMode == WaterEffectMode.ELECTRIC_AQUA) Color(0xFF006064) else Color(0xFF004D40)
                            drawCircle(
                                color = shadowColor.copy(alpha = ringAlpha * 0.22f),
                                radius = crestRadius + (strokeWidthPx * 0.6f),
                                center = center,
                                style = Stroke(width = strokeWidthPx * 1.2f)
                            )
                            // Refractive Cyan Crest
                            val crestColor = if (waterEffectMode == WaterEffectMode.ELECTRIC_AQUA) Color(0xFF00E5FF) else Color(0xFF4DD0E1)
                            drawCircle(
                                color = crestColor.copy(alpha = ringAlpha * 0.38f),
                                radius = crestRadius,
                                center = center,
                                style = Stroke(width = strokeWidthPx)
                            )
                            // Specular Crest Glint
                            drawCircle(
                                color = Color(0xFFFFFFFF).copy(alpha = ringAlpha * 0.48f),
                                radius = crestRadius - (strokeWidthPx * 0.35f),
                                center = center,
                                style = Stroke(width = strokeWidthPx * 0.75f)
                            )
                        }
                    }
                }

                // B. TRANSIENT IMPACT CENTER DROPLETS
                if (waterEffectMode == WaterEffectMode.WATER_DROPLET ||
                    waterEffectMode == WaterEffectMode.HYBRID_BOTH) {
                    for (impact in activeImpactDrops) {
                        val elapsedMs = (nowNanos - impact.startTimeNanos) / 1_000_000f
                        if (elapsedMs < 0f || elapsedMs > impact.durationMs) continue

                        val progress = (elapsedMs / impact.durationMs).coerceIn(0f, 1f)
                        val dropAlpha = (1f - progress).pow(1.4f)
                        val dropRadius = impact.initialRadiusPx * (1f + progress * 0.22f)

                        draw3DWaterDroplet(
                            centerX = impact.centerX,
                            centerY = impact.centerY,
                            radius = dropRadius,
                            alpha = dropAlpha,
                            wobblePhase = (elapsedMs / 60f)
                        )
                    }
                }

                // C. PERSISTENT 3D WATER DROPS
                if (waterEffectMode == WaterEffectMode.WATER_DROPLET || waterEffectMode == WaterEffectMode.HYBRID_BOTH) {
                    for (drop in activeDrops) {
                        if (drop.alpha < 0.01f) continue

                        val holdTimeSec = (nowNanos - drop.birthNanos) / 1_000_000_000f
                        val breathing = if (drop.isHeldDown) sin(holdTimeSec * 4.5f) * 1.5f else 0f
                        val r = (drop.baseRadiusPx + breathing).coerceAtLeast(8f)

                        // Glistening wet trail
                        for (trailPoint in drop.trail) {
                            val trailAgeSec = (nowNanos - trailPoint.timestampNanos) / 1_000_000_000f
                            val trailAlpha = ((1f - (trailAgeSec / 1.4f)) * 0.32f * drop.alpha).coerceIn(0f, 1f)
                            if (trailAlpha > 0.01f) {
                                drawCircle(
                                    color = Color(0x7080DEEA).copy(alpha = trailAlpha),
                                    radius = trailPoint.radius,
                                    center = Offset(trailPoint.x, trailPoint.y)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = trailAlpha * 0.60f),
                                    radius = trailPoint.radius * 0.45f,
                                    center = Offset(trailPoint.x - trailPoint.radius * 0.15f, trailPoint.y - trailPoint.radius * 0.15f)
                                )
                            }
                        }

                        draw3DWaterDroplet(
                            centerX = drop.x,
                            centerY = drop.y,
                            radius = r,
                            alpha = drop.alpha,
                            wobblePhase = holdTimeSec * 6.0f
                        )
                    }
                }

                // D. ELECTRIC LIGHTNING BOLTS (ELECTRIC_AQUA)
                if (waterEffectMode == WaterEffectMode.ELECTRIC_AQUA) {
                    for (bolt in activeElectricBolts) {
                        val elapsedMs = (nowNanos - bolt.startTimeNanos) / 1_000_000f
                        if (elapsedMs < 0f || elapsedMs > bolt.durationMs) continue
                        val alpha = (1f - (elapsedMs / bolt.durationMs)).pow(1.5f)

                        for (i in 0 until bolt.points.size - 1) {
                            val p1 = bolt.points[i]
                            val p2 = bolt.points[i + 1]

                            // Outer electric plasma aura
                            drawLine(
                                color = bolt.color.copy(alpha = alpha * 0.65f),
                                start = p1,
                                end = p2,
                                strokeWidth = 5.dp.toPx()
                            )
                            // Pure white core lightning
                            drawLine(
                                color = Color.White.copy(alpha = alpha * 0.95f),
                                start = p1,
                                end = p2,
                                strokeWidth = 1.8.dp.toPx()
                            )
                            // Spark dot at junction
                            drawCircle(
                                color = Color.White.copy(alpha = alpha),
                                radius = 2.dp.toPx(),
                                center = p2
                            )
                        }
                    }
                }

                // E. STARLIGHT SPARKLES (STARLIGHT_SPARKLE)
                if (waterEffectMode == WaterEffectMode.STARLIGHT_SPARKLE) {
                    for (star in activeStarlights) {
                        val elapsedMs = (nowNanos - star.startTimeNanos) / 1_000_000f
                        if (elapsedMs < 0f || elapsedMs > star.durationMs) continue
                        val progress = elapsedMs / star.durationMs
                        val alpha = (1f - progress).pow(1.2f)
                        val rot = star.rotationDeg + progress * 90f

                        drawStarlight(
                            centerX = star.x,
                            centerY = star.y,
                            size = star.sizePx * (1f - progress * 0.35f),
                            alpha = alpha,
                            rotationDeg = rot,
                            color = star.color
                        )
                    }
                }

                // F. ZEN DEW PEARLS (ZEN_SPRING)
                if (waterEffectMode == WaterEffectMode.ZEN_SPRING) {
                    for (pearl in activeZenPearls) {
                        val elapsedMs = (nowNanos - pearl.startTimeNanos) / 1_000_000f
                        if (elapsedMs < 0f || elapsedMs > pearl.durationMs) continue
                        val progress = elapsedMs / pearl.durationMs
                        val alpha = (1f - progress).pow(1.3f)

                        val pCenter = Offset(pearl.x, pearl.y)
                        val pr = pearl.radiusPx

                        // Soft shadow
                        drawCircle(
                            color = Color(0x33004D40).copy(alpha = alpha * 0.40f),
                            radius = pr * 1.15f,
                            center = pCenter + Offset(pr * 0.2f, pr * 0.25f)
                        )
                        // Pearl dew body
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xE0FFFFFF).copy(alpha = alpha * 0.85f), Color(0x8080CBC4).copy(alpha = alpha * 0.65f)),
                                center = pCenter - Offset(pr * 0.2f, pr * 0.2f),
                                radius = pr
                            ),
                            radius = pr,
                            center = pCenter
                        )
                        // Tiny highlight
                        drawCircle(
                            color = Color.White.copy(alpha = alpha * 0.95f),
                            radius = pr * 0.28f,
                            center = pCenter - Offset(pr * 0.35f, pr * 0.35f)
                        )
                    }
                }
            }
        }
    }
}
