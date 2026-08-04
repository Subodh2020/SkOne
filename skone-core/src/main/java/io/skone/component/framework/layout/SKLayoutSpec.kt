package io.skone.component.framework.layout

import io.skone.theme.tokens.SKDp

/**
 * Framework-agnostic layout width/height mode.
 */
public enum class SKLayoutMode {
    /** Wrap content intrinsic size. */
    Wrap,

    /** Fill maximum constraint. */
    Fill,

    /** Exact tokenized size. */
    Exact,
}

/**
 * Single-axis layout constraint.
 *
 * @property mode How the axis is measured.
 * @property exact Size when [mode] is [SKLayoutMode.Exact]; ignored otherwise.
 * @property min Optional minimum.
 * @property max Optional maximum.
 */
public data class SKLayoutAxis(
    public val mode: SKLayoutMode = SKLayoutMode.Wrap,
    public val exact: SKDp? = null,
    public val min: SKDp? = null,
    public val max: SKDp? = null,
)

/**
 * Layout specification for future widgets (Compose Modifier / XML LayoutParams bridges).
 *
 * No Compose/View types here — UI modules map this to platform APIs.
 *
 * @property width Horizontal constraint.
 * @property height Vertical constraint.
 * @property weight Optional flex weight for row/column parents.
 * @property margin Margin insets in dp tokens.
 * @property padding Padding insets in dp tokens.
 */
public data class SKLayoutSpec(
    public val width: SKLayoutAxis = SKLayoutAxis(),
    public val height: SKLayoutAxis = SKLayoutAxis(),
    public val weight: Float? = null,
    public val margin: SKEdgeInsets = SKEdgeInsets.Zero,
    public val padding: SKEdgeInsets = SKEdgeInsets.Zero,
) {
    public companion object {
        /** Wrap × wrap. */
        public val Wrap: SKLayoutSpec = SKLayoutSpec()

        /** Fill width, wrap height. */
        public val FillWidth: SKLayoutSpec = SKLayoutSpec(
            width = SKLayoutAxis(mode = SKLayoutMode.Fill),
        )
    }
}

/**
 * Edge insets expressed as density-independent pixels.
 */
public data class SKEdgeInsets(
    public val start: SKDp = SKDp(0f),
    public val top: SKDp = SKDp(0f),
    public val end: SKDp = SKDp(0f),
    public val bottom: SKDp = SKDp(0f),
) {
    public companion object {
        public val Zero: SKEdgeInsets = SKEdgeInsets()

        public fun all(value: SKDp): SKEdgeInsets = SKEdgeInsets(value, value, value, value)

        public fun symmetric(horizontal: SKDp = SKDp(0f), vertical: SKDp = SKDp(0f)): SKEdgeInsets =
            SKEdgeInsets(start = horizontal, top = vertical, end = horizontal, bottom = vertical)
    }
}
