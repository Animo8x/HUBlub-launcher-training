package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.util.LollipopSoundEffects
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Data representation of an active Android 5.0 water droplet ripple.
 */
data class WaterRippleInstance(
    val id: String = UUID.randomUUID().toString(),
    val center: Offset,
    val color: Color = Color(0xFF80DEEA), // Soft Lollipop Teal/Cyan
    val radiusAnim: Animatable<Float, *> = Animatable(6f),
    val alphaAnim: Animatable<Float, *> = Animatable(0.35f),
    val ringRadiusAnim: Animatable<Float, *> = Animatable(4f),
    val ringAlphaAnim: Animatable<Float, *> = Animatable(0.55f)
)

/**
 * Authentic Android 5.0 Lollipop Touch Ripple Container.
 * Intercepts touch down events to spawn an expanding, dissolving water ripple
 * with an accompanying soft, relaxing water droplet sound.
 */
@Composable
fun LollipopTouchRippleContainer(
    soundEnabled: Boolean = true,
    rippleEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val ripples = remember { mutableStateListOf<WaterRippleInstance>() }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Calibrated natural water droplet sizes (logical and delicate, not oversized)
    val maxDiscRadiusPx = with(density) { 34.dp.toPx() }
    val maxRingRadiusPx = with(density) { 44.dp.toPx() }
    val startDiscRadiusPx = with(density) { 6.dp.toPx() }
    val startRingRadiusPx = with(density) { 4.dp.toPx() }
    val ringStrokeWidthPx = with(density) { 1.5.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(soundEnabled, rippleEnabled, maxDiscRadiusPx, maxRingRadiusPx) {
                if (!rippleEnabled && !soundEnabled) return@pointerInput

                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Main, requireUnconsumed = false)
                    val touchPos = down.position

                    // 1. Play comforting soft sound (throttled inside sound engine)
                    if (soundEnabled) {
                        LollipopSoundEffects.playWaterDrop(enabled = true)
                    }

                    // 2. Spawn delicate water ripple animation (at most 2 concurrent ripples)
                    if (rippleEnabled && ripples.size < 2) {
                        val newRipple = WaterRippleInstance(
                            center = touchPos,
                            radiusAnim = Animatable(startDiscRadiusPx),
                            ringRadiusAnim = Animatable(startRingRadiusPx)
                        )
                        ripples.add(newRipple)

                        scope.launch {
                            // Animate primary wave and outer water droplet ring
                            launch {
                                newRipple.radiusAnim.animateTo(
                                    targetValue = maxDiscRadiusPx,
                                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                                )
                            }
                            launch {
                                newRipple.alphaAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                                )
                            }
                            launch {
                                newRipple.ringRadiusAnim.animateTo(
                                    targetValue = maxRingRadiusPx,
                                    animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                                )
                            }
                            launch {
                                newRipple.ringAlphaAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 320, easing = LinearEasing)
                                )
                            }

                            // Wait for animation to finish then clean up
                            kotlinx.coroutines.delay(340)
                            ripples.remove(newRipple)
                        }
                    }
                }
            }
    ) {
        // Base content
        content()

        // Water Ripple Overlay (Pass-through drawing)
        if (rippleEnabled && ripples.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (ripple in ripples) {
                    val alpha = ripple.alphaAnim.value
                    val ringAlpha = ripple.ringAlphaAnim.value

                    if (alpha > 0.01f) {
                        // Inner soft water disc
                        drawCircle(
                            color = Color(0xFFFFFFFF).copy(alpha = alpha * 0.30f),
                            radius = ripple.radiusAnim.value,
                            center = ripple.center
                        )
                        // Luminous Cyan / Teal tint wave (authentic Lollipop ink/water)
                        drawCircle(
                            color = ripple.color.copy(alpha = alpha * 0.35f),
                            radius = ripple.radiusAnim.value * 0.82f,
                            center = ripple.center
                        )
                    }

                    if (ringAlpha > 0.01f) {
                        // Outer water ring ripple (delicate water drop wave expanding naturally)
                        drawCircle(
                            color = Color(0xFFB2EBF2).copy(alpha = ringAlpha * 0.45f),
                            radius = ripple.ringRadiusAnim.value,
                            center = ripple.center,
                            style = Stroke(width = ringStrokeWidthPx)
                        )
                    }
                }
            }
        }
    }
}
