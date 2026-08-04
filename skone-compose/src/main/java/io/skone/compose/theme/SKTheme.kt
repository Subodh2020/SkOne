package io.skone.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import io.skone.theme.SKDefaultThemeProvider
import io.skone.theme.SKThemeMode
import io.skone.theme.SKThemeProvider
import io.skone.theme.SKThemes
import io.skone.theme.SKTheme as SKThemeModel

/**
 * CompositionLocal holding the active [SKThemeModel].
 */
public val LocalSKTheme: ProvidableCompositionLocal<SKThemeModel> =
    staticCompositionLocalOf { SKThemes.Light }

/**
 * Returns the [SKThemeModel] from the nearest [SKTheme] provider.
 */
public val skTheme: SKThemeModel
    @Composable
    @ReadOnlyComposable
    get() = LocalSKTheme.current

/**
 * Provides an [SKThemeModel] to the Compose hierarchy.
 *
 * This is a **theme bridge only** — it does not include SKOne widgets.
 *
 * ### Example
 * ```kotlin
 * SKTheme(mode = SKThemeMode.System) {
 *     val primary = skTheme.tokens.colors.primary.toColor()
 * }
 * ```
 *
 * @param mode Theme mode to resolve.
 * @param provider Theme provider; defaults to [SKDefaultThemeProvider].
 * @param theme Explicit theme override; when non-null, [mode]/[provider] are ignored.
 * @param content Composable content.
 */
@Composable
public fun SKTheme(
    mode: SKThemeMode = SKThemeMode.System,
    provider: SKThemeProvider = SKDefaultThemeProvider(),
    theme: SKThemeModel? = null,
    content: @Composable () -> Unit,
) {
    val resolved = theme ?: provider.theme(
        mode = mode,
        isSystemInDarkTheme = isSystemInDarkTheme(),
    )
    CompositionLocalProvider(LocalSKTheme provides resolved, content = content)
}
