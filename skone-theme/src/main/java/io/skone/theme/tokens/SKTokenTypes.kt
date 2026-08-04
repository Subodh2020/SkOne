package io.skone.theme.tokens

/**
 * ARGB color token represented as a packed 32-bit integer (`0xAARRGGBB`).
 *
 * Kept free of Compose/AndroidX graphics types so `skone-theme` stays UI-framework agnostic.
 */
@JvmInline
public value class SKColor(public val argb: Int)

/**
 * Dimension token in density-independent pixels.
 */
@JvmInline
public value class SKDp(public val value: Float)

/**
 * Font size / spacing token in scalable pixels.
 */
@JvmInline
public value class SKSp(public val value: Float)

/**
 * Duration token in milliseconds.
 */
@JvmInline
public value class SKDuration(public val millis: Long)

/**
 * Typography role definition without Compose [androidx.compose.ui.text.TextStyle].
 *
 * @property fontFamilyKey Logical font family key resolved by UI bridges.
 * @property weight Font weight (100–900).
 * @property size Font size in sp.
 * @property lineHeight Line height in sp.
 * @property letterSpacing Letter spacing in sp.
 */
public data class SKTypeScale(
    public val fontFamilyKey: String,
    public val weight: Int,
    public val size: SKSp,
    public val lineHeight: SKSp,
    public val letterSpacing: SKSp = SKSp(0f),
)

/**
 * Motion easing identifier resolved by animation bridges.
 */
@JvmInline
public value class SKEasing(public val key: String)
