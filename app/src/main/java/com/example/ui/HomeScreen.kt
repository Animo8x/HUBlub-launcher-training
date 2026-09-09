package com.example.ui

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AppManager
import com.example.model.AppInfo
import com.example.model.HomeShortcut
import com.example.ui.components.LollipopAppDrawer
import com.example.ui.components.LollipopAppItem
import com.example.ui.components.LollipopAppMenuDialog
import com.example.ui.components.LollipopClockWidget
import com.example.ui.components.LollipopDock
import com.example.ui.components.LollipopFirstRunDialog
import com.example.ui.components.LollipopHomeWidgetView
import com.example.ui.components.LollipopSearchBar
import com.example.ui.components.LollipopSettingsDialog
import com.example.ui.components.LollipopThemesDialog
import com.example.ui.components.LollipopTouchRippleContainer
import com.example.ui.components.LollipopWallpaper
import com.example.ui.components.LollipopWidgetPickerDialog
import com.example.util.LollipopSoundEffects
import com.example.viewmodel.LauncherViewModel

/**
 * Root Home Screen for HUBlub Launcher.
 * Assembles the Android 5.0 Lollipop authentic interface, multi-page desktop,
 * bottom dock, translucent app drawer, system widgets, swipe gestures, and themes.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    appWidgetHost: AppWidgetHost? = null,
    appWidgetManager: AppWidgetManager? = null,
    onAddWidgetProvider: ((AppWidgetProviderInfo) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val installedApps by viewModel.installedApps.collectAsState()
    val homeShortcuts by viewModel.homeShortcuts.collectAsState()
    val dockPackages by viewModel.dockPackages.collectAsState()
    val favoritePackages by viewModel.favoritePackages.collectAsState()
    val homeWidgets by viewModel.homeWidgets.collectAsState()
    val config by viewModel.config.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()

    val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val isThemesAppOpen by viewModel.isThemesAppOpen.collectAsState()
    val isWidgetPickerOpen by viewModel.isWidgetPickerOpen.collectAsState()
    val selectedAppForMenu by viewModel.selectedAppForMenu.collectAsState()
    val selectedShortcutForMenu by viewModel.selectedShortcutForMenu.collectAsState()
    val isFirstRunDialogOpen by viewModel.isFirstRunDialogOpen.collectAsState()
    val communityWallpapers by viewModel.communityWallpapers.collectAsState()

    // Intercept back button: close drawer/settings/themes/picker before exiting
    BackHandler(enabled = isDrawerOpen || isSettingsOpen || isThemesAppOpen || isWidgetPickerOpen) {
        when {
            isWidgetPickerOpen -> viewModel.closeWidgetPicker()
            isThemesAppOpen -> viewModel.closeThemesApp()
            isSettingsOpen -> viewModel.closeSettings()
            isDrawerOpen -> viewModel.closeDrawer()
        }
    }

    // Map installed apps by package name for quick shortcut lookups
    val appMap = remember(installedApps) {
        installedApps.associateBy { it.packageName }
    }

    // Prepare favorite apps list
    val favoriteApps = remember(installedApps, favoritePackages) {
        installedApps.filter { favoritePackages.contains(it.packageName) }
    }

    // Prepare dock apps list
    val dockAppList = remember(dockPackages, appMap) {
        dockPackages.mapNotNull { appMap[it] }
    }

    // Multi-page desktop pager state
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { config.pageCount }
    )

    LollipopTouchRippleContainer(
        soundEnabled = config.soundEffectsEnabled,
        rippleEnabled = config.touchRippleEnabled,
        waterEffectMode = config.waterEffectMode,
        waterTiltGravityEnabled = config.waterTiltGravityEnabled,
        soundProfile = config.waterSoundProfile,
        soundVolume = config.waterSoundVolume,
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_root")
    ) {
        // 1. Authentic Android 5.0 Wallpaper
        LollipopWallpaper(
            preset = config.wallpaperPreset,
            customUri = config.customWallpaperUri
        )

        // 2. Desktop Home Layer with Swipe-Up gesture to open App Drawer
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isDrawerOpen) {
                    if (!isDrawerOpen) {
                        detectVerticalDragGestures { _, dragAmount ->
                            // Dragging upwards opens the app drawer ("وايضا لما تسحب لفوق تفتح درج التطبيقات")
                            if (dragAmount < -18f) {
                                LollipopSoundEffects.playButtonClick()
                                viewModel.openDrawer()
                            }
                        }
                    }
                }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Android 5.0 Google Quick Search Bar (Toggleable in Settings, hidden by default)
                if (config.showGoogleSearchBar) {
                    LollipopSearchBar(
                        onSearchClick = {
                            viewModel.openDrawer()
                        },
                        onVoiceClick = {
                            AppManager.openWebSearch(context)
                        },
                        liquidGlassTheme = config.liquidGlassTheme
                    )
                }

                // Hosted System Widgets from device
                if (appWidgetHost != null && appWidgetManager != null && homeWidgets.isNotEmpty()) {
                    homeWidgets.forEach { widget ->
                        LollipopHomeWidgetView(
                            widget = widget,
                            appWidgetHost = appWidgetHost,
                            appWidgetManager = appWidgetManager,
                            onRemove = {
                                viewModel.removeWidget(widget.appWidgetId)
                            }
                        )
                    }
                }

                // Classic Roboto Digital Clock & Date Widget (Controlled by showClockWidget)
                if (config.showClockWidget) {
                    LollipopClockWidget(
                        onClockClick = {
                            AppManager.openClock(context)
                        },
                        liquidGlassTheme = config.liquidGlassTheme
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Multi-Page Desktop Pages
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("home_pager")
                ) { pageIndex ->
                    val pageShortcuts = homeShortcuts.filter { it.pageIndex == pageIndex }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(config.gridColumns),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = pageShortcuts,
                            key = { it.id }
                        ) { shortcut ->
                            val app = appMap[shortcut.packageName]
                            val label = app?.label ?: shortcut.label
                            val icon = app?.icon

                            LollipopAppItem(
                                label = label,
                                icon = icon,
                                packageName = shortcut.packageName,
                                onClick = {
                                    when (shortcut.packageName) {
                                        "com.example.themes" -> viewModel.openThemesApp()
                                        "com.example.launcher.settings" -> viewModel.openSettings()
                                        else -> AppManager.launchApp(context, shortcut.packageName)
                                    }
                                },
                                onLongClick = {
                                    val targetApp = app ?: AppInfo(
                                        packageName = shortcut.packageName,
                                        activityName = shortcut.activityName,
                                        label = shortcut.label
                                    )
                                    viewModel.openAppMenu(targetApp, shortcut)
                                },
                                iconPack = config.iconPack,
                                iconSize = config.iconSizeDp.dp,
                                showLabel = config.showAppLabels,
                                isOnWallpaper = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Page Indicator Dots
                if (config.pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .testTag("page_indicator"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(config.pageCount) { page ->
                            val isSelected = pagerState.currentPage == page
                            Canvas(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(6.dp)
                            ) {
                                drawCircle(
                                    color = if (isSelected) Color.White else Color(0x66FFFFFF),
                                    radius = size.minDimension / 2
                                )
                            }
                        }
                    }
                }

                // Bottom Dock with iconic Lollipop App Drawer button
                LollipopDock(
                    dockApps = dockAppList,
                    onAppClick = { app ->
                        when (app.packageName) {
                            "com.example.themes" -> viewModel.openThemesApp()
                            "com.example.launcher.settings" -> viewModel.openSettings()
                            else -> AppManager.launchApp(context, app.packageName)
                        }
                    },
                    onAppLongClick = { app ->
                        viewModel.openAppMenu(app)
                    },
                    onOpenDrawerClick = {
                        viewModel.openDrawer()
                    },
                    iconPack = config.iconPack,
                    iconSize = (config.iconSizeDp - 6).dp,
                    liquidGlassTheme = config.liquidGlassTheme
                )
            }
        }

        // 3. Android 5.0 App Drawer (Translucent glass / transparent background)
        LollipopAppDrawer(
            isOpen = isDrawerOpen,
            apps = filteredApps,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            onAppClick = { app ->
                when (app.packageName) {
                    "com.example.themes" -> {
                        viewModel.closeDrawer()
                        viewModel.openThemesApp()
                    }
                    "com.example.launcher.settings" -> {
                        viewModel.closeDrawer()
                        viewModel.openSettings()
                    }
                    else -> {
                        AppManager.launchApp(context, app.packageName)
                        viewModel.closeDrawer()
                    }
                }
            },
            onAppLongClick = { app ->
                viewModel.openAppMenu(app)
            },
            onCloseClick = {
                viewModel.closeDrawer()
            },
            onSettingsClick = {
                viewModel.openSettings()
            },
            favoriteApps = favoriteApps,
            iconPack = config.iconPack,
            drawerStyle = config.drawerStyle,
            columns = config.gridColumns,
            iconSize = (config.iconSizeDp - 4).dp,
            showLabels = config.showAppLabels,
            isDarkTheme = !config.nostalgiaMode,
            liquidGlassTheme = config.liquidGlassTheme
        )

        // 4. Long-Press App Context Menu
        val menuApp = selectedAppForMenu
        if (menuApp != null) {
            val isFavorite = favoritePackages.contains(menuApp.packageName)
            val isInDock = dockPackages.contains(menuApp.packageName)

            LollipopAppMenuDialog(
                app = menuApp,
                shortcut = selectedShortcutForMenu,
                isFavorite = isFavorite,
                isInDock = isInDock,
                onOpen = {
                    when (menuApp.packageName) {
                        "com.example.themes" -> viewModel.openThemesApp()
                        "com.example.launcher.settings" -> viewModel.openSettings()
                        else -> AppManager.launchApp(context, menuApp.packageName)
                    }
                    viewModel.closeAppMenu()
                },
                onAppInfo = {
                    AppManager.openAppInfo(context, menuApp.packageName)
                    viewModel.closeAppMenu()
                },
                onToggleFavorite = {
                    viewModel.toggleFavorite(menuApp.packageName)
                },
                onAddToHome = {
                    viewModel.addShortcutToHome(menuApp, pagerState.currentPage)
                },
                onRemoveFromHome = {
                    selectedShortcutForMenu?.let { viewModel.removeShortcut(it.id) }
                },
                onToggleDock = {
                    if (isInDock) {
                        viewModel.removeFromDock(menuApp.packageName)
                    } else {
                        viewModel.addToDock(menuApp.packageName)
                    }
                },
                onUninstall = {
                    AppManager.uninstallApp(context, menuApp.packageName)
                    viewModel.closeAppMenu()
                },
                onDismiss = {
                    viewModel.closeAppMenu()
                }
            )
        }

        // 5. Dedicated "Themes" App Dialog ("برنامج اسمه ثيم")
        if (isThemesAppOpen) {
            LollipopThemesDialog(
                config = config,
                onConfigChange = { viewModel.updateConfig(it) },
                onOpenWidgetPicker = {
                    viewModel.openWidgetPicker()
                },
                communityWallpapers = communityWallpapers,
                onPublishWallpaper = { title, author, desc, preset, uri ->
                    viewModel.publishCommunityWallpaper(title, author, desc, preset, uri)
                },
                onDeleteWallpaper = { id ->
                    viewModel.deleteCommunityWallpaper(id)
                },
                onApplyWallpaper = { preset, uri ->
                    viewModel.applyWallpaper(preset, uri)
                },
                onClose = { viewModel.closeThemesApp() }
            )
        }

        // 6. System Widget Picker Dialog ("خاصيه وضع Widget من الهاتف")
        if (isWidgetPickerOpen) {
            LollipopWidgetPickerDialog(
                onSelectProvider = { provider ->
                    onAddWidgetProvider?.invoke(provider)
                    viewModel.closeWidgetPicker()
                    Toast.makeText(context, "تمت إضافة الودجت بنجاح", Toast.LENGTH_SHORT).show()
                },
                onClose = { viewModel.closeWidgetPicker() }
            )
        }

        // 7. HUBlub Settings Dialog
        if (isSettingsOpen) {
            LollipopSettingsDialog(
                config = config,
                onConfigChange = { viewModel.updateConfig(it) },
                onSetDefaultHome = { AppManager.openHomeSettings(context) },
                onExportBackup = { viewModel.exportBackup() },
                onRestoreBackup = { viewModel.restoreBackup(it) },
                onDismiss = { viewModel.closeSettings() }
            )
        }

        // 8. First Run Welcome Dialog with Old vs New Wallpaper Selection
        if (isFirstRunDialogOpen) {
            LollipopFirstRunDialog(
                currentWallpaper = config.wallpaperPreset,
                onSelectWallpaper = { preset ->
                    viewModel.updateConfig(config.copy(wallpaperPreset = preset))
                },
                onSetDefaultHome = { AppManager.openHomeSettings(context) },
                onGetStarted = { viewModel.completeFirstRun() }
            )
        }
    }
}
