package com.example.model

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Represents an installed Android application discovered via PackageManager.
 */
data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val versionName: String = "",
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val icon: ImageBitmap? = null
)

/**
 * Represents an app shortcut positioned on a specific Home screen page and grid cell.
 */
data class HomeShortcut(
    val id: String,
    val packageName: String,
    val activityName: String,
    val label: String,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0
)

/**
 * Wallpaper style presets: classic Android 5.0 Lollipop and modern Android 16/17 inspired aesthetics.
 */
enum class WallpaperPreset(val title: String) {
    STOCK_LOLLIPOP("القديمة (الأصلية): أندرويد 5.0 الرسمية (Nexus 5 Paper)"),
    PURPLE_DEEP_BLUE("أندرويد 5: بنفسجي وأزرق غامق (Material Purple & Blue)"),
    LIVE_COLOR_MORPH("خلفية حية 1: تدرج ألوان سائل وانسيابي (Liquid Color Morph)"),
    LIVE_FLOATING_SQUARES("خلفية حية 2: مربعات زجاجية شفافة متطايرة (Floating Glass Squares)"),
    LIVE_FLOATING_CIRCLES("خلفية حية 3: دوائر وفقاعات ضوئية متحركة (Floating Bubbles & Circles)"),
    LIVE_GEOMETRIC_SHAPES("خلفية حية 4: أشكال هندسية شفافة متكررة (Geometric Polyhedra)"),
    LIVE_COSMIC_GRID_WAVE("خلفية حية 5: شبكة نيون وموجات كوزميك (Cosmic Wave Matrix)"),
    LIVE_FALLING_RAIN_DROPS("خلفية حية 6: قطرات مطر وندى متساقطة بانسيابية (Gentle Rain Drops)"),
    LIVE_AURORA_BOREALIS("خلفية حية 7: شفق قطبي أورورا ناعم وموجي (Aurora Borealis Ribbon)"),
    LIVE_STARLIGHT_GALAXY("خلفية حية 8: بريق نجوم وجزيئات فضائية متلألئة (Starlight Sparkles & Dust)"),
    LIVE_CYBER_HEX_GRID("خلفية حية 9: خلايا نيون سداسية نابضة متكررة (Cyber Neon Hex Grid)"),
    LIVE_HYPNOTIC_SPIRAL("خلفية حية 10: موجات دوامة طاقة متحدة المركز ناعمة (Hypnotic Energy Spiral)"),
    CORAL_RED_PAPER("أندرويد 5: أوريغامي أحمر وقرمزي مادي (Material Coral & Crimson)"),
    OCEAN_EMERALD_WAVE("أندرويد 5: طبقات أمواج المحيط والزمرد (Ocean Emerald Layers)"),
    DESERT_GOLDEN_DUNE("أندرويد 5: رمال ذهبية وغروب صحراوي (Golden Desert Dunes)"),
    OBSIDIAN_NIGHT_SKY("أندرويد 5: ليل مظلم وأوريغامي نيون داكن (Obsidian Midnight Origami)"),
    ROYAL_AMETHYST_POLY("أندرويد 5: بلورات الجمشت والبنفسجي الملكي (Royal Amethyst Crystals)"),
    MODERN_16_AURA("أندرويد 16: هالة ضوئية متدرجة (Luminous Aura)"),
    MODERN_16_FROSTED_GLASS("أندرويد 16: طبقات زجاجية متداخلة (Frosted Glass Layers)"),
    MODERN_17_CYBER_SUNSET("أندرويد 17: شفق الغروب الدافئ (Twilight Sunset)"),
    MODERN_17_COSMIC_NEBULA("أندرويد 17: سديم كوني عائم (Cosmic Glow)"),
    MODERN_17_MINIMAL_CHROMA("أندرويد 17: انحناءات ميكرو كروما (Minimal Chroma)"),
    CYAN_GEOMETRIC("Material Cyan Origami"),
    INDIGO_SUNSET("Material Indigo Night"),
    AMBER_SUNRISE("Material Amber Sunrise"),
    DARK_SLATE("Material Dark Slate")
}

/**
 * Icon pack presets for HUBlub Launcher.
 * Features 5 distinct styles spanning ultra-modern to vintage retro.
 */
enum class IconPackStyle(val title: String) {
    SYSTEM_FREEFORM("الشكل الحر الأصلي (Natural Freeform - بدون خلفية دائرية)"),
    MATERIAL_YOU_SQUIRCLE("المربعات المنحنية الحديثة (Modern Squircle OneUI)"),
    IOS_MINIMAL_FLAT("النمط الزجاجي المسطح الحديث (iOS Minimal Glass)"),
    ANDROID_5_ROUND("أندرويد 5.0 لوليبوب الدائري (Lollipop Classic Round)"),
    KITKAT_VINTAGE_RETRO("أندرويد 4.4 كيت كات ريترو كلاسيك (Vintage KitKat)")
}

/**
 * Community Wallpaper item that users can publish, describe, and apply.
 */
data class CommunityWallpaper(
    val id: String,
    val title: String,
    val author: String = "أنا",
    val description: String = "",
    val imageUri: String? = null,
    val isVideo: Boolean = false,
    val preset: WallpaperPreset? = null,
    val colorHex: Long = 0xFF009688,
    val likesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Drawer background transparency styles.
 */
enum class DrawerStyle(val title: String, val alpha: Float) {
    CLASSIC_SOLID("Classic Solid White (أبيض صافي كلاسيكي أندرويد 5 - الافتراضي)", 1.0f),
    FROSTED_BLUR("Frosted Blur Glass (تشويش زجاجي يموّه ما خلفه)", 0.65f),
    TRANSLUCENT_GLASS("Translucent Glass (زجاجي شفاف يظهر الخلفية)", 0.72f),
    SEMI_TRANSPARENT("Semi-Transparent Dark (داكن شبه شفاف)", 0.50f),
    CRYSTAL_CLEAR("Crystal Clear (شفاف بالكامل)", 0.22f)
}

/**
 * Android AppWidget hosted on Home screen.
 */
data class HomeWidget(
    val appWidgetId: Int,
    val providerPackage: String,
    val providerClass: String,
    val pageIndex: Int = 0,
    val label: String = "Widget"
)

/**
 * Interactive Water & Particle Effect Modes
 */
enum class WaterEffectMode {
    GALAXY_RIPPLE,       // Classic Samsung Galaxy S3/S4 Nature UX wave ripples (Default)
    WATER_DROPLET,       // Realistic 3D liquid glass water droplet
    HYBRID_BOTH,         // Both 3D water droplet + concentric ripples
    ELECTRIC_AQUA,       // Electric water sparks & plasma arcs (كهرباء وتوهج مائي)
    STARLIGHT_SPARKLE,   // Sparkling glitter stars & diamond dust (بريق ونجوم لامعة)
    ZEN_SPRING,          // Minimalist Zen spring dew beads & gentle ripple (ينبوع الزن الهادئ)
    DISABLED
}

/**
 * Water & Interaction Sound Profiles
 */
enum class WaterSoundProfile {
    SOFT_DROP,           // Quiet, gentle, soothing natural water drip
    SAMSUNG_CLASSIC,     // Classic Samsung Galaxy S3/S4 Nature UX bloop
    GENTLE_BUBBLE,       // Soft relaxing bubble pop
    MUTED                // Silent
}

/**
 * Configuration and user preferences for HUBlub Launcher.
 */
data class LauncherConfig(
    val gridColumns: Int = 4,
    val gridRows: Int = 5,
    val iconSizeDp: Int = 56,
    val showAppLabels: Boolean = true,
    val showClockWidget: Boolean = false,
    val showGoogleSearchBar: Boolean = false, // Google search bar is hidden by default as requested!
    val wallpaperPreset: WallpaperPreset = WallpaperPreset.STOCK_LOLLIPOP,
    val customWallpaperUri: String? = null,
    val isVideoWallpaper: Boolean = false,
    val iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    val drawerStyle: DrawerStyle = DrawerStyle.CLASSIC_SOLID,
    val liquidGlassTheme: Boolean = false, // Modern Liquid Glass visual theme with rounded curves and frosted glass
    val performanceMode: Boolean = false,
    val nostalgiaMode: Boolean = true,
    val animationsEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val touchRippleEnabled: Boolean = true,
    val waterEffectMode: WaterEffectMode = WaterEffectMode.GALAXY_RIPPLE, // Default is the classic old effect as requested!
    val waterTiltGravityEnabled: Boolean = true,
    val waterSoundProfile: WaterSoundProfile = WaterSoundProfile.SOFT_DROP,
    val waterSoundVolume: Float = 0.5f,
    val pageCount: Int = 2,
    val firstRunCompleted: Boolean = false
)
