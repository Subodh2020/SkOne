package io.skone.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.theme.SKTheme
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.playground.nav.PlaygroundApp
import io.skone.theme.SKThemeMode

/**
 * Host activity for the official SKOne developer playground.
 */
class PlaygroundActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by remember { mutableStateOf(SKThemeMode.System) }
            SKTheme(mode = themeMode) {
                val runtime = rememberSKComponentRuntime()
                val colors = skTheme.tokens.colors
                val colorScheme = when (themeMode) {
                    SKThemeMode.Dark -> darkColorScheme(
                        primary = colors.primary.toColor(),
                        onPrimary = colors.onPrimary.toColor(),
                        secondary = colors.secondary.toColor(),
                        background = colors.background.toColor(),
                        surface = colors.surface.toColor(),
                        onBackground = colors.onBackground.toColor(),
                        onSurface = colors.onSurface.toColor(),
                        error = colors.error.toColor(),
                    )
                    SKThemeMode.Light -> lightColorScheme(
                        primary = colors.primary.toColor(),
                        onPrimary = colors.onPrimary.toColor(),
                        secondary = colors.secondary.toColor(),
                        background = colors.background.toColor(),
                        surface = colors.surface.toColor(),
                        onBackground = colors.onBackground.toColor(),
                        onSurface = colors.onSurface.toColor(),
                        error = colors.error.toColor(),
                    )
                    SKThemeMode.System -> {
                        // Resolve via SKTheme; chrome mirrors active tokens.
                        lightColorScheme(
                            primary = colors.primary.toColor(),
                            onPrimary = colors.onPrimary.toColor(),
                            secondary = colors.secondary.toColor(),
                            background = colors.background.toColor(),
                            surface = colors.surface.toColor(),
                            onBackground = colors.onBackground.toColor(),
                            onSurface = colors.onSurface.toColor(),
                            error = colors.error.toColor(),
                        )
                    }
                }
                MaterialTheme(colorScheme = colorScheme) {
                    ProvideSKComponentRuntime(runtime) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.background.toColor()),
                        ) {
                            PlaygroundApp(
                                themeMode = themeMode,
                                onThemeModeChange = { themeMode = it },
                            )
                        }
                    }
                }
            }
        }
    }
}
