package com.example

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.HomeWidget
import com.example.ui.HomeScreen
import com.example.ui.theme.HUBlubLauncherTheme
import com.example.util.LollipopSoundEffects
import com.example.viewmodel.LauncherViewModel

/**
 * Main Activity acting as the Android Native Home / Launcher application.
 * Manages package monitoring broadcasts, edge-to-edge rendering, AppWidgetHost, and Home button dispatch.
 */
class MainActivity : ComponentActivity() {

    companion object {
        const val APPWIDGET_HOST_ID = 2048
    }

    private val viewModel: LauncherViewModel by viewModels()

    lateinit var appWidgetManager: AppWidgetManager
    lateinit var appWidgetHost: AppWidgetHost

    private var pendingWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private var pendingProviderInfo: AppWidgetProviderInfo? = null

    private val widgetBindLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val provider = pendingProviderInfo
        val widgetId = pendingWidgetId
        if (result.resultCode == Activity.RESULT_OK && provider != null && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            completeWidgetAddition(widgetId, provider)
        } else if (pendingWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            try {
                appWidgetHost.deleteAppWidgetId(pendingWidgetId)
            } catch (ignored: Exception) {}
            pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            pendingProviderInfo = null
        }
    }

    private val widgetConfigLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && pendingProviderInfo != null && pendingWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            val provider = pendingProviderInfo!!
            viewModel.addWidget(
                HomeWidget(
                    appWidgetId = pendingWidgetId,
                    providerPackage = provider.provider.packageName,
                    providerClass = provider.provider.className,
                    pageIndex = 0,
                    label = provider.loadLabel(packageManager) ?: "Widget"
                )
            )
            Toast.makeText(this, "تمت إضافة الودجت بنجاح", Toast.LENGTH_SHORT).show()
        } else if (pendingWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            try {
                appWidgetHost.deleteAppWidgetId(pendingWidgetId)
            } catch (ignored: Exception) {}
        }
        pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        pendingProviderInfo = null
    }

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val uri = intent.data ?: return
            val packageName = uri.schemeSpecificPart ?: return

            when (action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    if (!replacing) {
                        viewModel.onPackageAdded(packageName)
                    }
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                    if (!replacing) {
                        viewModel.onPackageRemoved(packageName)
                    }
                }
                Intent.ACTION_PACKAGE_REPLACED -> {
                    viewModel.onPackageChanged(packageName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        volumeControlStream = android.media.AudioManager.STREAM_MUSIC

        // 1. Initialize high performance SoundPool engine
        LollipopSoundEffects.init(this)

        // 2. Initialize AppWidget system host
        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)
        try {
            appWidgetHost.startListening()
        } catch (e: Exception) {}

        // 3. Register package listener
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(packageReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(packageReceiver, filter)
        }

        setContent {
            HUBlubLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    HomeScreen(
                        viewModel = viewModel,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        onAddWidgetProvider = { provider ->
                            bindAndAddWidget(provider)
                        }
                    )
                }
            }
        }
    }

    private fun bindAndAddWidget(provider: AppWidgetProviderInfo) {
        try {
            val appWidgetId = appWidgetHost.allocateAppWidgetId()
            val canBind = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider.provider)
            } else {
                true
            }

            if (canBind) {
                completeWidgetAddition(appWidgetId, provider)
            } else {
                pendingWidgetId = appWidgetId
                pendingProviderInfo = provider
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
                }
                widgetBindLauncher.launch(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "تعذر إضافة هذا الودجت", Toast.LENGTH_SHORT).show()
        }
    }

    private fun completeWidgetAddition(appWidgetId: Int, provider: AppWidgetProviderInfo) {
        try {
            if (provider.configure != null) {
                pendingWidgetId = appWidgetId
                pendingProviderInfo = provider
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                    component = provider.configure
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
                widgetConfigLauncher.launch(intent)
            } else {
                viewModel.addWidget(
                    HomeWidget(
                        appWidgetId = appWidgetId,
                        providerPackage = provider.provider.packageName,
                        providerClass = provider.provider.className,
                        pageIndex = 0,
                        label = provider.loadLabel(packageManager) ?: "Widget"
                    )
                )
                pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
                pendingProviderInfo = null
                Toast.makeText(this, "تمت إضافة الودجت بنجاح", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                appWidgetHost.deleteAppWidgetId(appWidgetId)
            } catch (ignored: Exception) {}
            pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            pendingProviderInfo = null
            Toast.makeText(this, "حدث خطأ أثناء تهيئة الودجت", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Never trigger expensive full app indexing on every onResume to avoid UI thread lag!
        // Package updates are already reactively handled by packageReceiver.
    }

    /**
     * When user presses system Home button while launcher is in background or in drawer/settings,
     * singleTask dispatches onNewIntent here to reset state back to Home screen.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_HOME)) {
            viewModel.closeDrawer()
            viewModel.closeSettings()
            viewModel.closeThemesApp()
            viewModel.closeWidgetPicker()
            viewModel.closeAppMenu()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(packageReceiver)
        } catch (ignored: Exception) {}

        try {
            appWidgetHost.stopListening()
        } catch (ignored: Exception) {}
    }
}
