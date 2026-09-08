package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackStyle
import com.example.ui.theme.LollipopTeal500
import com.example.util.LollipopSoundEffects

/**
 * Standard Android 5.0 Lollipop App Item component.
 * Displays the real PackageManager icon and label with authentic drop-shadow on wallpapers,
 * spring bounce animation on press, and soothing tactile feedback.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LollipopAppItem(
    label: String,
    icon: ImageBitmap?,
    packageName: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    iconSize: Dp = 56.dp,
    showLabel: Boolean = true,
    isOnWallpaper: Boolean = true,
    textColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "app_item_press_scale"
    )

    val textShadow = if (isOnWallpaper) {
        Shadow(
            color = Color(0x99000000),
            offset = Offset(1f, 2f),
            blurRadius = 3f
        )
    } else null

    Column(
        modifier = modifier
            .testTag("app_item_$packageName")
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    LollipopSoundEffects.playWaterDrop()
                    onClick()
                },
                onLongClick = {
                    LollipopSoundEffects.playSoftPop()
                    onLongClick()
                }
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Icon rendered with authentic Android 5.0 Lollipop circular icon pack
        LollipopAppIconView(
            label = label,
            packageName = packageName,
            systemIcon = icon,
            iconPack = iconPack,
            size = iconSize
        )

        // App Label
        if (showLabel) {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textColor,
                    shadow = textShadow
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
