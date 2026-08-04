package io.skone.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.theme.SKTheme as SKThemeModel
import io.skone.theme.shape.SKShape
import io.skone.theme.tokens.color
import io.skone.theme.tokens.dp

/**
 * Resolved Compose values for an [SKAppearanceConfig] against the active theme.
 *
 * Widgets will use this helper instead of reading raw literals.
 */
public data class SKResolvedAppearance(
    public val containerColor: Color,
    public val contentColor: Color,
    public val outlineColor: Color?,
    public val shape: Shape,
    public val height: Dp,
    public val horizontalPadding: Dp,
    public val verticalPadding: Dp,
    public val iconSize: Dp,
    public val elevation: Dp?,
)

/**
 * Resolves [config] against [theme] into Compose-ready values.
 */
public fun SKAppearanceConfig.resolve(theme: SKThemeModel): SKResolvedAppearance {
    val colors = theme.tokens.colors
    val resolvedShape: SKShape = shape ?: theme.shapes.resolve(shapeStyle)
    return SKResolvedAppearance(
        containerColor = colors.color(containerColorRole).toColor(),
        contentColor = colors.color(contentColorRole).toColor(),
        outlineColor = outlineColorRole?.let { colors.color(it).toColor() },
        shape = resolvedShape.toComposeShape(),
        height = theme.sizes.height(size).toDp(),
        horizontalPadding = theme.sizes.horizontalPadding(size).toDp(),
        verticalPadding = theme.sizes.verticalPadding(size).toDp(),
        iconSize = theme.sizes.iconSize(size).toDp(),
        elevation = elevation?.let { theme.tokens.elevation.dp(it).toDp() },
    )
}

/**
 * Resolves this appearance against [skTheme].
 */
@Composable
public fun SKAppearanceConfig.resolve(): SKResolvedAppearance = resolve(skTheme)
