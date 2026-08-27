@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.theme

import io.skone.theme.defaults.SKDefaultThemeTokens
import io.skone.theme.shape.SKDefaultShapeTokens
import io.skone.theme.shape.SKShapeTokens
import io.skone.theme.size.SKSizeTokens
import io.skone.theme.tokens.SKColorTokens
import io.skone.theme.tokens.SKElevationTokens
import io.skone.theme.tokens.SKIconTokens
import io.skone.theme.tokens.SKMotionTokens
import io.skone.theme.tokens.SKRadiusTokens
import io.skone.theme.tokens.SKSpacingTokens
import io.skone.theme.tokens.SKThemeTokens
import io.skone.theme.tokens.SKTypographyTokens

/**
 * Fluent builder for custom [SKTheme] instances.
 *
 * Start from light or dark defaults and override individual token categories.
 *
 * ### Example
 * ```kotlin
 * val theme = SKThemeBuilder.light()
 *     .name("acme.light")
 *     .colors(myColors)
 *     .build()
 * ```
 */
public class SKThemeBuilder private constructor(
    private var name: String,
    private var mode: SKThemeMode,
    private var colors: SKColorTokens,
    private var typography: SKTypographyTokens,
    private var spacing: SKSpacingTokens,
    private var elevation: SKElevationTokens,
    private var radius: SKRadiusTokens,
    private var motion: SKMotionTokens,
    private var icons: SKIconTokens,
    private var sizes: SKSizeTokens,
    private var shapes: SKShapeTokens?,
) {
    /** Sets the theme display name. */
    public fun name(value: String): SKThemeBuilder = apply { name = value }

    /** Sets the theme mode metadata. */
    public fun mode(value: SKThemeMode): SKThemeBuilder = apply { mode = value }

    /** Replaces the color token table. */
    public fun colors(value: SKColorTokens): SKThemeBuilder = apply { colors = value }

    /** Replaces the typography token table. */
    public fun typography(value: SKTypographyTokens): SKThemeBuilder = apply { typography = value }

    /** Replaces the spacing token table. */
    public fun spacing(value: SKSpacingTokens): SKThemeBuilder = apply { spacing = value }

    /** Replaces the elevation token table. */
    public fun elevation(value: SKElevationTokens): SKThemeBuilder = apply { elevation = value }

    /** Replaces the radius token table. */
    public fun radius(value: SKRadiusTokens): SKThemeBuilder = apply { radius = value }

    /** Replaces the motion token table. */
    public fun motion(value: SKMotionTokens): SKThemeBuilder = apply { motion = value }

    /** Replaces the icon token table. */
    public fun icons(value: SKIconTokens): SKThemeBuilder = apply { icons = value }

    /** Replaces the size token table. */
    public fun sizes(value: SKSizeTokens): SKThemeBuilder = apply { sizes = value }

    /** Replaces the shape token table. */
    public fun shapes(value: SKShapeTokens): SKThemeBuilder = apply { shapes = value }

    /** Builds an immutable [SKTheme]. */
    public fun build(): SKTheme {
        val tokenPack: SKThemeTokens = SKDefaultThemeTokens(
            colors = colors,
            typography = typography,
            spacing = spacing,
            elevation = elevation,
            radius = radius,
            motion = motion,
            icons = icons,
        )
        return SKTheme(
            name = name,
            mode = mode,
            tokens = tokenPack,
            sizes = sizes,
            shapes = shapes ?: SKDefaultShapeTokens(tokenPack.radius),
        )
    }

    public companion object {
        /** Builder seeded from [SKThemes.Light]. */
        @JvmStatic
        public fun light(): SKThemeBuilder = from(SKThemes.Light)

        /** Builder seeded from [SKThemes.Dark]. */
        @JvmStatic
        public fun dark(): SKThemeBuilder = from(SKThemes.Dark)

        /** Builder seeded from an existing [SKTheme]. */
        @JvmStatic
        public fun from(theme: SKTheme): SKThemeBuilder = SKThemeBuilder(
            name = theme.name,
            mode = theme.mode,
            colors = theme.tokens.colors,
            typography = theme.tokens.typography,
            spacing = theme.tokens.spacing,
            elevation = theme.tokens.elevation,
            radius = theme.tokens.radius,
            motion = theme.tokens.motion,
            icons = theme.tokens.icons,
            sizes = theme.sizes,
            shapes = theme.shapes,
        )
    }
}
