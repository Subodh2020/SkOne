package io.skone.theme.defaults

import io.skone.theme.tokens.SKDp
import io.skone.theme.tokens.SKDuration
import io.skone.theme.tokens.SKEasing
import io.skone.theme.tokens.SKElevationTokens
import io.skone.theme.tokens.SKIconTokens
import io.skone.theme.tokens.SKMotionTokens
import io.skone.theme.tokens.SKRadiusTokens
import io.skone.theme.tokens.SKSp
import io.skone.theme.tokens.SKSpacingTokens
import io.skone.theme.tokens.SKTypeScale
import io.skone.theme.tokens.SKTypographyTokens

private const val FONT_SANS = "skone.sans"

/**
 * Default typography scale (Material 3–aligned roles, framework-agnostic).
 */
public data class SKDefaultTypographyTokens(
    override val displayLarge: SKTypeScale = type(57f, 64f, 400),
    override val displayMedium: SKTypeScale = type(45f, 52f, 400),
    override val displaySmall: SKTypeScale = type(36f, 44f, 400),
    override val headlineLarge: SKTypeScale = type(32f, 40f, 400),
    override val headlineMedium: SKTypeScale = type(28f, 36f, 400),
    override val headlineSmall: SKTypeScale = type(24f, 32f, 400),
    override val titleLarge: SKTypeScale = type(22f, 28f, 400),
    override val titleMedium: SKTypeScale = type(16f, 24f, 500),
    override val titleSmall: SKTypeScale = type(14f, 20f, 500),
    override val bodyLarge: SKTypeScale = type(16f, 24f, 400),
    override val bodyMedium: SKTypeScale = type(14f, 20f, 400),
    override val bodySmall: SKTypeScale = type(12f, 16f, 400),
    override val labelLarge: SKTypeScale = type(14f, 20f, 500),
    override val labelMedium: SKTypeScale = type(12f, 16f, 500),
    override val labelSmall: SKTypeScale = type(11f, 16f, 500),
) : SKTypographyTokens

private fun type(size: Float, lineHeight: Float, weight: Int): SKTypeScale =
    SKTypeScale(
        fontFamilyKey = FONT_SANS,
        weight = weight,
        size = SKSp(size),
        lineHeight = SKSp(lineHeight),
    )

/** Default 4dp spacing scale. */
public data class SKDefaultSpacingTokens(
    override val none: SKDp = SKDp(0f),
    override val xxs: SKDp = SKDp(2f),
    override val xs: SKDp = SKDp(4f),
    override val sm: SKDp = SKDp(8f),
    override val md: SKDp = SKDp(16f),
    override val lg: SKDp = SKDp(24f),
    override val xl: SKDp = SKDp(32f),
    override val xxl: SKDp = SKDp(48f),
    override val xxxl: SKDp = SKDp(64f),
) : SKSpacingTokens

/** Default elevation levels. */
public data class SKDefaultElevationTokens(
    override val level0: SKDp = SKDp(0f),
    override val level1: SKDp = SKDp(1f),
    override val level2: SKDp = SKDp(3f),
    override val level3: SKDp = SKDp(6f),
    override val level4: SKDp = SKDp(8f),
    override val level5: SKDp = SKDp(12f),
) : SKElevationTokens

/** Default corner radii. */
public data class SKDefaultRadiusTokens(
    override val none: SKDp = SKDp(0f),
    override val xs: SKDp = SKDp(4f),
    override val sm: SKDp = SKDp(8f),
    override val md: SKDp = SKDp(12f),
    override val lg: SKDp = SKDp(16f),
    override val xl: SKDp = SKDp(28f),
    override val full: SKDp = SKDp(999f),
) : SKRadiusTokens

/** Default motion tokens. */
public data class SKDefaultMotionTokens(
    override val short1: SKDuration = SKDuration(50),
    override val short2: SKDuration = SKDuration(100),
    override val short3: SKDuration = SKDuration(150),
    override val short4: SKDuration = SKDuration(200),
    override val medium1: SKDuration = SKDuration(250),
    override val medium2: SKDuration = SKDuration(300),
    override val medium3: SKDuration = SKDuration(350),
    override val medium4: SKDuration = SKDuration(400),
    override val long1: SKDuration = SKDuration(450),
    override val long2: SKDuration = SKDuration(500),
    override val long3: SKDuration = SKDuration(550),
    override val long4: SKDuration = SKDuration(600),
    override val standard: SKEasing = SKEasing("standard"),
    override val emphasized: SKEasing = SKEasing("emphasized"),
    override val decelerated: SKEasing = SKEasing("decelerated"),
    override val accelerated: SKEasing = SKEasing("accelerated"),
) : SKMotionTokens

/** Default icon sizes. */
public data class SKDefaultIconTokens(
    override val xs: SKDp = SKDp(16f),
    override val sm: SKDp = SKDp(20f),
    override val md: SKDp = SKDp(24f),
    override val lg: SKDp = SKDp(32f),
    override val xl: SKDp = SKDp(40f),
) : SKIconTokens
