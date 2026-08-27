package io.skone.theme.shape

import io.skone.common.annotation.SKInternal
import io.skone.theme.tokens.SKDp
import io.skone.theme.tokens.SKRadiusTokens

/**
 * Shape descriptor resolved from theme tokens.
 *
 * UI bridges map these to Compose `Shape` / XML background drawables.
 */
public sealed interface SKShape {
    /** Sharp rectangle (0 radius). */
    public data object Rectangle : SKShape

    /** Fully circular / pill when applied to a square or tall enough bounds. */
    public data object Circle : SKShape

    /** Rounded rectangle with an explicit corner radius token value. */
    public data class Rounded(public val radius: SKDp) : SKShape
}

/**
 * Named shape scale for components.
 */
public enum class SKShapeStyle {
    None,
    ExtraSmall,
    Small,
    Medium,
    Large,
    ExtraLarge,
    Full,
}

/**
 * Shape token table. Values are derived from [SKRadiusTokens] by default.
 */
public interface SKShapeTokens {
    public val none: SKShape
    public val extraSmall: SKShape
    public val small: SKShape
    public val medium: SKShape
    public val large: SKShape
    public val extraLarge: SKShape
    public val full: SKShape

    /** Resolves a named [SKShapeStyle] to a concrete [SKShape]. */
    public fun resolve(style: SKShapeStyle): SKShape = when (style) {
        SKShapeStyle.None -> none
        SKShapeStyle.ExtraSmall -> extraSmall
        SKShapeStyle.Small -> small
        SKShapeStyle.Medium -> medium
        SKShapeStyle.Large -> large
        SKShapeStyle.ExtraLarge -> extraLarge
        SKShapeStyle.Full -> full
    }
}

/**
 * Default shapes backed by radius tokens.
 *
 * Library default implementation — applications should supply [SKShapeTokens]
 * via [io.skone.theme.SKThemeBuilder.shapes] rather than depending on this type.
 */
@SKInternal
public data class SKDefaultShapeTokens(
    private val radii: SKRadiusTokens,
) : SKShapeTokens {
    override val none: SKShape = SKShape.Rectangle
    override val extraSmall: SKShape = SKShape.Rounded(radii.xs)
    override val small: SKShape = SKShape.Rounded(radii.sm)
    override val medium: SKShape = SKShape.Rounded(radii.md)
    override val large: SKShape = SKShape.Rounded(radii.lg)
    override val extraLarge: SKShape = SKShape.Rounded(radii.xl)
    override val full: SKShape = SKShape.Circle
}
