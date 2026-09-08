package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CommunityWallpaper
import com.example.model.DrawerStyle
import com.example.model.IconPackStyle
import com.example.model.LauncherConfig
import com.example.model.WallpaperPreset
import com.example.ui.theme.LollipopAmber500
import com.example.ui.theme.LollipopTeal500
import com.example.ui.theme.LollipopTeal700
import com.example.ui.theme.MaterialCardWhite
import com.example.util.LollipopSoundEffects

/**
 * Dedicated Themes & Personalization Application ("برنامج الثيمات").
 * Manages Wallpapers, Icon Pack styles, Drawer Transparency, and Phone Widgets.
 */
@Composable
fun LollipopThemesDialog(
    config: LauncherConfig,
    onConfigChange: (LauncherConfig) -> Unit,
    onOpenWidgetPicker: () -> Unit,
    communityWallpapers: List<CommunityWallpaper> = emptyList(),
    onPublishWallpaper: (String, String, String, WallpaperPreset?) -> Unit = { _, _, _, _ -> },
    onApplyWallpaper: (WallpaperPreset?, String?) -> Unit = { _, _ -> },
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "خلفيات الشاشة (Wallpapers)",
        "خلفيات المجتمع (Community)",
        "حزم الأيقونات (5 Packs)",
        "الشفافية (Transparency)",
        "الودجات (Widgets)"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("themes_app_screen"),
            color = Color(0xFFECEFF1) // Lollipop Material Gray 50
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Authentic Android 5.0 Lollipop AppBar
                Surface(
                    color = LollipopTeal500,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    LollipopSoundEffects.playSoftPop()
                                    onClose()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Themes",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "تطبيق الثيمات والتخصيص",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "HUBlub Themes & Style",
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Lollipop Palette Badge
                        Surface(
                            shape = CircleShape,
                            color = LollipopTeal700,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ColorLens,
                                    contentDescription = null,
                                    tint = LollipopAmber500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Android 5.0 Material Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = LollipopTeal700,
                    contentColor = Color.White,
                    edgePadding = 12.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                LollipopSoundEffects.playButtonClick()
                                selectedTab = index
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = {
                                val icon = when (index) {
                                    0 -> Icons.Default.Image
                                    1 -> Icons.Default.Share
                                    2 -> Icons.Default.Palette
                                    3 -> Icons.Default.Opacity
                                    else -> Icons.Default.Widgets
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        )
                    }
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (selectedTab) {
                        0 -> WallpapersSection(
                            currentPreset = config.wallpaperPreset,
                            onSelect = { preset ->
                                LollipopSoundEffects.playWaterDrop()
                                onConfigChange(config.copy(wallpaperPreset = preset, customWallpaperUri = null))
                            }
                        )
                        1 -> CommunityWallpapersSection(
                            communityWallpapers = communityWallpapers,
                            currentPreset = config.wallpaperPreset,
                            onPublish = { title, author, desc, preset ->
                                onPublishWallpaper(title, author, desc, preset)
                            },
                            onApply = { preset, uri ->
                                LollipopSoundEffects.playButtonClick()
                                onApplyWallpaper(preset, uri)
                            }
                        )
                        2 -> IconPacksSection(
                            currentIconPack = config.iconPack,
                            onSelect = { pack ->
                                LollipopSoundEffects.playWaterDrop()
                                onConfigChange(config.copy(iconPack = pack))
                            }
                        )
                        3 -> TransparencySection(
                            currentStyle = config.drawerStyle,
                            onSelect = { style ->
                                LollipopSoundEffects.playWaterDrop()
                                onConfigChange(config.copy(drawerStyle = style))
                            }
                        )
                        4 -> WidgetsSection(
                            onAddWidgetClick = {
                                LollipopSoundEffects.playWaterDrop()
                                onOpenWidgetPicker()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Wallpapers Tab:
 * Displays all built-in authentic Android 5.0 Lollipop papercraft and modern wallpapers.
 */
@Composable
private fun WallpapersSection(
    currentPreset: WallpaperPreset,
    onSelect: (WallpaperPreset) -> Unit
) {
    val wallpapers = listOf(
        Triple(
            WallpaperPreset.STOCK_LOLLIPOP,
            "القديمة (الأصلية): أندرويد 5.0 الرسمية",
            "خلفية ورق الماتيريال الشهيرة (Nexus 5 Paper Material) بدرجات الفيروزي والأحمر والأصفر."
        ),
        Triple(
            WallpaperPreset.PURPLE_DEEP_BLUE,
            "الجديدة (الهندسية): بنفسجي وأزرق عميق",
            "تدرجات ورقية حديثة ذات ظلال ثلاثية الأبعاد أنيقة، متوازنة ومريحة للعين."
        ),
        Triple(
            WallpaperPreset.CYAN_GEOMETRIC,
            "أزرق فيروزي سماوي (Origami Cyan)",
            "خلفية ورقية ذات زوايا حادة وتباعد أنيق بين الطبقات."
        ),
        Triple(
            WallpaperPreset.INDIGO_SUNSET,
            "نيلي شتوي مهدئ (Indigo Sunset)",
            "ألوان طبيعية هادئة تمنح الشاشة الرئيسية إشراقاً وسهولة في قراءة النصوص."
        ),
        Triple(
            WallpaperPreset.DARK_SLATE,
            "الوضع الليلي (Dark Slate Minimal)",
            "خلفية داكنة موفرة للطاقة تبرز بطاقات وأيقونات التطبيقات الملونة."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(wallpapers) { (preset, title, description) ->
            val isSelected = currentPreset == preset

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
                elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.5.dp else 0.dp,
                        color = if (isSelected) LollipopTeal500 else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(preset) }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Live Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    ) {
                        LollipopWallpaper(preset = preset)

                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = LollipopTeal500,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "الخلفية الحالية",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Information & Apply Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF263238)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = description,
                                fontSize = 12.sp,
                                color = Color(0xFF78909C),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onSelect(preset) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) LollipopTeal700 else LollipopTeal500
                            ),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isSelected) "مفعلة ✓" else "تطبيق",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Community Wallpapers Tab:
 * Publish, share, and discover custom user wallpapers with names, descriptions, and authors.
 */
@Composable
private fun CommunityWallpapersSection(
    communityWallpapers: List<CommunityWallpaper>,
    currentPreset: WallpaperPreset,
    onPublish: (String, String, String, WallpaperPreset?) -> Unit,
    onApply: (WallpaperPreset?, String?) -> Unit
) {
    var showPublishDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var authorInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf(WallpaperPreset.PURPLE_DEEP_BLUE) }

    if (showPublishDialog) {
        AlertDialog(
            onDismissRequest = { showPublishDialog = false },
            title = {
                Text(
                    text = "نشر خلفية جديدة للمجتمع",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "شارك خلفيتك المفضلة مع اسمك ووصف مخصص لكي يتمكن الجميع من تحميلها وتطبيقها على هواتفهم.",
                        fontSize = 12.sp,
                        color = Color(0xFF546E7A)
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("اسم الخلفية (Title)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = authorInput,
                        onValueChange = { authorInput = it },
                        label = { Text("اسمك / الناشر (Author)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("وصف الخلفية (Description)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "اختر نمط الألوان والتصميم:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WallpaperPreset.values().take(4).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedPreset == preset) LollipopTeal500 else Color(0xFFCFD8DC),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clickable { selectedPreset = preset }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = preset.name.take(4),
                                        fontSize = 10.sp,
                                        color = if (selectedPreset == preset) Color.White else Color(0xFF37474F),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            onPublish(titleInput, authorInput, descInput, selectedPreset)
                            showPublishDialog = false
                            titleInput = ""
                            authorInput = ""
                            descInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LollipopTeal500)
                ) {
                    Text("نشر الآن (Publish)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublishDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Share / Upload Banner
        item {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = LollipopTeal700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "شارك إبداعك مع مجتمع اللانشر",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "انشر خلفيتك بالاسم والوصف ليراها ويستخدمها الجميع",
                            color = Color(0xDDFFFFFF),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            LollipopSoundEffects.playButtonClick()
                            showPublishDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LollipopAmber500),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF212121),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "نشر خلفية",
                            color = Color(0xFF212121),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(communityWallpapers) { item ->
            val isCurrent = item.preset != null && item.preset == currentPreset

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    ) {
                        if (item.preset != null) {
                            LollipopWallpaper(preset = item.preset)
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(item.colorHex))
                            )
                        }

                        // Author Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xCC000000),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "بواسطة: ${item.author}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Content & Apply
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF263238)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                color = Color(0xFF78909C),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFE91E63),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${item.likesCount} إعجاب",
                                    fontSize = 11.sp,
                                    color = Color(0xFF90A4AE)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { onApply(item.preset, item.imageUri) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrent) LollipopTeal700 else LollipopTeal500
                            ),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isCurrent) "مطبقة ✓" else "تطبيق",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5 Icon Pack Styles Tab:
 * Displays all 5 unique icon pack styles from ultra-modern to vintage retro,
 * including the default Freeform style that leaves square icons square without circular backgrounds!
 */
@Composable
private fun IconPacksSection(
    currentIconPack: IconPackStyle,
    onSelect: (IconPackStyle) -> Unit
) {
    val iconStyles = listOf(
        Triple(
            IconPackStyle.SYSTEM_FREEFORM,
            "1. الشكل الحر الأصلي (Natural Freeform) ★ الافتراضي",
            "عرض التطبيقات بأشكالها الطبيعية الحرة (المربع مربع، والدائري دائري) بدون أي خلفية دائرية بيضاء مقيدة."
        ),
        Triple(
            IconPackStyle.MATERIAL_YOU_SQUIRCLE,
            "2. نمط المربعات المنحنية الحديث (Modern Squircle)",
            "شكل هندسي حديث يدمج المربع بالدائرة لتقديم مظهر ناعم وعصري للغاية مثل واجهات أندرويد الحديثة."
        ),
        Triple(
            IconPackStyle.IOS_MINIMAL_FLAT,
            "3. النمط الزجاجي المسطح الحديث (iOS Minimal Flat)",
            "بطاقات مستطيلة زجاجية ناعمة مع لمسات فلات أنيقة وإطار شفاف دقيق يبرز الأيقونات."
        ),
        Triple(
            IconPackStyle.ANDROID_5_ROUND,
            "4. أندرويد 5.0 لوليبوب الدائري الكلاسيكي (Classic 5.0 Round)",
            "الشكل الدائري الكلاسيكي الأيقوني الأصلي لنظام أندرويد 5 مع البطاقة الورقية البيضاء لعام 2014."
        ),
        Triple(
            IconPackStyle.KITKAT_VINTAGE_RETRO,
            "5. أندرويد 4.4 كيت كات ريترو كلاسيك (Vintage Retro KitKat)",
            "تصميم ريترو كلاسيكي مستوحى من واجهات هولو الكلاسيكية القديمة لعشاق نوستالجيا أندرويد."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(iconStyles) { (style, title, desc) ->
            val isSelected = currentIconPack == style

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) LollipopTeal500 else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(style) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) LollipopTeal500 else Color(0xFFCFD8DC),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Layers,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF546E7A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = Color(0xFF78909C),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * App Drawer Transparency Tab:
 * Configure translucent glass or transparent pixels behind the drawer.
 */
@Composable
private fun TransparencySection(
    currentStyle: DrawerStyle,
    onSelect: (DrawerStyle) -> Unit
) {
    val transparencyOptions = listOf(
        Triple(
            DrawerStyle.TRANSLUCENT_GLASS,
            "زجاجي شفاف أنيق (Translucent Glass)",
            "خلفية شبه شفافة بنسبة 85% تظهر تفاصيل وألوان الخلفية التي خلفها بوضوح تام وانسيابية فائقة."
        ),
        Triple(
            DrawerStyle.CRYSTAL_CLEAR,
            "شفاف نقي (Crystal Clear)",
            "شفافية عالية بنسبة 60% مع رؤية كاملة لكل شيء خلف درج التطبيقات بدون حجب."
        ),
        Triple(
            DrawerStyle.SEMI_TRANSPARENT,
            "شفاف خفيف (Light Translucent)",
            "شفافية بنسبة 93% تمنح توازناً مثالياً بين قراءة أسماء التطبيقات ولمعان الخلفية."
        ),
        Triple(
            DrawerStyle.CLASSIC_SOLID,
            "أبيض صامت كلاسيكي (Lollipop Pure White)",
            "خلفية أندرويد 5 الرسمية الكلاسيكية باللون الأبيض النقي الكامل."
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(transparencyOptions) { (style, title, desc) ->
            val isSelected = currentStyle == style

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) LollipopTeal500 else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(style) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) LollipopTeal500 else Color(0xFFCFD8DC),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Layers,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF546E7A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = Color(0xFF78909C),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Widgets Management Tab:
 * Explains and launches phone widget picker.
 */
@Composable
private fun WidgetsSection(
    onAddWidgetClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = LollipopTeal500.copy(alpha = 0.15f),
            modifier = Modifier.size(76.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Widgets,
                    contentDescription = null,
                    tint = LollipopTeal500,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ودجات الشاشة الرئيسية من هاتفك",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "يمكنك وضع أي ودجت مثبت على هاتفك الأندرويد (مثل ودجت بحث جوجل، الساعة الرقمية، الطقس، مشغل الموسيقى، أو الواتساب) مباشرة على الشاشة الرئيسية.",
            fontSize = 13.sp,
            color = Color(0xFF546E7A),
            lineHeight = 19.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddWidgetClick,
            colors = ButtonDefaults.buttonColors(containerColor = LollipopTeal500),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "اختيار وإضافة ودجت من الهاتف",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
