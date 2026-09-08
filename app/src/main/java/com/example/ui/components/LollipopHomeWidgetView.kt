package com.example.ui.components

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.HomeWidget
import com.example.util.LollipopSoundEffects

/**
 * Hosts and renders a live Android AppWidget using AppWidgetHostView on the Home desktop.
 */
@Composable
fun LollipopHomeWidgetView(
    widget: HomeWidget,
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appWidgetInfo = remember(widget.appWidgetId) {
        try {
            appWidgetManager.getAppWidgetInfo(widget.appWidgetId)
        } catch (e: Exception) {
            null
        }
    }

    if (appWidgetInfo != null) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x28000000)),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AndroidView(
                    factory = { ctx ->
                        try {
                            appWidgetHost.createView(ctx, widget.appWidgetId, appWidgetInfo).apply {
                                setAppWidget(widget.appWidgetId, appWidgetInfo)
                            }
                        } catch (e: Exception) {
                            AppWidgetHostView(ctx)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp, max = 220.dp)
                        .padding(4.dp)
                )

                // Quick remove button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x66000000),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(26.dp)
                ) {
                    IconButton(
                        onClick = {
                            LollipopSoundEffects.playButtonClick()
                            onRemove()
                        },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Widget",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
