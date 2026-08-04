package io.skone.ui.text

import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole

/**
 * Text overflow behavior for [SKTextComponent] surfaces.
 */
public enum class SKTextOverflow {
    /** Clip overflowing glyphs. */
    Clip,

    /** Show an ellipsis at the truncation point. */
    Ellipsis,

    /** Allow visual overflow (Compose Visible / XML no ellipsize). */
    Visible,
}

/**
 * Horizontal text alignment. Respects RTL via Start/End.
 */
public enum class SKTextAlign {
    Start,
    Center,
    End,
    Justify,
}

/**
 * Portable span style — mapped to Compose [AnnotatedString] / XML [Spannable] by bridges.
 */
public sealed interface SKSpanStyle {
    /** Bold weight (700). */
    public data object Bold : SKSpanStyle

    /** Italic style. */
    public data object Italic : SKSpanStyle

    /** Underline decoration. */
    public data object Underline : SKSpanStyle

    /** Color from a theme color role (never a raw ARGB). */
    public data class ColorRole(public val role: SKColorRole) : SKSpanStyle

    /** Typography role for span-level type scale (size/weight/lineHeight). */
    public data class TypographyRole(public val role: SKTypographyRole) : SKSpanStyle
}

/**
 * A span applied to a [start], [end) range of [SKAnnotatedText.text].
 */
public data class SKTextSpan(
    public val start: Int,
    public val end: Int,
    public val styles: List<SKSpanStyle>,
) {
    init {
        require(start >= 0) { "start must be >= 0" }
        require(end >= start) { "end must be >= start" }
    }
}

/**
 * Framework-agnostic rich text model.
 *
 * @property text Plain string content (localization-ready).
 * @property spans Style spans; empty for plain text.
 */
public data class SKAnnotatedText(
    public val text: String,
    public val spans: List<SKTextSpan> = emptyList(),
) {
    public companion object {
        /** Plain text with no spans. */
        @JvmStatic
        public fun plain(text: String): SKAnnotatedText = SKAnnotatedText(text)
    }
}
