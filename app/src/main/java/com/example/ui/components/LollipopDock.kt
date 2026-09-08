package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AppInfo
import com.example.model.IconPackStyle
import com.example.util.LollipopSoundEffects

/**
 * Authentic Android 5.0 Lollipop Bottom Dock.
 * Displays favorite apps alongside the iconic 6-dot circular App Drawer button in the center.
 */
@Composable
fun LollipopDock(
    dockApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onOpenDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    iconSize: Dp = 50.dp
) {
    // Dock background: subtle translucent material shelf
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(Color(0x28000000))
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("lollipop_dock"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First 2 dock apps
            val leftApps = dockApps.take(2)
            for (app in leftApps) {
                LollipopAppItem(
                    label = app.label,
                    icon = app.icon,
                    packageName = app.packageName,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    iconPack = iconPack,
                    iconSize = iconSize,
                    showLabel = false,
                    isOnWallpaper = true,
                    modifier = Modifier.size(56.dp)
                )
            }

            // Fill slot if less than 2
            if (leftApps.size < 2) {
                repeat(2 - leftApps.size) {
                    Box(modifier = Modifier.size(56.dp))
                }
            }

            // Iconic Android 5.0 Lollipop App Drawer Launcher Button
            LollipopAppDrawerButton(
                onClick = onOpenDrawerClick,
                modifier = Modifier.size(56.dp)
            )

            // Next 2 dock apps
            val rightApps = dockApps.drop(2).take(2)
            for (app in rightApps) {
                LollipopAppItem(
                    label = app.label,
                    icon = app.icon,
                    packageName = app.packageName,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    iconPack = iconPack,
                    iconSize = iconSize,
                    showLabel = false,
                    isOnWallpaper = true,
                    modifier = Modifier.size(56.dp)
                )
            }

            // Fill slot if less than 2
            if (rightApps.size < 2) {
                repeat(2 - rightApps.size) {
                    Box(modifier = Modifier.size(56.dp))
                }
            }
        }
    }
}

/**
 * The iconic Android 5.0 Lollipop App Drawer Button:
 * A clean white circle containing 6 grey dots arranged in a 2x3 grid.
 */
@Composable
fun LollipopAppDrawerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.84f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "drawer_btn_scale"
    )

    Surface(
        shape = CircleShape,
        color = Color(0xF2FFFFFF),
        shadowElevation = if (isPressed) 1.dp else 4.dp,
        modifier = modifier
            .testTag("lollipop_drawer_button")
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    LollipopSoundEffects.playWaterDrop()
                    onClick()
                }
            )
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(24.dp)) {
                val dotColor = Color(0xFF616161)
                val dotRadius = 2.4.dp.toPx()

                // 2 rows, 3 columns of dots
                val colSpacing = size.width / 2.5f
                val rowSpacing = size.height / 1.8f

                val startX = (size.width - (colSpacing * 2)) / 2f
                val startY = (size.height - rowSpacing) / 2f

                for (row in 0..1) {
                    for (col in 0..2) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(startX + col * colSpacing, startY + row * rowSpacing)
                        )
                    }
                }
            }
        }
    }
}
