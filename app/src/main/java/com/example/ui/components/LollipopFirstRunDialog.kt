package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.WallpaperPreset
import com.example.ui.theme.LollipopTeal500
import com.example.ui.theme.LollipopTeal700
import com.example.ui.theme.MaterialCardWhite
import com.example.ui.theme.MaterialTextPrimary
import com.example.ui.theme.MaterialTextSecondary
import com.example.util.LollipopSoundEffects

/**
 * Android 5.0 First Run Setup Dialog:
 * Allows user to choose between the authentic Nexus 5 stock paper wallpaper and modern geometric purple/blue wallpaper on startup.
 */
@Composable
fun LollipopFirstRunDialog(
    currentWallpaper: WallpaperPreset,
    onSelectWallpaper: (WallpaperPreset) -> Unit,
    onSetDefaultHome: () -> Unit,
    onGetStarted: () -> Unit
) {
    Dialog(onDismissRequest = onGetStarted) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("first_run_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(LollipopTeal700),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_hublub_logo_vector),
                        contentDescription = "HUBlub Logo",
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "مرحباً بك في HUBlub Launcher",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "اختر مظهر خلفية الشاشة للبدء (يمكنك تغييرها لاحقاً في أي وقت من تطبيق الثيمات):",
                    fontSize = 12.sp,
                    color = MaterialTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Wallpaper Selection (Old Stock Nexus 5 vs New Geometric Purple/Blue)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Old Stock Wallpaper (Nexus 5 paper)
                    val isStockSelected = currentWallpaper == WallpaperPreset.STOCK_LOLLIPOP
                    Card(
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isStockSelected) 2.5.dp else 1.dp,
                                color = if (isStockSelected) LollipopTeal500 else Color(0xFFCFD8DC),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                LollipopSoundEffects.playWaterDrop()
                                onSelectWallpaper(WallpaperPreset.STOCK_LOLLIPOP)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                LollipopWallpaper(preset = WallpaperPreset.STOCK_LOLLIPOP)
                                if (isStockSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LollipopTeal500,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "القديمة (الأصلية)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isStockSelected) LollipopTeal500 else MaterialTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "أندرويد 5 الرسمية",
                                fontSize = 10.sp,
                                color = MaterialTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // 2. New Geometric Wallpaper (Purple & Deep Blue)
                    val isPurpleSelected = currentWallpaper == WallpaperPreset.PURPLE_DEEP_BLUE
                    Card(
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isPurpleSelected) 2.5.dp else 1.dp,
                                color = if (isPurpleSelected) LollipopTeal500 else Color(0xFFCFD8DC),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                LollipopSoundEffects.playWaterDrop()
                                onSelectWallpaper(WallpaperPreset.PURPLE_DEEP_BLUE)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                LollipopWallpaper(preset = WallpaperPreset.PURPLE_DEEP_BLUE)
                                if (isPurpleSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LollipopTeal500,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "الجديدة (الهندسية)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPurpleSelected) LollipopTeal500 else MaterialTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "بنفسجي وأزرق",
                                fontSize = 10.sp,
                                color = MaterialTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        LollipopSoundEffects.playButtonClick()
                        onSetDefaultHome()
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("first_run_set_default")
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = LollipopTeal700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تعيين كمشغل افتراضي للهاتف", color = LollipopTeal700, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        LollipopSoundEffects.playButtonClick()
                        onGetStarted()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LollipopTeal700),
                    modifier = Modifier.fillMaxWidth().testTag("first_run_get_started")
                ) {
                    Text("ابدأ الاستخدام الآن", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
