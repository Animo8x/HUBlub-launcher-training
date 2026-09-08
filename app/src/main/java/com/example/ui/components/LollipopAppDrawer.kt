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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    iconPack: IconPackStyle = IconPackStyle.SYSTEM_FREEFORM,
    drawerStyle: DrawerStyle = DrawerStyle.TRANSLUCENT_GLASS,
    columns: Int = 4,
    iconSize: Dp = 52.dp,
    showLabels: Boolean = true,
    isDarkTheme: Boolean = false
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
        // Compute translucent background according to selected DrawerStyle
        val backgroundColor = when (drawerStyle) {
            DrawerStyle.TRANSLUCENT_GLASS -> Color(0xD0121E24) // Sleek dark glass showing wallpaper
            DrawerStyle.SEMI_TRANSPARENT -> Color(0x90121E24)
            DrawerStyle.CRYSTAL_CLEAR -> Color(0x40121E24)
            DrawerStyle.CLASSIC_SOLID -> if (isDarkTheme) Color(0xFF263238) else MaterialCardWhite
        }

        val textColor = if (drawerStyle != DrawerStyle.CLASSIC_SOLID || isDarkTheme) Color.White else MaterialTextPrimary

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("lollipop_app_drawer"),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Sleek Modern App Drawer Header (No bulky old green bar, no gear icon)
                Surface(
                    color = Color(0x33000000),
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
                                tint = Color.White
                            )
                        }

                        if (!isSearchExpanded) {
                            // Title & App Count
                            Text(
                                text = "التطبيقات (${apps.size})",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 6.dp)
                            )

                            // Magnifying Glass Search Button ("زر مكبر كأيقونة للبحث")
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
                                    tint = Color.White
                                )
                            }
                        } else {
                            // Expandable Clean Search Bar
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                placeholder = {
                                    Text(
                                        text = "البحث عن التطبيقات...",
                                        color = Color(0xAAFFFFFF),
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (searchQuery.isNotEmpty()) {
                                                onSearchQueryChange("")
                                            } else {
                                                isSearchExpanded = false
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0x66FFFFFF),
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    cursorColor = Color.White,
                                    focusedContainerColor = Color(0x33000000),
                                    unfocusedContainerColor = Color(0x22000000)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .padding(end = 4.dp)
                                    .testTag("drawer_search_input")
                            )
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
