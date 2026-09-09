package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.model.WallpaperPreset

/**
 * Renders authentic Android 5.0 Lollipop wallpapers, featuring geometric paper craft,
 * diagonal fold lines, transparent overlays, and rich Material 1.0 color planes,
 * or custom photos chosen from the user's phone files.
 */
@Composable
fun LollipopWallpaper(
    preset: WallpaperPreset = WallpaperPreset.STOCK_LOLLIPOP,
    customUri: String? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (!customUri.isNullOrBlank()) {
            AsyncImage(
                model = customUri,
                contentDescription = "Custom User Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            when (preset) {
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
