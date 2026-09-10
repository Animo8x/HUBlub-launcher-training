package com.example.ui.components

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.model.WallpaperPreset

/**
 * Renders authentic Android 5.0 Lollipop wallpapers, featuring geometric paper craft,
 * diagonal fold lines, transparent overlays, and rich Material 1.0 color planes,
 * or live animated wallpapers (5 built-in styles), or custom video/photo wallpapers from device files.
 */
@Composable
fun LollipopWallpaper(
    preset: WallpaperPreset = WallpaperPreset.STOCK_LOLLIPOP,
    customUri: String? = null,
    isVideo: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (!customUri.isNullOrBlank()) {
            if (isVideo) {
                VideoLiveWallpaper(
                    videoUri = customUri,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = customUri,
                    contentDescription = "Custom User Wallpaper",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            when (preset) {
                WallpaperPreset.LIVE_COLOR_MORPH -> {
                    DrawLiveColorMorphWallpaper()
                }
                WallpaperPreset.LIVE_FLOATING_SQUARES -> {
                    DrawLiveFloatingSquaresWallpaper()
                }
                WallpaperPreset.LIVE_FLOATING_CIRCLES -> {
                    DrawLiveFloatingCirclesWallpaper()
                }
                WallpaperPreset.LIVE_GEOMETRIC_SHAPES -> {
                    DrawLiveGeometricShapesWallpaper()
                }
                WallpaperPreset.LIVE_COSMIC_GRID_WAVE -> {
                    DrawLiveCosmicGridWaveWallpaper()
                }
                WallpaperPreset.STOCK_LOLLIPOP -> {
                    Image(
                        painter = painterResource(id = R.drawable.bg_lollipop_default),
                        contentDescription = "Lollipop Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                WallpaperPreset.PURPLE_DEEP_BLUE -> {
                    DrawPurpleDeepBlueWallpaper()
                }
                WallpaperPreset.MODERN_16_AURA -> {
                    DrawModern16AuraWallpaper()
                }
                WallpaperPreset.MODERN_16_FROSTED_GLASS -> {
                    DrawModern16FrostedGlassWallpaper()
                }
                WallpaperPreset.MODERN_17_CYBER_SUNSET -> {
                    DrawModern17CyberSunsetWallpaper()
                }
                WallpaperPreset.MODERN_17_COSMIC_NEBULA -> {
                    DrawModern17CosmicNebulaWallpaper()
                }
                WallpaperPreset.MODERN_17_MINIMAL_CHROMA -> {
                    DrawModern17MinimalChromaWallpaper()
                }
                WallpaperPreset.CYAN_GEOMETRIC -> {
                    DrawGeometricWallpaper(
                        baseColor = Color(0xFF00695C),
                        layer1 = Color(0xFF00897B),
                        layer2 = Color(0xFF00ACC1),
                        accent = Color(0xFF26C6DA)
                    )
                }
                WallpaperPreset.INDIGO_SUNSET -> {
                    DrawGeometricWallpaper(
                        baseColor = Color(0xFF283593),
                        layer1 = Color(0xFF3949AB),
                        layer2 = Color(0xFF5E35B1),
                        accent = Color(0xFFFF7043)
                    )
                }
                WallpaperPreset.AMBER_SUNRISE -> {
                    DrawGeometricWallpaper(
                        baseColor = Color(0xFFD84315),
                        layer1 = Color(0xFFEF6C00),
                        layer2 = Color(0xFFFFA000),
                        accent = Color(0xFFFFD54F)
                    )
                }
                WallpaperPreset.DARK_SLATE -> {
                    DrawGeometricWallpaper(
                        baseColor = Color(0xFF1E272C),
                        layer1 = Color(0xFF263238),
                        layer2 = Color(0xFF37474F),
                        accent = Color(0xFF009688)
                    )
                }
            }
        }
    }
}

/**
 * Looping video live wallpaper player using Android TextureView and hardware accelerated MediaPlayer.
 * Runs silently in the background with zero lag and automatic cropping.
 */
@Composable
fun VideoLiveWallpaper(
    videoUri: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parsedUri = remember(videoUri) { Uri.parse(videoUri) }

    AndroidView(
        factory = { ctx ->
            TextureView(ctx).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    private var mediaPlayer: MediaPlayer? = null

                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        try {
                            mediaPlayer = MediaPlayer().apply {
                                setSurface(Surface(surface))
                                setDataSource(ctx, parsedUri)
                                isLooping = true
                                setVolume(0f, 0f) // Silent wallpaper
                                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                                setOnPreparedListener { mp ->
                                    try {
                                        mp.start()
                                    } catch (e: Exception) {}
                                }
                                prepareAsync()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        try {
                            mediaPlayer?.let { mp ->
                                if (mp.isPlaying) mp.stop()
                                mp.release()
                            }
                        } catch (e: Exception) {}
                        mediaPlayer = null
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Authentic Android 5.0 Lollipop style Geometric Wallpaper:
 * Smooth gradient of Purple into Deep Blue, layered with semi-transparent geometric squares,
 * angled paper folds, and crisp Material Design 1.0 shadows.
 */
@Composable
private fun DrawPurpleDeepBlueWallpaper() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Rich Base Gradient: Deep Purple (0xFF311B92 / 0xFF4A148C) to Deep Midnight Blue (0xFF0D47A1 / 0xFF0A192F)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2E0854), // Deep Imperial Purple
                    Color(0xFF3B1278), // Rich Lollipop Purple
                    Color(0xFF1A237E), // Deep Indigo
                    Color(0xFF0D1B2A), // Midnight Deep Blue
                    Color(0xFF060D17)  // Deep Blue Night
                )
            )
        )

        // 2. Translucent Geometric Squares / Diamond Planes (Android 5.0 paper craft)
        // Square 1: Top Right tilted diamond
        val square1 = Path().apply {
            moveTo(w * 0.45f, 0f)
            lineTo(w * 1.15f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.48f)
            lineTo(w * 0.15f, h * 0.30f)
            close()
        }
        drawPath(square1, Color(0x28BA68C8)) // Soft purple sheet

        // Shadow under square 1
        val shadowSquare1 = Path().apply {
            moveTo(w * 0.15f, h * 0.30f)
            lineTo(w * 0.85f, h * 0.48f)
            lineTo(w * 0.85f, h * 0.48f + 20f)
            lineTo(w * 0.15f, h * 0.30f + 20f)
            close()
        }
        drawPath(shadowSquare1, Color(0x35000000))

        // 3. Primary Diagonal Paper Fold (45-degree angle Material Plane)
        val fold1 = Path().apply {
            moveTo(0f, h * 0.28f)
            lineTo(w, h * 0.48f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(fold1, Color(0x334527A0)) // Deep Purple 800 sheet

        // Shadow for fold 1
        val shadowFold1 = Path().apply {
            moveTo(0f, h * 0.28f)
            lineTo(w, h * 0.48f)
            lineTo(w, h * 0.48f + 28f)
            lineTo(0f, h * 0.28f + 28f)
            close()
        }
        drawPath(shadowFold1, Color(0x3A000000))

        // 4. Floating Semi-Transparent Square in Mid-Screen
        val square2 = Path().apply {
            moveTo(w * 0.05f, h * 0.42f)
            lineTo(w * 0.65f, h * 0.36f)
            lineTo(w * 0.58f, h * 0.64f)
            lineTo(w * -0.02f, h * 0.70f)
            close()
        }
        drawPath(square2, Color(0x255C6BC0)) // Translucent Indigo

        // 5. Deep Navy Blue Origami Layer (Lower Section)
        val fold2 = Path().apply {
            moveTo(0f, h * 0.58f)
            lineTo(w, h * 0.42f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(fold2, Color(0x401565C0)) // Material Blue 800

        // Shadow for fold 2
        val shadowFold2 = Path().apply {
            moveTo(0f, h * 0.58f)
            lineTo(w, h * 0.42f)
            lineTo(w, h * 0.42f + 32f)
            lineTo(0f, h * 0.58f + 32f)
            close()
        }
        drawPath(shadowFold2, Color(0x40000000))

        // 6. Translucent Accent Square / Diamond on Bottom Right
        val square3 = Path().apply {
            moveTo(w * 0.40f, h * 0.72f)
            lineTo(w * 0.95f, h * 0.60f)
            lineTo(w * 1.10f, h * 0.88f)
            lineTo(w * 0.55f, h * 1.00f)
            close()
        }
        drawPath(square3, Color(0x207E57C2)) // Deep Purple Accent 400

        // 7. Vivid Material Accent Triangle (Cyan / Violet flare)
        val accentTriangle = Path().apply {
            moveTo(w * 0.25f, h)
            lineTo(w, h * 0.78f)
            lineTo(w, h)
            close()
        }
        drawPath(accentTriangle, Color(0x3500BCD4)) // Material Cyan Accent
    }
}

@Composable
private fun DrawGeometricWallpaper(
    baseColor: Color,
    layer1: Color,
    layer2: Color,
    accent: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Base canvas
        drawRect(color = baseColor)

        // Diagonal paper fold 1
        val path1 = Path().apply {
            moveTo(0f, h * 0.35f)
            lineTo(w, h * 0.15f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(path1, layer1)

        // Drop shadow for fold 1
        val shadow1 = Path().apply {
            moveTo(0f, h * 0.35f)
            lineTo(w, h * 0.15f)
            lineTo(w, h * 0.15f + 24f)
            lineTo(0f, h * 0.35f + 24f)
            close()
        }
        drawPath(shadow1, Color(0x33000000))

        // Diagonal paper fold 2
        val path2 = Path().apply {
            moveTo(0f, h * 0.65f)
            lineTo(w, h * 0.45f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(path2, layer2)

        // Drop shadow for fold 2
        val shadow2 = Path().apply {
            moveTo(0f, h * 0.65f)
            lineTo(w, h * 0.45f)
            lineTo(w, h * 0.45f + 32f)
            lineTo(0f, h * 0.65f + 32f)
            close()
        }
        drawPath(shadow2, Color(0x28000000))

        // Geometric accent triangle in bottom right
        val accentTriangle = Path().apply {
            moveTo(w * 0.3f, h)
            lineTo(w, h * 0.72f)
            lineTo(w, h)
            close()
        }
        drawPath(accentTriangle, accent)

        // Drop shadow for accent triangle
        val accentShadow = Path().apply {
            moveTo(w * 0.3f, h)
            lineTo(w, h * 0.72f)
            lineTo(w, h * 0.72f + 16f)
            lineTo(w * 0.3f, h)
            close()
        }
        drawPath(accentShadow, Color(0x22000000))
    }
}

/**
 * 1. Android 16: Luminous Aura Flow.
 * Soft organic glowing radiant spheres on a deep titanium canvas.
 */
@Composable
private fun DrawModern16AuraWallpaper() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Deep obsidian navy base
        drawRect(color = Color(0xFF0C101A))

        // Ambient radial aura 1 (Emerald/Teal top-right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF10B981).copy(alpha = 0.40f), Color(0xFF064E3B).copy(alpha = 0.12f), Color.Transparent),
                center = Offset(w * 0.85f, h * 0.18f),
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = Offset(w * 0.85f, h * 0.18f)
        )

        // Ambient radial aura 2 (Lavender violet center-left)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.45f), Color(0xFF4C1D95).copy(alpha = 0.15f), Color.Transparent),
                center = Offset(w * 0.15f, h * 0.52f),
                radius = w * 0.85f
            ),
            radius = w * 0.85f,
            center = Offset(w * 0.15f, h * 0.52f)
        )

        // Ambient radial aura 3 (Cyan/Indigo bottom-right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.70f, h * 0.82f),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(w * 0.70f, h * 0.82f)
        )
    }
}

/**
 * 2. Android 16: Frosted Glass Layers.
 * Flowing curved translucent ribbons with subtle frosted sheen.
 */
@Composable
private fun DrawModern16FrostedGlassWallpaper() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Deep cool graphite background
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF111827), Color(0xFF0F172A))
            )
        )

        // Layer 1: Bottom subtle curve
        val ribbon1 = Path().apply {
            moveTo(0f, h * 0.38f)
            cubicTo(w * 0.3f, h * 0.28f, w * 0.7f, h * 0.48f, w, h * 0.36f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = ribbon1,
            brush = Brush.linearGradient(
                listOf(Color(0x3538BDF8), Color(0x180284C7))
            )
        )

        // Frosted glass edge highlight
        val highlight1 = Path().apply {
            moveTo(0f, h * 0.38f)
            cubicTo(w * 0.3f, h * 0.28f, w * 0.7f, h * 0.48f, w, h * 0.36f)
        }
        drawPath(
            path = highlight1,
            color = Color(0x40FFFFFF),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )

        // Layer 2: Middle floating glass veil
        val ribbon2 = Path().apply {
            moveTo(0f, h * 0.60f)
            cubicTo(w * 0.35f, h * 0.72f, w * 0.65f, h * 0.52f, w, h * 0.64f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = ribbon2,
            brush = Brush.linearGradient(
                listOf(Color(0x40818CF8), Color(0x224F46E5))
            )
        )

        val highlight2 = Path().apply {
            moveTo(0f, h * 0.60f)
            cubicTo(w * 0.35f, h * 0.72f, w * 0.65f, h * 0.52f, w, h * 0.64f)
        }
        drawPath(
            path = highlight2,
            color = Color(0x35FFFFFF),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

/**
 * 3. Android 17: Cyber Sunset Twilight.
 * Radiant sunset transition with warm coral and midnight violet horizon.
 */
@Composable
private fun DrawModern17CyberSunsetWallpaper() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Rich twilight sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F081D), // Midnight Abyss
                    Color(0xFF2E1065), // Deep Violet
                    Color(0xFF581C87), // Rich Purple
                    Color(0xFF9333EA), // Radiant Violet
                    Color(0xFFF43F5E), // Coral Neon
                    Color(0xFFFB923C)  // Peach Sunrise
                )
            )
        )

        // Soft solar horizon glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x99FFEDD5), Color(0x40FB923C), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.88f),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(w * 0.5f, h * 0.88f)
        )
    }
}

/**
 * 4. Android 17: Cosmic Nebula 3D Glow.
 * Floating celestial orb fields with smooth, deep volumetric luminescence.
 */
@Composable
private fun DrawModern17CosmicNebulaWallpaper() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Deep cosmos space black
        drawRect(color = Color(0xFF070913))

        // Magenta Nebula orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFD946EF).copy(alpha = 0.50f), Color(0xFF86198F).copy(alpha = 0.18f), Color.Transparent),
                center = Offset(w * 0.25f, h * 0.28f),
                radius = w * 0.70f
            ),
            radius = w * 0.70f,
            center = Offset(w * 0.25f, h * 0.28f)
        )

        // Cyan Nebula orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.45f), Color(0xFF0E7490).copy(alpha = 0.15f), Color.Transparent),
                center = Offset(w * 0.80f, h * 0.65f),
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = Offset(w * 0.80f, h * 0.65f)
        )

        // Indigo base light
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6366F1).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.45f, h * 0.90f),
                radius = w * 0.60f
            ),
            radius = w * 0.60f,
            center = Offset(w * 0.45f, h * 0.90f)
        )
    }
}

/**
 * 5. Android 17: Minimal Chroma Sculptural.
 * Modern industrial micro-curves with elegant chromatic ambient light.
 */
@Composable
private fun DrawModern17MinimalChromaWallpaper() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Pure dark titanium base
        drawRect(color = Color(0xFF111418))

        // Sculptural Chroma Curve
        val chromaPath = Path().apply {
            moveTo(0f, h * 0.78f)
            cubicTo(w * 0.4f, h * 0.85f, w * 0.65f, h * 0.45f, w, h * 0.50f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = chromaPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B),
                    Color(0xFF0F172A)
                ),
                start = Offset(0f, h * 0.78f),
                end = Offset(w, h)
            )
        )

        // Glowing chromatic edge line
        val edgePath = Path().apply {
            moveTo(0f, h * 0.78f)
            cubicTo(w * 0.4f, h * 0.85f, w * 0.65f, h * 0.45f, w, h * 0.50f)
        }

        drawPath(
            path = edgePath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF3B82F6), // Electric Blue
                    Color(0xFF10B981)  // Emerald Chroma
                )
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
        )
    }
}

/**
 * 1. Live Animated Wallpaper: Liquid Color Morph (تغير ألوان تدريجياً وسلس بشكل متواصل)
 * Multi-stop organic flowing radial & linear gradient mesh shifting through vibrant aurora hues.
 */
@Composable
fun DrawLiveColorMorphWallpaper() {
    val transition = rememberInfiniteTransition(label = "ColorMorph")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val x1 = (0.5f + 0.35f * Math.cos(phase.toDouble()).toFloat()) * w
        val y1 = (0.5f + 0.35f * Math.sin(phase.toDouble()).toFloat()) * h

        val x2 = (0.5f + 0.35f * Math.cos((phase + Math.PI).toDouble()).toFloat()) * w
        val y2 = (0.5f + 0.35f * Math.sin((phase + Math.PI).toDouble()).toFloat()) * h

        val x3 = (0.5f + 0.28f * Math.sin((phase * 1.6).toDouble()).toFloat()) * w
        val y3 = (0.5f + 0.28f * Math.cos((phase * 1.6).toDouble()).toFloat()) * h

        // Deep galactic base gradient
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0F172A),
                    Color(0xFF1E1B4B),
                    Color(0xFF0D1B2A)
                ),
                start = Offset(x1, y1),
                end = Offset(x2, y2)
            )
        )

        // Radial glowing aura 1 (Cyan / Aquamarine)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00E5FF).copy(alpha = 0.55f),
                    Color(0xFF00897B).copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(x1, y1),
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = Offset(x1, y1)
        )

        // Radial glowing aura 2 (Magenta / Sunset Purple)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF4081).copy(alpha = 0.50f),
                    Color(0xFF7C4DFF).copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = Offset(x2, y2),
                radius = w * 0.80f
            ),
            radius = w * 0.80f,
            center = Offset(x2, y2)
        )

        // Radial glowing aura 3 (Amber Sunrise / Gold)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFB300).copy(alpha = 0.40f),
                    Color(0xFFFF6D00).copy(alpha = 0.16f),
                    Color.Transparent
                ),
                center = Offset(x3, y3),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(x3, y3)
        )
    }
}

/**
 * 2. Live Animated Wallpaper: Floating Translucent Squares (مربعات شفافة متحركة تتكرر بشكل دائم)
 * Frosted geometric glass squares drifting upwards and rotating smoothly with seamless modulo wrap.
 */
@Composable
fun DrawLiveFloatingSquaresWallpaper() {
    val transition = rememberInfiniteTransition(label = "FloatingSquares")
    val animTime by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animTime"
    )

    val squares = remember {
        listOf(
            floatArrayOf(0.12f, 28f, 0.18f, 0.04f, 0.20f, 16f),
            floatArrayOf(0.78f, 36f, 0.24f, -0.03f, 0.16f, 20f),
            floatArrayOf(0.42f, 22f, 0.14f, 0.05f, 0.25f, 12f),
            floatArrayOf(0.85f, 45f, 0.12f, 0.06f, 0.18f, 10f),
            floatArrayOf(0.25f, 50f, 0.22f, -0.04f, 0.15f, 18f),
            floatArrayOf(0.60f, 30f, 0.16f, 0.035f, 0.22f, 14f),
            floatArrayOf(0.05f, 40f, 0.20f, -0.05f, 0.14f, 16f),
            floatArrayOf(0.92f, 25f, 0.15f, 0.045f, 0.19f, 12f),
            floatArrayOf(0.50f, 60f, 0.10f, -0.07f, 0.26f, 8f),
            floatArrayOf(0.32f, 34f, 0.26f, 0.03f, 0.13f, 22f),
            floatArrayOf(0.70f, 42f, 0.17f, -0.04f, 0.18f, 14f),
            floatArrayOf(0.18f, 55f, 0.13f, 0.06f, 0.21f, 10f)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A192F),
                    Color(0xFF112240),
                    Color(0xFF020C1B)
                )
            )
        )

        squares.forEachIndexed { i, s ->
            val baseX = s[0] * w
            val speedY = s[1]
            val sqSize = s[2] * w
            val rotSpeed = s[3]
            val alpha = s[4]
            val cr = s[5]

            val period = (h + sqSize * 2)
            val currentY = h + sqSize - ((animTime * 0.05f * speedY + i * (period / squares.size)) % period)
            val currentX = baseX + Math.sin((animTime * 0.0015f + i).toDouble()).toFloat() * (w * 0.06f)
            val rotation = (animTime * rotSpeed + i * 30f) % 360f

            rotate(
                degrees = rotation,
                pivot = Offset(currentX + sqSize / 2f, currentY + sqSize / 2f)
            ) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF64FFDA).copy(alpha = alpha * 0.7f),
                            Color(0xFF00E5FF).copy(alpha = alpha * 0.25f)
                        ),
                        start = Offset(currentX, currentY),
                        end = Offset(currentX + sqSize, currentY + sqSize)
                    ),
                    topLeft = Offset(currentX, currentY),
                    size = androidx.compose.ui.geometry.Size(sqSize, sqSize),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cr, cr)
                )

                drawRoundRect(
                    color = Color.White.copy(alpha = alpha * 0.85f),
                    topLeft = Offset(currentX, currentY),
                    size = androidx.compose.ui.geometry.Size(sqSize, sqSize),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cr, cr),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                )
            }
        }
    }
}

/**
 * 3. Live Animated Wallpaper: Floating Bubbles & Circles (دوائر وفقاعات ضوئية متحركة تتكرر بسلاسة)
 * Glowing translucent glass spheres rising continuously with organic horizontal swaying.
 */
@Composable
fun DrawLiveFloatingCirclesWallpaper() {
    val transition = rememberInfiniteTransition(label = "FloatingCircles")
    val animTime by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "circleTime"
    )

    val circles = remember {
        listOf(
            floatArrayOf(0.15f, 32f, 0.12f, 0.25f),
            floatArrayOf(0.80f, 25f, 0.18f, 0.18f),
            floatArrayOf(0.45f, 40f, 0.08f, 0.30f),
            floatArrayOf(0.65f, 20f, 0.22f, 0.15f),
            floatArrayOf(0.28f, 48f, 0.10f, 0.24f),
            floatArrayOf(0.90f, 35f, 0.14f, 0.20f),
            floatArrayOf(0.08f, 28f, 0.16f, 0.22f),
            floatArrayOf(0.52f, 52f, 0.07f, 0.35f),
            floatArrayOf(0.38f, 30f, 0.15f, 0.19f),
            floatArrayOf(0.72f, 45f, 0.11f, 0.26f),
            floatArrayOf(0.20f, 22f, 0.20f, 0.16f),
            floatArrayOf(0.84f, 50f, 0.09f, 0.28f),
            floatArrayOf(0.58f, 36f, 0.13f, 0.21f),
            floatArrayOf(0.04f, 44f, 0.11f, 0.23f)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF001F3F),
                    Color(0xFF003366),
                    Color(0xFF001122)
                )
            )
        )

        circles.forEachIndexed { i, c ->
            val baseX = c[0] * w
            val speedY = c[1]
            val radius = c[2] * w * 0.5f
            val alpha = c[3]

            val period = (h + radius * 4)
            val currentY = h + radius * 2 - ((animTime * 0.05f * speedY + i * (period / circles.size)) % period)
            val sway = Math.sin((animTime * 0.002f + i * 1.5).toDouble()).toFloat() * (w * 0.05f)
            val currentX = baseX + sway

            val center = Offset(currentX, currentY)

            // Outer soft glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = alpha * 0.5f),
                        Color(0xFF0077B6).copy(alpha = alpha * 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.4f
                ),
                radius = radius * 1.4f,
                center = center
            )

            // Bubble sphere body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE0F7FA).copy(alpha = alpha * 0.25f),
                        Color(0xFF00E5FF).copy(alpha = alpha * 0.55f),
                        Color(0xFF0097A7).copy(alpha = alpha * 0.70f)
                    ),
                    center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Translucent rim outline
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.85f),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0f)
            )

            // Specular highlight
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.9f),
                radius = radius * 0.24f,
                center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
            )
        }
    }
}

/**
 * 4. Live Animated Wallpaper: Geometric Polyhedra (أشكال هندسية شفافة متحركة وتنعاد وتتكرر)
 * Rotating translucent triangles, diamonds, and hexagons floating with ambient parallax.
 */
@Composable
fun DrawLiveGeometricShapesWallpaper() {
    val transition = rememberInfiniteTransition(label = "GeometricPolyhedra")
    val animTime by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "polyTime"
    )

    val shapeTypes = remember { intArrayOf(0, 1, 2, 0, 1, 2, 1, 0, 2, 1, 0, 2) }
    val shapeData = remember {
        listOf(
            floatArrayOf(0.15f, 30f, 0.16f, 0.04f, 0.22f),
            floatArrayOf(0.75f, 24f, 0.20f, -0.03f, 0.18f),
            floatArrayOf(0.40f, 38f, 0.14f, 0.05f, 0.25f),
            floatArrayOf(0.88f, 45f, 0.12f, -0.06f, 0.19f),
            floatArrayOf(0.22f, 32f, 0.18f, 0.035f, 0.20f),
            floatArrayOf(0.60f, 26f, 0.22f, -0.04f, 0.16f),
            floatArrayOf(0.06f, 42f, 0.15f, 0.045f, 0.23f),
            floatArrayOf(0.92f, 34f, 0.13f, -0.05f, 0.20f),
            floatArrayOf(0.50f, 50f, 0.11f, 0.065f, 0.27f),
            floatArrayOf(0.33f, 28f, 0.19f, -0.035f, 0.17f),
            floatArrayOf(0.70f, 40f, 0.15f, 0.04f, 0.21f),
            floatArrayOf(0.18f, 48f, 0.12f, -0.055f, 0.24f)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF130F26),
                    Color(0xFF1F1A3A),
                    Color(0xFF0F0C1B)
                )
            )
        )

        shapeData.forEachIndexed { i, s ->
            val baseX = s[0] * w
            val speedY = s[1]
            val shapeSize = s[2] * w
            val rotSpeed = s[3]
            val alpha = s[4]
            val type = shapeTypes[i % shapeTypes.size]

            val period = (h + shapeSize * 3)
            val currentY = h + shapeSize - ((animTime * 0.05f * speedY + i * (period / shapeData.size)) % period)
            val currentX = baseX + Math.sin((animTime * 0.0016f + i * 2).toDouble()).toFloat() * (w * 0.07f)
            val rotation = (animTime * rotSpeed + i * 45f) % 360f

            val cx = currentX + shapeSize / 2f
            val cy = currentY + shapeSize / 2f
            val r = shapeSize / 2f

            rotate(
                degrees = rotation,
                pivot = Offset(cx, cy)
            ) {
                val path = Path()
                when (type) {
                    0 -> { // Equilateral Triangle
                        path.moveTo(cx, cy - r)
                        path.lineTo(cx + r * 0.866f, cy + r * 0.5f)
                        path.lineTo(cx - r * 0.866f, cy + r * 0.5f)
                        path.close()
                    }
                    1 -> { // Diamond / Rhombus
                        path.moveTo(cx, cy - r)
                        path.lineTo(cx + r * 0.7f, cy)
                        path.lineTo(cx, cy + r)
                        path.lineTo(cx - r * 0.7f, cy)
                        path.close()
                    }
                    else -> { // Hexagon
                        for (step in 0 until 6) {
                            val angle = (step * 60.0 * Math.PI / 180.0)
                            val px = (cx + r * Math.cos(angle)).toFloat()
                            val py = (cy + r * Math.sin(angle)).toFloat()
                            if (step == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }
                        path.close()
                    }
                }

                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9C27B0).copy(alpha = alpha * 0.65f),
                            Color(0xFF00E5FF).copy(alpha = alpha * 0.30f)
                        ),
                        start = Offset(cx - r, cy - r),
                        end = Offset(cx + r, cy + r)
                    )
                )

                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = alpha * 0.9f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.2f)
                )
            }
        }
    }
}

/**
 * 5. Live Animated Wallpaper: Cosmic Wave Matrix (مصفوفة نيون وموجات هندسية متحركة)
 * Undulating glowing laser ribbons and pulsating cyber grid lines repeating smoothly.
 */
@Composable
fun DrawLiveCosmicGridWaveWallpaper() {
    val transition = rememberInfiniteTransition(label = "CosmicGridWave")
    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF070B19),
                    Color(0xFF0D1424),
                    Color(0xFF050811)
                )
            )
        )

        // Multiple undulating geometric wave ribbons
        val ribbonCount = 5
        for (r in 0 until ribbonCount) {
            val progressRatio = r.toFloat() / (ribbonCount - 1)
            val baseY = h * (0.35f + progressRatio * 0.40f)
            val waveFreq = 0.0035f + r * 0.0005f
            val waveAmp = (h * 0.035f) * (1f + r * 0.15f)
            val phaseShift = waveOffset + r * 0.8f

            val path = Path()
            val stepPx = 18f
            var currentX = 0f

            while (currentX <= w + stepPx) {
                val waveY = baseY + Math.sin((currentX * waveFreq + phaseShift).toDouble()).toFloat() * waveAmp
                if (currentX == 0f) path.moveTo(currentX, waveY) else path.lineTo(currentX, waveY)
                currentX += stepPx
            }

            val ribbonColor = when (r) {
                0 -> Color(0xFF00E5FF)
                1 -> Color(0xFF00B0FF)
                2 -> Color(0xFF7C4DFF)
                3 -> Color(0xFFFF4081)
                else -> Color(0xFF64FFDA)
            }

            drawPath(
                path = path,
                color = ribbonColor.copy(alpha = 0.45f - r * 0.06f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.0f - r * 0.3f)
            )
        }

        // Vertical laser grid lines
        val cols = 8
        for (c in 0..cols) {
            val colX = w * (c.toFloat() / cols)
            val lineAlpha = 0.08f + 0.04f * Math.sin((waveOffset + c * 0.5).toDouble()).toFloat()
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = lineAlpha),
                start = Offset(colX, 0f),
                end = Offset(colX, h),
                strokeWidth = 1.2f
            )
        }
    }
}
