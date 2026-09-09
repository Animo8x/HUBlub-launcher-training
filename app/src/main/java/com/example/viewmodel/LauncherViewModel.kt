package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppManager
import com.example.data.LauncherPreferencesRepository
import com.example.model.AppInfo
import com.example.model.CommunityWallpaper
import com.example.model.HomeShortcut
import com.example.model.LauncherConfig
import com.example.model.WallpaperPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Main ViewModel for HUBlub Launcher.
 * Handles app indexing, search filtering, home grid layout, dock state, and persistent settings.
 */
class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LauncherPreferencesRepository(application)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _homeShortcuts = MutableStateFlow<List<HomeShortcut>>(emptyList())
    val homeShortcuts: StateFlow<List<HomeShortcut>> = _homeShortcuts.asStateFlow()

    private val _dockPackages = MutableStateFlow<List<String>>(emptyList())
    val dockPackages: StateFlow<List<String>> = _dockPackages.asStateFlow()

    private val _favoritePackages = MutableStateFlow<Set<String>>(emptySet())
    val favoritePackages: StateFlow<Set<String>> = _favoritePackages.asStateFlow()

    private val _config = MutableStateFlow(repository.loadConfig())
    val config: StateFlow<LauncherConfig> = _config.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isThemesAppOpen = MutableStateFlow(false)
    val isThemesAppOpen: StateFlow<Boolean> = _isThemesAppOpen.asStateFlow()

    private val _isWidgetPickerOpen = MutableStateFlow(false)
    val isWidgetPickerOpen: StateFlow<Boolean> = _isWidgetPickerOpen.asStateFlow()

    private val _homeWidgets = MutableStateFlow<List<com.example.model.HomeWidget>>(emptyList())
    val homeWidgets: StateFlow<List<com.example.model.HomeWidget>> = _homeWidgets.asStateFlow()

    private val _selectedAppForMenu = MutableStateFlow<AppInfo?>(null)
    val selectedAppForMenu: StateFlow<AppInfo?> = _selectedAppForMenu.asStateFlow()

    private val _selectedShortcutForMenu = MutableStateFlow<HomeShortcut?>(null)
    val selectedShortcutForMenu: StateFlow<HomeShortcut?> = _selectedShortcutForMenu.asStateFlow()

    private val _isFirstRunDialogOpen = MutableStateFlow(repository.isFirstRun())
    val isFirstRunDialogOpen: StateFlow<Boolean> = _isFirstRunDialogOpen.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(_installedApps, _searchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            val q = query.trim().lowercase()
            apps.filter { app ->
                app.label.lowercase().contains(q) || app.packageName.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        _homeShortcuts.value = repository.loadShortcuts()
        _dockPackages.value = repository.loadDockPackages()
        _favoritePackages.value = repository.loadFavorites()
        _homeWidgets.value = repository.loadWidgets()
        refreshApps(isInitial = true)
    }

    /**
     * Refreshes the list of installed applications from PackageManager and adds the built-in Themes and Settings apps.
     */
    fun refreshApps(isInitial: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val rawApps = AppManager.getInstalledApps(getApplication())

            // Built-in dedicated Themes app ("برنامج اسمه ثيم")
            val themesApp = AppInfo(
                packageName = "com.example.themes",
                activityName = "ThemesActivity",
                label = "الثيمات (Themes)",
                versionName = "3.0",
                isSystemApp = true
            )

            // Built-in dedicated Settings app ("خلي الاعدادات تطبيق مو ايقونه الترس")
            val settingsApp = AppInfo(
                packageName = "com.example.launcher.settings",
                activityName = "SettingsActivity",
                label = "إعدادات اللانشر (Settings)",
                versionName = "3.0",
                isSystemApp = true
            )

            val fullList = mutableListOf<AppInfo>()
            fullList.add(themesApp)
            fullList.add(settingsApp)
            fullList.addAll(rawApps.filter { it.packageName != "com.example.themes" && it.packageName != "com.example.launcher.settings" })

            _installedApps.value = fullList

            // If this is the initial launch and shortcuts/dock are empty, provision default popular shortcuts
            if (isInitial && _homeShortcuts.value.isEmpty() && fullList.isNotEmpty()) {
                setupDefaultShortcutsAndDock(fullList)
            }
        }
    }

    private fun setupDefaultShortcutsAndDock(apps: List<AppInfo>) {
        val defaultDock = mutableListOf<String>()
        val defaultShortcuts = mutableListOf<HomeShortcut>()

        // Find common app candidates: dialer, messaging, browser, camera, settings
        val dialer = apps.find { it.packageName.contains("dialer") || it.packageName.contains("phone") }
        val browser = apps.find { it.packageName.contains("chrome") || it.packageName.contains("browser") }
        val messaging = apps.find { it.packageName.contains("message") || it.packageName.contains("sms") }
        val camera = apps.find { it.packageName.contains("camera") }
        val settings = apps.find { it.packageName.contains("settings") }

        val candidates = listOfNotNull(dialer, messaging, browser, camera, settings).distinctBy { it.packageName }
        val dockList = if (candidates.size >= 4) candidates.take(4) else apps.take(4)

        dockList.forEach { defaultDock.add(it.packageName) }
        _dockPackages.value = defaultDock
        repository.saveDockPackages(defaultDock)

        // Add remaining top apps as home shortcuts on page 0
        val remainingApps = apps.filter { it.packageName !in defaultDock }.take(8)
        remainingApps.forEachIndexed { index, app ->
            val col = index % 4
            val row = index / 4
            defaultShortcuts.add(
                HomeShortcut(
                    id = "${app.packageName}_def_$index",
                    packageName = app.packageName,
                    activityName = app.activityName,
                    label = app.label,
                    pageIndex = 0,
                    cellX = col,
                    cellY = row
                )
            )
        }
        _homeShortcuts.value = defaultShortcuts
        repository.saveShortcuts(defaultShortcuts)
    }

    fun onPackageAdded(packageName: String) {
        refreshApps()
    }

    fun onPackageRemoved(packageName: String) {
        AppManager.clearCacheForPackage(packageName)
        // Remove from shortcuts and dock if present
        val updatedShortcuts = _homeShortcuts.value.filter { it.packageName != packageName }
        if (updatedShortcuts.size != _homeShortcuts.value.size) {
            _homeShortcuts.value = updatedShortcuts
            repository.saveShortcuts(updatedShortcuts)
        }

        val updatedDock = _dockPackages.value.filter { it != packageName }
        if (updatedDock.size != _dockPackages.value.size) {
            _dockPackages.value = updatedDock
            repository.saveDockPackages(updatedDock)
        }

        val updatedFavorites = _favoritePackages.value.filter { it != packageName }.toSet()
        if (updatedFavorites.size != _favoritePackages.value.size) {
            _favoritePackages.value = updatedFavorites
            repository.saveFavorites(updatedFavorites)
        }

        refreshApps()
    }

    fun onPackageChanged(packageName: String) {
        AppManager.clearCacheForPackage(packageName)
        refreshApps()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openDrawer() {
        _isDrawerOpen.value = true
    }

    fun closeDrawer() {
        _isDrawerOpen.value = false
        _searchQuery.value = ""
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun openThemesApp() {
        _isThemesAppOpen.value = true
    }

    fun closeThemesApp() {
        _isThemesAppOpen.value = false
    }

    fun openWidgetPicker() {
        _isWidgetPickerOpen.value = true
    }

    fun closeWidgetPicker() {
        _isWidgetPickerOpen.value = false
    }

    fun addWidget(widget: com.example.model.HomeWidget) {
        val current = _homeWidgets.value.toMutableList()
        current.add(widget)
        _homeWidgets.value = current
        repository.saveWidgets(current)
    }

    fun removeWidget(appWidgetId: Int) {
        val current = _homeWidgets.value.filter { it.appWidgetId != appWidgetId }
        _homeWidgets.value = current
        repository.saveWidgets(current)
    }

    fun openAppMenu(app: AppInfo, shortcut: HomeShortcut? = null) {
        _selectedAppForMenu.value = app
        _selectedShortcutForMenu.value = shortcut
    }

    fun closeAppMenu() {
        _selectedAppForMenu.value = null
        _selectedShortcutForMenu.value = null
    }

    fun addShortcutToHome(app: AppInfo, page: Int = 0) {
        val current = _homeShortcuts.value.toMutableList()
        // Find next available slot on requested page
        val maxItemsPerPage = _config.value.gridColumns * _config.value.gridRows
        val pageItems = current.filter { it.pageIndex == page }
        val newIndex = pageItems.size.coerceAtMost(maxItemsPerPage - 1)
        val col = newIndex % _config.value.gridColumns
        val row = newIndex / _config.value.gridColumns

        val newShortcut = HomeShortcut(
            id = "${app.packageName}_${System.currentTimeMillis()}",
            packageName = app.packageName,
            activityName = app.activityName,
            label = app.label,
            pageIndex = page,
            cellX = col,
            cellY = row
        )
        current.add(newShortcut)
        _homeShortcuts.value = current
        repository.saveShortcuts(current)
        closeAppMenu()
    }

    fun removeShortcut(shortcutId: String) {
        val updated = _homeShortcuts.value.filter { it.id != shortcutId }
        _homeShortcuts.value = updated
        repository.saveShortcuts(updated)
        closeAppMenu()
    }

    fun toggleFavorite(packageName: String) {
        val current = _favoritePackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _favoritePackages.value = current
        repository.saveFavorites(current)
        closeAppMenu()
    }

    fun addToDock(packageName: String) {
        val current = _dockPackages.value.toMutableList()
        if (!current.contains(packageName)) {
            if (current.size >= 5) {
                current.removeAt(current.size - 1)
            }
            current.add(packageName)
            _dockPackages.value = current
            repository.saveDockPackages(current)
        }
        closeAppMenu()
    }

    fun toggleDock(packageName: String) {
        if (_dockPackages.value.contains(packageName)) {
            removeFromDock(packageName)
        } else {
            addToDock(packageName)
        }
    }

    fun removeFromDock(packageName: String) {
        val current = _dockPackages.value.toMutableList()
        current.remove(packageName)
        _dockPackages.value = current
        repository.saveDockPackages(current)
        closeAppMenu()
    }

    fun addPage() {
        val newCount = (_config.value.pageCount + 1).coerceAtMost(9)
        updateConfig(_config.value.copy(pageCount = newCount))
    }

    fun removePage(pageIndex: Int) {
        if (_config.value.pageCount <= 1) return
        val updatedShortcuts = _homeShortcuts.value
            .filter { it.pageIndex != pageIndex }
            .map { if (it.pageIndex > pageIndex) it.copy(pageIndex = it.pageIndex - 1) else it }
        _homeShortcuts.value = updatedShortcuts
        repository.saveShortcuts(updatedShortcuts)
        updateConfig(_config.value.copy(pageCount = _config.value.pageCount - 1))
    }

    fun addShortcutToHomeAt(app: AppInfo, page: Int, cellX: Int, cellY: Int) {
        val current = _homeShortcuts.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.pageIndex == page && it.cellX == cellX && it.cellY == cellY }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        }
        val newShortcut = HomeShortcut(
            id = "${app.packageName}_${System.currentTimeMillis()}",
            packageName = app.packageName,
            activityName = app.activityName,
            label = app.label,
            pageIndex = page,
            cellX = cellX,
            cellY = cellY
        )
        current.add(newShortcut)
        _homeShortcuts.value = current
        repository.saveShortcuts(current)
    }

    private val _draggedApp = MutableStateFlow<AppInfo?>(null)
    val draggedApp: StateFlow<AppInfo?> = _draggedApp.asStateFlow()

    fun startDraggingApp(app: AppInfo) {
        _draggedApp.value = app
        closeDrawer()
    }

    fun endDraggingApp() {
        _draggedApp.value = null
    }

    fun updateConfig(newConfig: LauncherConfig) {
        _config.value = newConfig
        repository.saveConfig(newConfig)
    }

    fun completeFirstRun() {
        _isFirstRunDialogOpen.value = false
        repository.setFirstRunCompleted(true)
        val updated = _config.value.copy(firstRunCompleted = true)
        _config.value = updated
        repository.saveConfig(updated)
    }

    fun exportBackup(): String {
        return repository.exportBackup()
    }

    fun restoreBackup(json: String): Boolean {
        val success = repository.restoreBackup(json)
        if (success) {
            _config.value = repository.loadConfig()
            _homeShortcuts.value = repository.loadShortcuts()
            _dockPackages.value = repository.loadDockPackages()
            _favoritePackages.value = repository.loadFavorites()
        }
        return success
    }

    // Community Wallpapers (Local user photos from device files, without fake data)
    private val _communityWallpapers = MutableStateFlow<List<CommunityWallpaper>>(
        repository.loadCommunityWallpapers()
    )
    val communityWallpapers: StateFlow<List<CommunityWallpaper>> = _communityWallpapers.asStateFlow()

    fun publishCommunityWallpaper(title: String, author: String, description: String, preset: WallpaperPreset?, uri: String? = null) {
        val newWallpaper = CommunityWallpaper(
            id = "comm_${System.currentTimeMillis()}",
            title = title.ifBlank { "خلفية من ملفات جهازي" },
            author = author.ifBlank { "أنا" },
            description = description.ifBlank { "صورة مخصصة تم اختيارها من ذاكرة الهاتف." },
            preset = preset,
            imageUri = uri,
            colorHex = 0xFF009688,
            likesCount = 0
        )
        val current = _communityWallpapers.value.toMutableList()
        current.add(0, newWallpaper)
        _communityWallpapers.value = current
        repository.saveCommunityWallpapers(current)
    }

    fun deleteCommunityWallpaper(id: String) {
        val current = _communityWallpapers.value.filter { it.id != id }
        _communityWallpapers.value = current
        repository.saveCommunityWallpapers(current)
    }

    fun applyWallpaper(preset: WallpaperPreset?, customUri: String? = null) {
        val updated = if (customUri != null) {
            _config.value.copy(customWallpaperUri = customUri)
        } else if (preset != null) {
            _config.value.copy(wallpaperPreset = preset, customWallpaperUri = null)
        } else {
            _config.value
        }
        updateConfig(updated)
    }
}
