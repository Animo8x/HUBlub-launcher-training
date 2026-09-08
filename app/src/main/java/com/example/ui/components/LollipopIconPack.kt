package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackStyle

/**
 * Authentic Android 5.0 Lollipop Circular Icon Pack (Moonshine / Lollipop Round).
 * Provides beautifully crafted circular Material Design 1.0 icons for core Google and system apps,
 * and frames third-party installed apps with circular Material paper cards with realistic shadows.
 */
object LollipopIconPack {

    data class LollipopIconDef(
        val backgroundColor: Color,
        val iconVector: ImageVector,
        val iconTint: Color = Color.White
    )

    /**
     * Resolves an app's package name or label to its canonical Android 5.0 Material circular icon.
     */
    fun resolveCanonicalIcon(packageName: String, label: String): LollipopIconDef? {
        val pkg = packageName.lowercase()
        val name = label.lowercase()

        return when {
            // Phone / Dialer
            pkg.contains("dialer") || pkg.contains("phone") || pkg.contains("telecom") || name.contains("dialer") || name == "phone" || name.contains("هاتف") || name.contains("اتصال") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF4CAF50), // Material Green 500
                    iconVector = Icons.Default.Phone
                )
            }
            // SMS / Messages / WhatsApp / Telegram / Chat
            pkg.contains("messaging") || pkg.contains("mms") || pkg.contains("whatsapp") || pkg.contains("telegram") || pkg.contains("chat") || name.contains("message") || name.contains("sms") || name.contains("رسائل") || name.contains("محادث") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF00BCD4), // Material Cyan 500
                    iconVector = Icons.Default.Chat
                )
            }
            // Chrome / Web Browser / Internet
            pkg.contains("chrome") || pkg.contains("browser") || pkg.contains("firefox") || pkg.contains("opera") || name.contains("browser") || name.contains("chrome") || name.contains("متصفح") || name.contains("إنترنت") || name.contains("انترنت") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF1976D2), // Material Blue 700
                    iconVector = Icons.Default.Public
                )
            }
            // Camera
            pkg.contains("camera") || name.contains("camera") || name.contains("كاميرا") || name.contains("تصوير") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF37474F), // Material Blue Grey 800
                    iconVector = Icons.Default.CameraAlt
                )
            }
            // Photos / Gallery
            pkg.contains("gallery") || pkg.contains("photos") || pkg.contains("image") || name.contains("gallery") || name.contains("photo") || name.contains("صور") || name.contains("معرض") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFFFF5722), // Material Deep Orange 500
                    iconVector = Icons.Default.PhotoLibrary
                )
            }
            // Settings
            pkg.contains("settings") || name.contains("setting") || name.contains("إعدادات") || name.contains("اعدادات") || name.contains("ضبط") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF546E7A), // Material Blue Grey 600
                    iconVector = Icons.Default.Settings
                )
            }
            // Google Play Store / Market / Store
            pkg.contains("vending") || pkg.contains("market") || pkg.contains("playstore") || pkg.contains("store") || name.contains("play store") || name.contains("متجر") || name.contains("سوق") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF009688), // Material Teal 500
                    iconVector = Icons.Default.ShoppingBag
                )
            }
            // Clock / Alarm / Timer
            pkg.contains("clock") || pkg.contains("deskclock") || pkg.contains("alarm") || name.contains("clock") || name.contains("alarm") || name.contains("ساعة") || name.contains("منبه") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF00897B), // Material Teal 600
                    iconVector = Icons.Default.AccessTime
                )
            }
            // Calculator
            pkg.contains("calc") || name.contains("calc") || name.contains("حاسبة") || name.contains("حاسبه") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF00796B), // Material Teal 700
                    iconVector = Icons.Default.Calculate
                )
            }
            // Calendar
            pkg.contains("calendar") || name.contains("calendar") || name.contains("تقويم") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF1E88E5), // Material Blue 600
                    iconVector = Icons.Default.CalendarToday
                )
            }
            // Contacts / People
            pkg.contains("contact") || pkg.contains("people") || name.contains("contact") || name.contains("جهات الاتصال") || name.contains("ارقام") || name.contains("أرقام") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF2196F3), // Material Blue 500
                    iconVector = Icons.Default.Contacts
                )
            }
            // Gmail / Email
            pkg.contains("email") || pkg.contains("mail") || pkg.contains(".gm") || name.contains("gmail") || name.contains("email") || name.contains("بريد") || name.contains("ايميل") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFFE53935), // Material Red 600
                    iconVector = Icons.Default.Email
                )
            }
            // Maps / Navigation
            pkg.contains("maps") || name.contains("maps") || name.contains("navigation") || name.contains("خرائط") || name.contains("خريطة") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF43A047), // Material Green 600
                    iconVector = Icons.Default.LocationOn
                )
            }
            // YouTube / Video Player
            pkg.contains("youtube") || pkg.contains("video") || name.contains("youtube") || name.contains("فيديو") || name.contains("يوتيوب") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFFD32F2F), // Material Red 700
                    iconVector = Icons.Default.PlayArrow
                )
            }
            // Music / Audio Player / Sound / Audio
            pkg.contains("music") || pkg.contains("audio") || pkg.contains("sound") || name.contains("music") || name.contains("موسيقى") || name.contains("صوت") || name.contains("أغاني") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFFFB8C00), // Material Orange 600
                    iconVector = Icons.Default.MusicNote
                )
            }
            // Files / Downloads / File Manager
            pkg.contains("documentsui") || pkg.contains("files") || pkg.contains("filemanager") || pkg.contains("download") || name.contains("files") || name.contains("file") || name.contains("ملفات") || name.contains("تنزيل") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFF0288D1), // Material Light Blue 700
                    iconVector = Icons.Default.Folder
                )
            }
            // Notes / Keep
            pkg.contains("keep") || pkg.contains("notes") || pkg.contains("memo") || name.contains("note") || name.contains("ملاحظات") || name.contains("مفكرة") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFFFFA000), // Material Amber 700
                    iconVector = Icons.Default.Edit
                )
            }
            // Google Voice Search / Voice Recorder
            pkg.contains("quicksearchbox") || pkg.contains("recorder") || name.contains("voice search") || name.contains("البحث الصوتي") || name.contains("تسجيل") -> {
                LollipopIconDef(
                    backgroundColor = Color(0xFFFFFFFF),
                    iconVector = Icons.Default.Mic,
                    iconTint = Color(0xFFE53935)
                )
            }
            else -> null
        }
    }

    private val materialFallbackColors = listOf(
        Color(0xFF009688), // Teal
        Color(0xFF00BCD4), // Cyan
        Color(0xFF2196F3), // Blue
        Color(0xFF3F51B5), // Indigo
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF9C27B0), // Purple
        Color(0xFFE91E63), // Pink
        Color(0xFFF44336), // Red
        Color(0xFFFF5722), // Deep Orange
        Color(0xFFFF9800), // Orange
        Color(0xFF4CAF50), // Green
        Color(0xFF607D8B)  // Blue Grey
    )

    fun getFallbackColor(label: String): Color {
        val index = kotlin.math.abs(label.hashCode()) % materialFallbackColors.size
        return materialFallbackColors[index]
    }
}

/**
 * Universal App Icon Composable supporting Android 5.0 Circular Icon Pack and System original.
 * Every app without exception is rendered as a clean Android 5.0 circular Material element.
 */
@Composable
fun LollipopAppIconView(
    label: String,
    packageName: String,
    systemIcon: ImageBitmap?,
    modifier: Modifier = Modifier,
    iconPack: IconPackStyle = IconPackStyle.ANDROID_5_ROUND,
    size: Dp = 56.dp
) {
    // Check if this is the built-in Themes app
    if (packageName == "com.example.themes" || label == "Themes" || label == "الثيمات") {
        Surface(
            shape = CircleShape,
            color = Color(0xFF009688), // Lollipop Teal
            shadowElevation = 3.dp,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = label,
                    tint = Color(0xFFFFEB3B), // Vibrant amber accent
                    modifier = Modifier.size(size * 0.58f)
                )
            }
        }
        return
    }

    if (iconPack == IconPackStyle.ANDROID_5_ROUND) {
        if (systemIcon != null) {
            // Android 5.0 Circular Material representation of the REAL app icon:
            // YouTube is YouTube, Messenger is Messenger, Chrome is Chrome!
            // Cleanly presented inside a subtle circular paper container with Material elevation
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(size * 0.12f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = systemIcon,
                        contentDescription = label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Only fallback to canonical vector or letter if system icon is genuinely missing
            val canonicalDef = LollipopIconPack.resolveCanonicalIcon(packageName, label)
            if (canonicalDef != null) {
                Surface(
                    shape = CircleShape,
                    color = canonicalDef.backgroundColor,
                    shadowElevation = 2.dp,
                    modifier = modifier
                        .size(size)
                        .clip(CircleShape)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = canonicalDef.iconVector,
                            contentDescription = label,
                            tint = canonicalDef.iconTint,
                            modifier = Modifier.size(size * 0.54f)
                        )
                    }
                }
            } else {
                val firstLetter = label.firstOrNull()?.uppercase() ?: "?"
                val bgColor = LollipopIconPack.getFallbackColor(label)
                Surface(
                    shape = CircleShape,
                    color = bgColor,
                    shadowElevation = 2.dp,
                    modifier = modifier
                        .size(size)
                        .clip(CircleShape)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = firstLetter,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = (size.value * 0.44f).sp
                        )
                    }
                }
            }
        }
    } else {
        // System Original Icons
        if (systemIcon != null) {
            Image(
                bitmap = systemIcon,
                contentDescription = label,
                contentScale = ContentScale.Fit,
                modifier = modifier
                    .size(size)
                    .padding(2.dp)
            )
        } else {
            val firstLetter = label.firstOrNull()?.uppercase() ?: "?"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF009688)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstLetter,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.45f).sp
                )
            }
        }
    }
}
