package io.skone.component.appearance

import io.skone.theme.shape.SKShape
import io.skone.theme.shape.SKShapeStyle
import io.skone.theme.size.SKSize
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKElevationLevel
import io.skone.theme.tokens.SKTypographyRole

/**
 * Appearance contract for future SKOne widgets.
 *
 * All visual choices are **roles / semantic sizes** resolved against [io.skone.theme.SKTheme].
 * Do not put raw ARGB, dp, or sp literals here.
 *
 * @property size Semantic control size.
 * @property shapeStyle Named shape from theme shape tokens; ignored when [shape] is set.
 * @property shape Optional explicit shape override (still token-backed via [io.skone.theme.shape.SKShape.Rounded]).
 * @property containerColorRole Fill / container color role.
 * @property contentColorRole Foreground / content color role.
 * @property outlineColorRole Optional outline / border color role.
 * @property typographyRole Optional text role for labeled components.
 * @property elevation Optional elevation level.
 */
public data class SKAppearanceConfig(
    public val size: SKSize = SKSize.Medium,
    public val shapeStyle: SKShapeStyle = SKShapeStyle.Medium,
    public val shape: SKShape? = null,
    public val containerColorRole: SKColorRole = SKColorRole.Primary,
    public val contentColorRole: SKColorRole = SKColorRole.OnPrimary,
    public val outlineColorRole: SKColorRole? = null,
    public val typographyRole: SKTypographyRole? = SKTypographyRole.LabelLarge,
    public val elevation: SKElevationLevel? = null,
) {
    public companion object {
        /** Neutral surface appearance (cards, containers). */
        public val Surface: SKAppearanceConfig = SKAppearanceConfig(
            containerColorRole = SKColorRole.Surface,
            contentColorRole = SKColorRole.OnSurface,
            outlineColorRole = SKColorRole.OutlineVariant,
            typographyRole = SKTypographyRole.BodyMedium,
            elevation = SKElevationLevel.Level0,
        )

        /** Primary emphasis appearance (primary actions). */
        public val Primary: SKAppearanceConfig = SKAppearanceConfig()

        /** Error appearance. */
        public val Error: SKAppearanceConfig = SKAppearanceConfig(
            containerColorRole = SKColorRole.Error,
            contentColorRole = SKColorRole.OnError,
        )
    }
}
