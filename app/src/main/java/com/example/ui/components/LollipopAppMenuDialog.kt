package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppInfo
import com.example.model.HomeShortcut
import com.example.ui.theme.LollipopAmber500
import com.example.ui.theme.LollipopDeepOrange500
import com.example.ui.theme.LollipopTeal500
import com.example.ui.theme.LollipopTeal700
import com.example.ui.theme.MaterialCardWhite
import com.example.ui.theme.MaterialTextPrimary
import com.example.ui.theme.MaterialTextSecondary
import com.example.util.LollipopSoundEffects

/**
 * Material 1.0 context menu dialog displayed upon long pressing an application.
 * Provides instant actions: Open, App Info, Favorite, Add/Remove Home shortcut, Dock toggle, and Uninstall.
 */
@Composable
fun LollipopAppMenuDialog(
    app: AppInfo,
    shortcut: HomeShortcut?,
    isFavorite: Boolean,
    isInDock: Boolean,
    onOpen: () -> Unit,
    onAppInfo: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToHome: () -> Unit,
    onRemoveFromHome: () -> Unit,
    onToggleDock: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("app_menu_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // App Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LollipopAppIconView(
                        label = app.label,
                        packageName = app.packageName,
                        systemIcon = app.icon,
                        size = 52.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = app.packageName,
                            fontSize = 12.sp,
                            color = MaterialTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (app.versionName.isNotEmpty()) {
                            Text(
                                text = "v${app.versionName}",
                                fontSize = 11.sp,
                                color = LollipopTeal700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0x1F000000))
                Spacer(modifier = Modifier.height(8.dp))

                // Actions
                MenuActionItem(
                    icon = Icons.Default.OpenInNew,
                    iconTint = LollipopTeal700,
                    text = "Open App",
                    testTag = "menu_open_app",
                    onClick = onOpen
                )

                if (shortcut != null) {
                    MenuActionItem(
                        icon = Icons.Default.PlaylistRemove,
                        iconTint = LollipopDeepOrange500,
                        text = "Remove from Home",
                        testTag = "menu_remove_from_home",
                        onClick = onRemoveFromHome
                    )
                } else {
                    MenuActionItem(
                        icon = Icons.Default.PlaylistAdd,
                        iconTint = LollipopTeal500,
                        text = "Add to Home Screen",
                        testTag = "menu_add_to_home",
                        onClick = onAddToHome
                    )
                }

                MenuActionItem(
                    icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    iconTint = if (isFavorite) LollipopDeepOrange500 else MaterialTextSecondary,
                    text = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                    testTag = "menu_toggle_favorite",
                    onClick = onToggleFavorite
                )

                MenuActionItem(
                    icon = Icons.Default.Star,
                    iconTint = if (isInDock) LollipopAmber500 else MaterialTextSecondary,
                    text = if (isInDock) "Remove from Dock" else "Add to Dock",
                    testTag = "menu_toggle_dock",
                    onClick = onToggleDock
                )

                MenuActionItem(
                    icon = Icons.Default.Info,
                    iconTint = LollipopTeal700,
                    text = "App Info",
                    testTag = "menu_app_info",
                    onClick = onAppInfo
                )

                if (!app.isSystemApp) {
                    MenuActionItem(
                        icon = Icons.Default.Delete,
                        iconTint = LollipopDeepOrange500,
                        text = "Uninstall",
                        testTag = "menu_uninstall",
                        onClick = onUninstall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        LollipopSoundEffects.playSoftPop()
                        onDismiss()
                    }) {
                        Text(
                            text = "CLOSE",
                            fontWeight = FontWeight.Bold,
                            color = LollipopTeal700,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuActionItem(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                LollipopSoundEffects.playWaterDrop()
                onClick()
            }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = MaterialTextPrimary
        )
    }
}
