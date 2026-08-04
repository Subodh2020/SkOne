package io.skone.xml.theme

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.theme.SKTheme
import io.skone.theme.shape.SKShape
import io.skone.theme.tokens.SKColor
import io.skone.theme.tokens.SKDp
import io.skone.theme.tokens.color
import io.skone.theme.tokens.dp

/**
 * Returns the packed ARGB int for View / Drawable APIs.
 */
public fun SKColor.toArgb(): Int = argb

/**
 * Converts [SKDp] to raw pixels for [view]'s display metrics.
 */
public fun SKDp.toPx(view: View): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, view.resources.displayMetrics)

/**
 * Resolved View-friendly appearance values.
 */
public data class SKXmlResolvedAppearance(
    public val containerColor: Int,
    public val contentColor: Int,
    public val outlineColor: Int?,
    public val cornerRadiusPx: Float,
    public val heightPx: Float,
    public val horizontalPaddingPx: Float,
    public val verticalPaddingPx: Float,
    public val iconSizePx: Float,
    public val elevationPx: Float?,
)

/**
 * Resolves [config] against [theme] into XML/View-ready values for [view].
 */
public fun SKAppearanceConfig.resolve(theme: SKTheme, view: View): SKXmlResolvedAppearance {
    val colors = theme.tokens.colors
    val resolvedShape: SKShape = shape ?: theme.shapes.resolve(shapeStyle)
    val radiusPx = when (resolvedShape) {
        SKShape.Rectangle -> 0f
        SKShape.Circle -> theme.tokens.radius.full.toPx(view)
        is SKShape.Rounded -> resolvedShape.radius.toPx(view)
    }
    return SKXmlResolvedAppearance(
        containerColor = colors.color(containerColorRole).toArgb(),
        contentColor = colors.color(contentColorRole).toArgb(),
        outlineColor = outlineColorRole?.let { colors.color(it).toArgb() },
        cornerRadiusPx = radiusPx,
        heightPx = theme.sizes.height(size).toPx(view),
        horizontalPaddingPx = theme.sizes.horizontalPadding(size).toPx(view),
        verticalPaddingPx = theme.sizes.verticalPadding(size).toPx(view),
        iconSizePx = theme.sizes.iconSize(size).toPx(view),
        elevationPx = elevation?.let { theme.tokens.elevation.dp(it).toPx(view) },
    )
}

/**
 * Creates a simple rounded rect drawable from a resolved appearance (utility for samples / tests).
 */
public fun SKXmlResolvedAppearance.toBackgroundDrawable(strokeWidthPx: Float = 0f): GradientDrawable =
    GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(containerColor)
        cornerRadius = cornerRadiusPx
        if (outlineColor != null && strokeWidthPx > 0f) {
            setStroke(strokeWidthPx.toInt().coerceAtLeast(1), outlineColor)
        }
    }
