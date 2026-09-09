package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.DrawerStyle
import com.example.model.IconPackStyle
import com.example.ui.theme.LollipopAmber500
import com.example.ui.theme.LollipopTeal500
import com.example.ui.theme.LollipopTeal700
import com.example.ui.theme.MaterialCardWhite
import com.example.ui.theme.MaterialTextPrimary
import com.example.ui.theme.MaterialTextSecondary
import com.example.util.LollipopSoundEffects

/**
 * Authentic Android 5.0 Lollipop App Drawer.
 * High-performance vertical grid with crash-proof instant search, favorites, drag to desktop, and pure white or frosted blur background.
 */
@Composable
fun LollipopAppDrawer(
    isOpen: Boolean,
    apps: List<AppInfo>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onCloseClick: () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
    onStartDrag: ((AppInfo) -> Unit)? = null,
    favoriteApps: List<AppInfo> = emptyList(),
    modifier: Modifier = Modifier,
    iconPack: IconPackStyle = IconPackStyle.SYSTEM_FREEFORM,
    drawerStyle: DrawerStyle = DrawerStyle.CLASSIC_SOLID,
    columns: Int = 4,
    iconSize: Dp = 52.dp,
    showLabels: Boolean = true,
    isDarkTheme: Boolean = false,
    liquidGlassTheme: Boolean = false
) {
    var isSearchExpanded by remember { mutableStateOf(searchQuery.isNotEmpty()) }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(180)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(160)
        ),
        modifier = modifier.fillMaxSize()
    ) {
        // Compute background according to selected DrawerStyle or Liquid Glass
        val backgroundColor = if (liquidGlassTheme) {
            if (isDarkTheme) Color(0xD9101E26) else Color(0xDCF0F8FF)
        } else {
            when (drawerStyle) {
                DrawerStyle.CLASSIC_SOLID -> if (isDarkTheme) Color(0xFF263238) else Color.White
                DrawerStyle.FROSTED_BLUR -> if (isDarkTheme) Color(0xCC1A2327) else Color(0xDCF5F7FA)
                DrawerStyle.TRANSLUCENT_GLASS -> Color(0xD0121E24)
                DrawerStyle.SEMI_TRANSPARENT -> Color(0x90121E24)
                DrawerStyle.CRYSTAL_CLEAR -> Color(0x40121E24)
            }
        }

        val isLightBg = (drawerStyle == DrawerStyle.CLASSIC_SOLID && !isDarkTheme) ||
                (drawerStyle == DrawerStyle.FROSTED_BLUR && !isDarkTheme) ||
                (liquidGlassTheme && !isDarkTheme)
        val headerColor = if (liquidGlassTheme) {
            if (isLightBg) Color(0x55FFFFFF) else Color(0x44000000)
        } else {
            if (isLightBg) Color(0xFFEEEEEE) else Color(0x33000000)
        }
        val headerTextColor = if (isLightBg) Color(0xFF263238) else Color.White
        val bodyTextColor = if (isLightBg) MaterialTextPrimary else Color.White

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("lollipop_app_drawer"),
            shape = if (liquidGlassTheme) RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp) else RoundedCornerShape(0.dp),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Sleek Modern App Drawer Header
                Surface(
                    color = headerColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                LollipopSoundEffects.playSoftPop()
                                if (isSearchExpanded) {
                                    isSearchExpanded = false
                                    onSearchQueryChange("")
                                } else {
                                    onCloseClick()
                                }
                            },
                            modifier = Modifier.testTag("drawer_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = headerTextColor
                            )
                        }

                        if (!isSearchExpanded) {
                            // Title & App Count
                            Text(
                                text = "التطبيقات (${apps.size})",
                                color = headerTextColor,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 6.dp)
                            )

                            // Magnifying Glass Search Button
                            IconButton(
                                onClick = {
                                    LollipopSoundEffects.playButtonClick()
                                    isSearchExpanded = true
                                },
                                modifier = Modifier.testTag("drawer_search_toggle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = headerTextColor
                                )
                            }
                        } else {
                            // Crash-Proof Safe Search Bar using BasicTextField
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .background(
                                        color = if (isLightBg) Color(0xFFE0E0E0) else Color(0x44FFFFFF),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = if (isLightBg) Color(0xFF616161) else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "البحث عن التطبيقات...",
                                                color = if (isLightBg) Color(0xFF757575) else Color(0xAAFFFFFF),
                                                fontSize = 14.sp
                                            )
                                        }
                                        BasicTextField(
                                            value = searchQuery,
                                            onValueChange = onSearchQueryChange,
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                color = if (isLightBg) Color(0xFF212121) else Color.White,
                                                fontSize = 15.sp
                                            ),
                                            cursorBrush = SolidColor(if (isLightBg) LollipopTeal700 else Color.White),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("drawer_search_input")
                                        )
                                    }
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { onSearchQueryChange("") },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                tint = if (isLightBg) Color(0xFF616161) else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // App Grid
                if (apps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "لا توجد تطبيقات تطابق \"$searchQuery\"" else "جاري تحميل التطبيقات...",
                            color = if (isLightBg) MaterialTextSecondary else Color(0xB3FFFFFF),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("drawer_grid")
                    ) {
                        // Real Functional Favorites Row in App Drawer
                        if (favoriteApps.isNotEmpty() && searchQuery.isEmpty()) {
                            item(span = { GridItemSpan(columns) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = LollipopAmber500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "المفضلة (${favoriteApps.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isLightBg) LollipopTeal700 else LollipopAmber500
                                        )
                                    }

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(favoriteApps, key = { "fav_${it.packageName}" }) { favApp ->
                                            LollipopAppItem(
                                                label = favApp.label,
                                                icon = favApp.icon,
                                                packageName = favApp.packageName,
                                                onClick = { onAppClick(favApp) },
                                                onLongClick = { onAppLongClick(favApp) },
                                                iconPack = iconPack,
                                                iconSize = iconSize,
                                                showLabel = showLabels,
                                                isOnWallpaper = false,
                                                textColor = bodyTextColor,
                                                modifier = Modifier.width(72.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(if (isLightBg) Color(0x1F000000) else Color(0x22FFFFFF))
                                    )
                                }
                            }
                        }

                        // Installed Apps List
                        items(
                            items = apps,
                            key = { it.packageName }
                        ) { app ->
                            LollipopAppItem(
                                label = app.label,
                                icon = app.icon,
                                packageName = app.packageName,
                                onClick = { onAppClick(app) },
                                onLongClick = {
                                    if (onStartDrag != null) {
                                        onStartDrag(app)
                                    } else {
                                        onAppLongClick(app)
                                    }
                                },
                                iconPack = iconPack,
                                iconSize = iconSize,
                                showLabel = showLabels,
                                isOnWallpaper = false,
                                textColor = bodyTextColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
