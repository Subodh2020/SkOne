package io.skone.theme.size

import io.skone.theme.tokens.SKDp

/**
 * Semantic size scale for SKOne components.
 *
 * Widgets accept [SKSize] and resolve dimensions through [SKSizeTokens] —
 * never through hardcoded dp values in public APIs.
 */
public enum class SKSize {
    ExtraSmall,
    Small,
    Medium,
    Large,
    ExtraLarge,
}

/**
 * Token table mapping [SKSize] to layout dimensions.
 *
 * All values are density-independent pixels.
 */
public interface SKSizeTokens {
    /** Minimum control height for [size]. */
    public fun height(size: SKSize): SKDp

    /** Horizontal content padding for [size]. */
    public fun horizontalPadding(size: SKSize): SKDp

    /** Vertical content padding for [size]. */
    public fun verticalPadding(size: SKSize): SKDp

    /** Leading/trailing icon size for [size]. */
    public fun iconSize(size: SKSize): SKDp

    /** Corner radius preference for [size] (may be overridden by shape tokens). */
    public fun preferredRadius(size: SKSize): SKDp

    /**
     * Minimum touch target (accessibility). Defaults should be ≥ 48dp.
     */
    public val minTouchTarget: SKDp
}

/**
 * Default SKOne size token table (4dp grid aligned).
 */
public data class SKDefaultSizeTokens(
    override val minTouchTarget: SKDp = SKDp(48f),
) : SKSizeTokens {

    override fun height(size: SKSize): SKDp = when (size) {
        SKSize.ExtraSmall -> SKDp(28f)
        SKSize.Small -> SKDp(32f)
        SKSize.Medium -> SKDp(40f)
        SKSize.Large -> SKDp(48f)
        SKSize.ExtraLarge -> SKDp(56f)
    }

    override fun horizontalPadding(size: SKSize): SKDp = when (size) {
        SKSize.ExtraSmall -> SKDp(8f)
        SKSize.Small -> SKDp(12f)
        SKSize.Medium -> SKDp(16f)
        SKSize.Large -> SKDp(20f)
        SKSize.ExtraLarge -> SKDp(24f)
    }

    override fun verticalPadding(size: SKSize): SKDp = when (size) {
        SKSize.ExtraSmall -> SKDp(4f)
        SKSize.Small -> SKDp(6f)
        SKSize.Medium -> SKDp(8f)
        SKSize.Large -> SKDp(10f)
        SKSize.ExtraLarge -> SKDp(12f)
    }

    override fun iconSize(size: SKSize): SKDp = when (size) {
        SKSize.ExtraSmall -> SKDp(16f)
        SKSize.Small -> SKDp(18f)
        SKSize.Medium -> SKDp(20f)
        SKSize.Large -> SKDp(24f)
        SKSize.ExtraLarge -> SKDp(28f)
    }

    override fun preferredRadius(size: SKSize): SKDp = when (size) {
        SKSize.ExtraSmall -> SKDp(4f)
        SKSize.Small -> SKDp(6f)
        SKSize.Medium -> SKDp(8f)
        SKSize.Large -> SKDp(12f)
        SKSize.ExtraLarge -> SKDp(16f)
    }
}
