package org.yahya3886.rootchecker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                RootApp()
            }
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val dynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    val scheme = when {
        dynamic && dark -> dynamicDarkColorScheme(context)
        dynamic -> dynamicLightColorScheme(context)
        dark -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}

private enum class Screen { Home, Settings, About }

@Composable
fun RootApp() {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }

    BackHandler(enabled = screen != Screen.Home) {
        screen = when (screen) {
            Screen.About -> Screen.Settings
            else -> Screen.Home
        }
    }

    when (screen) {
        Screen.Home -> HomeScreen(onOpenSettings = { screen = Screen.Settings })
        Screen.Settings -> SettingsScreen(
            onBack = { screen = Screen.Home },
            onOpenAbout = { screen = Screen.About }
        )
        Screen.About -> AboutScreen(onBack = { screen = Screen.Settings })
    }
}

data class RootResult(
    val rooted: Boolean?,
    val suPath: String?,
    val suVersion: String?,
    val idOutput: String?,
    val details: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenSettings: () -> Unit) {
    var result by remember { mutableStateOf<RootResult?>(null) }
    var checking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun runCheck() {
        if (checking) return
        checking = true
        scope.launch {
            result = withContext(Dispatchers.IO) { checkRoot() }
            checking = false
        }
    }

    LaunchedEffect(Unit) { runCheck() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Yahya's Checker", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { runCheck() }, enabled = !checking) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Yenile")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Ayarlar")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { StatusCard(result = result, checking = checking) }

            item {
                FilledTonalButton(
                    onClick = { runCheck() },
                    enabled = !checking,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (checking) "Kontrol ediliyor..." else "Tekrar kontrol et")
                }
            }

            item { InfoCard(result = result) }
            item { DetailsCard(result = result) }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
fun StatusCard(result: RootResult?, checking: Boolean) {
    val rooted = result?.rooted
    val cs = MaterialTheme.colorScheme
    val container = when {
        checking -> cs.secondaryContainer
        rooted == true -> cs.primaryContainer
        rooted == false -> cs.errorContainer
        else -> cs.surfaceVariant
    }
    val onContainer = when {
        checking -> cs.onSecondaryContainer
        rooted == true -> cs.onPrimaryContainer
        rooted == false -> cs.onErrorContainer
        else -> cs.onSurfaceVariant
    }
    val icon: ImageVector = when {
        checking -> Icons.Outlined.HelpOutline
        rooted == true -> Icons.Outlined.CheckCircle
        rooted == false -> Icons.Outlined.ErrorOutline
        else -> Icons.Outlined.HelpOutline
    }
    val title = when {
        checking -> "Kontrol ediliyor"
        rooted == true -> "Root erişimi var"
        rooted == false -> "Root bulunamadı"
        else -> "Bilinmiyor"
    }
    val subtitle = when {
        checking -> "Root durumu sorgulanıyor..."
        rooted == true -> "Cihazda root (su) kullanılabilir görünüyor."
        rooted == false -> "su çalıştırılamadı veya bulunamadı."
        else -> "Henüz kontrol edilmedi veya sonuç belirsiz."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = container, contentColor = onContainer),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                if (result?.idOutput != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        result.idOutput,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(result: RootResult?) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Bilgiler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            if (result == null) {
                Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                InfoRow("su yolu", result.suPath ?: "bulunamadı")
                InfoRow("su sürümü", result.suVersion ?: "bilinmiyor")
                InfoRow("uid", result.idOutput ?: "alınamadı")
            }
        }
    }
}

@Composable
fun DetailsCard(result: RootResult?) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Ayrıntılar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            if (result == null) {
                Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                result.details.forEach { line ->
                    Text(
                        text = "• $line",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onOpenAbout: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SectionHeader("Genel")
            }
            item {
                ElevatedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Tema") },
                        supportingContent = { Text("Material 3 Expressive · Sistem teması ve dinamik renk (Android 12+)") },
                        leadingContent = { Icon(Icons.Filled.Settings, contentDescription = null) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Otomatik kontrol") },
                        supportingContent = { Text("Uygulama açıldığında root durumu otomatik kontrol edilir") },
                        leadingContent = { Icon(Icons.Filled.Security, contentDescription = null) }
                    )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            item { SectionHeader("Uygulama") }
            item {
                ElevatedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = onOpenAbout
                ) {
                    ListItem(
                        headlineContent = { Text("Hakkında") },
                        supportingContent = { Text("Uygulama bilgisi, sürüm ve lisans") },
                        leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) }
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.1.0"
    } catch (_: Exception) { "1.1.0" }
    val versionCode = try {
        val p = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) p.longVersionCode else @Suppress("DEPRECATION") p.versionCode.toLong()
    } catch (_: Exception) { 2L }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Hakkında") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Yahya's Checker",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "MIUIX'ten Material 3 Expressive tasarıma geçilmiş root kontrol uygulaması.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Uygulama hakkında", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Yahya's Checker, Android cihazlarda root (su) erişimini hızlıca kontrol etmek için geliştirilmiş hafif bir araçtır. " +
                                "Bilinen su yollarını tarar, `which su`, `su -c id` ve `su -v` çıktılarıyla durumu ve ayrıntıları gösterir.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Arayüz Material 3 (Expressive) bileşenleri ve dinamik renk desteğiyle tasarlanmıştır.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                    Column {
                        AboutRow("Paket", "org.yahya3886.rootchecker")
                        HorizontalDivider()
                        AboutRow("Sürüm", "$versionName ($versionCode)")
                        HorizontalDivider()
                        AboutRow("Geliştirici", "yahya3886")
                    }
                }
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Lisans ve atıf", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Bu proje GNU General Public License v3.0 (GPL-3.0) kapsamında lisanslanmıştır. Ayrıntılar için depodaki LICENSE dosyasına bakın.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Arayüz geliştirilirken KernelSU (tiann/KernelSU, GPL-3.0) yönetici uygulamasındaki bazı tasarım desenlerinden ilham alınmıştır: https://github.com/tiann/KernelSU",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AboutRow(label: String, value: String) {
    ListItem(
        headlineContent = { Text(value) },
        overlineContent = { Text(label) }
    )
}

fun checkRoot(): RootResult {
    val details = mutableListOf<String>()
    val paths = listOf(
        "/system/bin/su", "/system/xbin/su", "/sbin/su",
        "/system/su", "/system/bin/.ext/.su", "/system/usr/we-need-root/su",
        "/data/local/xbin/su", "/data/local/bin/su", "/data/local/su",
        "/su/bin/su", "/magisk/.core/bin/su"
    )

    val found = paths.filter { File(it).exists() }
    val which = runCmd(listOf("which", "su"))
    val suPath = when {
        found.isNotEmpty() -> found.first()
        which.first -> which.second.trim().lineSequence().firstOrNull()?.takeIf { it.isNotBlank() }
        else -> null
    }
    details.add("Bilinen yollarda su: ${if (found.isEmpty()) "yok" else found.joinToString()}")
    details.add("which su: ${if (which.first) which.second.trim().ifBlank { "(boş)" } else "çalıştırılamadı"}")

    val id = runCmd(listOf("su", "-c", "id"))
    val idOut = if (id.first) id.second.trim() else null
    details.add("su -c id: ${if (id.first) (idOut ?: "") else "başarısız"}")

    val ver = runCmd(listOf("su", "-v"))
    val verOut = if (ver.first) ver.second.trim().lineSequence().firstOrNull() else null
    if (verOut != null) details.add("su -v: $verOut")

    val rooted = (id.first && (idOut?.contains("uid=0") == true)) ||
            (suPath != null && id.first)

    return RootResult(
        rooted = rooted,
        suPath = suPath,
        suVersion = verOut,
        idOutput = idOut,
        details = details
    )
}

fun runCmd(cmd: List<String>): Pair<Boolean, String> {
    return try {
        val p = ProcessBuilder(cmd)
            .redirectErrorStream(true)
            .start()
        val out = BufferedReader(InputStreamReader(p.inputStream)).use { it.readText() }
        val code = p.waitFor()
        (code == 0) to out
    } catch (e: Exception) {
        false to (e.message ?: e.toString())
    }
}
