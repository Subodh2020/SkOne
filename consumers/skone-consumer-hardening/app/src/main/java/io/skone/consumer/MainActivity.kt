package io.skone.consumer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.theme.SKTheme
import io.skone.compose.widget.SKText
import io.skone.consumer.ui.FormValidationScreen
import io.skone.consumer.ui.ListSearchFilterScreen
import io.skone.consumer.ui.ShellNavigationScreen
import io.skone.consumer.xml.XmlFormActivity
import io.skone.consumer.xml.XmlListFilterActivity
import io.skone.consumer.xml.XmlShellActivity
import io.skone.theme.SKThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SKTheme(mode = SKThemeMode.System) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    ConsumerHome()
                }
            }
        }
    }
}

enum class ConsumerRoute {
    Home,
    ListFilter,
    Form,
    Shell,
}

@Composable
private fun ConsumerHome() {
    val context = LocalContext.current
    var route by remember { mutableStateOf(ConsumerRoute.Home) }
    when (route) {
        ConsumerRoute.Home -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SKText(text = "SKOne Consumer Hardening")
                SKText(text = "Maven Central only — skone-bom:1.4.0-alpha02")
                Button(
                    onClick = { route = ConsumerRoute.ListFilter },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Compose: List + Search + Filter") }
                Button(
                    onClick = { route = ConsumerRoute.Form },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Compose: Form + Validation") }
                Button(
                    onClick = { route = ConsumerRoute.Shell },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Compose: App Shell + Navigation") }
                Button(
                    onClick = {
                        context.startActivity(Intent(context, XmlListFilterActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("XML: List + Search + Filter") }
                Button(
                    onClick = {
                        context.startActivity(Intent(context, XmlFormActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("XML: Form + Validation") }
                Button(
                    onClick = {
                        context.startActivity(Intent(context, XmlShellActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("XML: App Shell + Navigation") }
            }
        }
        ConsumerRoute.ListFilter -> ListSearchFilterScreen(onBack = { route = ConsumerRoute.Home })
        ConsumerRoute.Form -> FormValidationScreen(onBack = { route = ConsumerRoute.Home })
        ConsumerRoute.Shell -> ShellNavigationScreen(onBack = { route = ConsumerRoute.Home })
    }
}
