@file:OptIn(
    io.skone.common.annotation.SKExperimental::class,
    io.skone.common.annotation.SKInternal::class,
)

package io.skone.xml.theme

import android.content.Context
import android.content.res.Configuration
import io.skone.theme.SKDefaultThemeProvider
import io.skone.theme.SKTheme
import io.skone.theme.SKThemeMode
import io.skone.theme.SKThemeProvider
import io.skone.theme.SKThemes

/**
 * Context-scoped theme bridge for XML / View-based UIs.
 *
 * Install a theme on an Activity/Application [Context]; inflate-time helpers
 * and future XML widgets resolve tokens through [require].
 *
 * This module contains **no widgets** — only theme plumbing.
 */
public object SKThemeHelper {
    private val lock = Any()

    @Volatile
    private var installed: SKTheme? = null

    /**
     * Installs [theme] for the process (typically from Application / Activity).
     */
    @JvmStatic
    public fun install(theme: SKTheme) {
        synchronized(lock) {
            installed = theme
        }
    }

    /**
     * Resolves and installs a theme for [mode] using [provider].
     */
    @JvmStatic
    @JvmOverloads
    public fun install(
        context: Context,
        mode: SKThemeMode = SKThemeMode.System,
        provider: SKThemeProvider = SKDefaultThemeProvider(),
    ) {
        val dark = context.isSystemInDarkTheme()
        install(provider.theme(mode = mode, isSystemInDarkTheme = dark))
    }

    /**
     * Returns the installed theme, or [SKThemes.Light] if none was installed.
     */
    @JvmStatic
    public fun current(): SKTheme = installed ?: SKThemes.Light

    /**
     * Returns the installed theme or throws if missing.
     */
    @JvmStatic
    public fun require(): SKTheme = installed ?: error("SKThemeHelper is not installed. Call SKThemeHelper.install(...) first.")

    /**
     * Clears the installed theme. Intended for tests.
     */
    @JvmStatic
    public fun clear() {
        synchronized(lock) {
            installed = null
        }
    }
}

/**
 * `true` when the context configuration is currently in night mode.
 */
public fun Context.isSystemInDarkTheme(): Boolean {
    val mask = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mask == Configuration.UI_MODE_NIGHT_YES
}
