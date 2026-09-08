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
 * Wallpaper style presets inspired by Android 5.0 Lollipop.
 */
enum class WallpaperPreset(val title: String) {
    PURPLE_DEEP_BLUE("Material Purple & Deep Blue (أندرويد 5 بنفسجي وأزرق غامق - افتراضي)"),
    STOCK_LOLLIPOP("Lollipop Original Paper"),
    CYAN_GEOMETRIC("Material Cyan Origami"),
    INDIGO_SUNSET("Material Indigo Night"),
    AMBER_SUNRISE("Material Amber Sunrise"),
    DARK_SLATE("Material Dark Slate")
}

/**
 * Icon pack presets for HUBlub Launcher.
 */
enum class IconPackStyle(val title: String) {
    ANDROID_5_ROUND("Android 5.0 Lollipop Round (حزمة أندرويد 5 الدائرية - افتراضي)"),
    SYSTEM_ORIGINAL("System Original (أيقونات النظام الأصلية)")
}

/**
 * Drawer background transparency styles.
 */
enum class DrawerStyle(val title: String, val alpha: Float) {
    TRANSLUCENT_GLASS("Translucent Glass (زجاجي شفاف يظهر الخلفية)", 0.72f),
    SEMI_TRANSPARENT("Semi-Transparent Dark (داكن شبه شفاف)", 0.50f),
    CRYSTAL_CLEAR("Crystal Clear (شفاف بالكامل)", 0.22f),
    CLASSIC_SOLID("Classic Solid Card (أبيض كلاسيكي)", 1.0f)
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
    val wallpaperPreset: WallpaperPreset = WallpaperPreset.STOCK_LOLLIPOP,
    val iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    val drawerStyle: DrawerStyle = DrawerStyle.TRANSLUCENT_GLASS,
    val performanceMode: Boolean = false,
    val nostalgiaMode: Boolean = true,
    val animationsEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val touchRippleEnabled: Boolean = true,
    val pageCount: Int = 2,
    val firstRunCompleted: Boolean = false
)
