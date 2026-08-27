@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.theme

import io.skone.theme.defaults.SKDefaultThemeTokens
import io.skone.theme.shape.SKDefaultShapeTokens
import io.skone.theme.shape.SKShapeTokens
import io.skone.theme.size.SKDefaultSizeTokens
import io.skone.theme.size.SKSizeTokens
import io.skone.theme.tokens.SKRadiusTokens
import io.skone.theme.tokens.SKThemeTokens

/**
 * Immutable resolved theme for SKOne.
 *
 * UI bridges expose this to Compose and XML. Widgets read tokens/size/shape
 * from [SKTheme] and never hardcode visual literals.
 *
 * @property name Human-readable theme name (for tooling / debugging).
 * @property mode Preferred mode this theme represents.
 * @property tokens Design token pack (color, type, space, …).
 * @property sizes Size scale token table.
 * @property shapes Shape token table.
 */
public data class SKTheme(
    public val name: String,
    public val mode: SKThemeMode,
    public val tokens: SKThemeTokens,
    public val sizes: SKSizeTokens = skoneDefaultSizeTokens(),
    public val shapes: SKShapeTokens = skoneDefaultShapeTokens(tokens.radius),
)

/**
 * Built-in SKOne themes.
 */
public object SKThemes {
    /** Default light theme. */
    public val Light: SKTheme = SKTheme(
        name = "skone.light",
        mode = SKThemeMode.Light,
        tokens = SKDefaultThemeTokens.light(),
    )

    /** Default dark theme. */
    public val Dark: SKTheme = SKTheme(
        name = "skone.dark",
        mode = SKThemeMode.Dark,
        tokens = SKDefaultThemeTokens.dark(),
    )
}

/** Resolves library default size tokens without naming the impl in [SKTheme] signatures. */
private fun skoneDefaultSizeTokens(): SKSizeTokens = SKDefaultSizeTokens()

/** Resolves library default shape tokens without naming the impl in [SKTheme] signatures. */
private fun skoneDefaultShapeTokens(radius: SKRadiusTokens): SKShapeTokens =
    SKDefaultShapeTokens(radius)
