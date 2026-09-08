package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Authentic Android 5.0 Lollipop digital clock and date widget.
 * Features classic Roboto typography with light hour and bold minute styling.
 * Clicking opens the system Clock/Alarm application.
 */
@Composable
fun LollipopClockWidget(
    onClockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val hourFormat = remember { SimpleDateFormat("HH", Locale.getDefault()) }
    val minuteFormat = remember { SimpleDateFormat("mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    val hours = hourFormat.format(currentTime)
    val minutes = minuteFormat.format(currentTime)
    val dateString = dateFormat.format(currentTime)

    val textShadow = Shadow(
        color = Color(0x66000000),
        offset = Offset(2f, 2f),
        blurRadius = 4f
    )

    Column(
        modifier = modifier
            .padding(vertical = 12.dp)
            .testTag("lollipop_clock_widget")
            .clickable(onClick = onClockClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Digital Clock: e.g. 10:45
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hours,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    fontSize = 56.sp,
                    color = Color.White,
                    shadow = textShadow
                )
            )
            Text(
                text = ":",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 52.sp,
                    color = Color(0xE6FFFFFF),
                    shadow = textShadow
                ),
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Text(
                text = minutes,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp,
                    color = Color.White,
                    shadow = textShadow
                )
            )
        }

        // Date String: e.g. Monday, September 7
        Text(
            text = dateString,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                color = Color(0xF2FFFFFF),
                shadow = textShadow
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
