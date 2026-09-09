package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.IconPackStyle
import com.example.model.LauncherConfig
import com.example.model.WallpaperPreset
import com.example.ui.theme.LollipopAmber500
import com.example.ui.theme.LollipopTeal500
import com.example.ui.theme.LollipopTeal700
import com.example.ui.theme.MaterialCardWhite
import com.example.ui.theme.MaterialTextPrimary
import com.example.ui.theme.MaterialTextSecondary
import com.example.util.LollipopSoundEffects

/**
 * Android 5.0 Lollipop style Settings screen.
 * Configures Home screen layout, wallpapers, performance mode, default home app, and backup/restore.
 */
@Composable
fun LollipopSettingsDialog(
    config: LauncherConfig,
    onConfigChange: (LauncherConfig) -> Unit,
    onSetDefaultHome: () -> Unit,
    onExportBackup: () -> String,
    onRestoreBackup: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showReleasesDialog by remember { mutableStateOf(false) }
    var restoreJsonInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(vertical = 12.dp)
                .testTag("hublub_settings_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Settings",
                        tint = LollipopTeal700
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "HUBlub Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTextPrimary
                    )
                }

                HorizontalDivider(color = Color(0x1F000000))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    // System Home Section
                    SectionHeader("SYSTEM LAUNCHER")
                    Button(
                        onClick = {
                            LollipopSoundEffects.playButtonClick()
                            onSetDefaultHome()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LollipopTeal700),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("set_default_home_button")
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SET AS DEFAULT HOME APP", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Appearance & Themes Notice
                    SectionHeader("THEMES & WALLPAPERS (الثيمات والخلفيات)")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = LollipopTeal700)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تم نقل كافة خلفيات الشاشة وخلفيات المجتمع وحزم الأيقونات إلى تطبيق \"الثيمات\" المخصص المتاح في درج التطبيقات والشاشة الرئيسية.",
                                fontSize = 12.sp,
                                color = MaterialTextPrimary
                            )
                        }
                    }

                    SettingToggle(
                        title = "Nostalgia Mode (Lollipop 5.0)",
                        subtitle = "Classic Material 1.0 typography, cards and animations",
                        checked = config.nostalgiaMode,
                        onCheckedChange = { onConfigChange(config.copy(nostalgiaMode = it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Home Screen Grid
                    SectionHeader("HOME SCREEN LAYOUT")

                    Text("Grid Columns: ${config.gridColumns}", fontSize = 14.sp, color = MaterialTextPrimary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(4, 5).forEach { cols ->
                            OutlinedButton(
                                onClick = {
                                    LollipopSoundEffects.playButtonClick()
                                    onConfigChange(config.copy(gridColumns = cols))
                                },
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (config.gridColumns == cols) LollipopTeal500.copy(alpha = 0.15f) else Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("${cols}x${config.gridRows}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Icon Size: ${config.iconSizeDp}dp", fontSize = 14.sp, color = MaterialTextPrimary)
                    Slider(
                        value = config.iconSizeDp.toFloat(),
                        onValueChange = { onConfigChange(config.copy(iconSizeDp = it.toInt())) },
                        valueRange = 44f..68f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = LollipopTeal700,
                            activeTrackColor = LollipopTeal500
                        )
                    )

                    SettingToggle(
                        title = "Show Clock Widget (إظهار ودجت الساعة)",
                        subtitle = "Display Android 5.0 digital clock on home screen",
                        checked = config.showClockWidget,
                        onCheckedChange = { onConfigChange(config.copy(showClockWidget = it)) }
                    )

                    SettingToggle(
                        title = "Show App Labels",
                        subtitle = "Display application names below icons",
                        checked = config.showAppLabels,
                        onCheckedChange = { onConfigChange(config.copy(showAppLabels = it)) }
                    )

                    SettingToggle(
                        title = "Show Google Search Bar (شريط بحث Google)",
                        subtitle = "Display Google quick search bar on home screen (Default: Hidden / مخفي)",
                        checked = config.showGoogleSearchBar,
                        onCheckedChange = { onConfigChange(config.copy(showGoogleSearchBar = it)) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "Icon Pack (حزمة الأيقونات):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    IconPackStyle.values().forEach { pack ->
                        val isSelected = config.iconPack == pack
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    LollipopSoundEffects.playWaterDrop()
                                    onConfigChange(config.copy(iconPack = pack))
                                }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    LollipopSoundEffects.playWaterDrop()
                                    onConfigChange(config.copy(iconPack = pack))
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = LollipopTeal700)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = pack.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) LollipopTeal700 else MaterialTextPrimary
                                )
                                Text(
                                    text = when (pack) {
                                        IconPackStyle.SYSTEM_FREEFORM -> "الشكل الحر الأصلي (المربع مربع والدائري دائري بدون أي خلفية دائرية بيضاء) ★ الافتراضي"
                                        IconPackStyle.MATERIAL_YOU_SQUIRCLE -> "نمط المربعات المنحنية الحديثة (Modern Squircle)"
                                        IconPackStyle.IOS_MINIMAL_FLAT -> "نمط البطاقات الزجاجية المسطحة (iOS Minimal Flat)"
                                        IconPackStyle.ANDROID_5_ROUND -> "أندرويد 5.0 لوليبوب الدائري الكلاسيكي الأصلي لعام 2014"
                                        IconPackStyle.KITKAT_VINTAGE_RETRO -> "أندرويد 4.4 كيت كات ريترو كلاسيك (Retro Vintage KitKat)"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Interaction & Feedback Section
                    SectionHeader("INTERACTION & TACTILE FEEDBACK")

                    SettingToggle(
                        title = "Samsung Nature UX Water Drop Sound",
                        subtitle = "صوت قطرة ماء سامسونج الكلاسيكي النقي (Galaxy S3/S4) عالي الوضوح بدون كتم",
                        checked = config.soundEffectsEnabled,
                        onCheckedChange = { onConfigChange(config.copy(soundEffectsEnabled = it)) }
                    )

                    SettingToggle(
                        title = "OG Liquid Glass Water Ripple (ماء سامسونج التفاعلي)",
                        subtitle = "تموجات ماء واقعية على الشاشة مع قطرات ماء وانكسارات ضوئية وأثر انسيابي عند السحب",
                        checked = config.touchRippleEnabled,
                        onCheckedChange = { onConfigChange(config.copy(touchRippleEnabled = it)) }
                    )

                    SettingToggle(
                        title = "Enable Motion Animations",
                        subtitle = "Spring bounce on icons, smooth drawer scale and transitions",
                        checked = config.animationsEnabled,
                        onCheckedChange = { onConfigChange(config.copy(animationsEnabled = it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Performance Section
                    SectionHeader("PERFORMANCE")

                    SettingToggle(
                        title = "Performance Mode",
                        subtitle = "Reduces animations for ultra-fast response on budget devices",
                        checked = config.performanceMode,
                        onCheckedChange = { onConfigChange(config.copy(performanceMode = it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Installation Guide Section
                    SectionHeader("INSTALLATION ON YOUR PHONE (كيفية التثبيت)")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "كيفية تنزيل وتثبيت HUBlub Launcher:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = LollipopTeal700
                            )
                            Text(
                                text = "1. من قائمة خيارات AI Studio في المتصفح، اختر \"Download APK\" أو تصدير المشروع كملف ZIP لتوليد ملف الـ APK.\n" +
                                       "2. انقل ملف الـ APK لهاتفك أو قم بتنزيله مباشرة واضغط عليه للتثبيت (مع تفعيل خيار السماح بالتثبيت من مصادر غير معروفة إذا طُلب منك).\n" +
                                       "3. اضغط على زر الرئيسية (Home) في هاتفك، ستظهر لك نافذة لاختيار المشغل، حدد HUBlub Launcher واختر \"دومًا / Always\".",
                                fontSize = 11.sp,
                                color = MaterialTextPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Backup & Restore Section
                    SectionHeader("BACKUP & RESTORE")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                LollipopSoundEffects.playButtonClick()
                                val backup = onExportBackup()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("HUBlub Backup", backup)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Layout backup copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = LollipopTeal700)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("EXPORT", color = LollipopTeal700, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                LollipopSoundEffects.playButtonClick()
                                showRestoreDialog = true
                            },
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Restore, contentDescription = null, tint = LollipopTeal700)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RESTORE", color = LollipopTeal700, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About
                    SectionHeader("ABOUT HUBLUB LAUNCHER")
                    Text("HUBlub Launcher v3.0 (Galaxy Nature Water Edition)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTextPrimary)
                    Text(
                        "An authentic Launcher experience with legendary Samsung Galaxy Nature UX liquid water ripples, authentic water droplet acoustics, freeform & 5 icon packs, expandable drawer, and custom wallpapers.",
                        fontSize = 12.sp,
                        color = MaterialTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            LollipopSoundEffects.playButtonClick()
                            showReleasesDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LollipopTeal500),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.NewReleases, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RELEASES & WEBSITE (إصدارات التطبيق والموقع)", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                HorizontalDivider(color = Color(0x1F000000))

                // Footer button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        LollipopSoundEffects.playButtonClick()
                        onDismiss()
                    }) {
                        Text("DONE", fontWeight = FontWeight.Bold, color = LollipopTeal700, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore Layout") },
            text = {
                Column {
                    Text("Paste your backup JSON below:", fontSize = 13.sp, color = MaterialTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonInput,
                        onValueChange = { restoreJsonInput = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Paste JSON here...") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val success = onRestoreBackup(restoreJsonInput)
                        if (success) {
                            Toast.makeText(context, "Layout restored successfully!", Toast.LENGTH_SHORT).show()
                            showRestoreDialog = false
                        } else {
                            Toast.makeText(context, "Invalid backup JSON", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("RESTORE", fontWeight = FontWeight.Bold, color = LollipopTeal700)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    if (showReleasesDialog) {
        AlertDialog(
            onDismissRequest = { showReleasesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.NewReleases, contentDescription = null, tint = LollipopTeal700)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إصدارات HUBlub Launcher", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // v3.0
                    Text("v3.0 (Galaxy Nature Water Edition - الإصدار الأحدث)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LollipopTeal700)
                    Text("• إعادة بناء كاملة لميزة الماء التفاعلي (OG Liquid Glass) بمحاكاة شاشات سامسونج جالاكسي الكلاسيكية (Nature UX).", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• تموجات دائرية واقعية متعددة الطبقات (Concentric Wave Packets) مع قمم وقيعان انكسارية طبيعية.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• قطرة ماء ثلاثية الأبعاد تنبثق في موضع اللمس مع انعكاس ضوئي زجاجي حقيقي وظلال انكسارية.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• تموج انسيابي متدفق عند السحب (Drag Fluid Wake) يتتبع حركة الإصبع على الشاشة بسلاسة فائقة.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• صوت قطرة ماء سامسونج الشهير الأصلي نقي وعالي الوضوح يعمل عبر قناة الوسائط لضمان عدم كتمه.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• كفاءة وأداء فائق 60/120 FPS عبر رسم Skia Canvas فوري بدون أي استهلاك زائد للطاقة.", fontSize = 13.sp, color = MaterialTextPrimary)

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x1F000000))
                    Spacer(modifier = Modifier.height(12.dp))

                    // v2.5
                    Text("v2.5 (مستقر)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTextSecondary)
                    Text("• الشكل الحر الطبيعي للأيقونات بدون أي خلفية دائرية بيضاء إجبارية.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• توفير 5 حزم أيقونات متنوعة من الحديث للريترو كلاسيك.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• زر مكبر كأيقونة بحث قابلة للفتح والإغلاق في درج التطبيقات بدلاً من الشريط القديم.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• نقل الخلفيات إلى تطبيق \"الثيمات\" المستقل وإمكانية نشر ومشاركة الخلفيات للمجتمع مع الاسم والوصف.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• إتاحة الإعدادات كتطبيق مستقل في درج التطبيقات والشاشة الرئيسية.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• تحسين فائق في سرعة واستجابة درج التطبيقات وتخفيف الحمل على المعالج.", fontSize = 13.sp, color = MaterialTextPrimary)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x1F000000))
                    Spacer(modifier = Modifier.height(12.dp))

                    // v1.2.0
                    Text("v1.2.0 (مستقر)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTextSecondary)
                    Text("• حزمة أيقونات أندرويد 5 الدائرية كحزمة افتراضية.", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• ضبط دقيق لحجم موجة قطرة الماء لتكون منطقية وطبيعية (~44dp).", fontSize = 13.sp, color = MaterialTextPrimary)
                    Text("• خيار التبديل بين الأيقونات الدائرية وأيقونات النظام في الإعدادات.", fontSize = 13.sp, color = MaterialTextPrimary)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x1F000000))
                    Spacer(modifier = Modifier.height(12.dp))

                    // v1.1.0
                    Text("v1.1.0 (مستقر - Stable)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTextPrimary)
                    Text("• إضافة صوت نقرة قطرة الماء الكلاسيكية وتموج اللمس.", fontSize = 13.sp, color = MaterialTextSecondary)
                    Text("• شريط بحث جوجل الكلاسيكي مع زر البحث الصوتي.", fontSize = 13.sp, color = MaterialTextSecondary)
                    Text("• مكتبة خلفيات أندرويد 5 الهندسية الأصلية.", fontSize = 13.sp, color = MaterialTextSecondary)

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x1F000000))
                    Spacer(modifier = Modifier.height(12.dp))

                    // v1.0.0
                    Text("v1.0.0 (الإطلاق الأولي)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTextPrimary)
                    Text("• إطلاق مشغل HUBlub Launcher بتصميم أندرويد 5 الماتيريال.", fontSize = 13.sp, color = MaterialTextSecondary)
                    Text("• قراءة وتثبيت وتشغيل كافة تطبيقات النظام الحقيقية.", fontSize = 13.sp, color = MaterialTextSecondary)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            val websiteIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ais-pre-6vovccul5gujdwdp7e4yyf-534499950451.europe-west3.run.app"))
                            websiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(websiteIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = LollipopTeal700, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OPEN WEBSITE (فتح الموقع)", fontWeight = FontWeight.Bold, color = LollipopTeal700)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReleasesDialog = false }) {
                    Text("CLOSE (إغلاق)")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = LollipopTeal700,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                LollipopSoundEffects.playButtonClick()
                onCheckedChange(!checked)
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = {
                LollipopSoundEffects.playButtonClick()
                onCheckedChange(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = LollipopTeal700,
                checkedTrackColor = LollipopTeal500.copy(alpha = 0.5f)
            )
        )
    }
}
