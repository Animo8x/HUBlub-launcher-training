package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
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
import androidx.compose.material.icons.filled.Delete
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
    onPublishWallpaper: (String, String, String, WallpaperPreset?, String?) -> Unit = { _, _, _, _, _ -> },
    onDeleteWallpaper: (String) -> Unit = {},
    onApplyWallpaper: (WallpaperPreset?, String?) -> Unit = { _, _ -> },
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "خلفيات الشاشة (Wallpapers)",
        "خلفيات من جهازي (My Wallpapers)",
        "حزم الأيقونات (5 Packs)",
        "مظهر الدرج والشفافية (Drawer Style)",
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
                        1 -> MyWallpapersSection(
                            communityWallpapers = communityWallpapers,
                            currentPreset = config.wallpaperPreset,
                            currentCustomUri = config.customWallpaperUri,
                            onPublish = { title, uri ->
                                onPublishWallpaper(title, "", "", null, uri)
                            },
                            onDelete = { id ->
                                onDeleteWallpaper(id)
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
        ),
        Triple(
            WallpaperPreset.MODERN_16_AURA,
            "أندرويد 16: هالة ضوئية متدرجة (Luminous Aura)",
            "تدرجات أثيرية متوهجة مستوحاة من أحدث لغات التصميم لمستقبل أندرويد."
        ),
        Triple(
            WallpaperPreset.MODERN_16_FROSTED_GLASS,
            "أندرويد 16: طبقات زجاجية متداخلة (Frosted Glass)",
            "تأثير زجاجي مصقول مع انكسارات ضوئية ناعمة وظلال خفيفة راقية."
        ),
        Triple(
            WallpaperPreset.MODERN_17_CYBER_SUNSET,
            "أندرويد 17: شفق الغروب الدافئ (Twilight Sunset)",
            "تدرجات لونية هادئة تمزج الشفق الكوني الدافئ لراحة العين والوضوح الفائق."
        ),
        Triple(
            WallpaperPreset.MODERN_17_COSMIC_NEBULA,
            "أندرويد 17: سديم كوني عائم (Cosmic Glow)",
            "دوائر وتوهجات نيبولا عائمة تمنح إحساساً بالعمق والفضاء الفسيح والشياكة."
        ),
        Triple(
            WallpaperPreset.MODERN_17_MINIMAL_CHROMA,
            "أندرويد 17: انحناءات ميكرو كروما (Minimal Chroma)",
            "تصميم نقي وبسيط مع خطوط انحناء حيوية تتناغم مع أيقونات الشاشة."
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
 * My Wallpapers Tab (خلفيات من جهازي):
 * Pick genuine images from device files (Photo Picker), preview them, save them with name & description,
 * apply them directly, or delete them from the local list.
 * Zero fake likes, zero fake users.
 */
@Composable
private fun MyWallpapersSection(
    communityWallpapers: List<CommunityWallpaper>,
    currentPreset: WallpaperPreset,
    currentCustomUri: String?,
    onPublish: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onApply: (WallpaperPreset?, String?) -> Unit
) {
    val context = LocalContext.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some providers might not support persistable URIs
            }
            selectedImageUri = uri.toString()
            titleInput = "خلفية مخصصة ${communityWallpapers.size + 1}"
            descInput = "صورة من ذاكرة الهاتف"
            showSaveDialog = true
        }
    }

    if (showSaveDialog && selectedImageUri != null) {
        val uriStr = selectedImageUri!!
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "حفظ وتطبيق الخلفية من ملفاتك",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = uriStr,
                            contentDescription = "معاينة الخلفية",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("اسم الخلفية (Title)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("الوصف (اختياري)") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = titleInput.ifBlank { "خلفية من جهازي" }
                        onPublish(title, uriStr)
                        onApply(null, uriStr)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LollipopTeal500)
                ) {
                    Text("حفظ وتطبيق الآن")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = {
                            val title = titleInput.ifBlank { "خلفية من جهازي" }
                            onPublish(title, uriStr)
                            showSaveDialog = false
                        }
                    ) {
                        Text("حفظ فقط")
                    }
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("إلغاء")
                    }
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Button to pick image from device
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
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
                            text = "اختيار صورة كخلفية من ملفات الجهاز",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "اختر صورة حقيقية من معرض الصور، عاينها، واحفظها في قائمتك",
                            color = Color(0xDDFFFFFF),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            LollipopSoundEffects.playButtonClick()
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LollipopAmber500),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF212121),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "اختيار صورة",
                            color = Color(0xFF212121),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (communityWallpapers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لم تقم بإضافة أي خلفية مخصصة بعد.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF455A64)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط على زر \"اختيار صورة\" أعلاه لاختيار صورة من هاتفك وتطبيقها.",
                            fontSize = 12.sp,
                            color = Color(0xFF78909C)
                        )
                    }
                }
            }
        } else {
            items(communityWallpapers) { item ->
                val isCurrent = (item.imageUri != null && item.imageUri == currentCustomUri) ||
                        (item.imageUri == null && item.preset != null && item.preset == currentPreset)

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialCardWhite),
                    elevation = CardDefaults.cardElevation(if (isCurrent) 4.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isCurrent) 2.dp else 0.dp,
                            color = if (isCurrent) LollipopTeal500 else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        ) {
                            if (item.imageUri != null) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (item.preset != null) {
                                LollipopWallpaper(preset = item.preset)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(item.colorHex))
                                )
                            }

                            if (isCurrent) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = LollipopTeal500,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "الخلفية الحالية ✓",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
                                if (item.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF78909C),
                                        maxLines = 2
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        LollipopSoundEffects.playSoftPop()
                                        onDelete(item.id)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف الخلفية",
                                        tint = Color(0xFFE53935)
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                Button(
                                    onClick = {
                                        LollipopSoundEffects.playButtonClick()
                                        onApply(item.preset, item.imageUri)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrent) LollipopTeal700 else LollipopTeal500
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
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
