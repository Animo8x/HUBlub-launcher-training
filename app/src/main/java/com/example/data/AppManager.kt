package com.example.data

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.Settings
import android.util.LruCache
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android Native AppManager responsible for discovering installed applications,
 * loading and caching app icons, launching apps safely, and opening system settings.
 */
object AppManager {

    private const val MAX_ICON_CACHE_SIZE = 120
    private val iconCache = object : LruCache<String, ImageBitmap>(MAX_ICON_CACHE_SIZE) {}

    /**
     * Queries all installed launchable apps on the device using Android PackageManager.
     */
    suspend fun getInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val myPackageName = context.packageName

        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(mainIntent, 0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val appList = mutableListOf<AppInfo>()

        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo ?: continue
            val pkgName = activityInfo.packageName ?: continue
            val actName = activityInfo.name ?: ""

            // Skip self from drawer/shortcuts
            if (pkgName == myPackageName) continue

            val label = try {
                resolveInfo.loadLabel(pm).toString()
            } catch (e: Exception) {
                pkgName
            }

            var versionName = ""
            var isSystem = false
            var installTime = 0L

            try {
                val pkgInfo = pm.getPackageInfo(pkgName, 0)
                versionName = pkgInfo.versionName ?: ""
                installTime = pkgInfo.firstInstallTime
                isSystem = (pkgInfo.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
            } catch (ignored: Exception) {}

            val icon = getOrLoadIcon(pm, resolveInfo, pkgName)

            appList.add(
                AppInfo(
                    packageName = pkgName,
                    activityName = actName,
                    label = label,
                    versionName = versionName,
                    isSystemApp = isSystem,
                    installTime = installTime,
                    icon = icon
                )
            )
        }

        // Sort alphabetically according to classic Lollipop App Drawer
        appList.sortedBy { it.label.lowercase() }
    }

    /**
     * Efficiently loads and caches an application's icon, avoiding UI thread freezing.
     */
    private fun getOrLoadIcon(pm: PackageManager, resolveInfo: ResolveInfo, packageName: String): ImageBitmap? {
        synchronized(iconCache) {
            val cached = iconCache.get(packageName)
            if (cached != null) return cached
        }

        return try {
            val drawable = resolveInfo.loadIcon(pm)
            val bitmap = drawableToBitmap(drawable)
            val imageBitmap = bitmap?.asImageBitmap()
            if (imageBitmap != null) {
                synchronized(iconCache) {
                    iconCache.put(packageName, imageBitmap)
                }
            }
            imageBitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Converts any Android Drawable to a high-quality, memory-efficient Bitmap.
     */
    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth.coerceIn(48, 128) else 96
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight.coerceIn(48, 128) else 96

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Clears icon cache when an app is updated or memory pressure occurs.
     */
    fun clearCacheForPackage(packageName: String) {
        synchronized(iconCache) {
            iconCache.remove(packageName)
        }
    }

    /**
     * Launches the chosen application using its real Intent.
     */
    fun launchApp(context: Context, packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                context.startActivity(intent)
                true
            } else {
                Toast.makeText(context, "Could not open $packageName", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to launch app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Opens Android System App Info settings screen for the specified application.
     */
    fun openAppInfo(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open app settings", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Prompts system dialog to uninstall the application.
     */
    fun uninstallApp(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot uninstall app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens Android system Home App chooser settings screen.
     */
    fun openHomeSettings(context: Context) {
        val intents = listOf(
            Intent(Settings.ACTION_HOME_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
            Intent(Settings.ACTION_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        )

        for (intent in intents) {
            try {
                context.startActivity(intent)
                return
            } catch (ignored: Exception) {}
        }
        Toast.makeText(context, "Please set Default Home app in Device Settings", Toast.LENGTH_LONG).show()
    }

    /**
     * Launches the system Clock / Alarm app.
     */
    fun openClock(context: Context) {
        val intents = listOf(
            Intent(AlarmClock.ACTION_SHOW_ALARMS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

        for (intent in intents) {
            try {
                context.startActivity(intent)
                return
            } catch (ignored: Exception) {}
        }

        // Try launching any installed clock package
        val knownClockPackages = listOf(
            "com.google.android.deskclock",
            "com.android.deskclock",
            "com.sec.android.app.clockpackage"
        )
        for (pkg in knownClockPackages) {
            if (launchApp(context, pkg)) return
        }

        Toast.makeText(context, "Clock application not found", Toast.LENGTH_SHORT).show()
    }

    /**
     * Launches Web Search or Google search if present on device.
     */
    fun openWebSearch(context: Context, query: String = "") {
        try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query))).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Browser not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
