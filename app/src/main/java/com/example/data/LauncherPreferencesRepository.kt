package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.DrawerStyle
import com.example.model.HomeShortcut
import com.example.model.HomeWidget
import com.example.model.IconPackStyle
import com.example.model.LauncherConfig
import com.example.model.WallpaperPreset
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages local persistence for HUBlub Launcher using SharedPreferences.
 * Stores Home screen layout, dock shortcuts, favorites, wallpaper and personalization options.
 */
class LauncherPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hublub_launcher_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONFIG = "config_json"
        private const val KEY_SHORTCUTS = "shortcuts_json"
        private const val KEY_DOCK = "dock_packages_json"
        private const val KEY_FAVORITES = "favorite_packages"
        private const val KEY_FIRST_RUN = "first_run_completed"
        private const val KEY_WIDGETS = "home_widgets_json"
    }

    fun loadConfig(): LauncherConfig {
        val raw = prefs.getString(KEY_CONFIG, null) ?: return LauncherConfig()
        return try {
            val json = JSONObject(raw)
            LauncherConfig(
                gridColumns = json.optInt("gridColumns", 4),
                gridRows = json.optInt("gridRows", 5),
                iconSizeDp = json.optInt("iconSizeDp", 56),
                showAppLabels = json.optBoolean("showAppLabels", true),
                wallpaperPreset = try {
                    WallpaperPreset.valueOf(json.optString("wallpaperPreset", WallpaperPreset.STOCK_LOLLIPOP.name))
                } catch (e: Exception) {
                    WallpaperPreset.STOCK_LOLLIPOP
                },
                iconPack = try {
                    IconPackStyle.valueOf(json.optString("iconPack", IconPackStyle.ANDROID_5_ROUND.name))
                } catch (e: Exception) {
                    IconPackStyle.ANDROID_5_ROUND
                },
                drawerStyle = try {
                    DrawerStyle.valueOf(json.optString("drawerStyle", DrawerStyle.TRANSLUCENT_GLASS.name))
                } catch (e: Exception) {
                    DrawerStyle.TRANSLUCENT_GLASS
                },
                performanceMode = json.optBoolean("performanceMode", false),
                nostalgiaMode = json.optBoolean("nostalgiaMode", true),
                animationsEnabled = json.optBoolean("animationsEnabled", true),
                soundEffectsEnabled = json.optBoolean("soundEffectsEnabled", true),
                touchRippleEnabled = json.optBoolean("touchRippleEnabled", true),
                pageCount = json.optInt("pageCount", 2),
                firstRunCompleted = prefs.getBoolean(KEY_FIRST_RUN, false)
            )
        } catch (e: Exception) {
            LauncherConfig()
        }
    }

    fun saveConfig(config: LauncherConfig) {
        val json = JSONObject().apply {
            put("gridColumns", config.gridColumns)
            put("gridRows", config.gridRows)
            put("iconSizeDp", config.iconSizeDp)
            put("showAppLabels", config.showAppLabels)
            put("wallpaperPreset", config.wallpaperPreset.name)
            put("iconPack", config.iconPack.name)
            put("drawerStyle", config.drawerStyle.name)
            put("performanceMode", config.performanceMode)
            put("nostalgiaMode", config.nostalgiaMode)
            put("animationsEnabled", config.animationsEnabled)
            put("soundEffectsEnabled", config.soundEffectsEnabled)
            put("touchRippleEnabled", config.touchRippleEnabled)
            put("pageCount", config.pageCount)
        }
        prefs.edit()
            .putString(KEY_CONFIG, json.toString())
            .putBoolean(KEY_FIRST_RUN, config.firstRunCompleted)
            .apply()
    }

    fun isFirstRun(): Boolean {
        return !prefs.getBoolean(KEY_FIRST_RUN, false)
    }

    fun setFirstRunCompleted(completed: Boolean = true) {
        prefs.edit().putBoolean(KEY_FIRST_RUN, completed).apply()
    }

    fun loadShortcuts(): List<HomeShortcut> {
        val raw = prefs.getString(KEY_SHORTCUTS, null) ?: return emptyList()
        val list = mutableListOf<HomeShortcut>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                list.add(
                    HomeShortcut(
                        id = item.optString("id", "${item.optString("packageName")}_$i"),
                        packageName = item.getString("packageName"),
                        activityName = item.optString("activityName", ""),
                        label = item.optString("label", ""),
                        pageIndex = item.optInt("pageIndex", 0),
                        cellX = item.optInt("cellX", 0),
                        cellY = item.optInt("cellY", 0)
                    )
                )
            }
        } catch (ignored: Exception) {}
        return list
    }

    fun saveShortcuts(shortcuts: List<HomeShortcut>) {
        val arr = JSONArray()
        for (sc in shortcuts) {
            val item = JSONObject().apply {
                put("id", sc.id)
                put("packageName", sc.packageName)
                put("activityName", sc.activityName)
                put("label", sc.label)
                put("pageIndex", sc.pageIndex)
                put("cellX", sc.cellX)
                put("cellY", sc.cellY)
            }
            arr.put(item)
        }
        prefs.edit().putString(KEY_SHORTCUTS, arr.toString()).apply()
    }

    fun loadDockPackages(): List<String> {
        val raw = prefs.getString(KEY_DOCK, null) ?: return emptyList()
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        } catch (ignored: Exception) {}
        return list
    }

    fun saveDockPackages(packages: List<String>) {
        val arr = JSONArray()
        packages.forEach { arr.put(it) }
        prefs.edit().putString(KEY_DOCK, arr.toString()).apply()
    }

    fun loadFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun saveFavorites(favorites: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    fun loadWidgets(): List<HomeWidget> {
        val raw = prefs.getString(KEY_WIDGETS, null) ?: return emptyList()
        val list = mutableListOf<HomeWidget>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                list.add(
                    HomeWidget(
                        appWidgetId = item.getInt("appWidgetId"),
                        providerPackage = item.getString("providerPackage"),
                        providerClass = item.getString("providerClass"),
                        pageIndex = item.optInt("pageIndex", 0),
                        label = item.optString("label", "Widget")
                    )
                )
            }
        } catch (ignored: Exception) {}
        return list
    }

    fun saveWidgets(widgets: List<HomeWidget>) {
        val arr = JSONArray()
        for (w in widgets) {
            val item = JSONObject().apply {
                put("appWidgetId", w.appWidgetId)
                put("providerPackage", w.providerPackage)
                put("providerClass", w.providerClass)
                put("pageIndex", w.pageIndex)
                put("label", w.label)
            }
            arr.put(item)
        }
        prefs.edit().putString(KEY_WIDGETS, arr.toString()).apply()
    }

    /**
     * Creates a JSON backup containing all shortcuts, dock items, favorites, and config.
     */
    fun exportBackup(): String {
        val backupObj = JSONObject().apply {
            put("version", 1)
            put("timestamp", System.currentTimeMillis())
            put("config", prefs.getString(KEY_CONFIG, "{}"))
            put("shortcuts", prefs.getString(KEY_SHORTCUTS, "[]"))
            put("dock", prefs.getString(KEY_DOCK, "[]"))
            val favArr = JSONArray()
            loadFavorites().forEach { favArr.put(it) }
            put("favorites", favArr)
        }
        return backupObj.toString(2)
    }

    /**
     * Restores launcher preferences and layout from JSON.
     */
    fun restoreBackup(backupJson: String): Boolean {
        return try {
            val obj = JSONObject(backupJson)
            val editor = prefs.edit()
            if (obj.has("config")) editor.putString(KEY_CONFIG, obj.getString("config"))
            if (obj.has("shortcuts")) editor.putString(KEY_SHORTCUTS, obj.getString("shortcuts"))
            if (obj.has("dock")) editor.putString(KEY_DOCK, obj.getString("dock"))
            if (obj.has("favorites")) {
                val favArr = obj.getJSONArray("favorites")
                val favSet = mutableSetOf<String>()
                for (i in 0 until favArr.length()) favSet.add(favArr.getString(i))
                editor.putStringSet(KEY_FAVORITES, favSet)
            }
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }
    }
}
