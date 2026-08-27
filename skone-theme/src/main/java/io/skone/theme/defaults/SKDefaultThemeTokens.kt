@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.theme.defaults

import io.skone.common.annotation.SKInternal
import io.skone.theme.tokens.SKColorTokens
import io.skone.theme.tokens.SKElevationTokens
import io.skone.theme.tokens.SKIconTokens
import io.skone.theme.tokens.SKMotionTokens
import io.skone.theme.tokens.SKRadiusTokens
import io.skone.theme.tokens.SKSpacingTokens
import io.skone.theme.tokens.SKThemeTokens
import io.skone.theme.tokens.SKTypographyTokens

/**
 * Assembled default [SKThemeTokens] for a given color palette.
 *
 * Library default implementation — applications customize via [io.skone.theme.SKThemeBuilder]
 * and public color seeds ([SKLightColorTokens] / [SKDarkColorTokens]).
 */
@SKInternal
public data class SKDefaultThemeTokens(
    override val colors: SKColorTokens,
    override val typography: SKTypographyTokens = SKDefaultTypographyTokens(),
    override val spacing: SKSpacingTokens = SKDefaultSpacingTokens(),
    override val elevation: SKElevationTokens = SKDefaultElevationTokens(),
    override val radius: SKRadiusTokens = SKDefaultRadiusTokens(),
    override val motion: SKMotionTokens = SKDefaultMotionTokens(),
    override val icons: SKIconTokens = SKDefaultIconTokens(),
) : SKThemeTokens {
    public companion object {
        /** Light-mode default token pack. */
        public fun light(): SKDefaultThemeTokens =
            SKDefaultThemeTokens(colors = SKLightColorTokens())

        /** Dark-mode default token pack. */
        public fun dark(): SKDefaultThemeTokens =
            SKDefaultThemeTokens(colors = SKDarkColorTokens())
    }
}
