package io.skone.theme

import io.skone.theme.tokens.SKThemeTokens

/**
 * Resolves the active [SKTheme] for a given [SKThemeMode].
 *
 * Concrete Material3 / Compose / XML bridges consume this interface.
 * Dynamic Color may wrap or decorate a provider without changing token contracts.
 */
public interface SKThemeProvider {
    /**
     * Returns the [SKTheme] for [mode].
     *
     * When [mode] is [SKThemeMode.System], [isSystemInDarkTheme] decides light vs dark.
     */
    public fun theme(
        mode: SKThemeMode = SKThemeMode.System,
        isSystemInDarkTheme: Boolean = false,
    ): SKTheme

    /**
     * Convenience: token pack for [mode].
     */
    public fun tokens(
        mode: SKThemeMode = SKThemeMode.System,
        isSystemInDarkTheme: Boolean = false,
    ): SKThemeTokens = theme(mode, isSystemInDarkTheme).tokens
}

/**
 * Default provider using [SKThemes.Light] / [SKThemes.Dark], with optional overrides.
 *
 * @property lightTheme Theme used for light / system-light.
 * @property darkTheme Theme used for dark / system-dark.
 */
public class SKDefaultThemeProvider(
    private val lightTheme: SKTheme = SKThemes.Light,
    private val darkTheme: SKTheme = SKThemes.Dark,
) : SKThemeProvider {

    override fun theme(mode: SKThemeMode, isSystemInDarkTheme: Boolean): SKTheme = when (mode) {
        SKThemeMode.Light -> lightTheme
        SKThemeMode.Dark -> darkTheme
        SKThemeMode.System -> if (isSystemInDarkTheme) darkTheme else lightTheme
    }
}
