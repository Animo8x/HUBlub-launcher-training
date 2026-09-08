package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.MaterialCardWhite
import com.example.util.LollipopSoundEffects

/**
 * Classic Android 5.0 Lollipop Google Search bar card.
 * Sits at the top of the Home screen with authentic typography, subtle elevation and microphone icon.
 */
@Composable
fun LollipopSearchBar(
    onSearchClick: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 12.dp)
            .testTag("lollipop_search_bar")
            .clickable {
                LollipopSoundEffects.playWaterDrop()
                onSearchClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Classic colorful Google-style logo text
            val logoText = buildAnnotatedString {
                withStyle(SpanStyle(color = GoogleBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    append("G")
                }
                withStyle(SpanStyle(color = GoogleRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    append("o")
                }
                withStyle(SpanStyle(color = GoogleYellow, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    append("o")
                }
                withStyle(SpanStyle(color = GoogleBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    append("g")
                }
                withStyle(SpanStyle(color = GoogleGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    append("l")
                }
                withStyle(SpanStyle(color = GoogleRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                    append("e")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = logoText,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Search apps...",
                    color = Color(0x8A000000),
                    fontSize = 14.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        LollipopSoundEffects.playWaterDrop()
                        onVoiceClick()
                    },
                    modifier = Modifier.size(36.dp).testTag("voice_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = GoogleBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = {
                        LollipopSoundEffects.playWaterDrop()
                        onSearchClick()
                    },
                    modifier = Modifier.size(36.dp).testTag("app_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0x8A000000),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
