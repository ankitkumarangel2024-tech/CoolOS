package com.coolos.launcher

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Night = Color(0xFF07111F)
private val Panel = Color(0xFF10243B)
private val Blue = Color(0xFF42A5FF)
private val Cyan = Color(0xFF75D7FF)
private val White = Color(0xFFF3F7FF)
private val Muted = Color(0xFF9EB2C9)

data class LaunchApp(val label: String, val packageName: String, val icon: Drawable)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 17, 31)
        window.navigationBarColor = android.graphics.Color.rgb(7, 17, 31)
        setContent { CoolOSApp() }
    }
}

@Composable
fun CoolOSApp() {
    val context = LocalContext.current
    val pm = context.packageManager
    var showDrawer by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }

    val apps = remember(refresh) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(intent, 0)
            .mapNotNull { info ->
                val pkg = info.activityInfo.packageName
                if (pkg == context.packageName) null
                else LaunchApp(info.loadLabel(pm).toString(), pkg, info.loadIcon(pm))
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    val filtered = apps.filter { it.label.contains(search, ignoreCase = true) }
    val featured = apps.take(4)

    MaterialTheme(colorScheme = darkColorScheme(
        background = Night, surface = Panel, primary = Blue,
        onBackground = White, onSurface = White
    )) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF102B49), Night, Color(0xFF050B14)))
            )
        ) {
            if (showSettings) {
                SettingsScreen(onBack = { showSettings = false })
            } else if (showDrawer) {
                AppDrawer(
                    apps = filtered,
                    search = search,
                    onSearch = { search = it },
                    onBack = { showDrawer = false; search = "" },
                    onLaunch = { launchApp(context, it.packageName) }
                )
            } else {
                HomeScreen(
                    featured = featured,
                    onOpenDrawer = { showDrawer = true },
                    onSettings = { showSettings = true },
                    onLaunch = { launchApp(context, it.packageName) },
                    onRefresh = { refresh++ }
                )
            }
        }
    }
}

private fun launchApp(context: android.content.Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) context.startActivity(intent)
}

@Composable
fun HomeScreen(
    featured: List<LaunchApp>,
    onOpenDrawer: () -> Unit,
    onSettings: () -> Unit,
    onLaunch: (LaunchApp) -> Unit,
    onRefresh: () -> Unit
) {
    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("COOLOS", color = Cyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
            Text("v1.0", color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(48.dp))
        Text(
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            color = White, fontSize = 76.sp, fontWeight = FontWeight.Light, letterSpacing = (-3).sp
        )
        Text(date, color = Muted, fontSize = 15.sp)
        Spacer(Modifier.height(34.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(Color(0xAA1D5D91), Color(0x88305C83))))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("YOUR SPACE", color = Cyan, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text("Make it yours.", color = White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("A calmer, cooler Android experience.", color = Muted, fontSize = 11.sp)
            }
            Box(Modifier.size(54.dp).clip(CircleShape).background(Color(0x553DAAFF)), Alignment.Center) {
                Text("✦", color = Cyan, fontSize = 30.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp))
                .background(Color(0xAA10243B)).padding(vertical = 13.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            featured.forEach { app -> AppIcon(app, onLaunch) }
            if (featured.isEmpty()) Text("Open app drawer to get started", color = Muted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            HomeAction("Apps", "▦", onOpenDrawer)
            HomeAction("Refresh", "↻", onRefresh)
            HomeAction("Settings", "⚙", onSettings)
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun AppIcon(app: LaunchApp, onLaunch: (LaunchApp) -> Unit) {
    val bitmap = remember(app.packageName) { app.icon.toBitmap(96, 96).asImageBitmap() }
    Column(
        Modifier.width(66.dp).clickable { onLaunch(app) }.padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.Image(
            bitmap = bitmap, contentDescription = app.label,
            modifier = Modifier.size(43.dp).clip(RoundedCornerShape(13.dp))
        )
        Spacer(Modifier.height(5.dp))
        Text(app.label, color = White, fontSize = 9.sp, maxLines = 1, textAlign = TextAlign.Center)
    }
}

@Composable
fun HomeAction(label: String, symbol: String, onClick: () -> Unit) {
    Column(Modifier.clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(43.dp).clip(CircleShape).background(Color(0x332F9FFF)), Alignment.Center) {
            Text(symbol, color = Cyan, fontSize = 22.sp)
        }
        Spacer(Modifier.height(5.dp))
        Text(label, color = Muted, fontSize = 11.sp)
    }
}

@Composable
fun AppDrawer(
    apps: List<LaunchApp>,
    search: String,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    onLaunch: (LaunchApp) -> Unit
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 18.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 34.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Text("All apps", color = White, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("${apps.size}", color = Muted, fontSize = 13.sp)
        }
        OutlinedTextField(
            value = search, onValueChange = onSearch,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search apps", color = Muted) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White, unfocusedTextColor = White,
                focusedBorderColor = Blue, unfocusedBorderColor = Color(0x665B7897),
                cursorColor = Cyan
            )
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(apps, key = { it.packageName }) { app ->
                AppIcon(app, onLaunch)
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var glow by remember { mutableStateOf(true) }
    var style by remember { mutableStateOf("Ocean Blue") }
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(22.dp)) {
        Text("‹  Back", color = Cyan, modifier = Modifier.clickable { onBack() }, fontSize = 17.sp)
        Spacer(Modifier.height(24.dp))
        Text("CoolOS Settings", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("Personalize your space", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        SettingRow("Blue glow accents", "Use CoolOS blue highlights") {
            Switch(checked = glow, onCheckedChange = { glow = it })
        }
        SettingRow("Theme color", "Current: $style") {
            TextButton(onClick = { style = if (style == "Ocean Blue") "Deep Cyan" else "Ocean Blue" }) {
                Text("Change", color = Cyan)
            }
        }
        SettingRow("Version", "CoolOS Launcher 1.0") { Text("✓", color = Cyan) }
        Spacer(Modifier.height(20.dp))
        Text("Note: This is the first launcher prototype. System-level controls, lock screen and fingerprint UI require Android system privileges and are not replaced by this app.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable
fun SettingRow(title: String, subtitle: String, trailing: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 9.dp).clip(RoundedCornerShape(18.dp))
            .background(Color(0x9910243B)).padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
        trailing()
    }
}
