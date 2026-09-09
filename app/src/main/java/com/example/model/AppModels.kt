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
    val iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    val drawerStyle: DrawerStyle = DrawerStyle.CLASSIC_SOLID,
    val performanceMode: Boolean = false,
    val nostalgiaMode: Boolean = true,
    val animationsEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val touchRippleEnabled: Boolean = true,
    val waterSoundVolume: Float = 0.6f,
    val pageCount: Int = 2,
    val firstRunCompleted: Boolean = false
)
