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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.DrawerStyle
import com.example.model.IconPackStyle
import com.example.ui.theme.LollipopTeal500
import com.example.ui.theme.LollipopTeal700
import com.example.ui.theme.MaterialCardWhite
import com.example.ui.theme.MaterialTextPrimary
import com.example.ui.theme.MaterialTextSecondary
import com.example.util.LollipopSoundEffects

/**
 * Authentic Android 5.0 Lollipop App Drawer.
 * High-performance vertical grid with instant search and translucent glass / transparent background.
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
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    drawerStyle: DrawerStyle = DrawerStyle.TRANSLUCENT_GLASS,
    columns: Int = 4,
    iconSize: Dp = 52.dp,
    showLabels: Boolean = true,
    isDarkTheme: Boolean = false
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(320, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.92f,
            animationSpec = tween(320, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(240)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it / 3 },
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + scaleOut(
            targetScale = 0.92f,
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        modifier = modifier.fillMaxSize()
    ) {
        // Compute translucent background according to selected DrawerStyle
        val backgroundColor = when (drawerStyle) {
            DrawerStyle.TRANSLUCENT_GLASS -> Color(0xB8102027) // ~72% glass opacity, showing wallpaper through
            DrawerStyle.SEMI_TRANSPARENT -> Color(0x80102027) // 50% opacity
            DrawerStyle.CRYSTAL_CLEAR -> Color(0x38102027) // 22% crystal opacity
            DrawerStyle.CLASSIC_SOLID -> if (isDarkTheme) Color(0xFF263238) else MaterialCardWhite
        }

        val topBarColor = when (drawerStyle) {
            DrawerStyle.CLASSIC_SOLID -> LollipopTeal700
            else -> Color(0xDD00796B)
        }

        val textColor = if (drawerStyle != DrawerStyle.CLASSIC_SOLID || isDarkTheme) Color.White else MaterialTextPrimary

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("lollipop_app_drawer"),
            color = backgroundColor,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Top AppBar in Lollipop Teal style
                Surface(
                    color = topBarColor,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(top = 8.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        LollipopSoundEffects.playSoftPop()
                                        onCloseClick()
                                    },
                                    modifier = Modifier.testTag("drawer_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Close Drawer",
                                        tint = Color.White
                                    )
                                }
                                Text(
                                    text = "APPS (${apps.size})",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    LollipopSoundEffects.playSoftPop()
                                    onSettingsClick()
                                },
                                modifier = Modifier.testTag("drawer_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Launcher Settings",
                                    tint = Color.White
                                )
                            }
                        }

                        // Search Field inside drawer
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = {
                                Text(
                                    text = "Search apps...",
                                    color = Color(0x99FFFFFF),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0x66FFFFFF),
                                cursorColor = Color.White,
                                focusedContainerColor = Color(0x22000000),
                                unfocusedContainerColor = Color(0x15000000)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(horizontal = 8.dp)
                                .testTag("drawer_search_input")
                        )
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
                            text = if (searchQuery.isNotBlank()) "No apps matching \"$searchQuery\"" else "Loading applications...",
                            color = if (isDarkTheme) Color(0xB3FFFFFF) else MaterialTextSecondary,
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
                        items(
                            items = apps,
                            key = { it.packageName }
                        ) { app ->
                            LollipopAppItem(
                                label = app.label,
                                icon = app.icon,
                                packageName = app.packageName,
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) },
                                iconPack = iconPack,
                                iconSize = iconSize,
                                showLabel = showLabels,
                                isOnWallpaper = false,
                                textColor = textColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
