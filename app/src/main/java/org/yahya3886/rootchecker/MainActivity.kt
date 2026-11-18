package org.yahya3886.rootchecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dark = isSystemInDarkTheme()
            MiuixTheme(colors = if (dark) darkColorScheme() else lightColorScheme()) {
                RootCheckerApp()
            }
        }
    }
}

data class RootResult(
    val rooted: Boolean?,
    val suPath: String?,
    val suVersion: String?,
    val idOutput: String?,
    val details: List<String>
)

@Composable
fun RootCheckerApp() {
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

    Scaffold(
        topBar = {
            SmallTopAppBar(title = "Yahya's Checker")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(result = result, checking = checking)

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { runCheck() },
                enabled = !checking
            ) {
                Text(text = if (checking) "Kontrol ediliyor..." else "Tekrar kontrol et")
            }

            DetailsCard(result = result)
        }
    }
}

@Composable
fun StatusCard(result: RootResult?, checking: Boolean) {
    val (title, subtitle) = when {
        checking -> "Kontrol ediliyor" to "Root durumu sorgulanıyor..."
        result == null -> "Bilinmiyor" to "Henüz kontrol edilmedi"
        result.rooted == true -> "Root erişimi var" to "Cihazda root (su) kullanılabilir görünüyor"
        result.rooted == false -> "Root bulunamadı" to "su çalıştırılamadı veya bulunamadı"
        else -> "Belirsiz" to "Sonuç kesin değil"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = subtitle)
        if (result?.idOutput != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = result.idOutput)
        }
    }
}

@Composable
fun DetailsCard(result: RootResult?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        insideMargin = PaddingValues(16.dp)
    ) {
        Text(
            text = "Ayrıntılar",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (result == null) {
            Text(text = "—")
        } else {
            InfoRow("su yolu", result.suPath ?: "bulunamadı")
            InfoRow("su sürümü", result.suVersion ?: "bilinmiyor")
            Spacer(modifier = Modifier.height(8.dp))
            result.details.forEach { line ->
                Text(text = "• $line")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp)
        )
        Text(text = value)
    }
    Spacer(modifier = Modifier.height(4.dp))
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
