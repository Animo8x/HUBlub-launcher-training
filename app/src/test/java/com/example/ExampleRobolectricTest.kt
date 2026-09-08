package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LauncherPreferencesRepository
import com.example.model.HomeShortcut
import com.example.model.IconPackStyle
import com.example.model.LauncherConfig
import com.example.model.WallpaperPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HUBlub Launcher", appName)
    }

    @Test
    fun `test repository saves and restores config and shortcuts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LauncherPreferencesRepository(context)

        val config = LauncherConfig(
            gridColumns = 4,
            gridRows = 5,
            iconSizeDp = 60,
            showAppLabels = true,
            wallpaperPreset = WallpaperPreset.CYAN_GEOMETRIC,
            iconPack = IconPackStyle.ANDROID_5_ROUND,
            performanceMode = true,
            nostalgiaMode = true,
            soundEffectsEnabled = true,
            touchRippleEnabled = true
        )
        repo.saveConfig(config)
        val loadedConfig = repo.loadConfig()
        assertEquals(4, loadedConfig.gridColumns)
        assertEquals(60, loadedConfig.iconSizeDp)
        assertEquals(WallpaperPreset.CYAN_GEOMETRIC, loadedConfig.wallpaperPreset)
        assertEquals(IconPackStyle.ANDROID_5_ROUND, loadedConfig.iconPack)
        assertTrue(loadedConfig.performanceMode)
        assertTrue(loadedConfig.soundEffectsEnabled)
        assertTrue(loadedConfig.touchRippleEnabled)

        val shortcuts = listOf(
            HomeShortcut("test_1", "com.android.settings", "SettingsActivity", "Settings", 0, 0, 0)
        )
        repo.saveShortcuts(shortcuts)
        val loadedShortcuts = repo.loadShortcuts()
        assertEquals(1, loadedShortcuts.size)
        assertEquals("com.android.settings", loadedShortcuts[0].packageName)
    }
}
