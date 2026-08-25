package io.skone.forms.formatter

import io.skone.common.annotation.SKInternal

/**
 * Converts between model values and display strings for form fields.
 *
 * Widgets never hardcode formatting — they ask the [SKFormatterEngine].
 */
public interface SKFormatter {
    /** Formats a model [value] for display. */
    public fun format(value: Any?): String

    /** Parses a display [text] back to a model value. */
    public fun parse(text: String): Any?
}

/** Trims whitespace on parse; identity format. */
public object SKTrimFormatter : SKFormatter {
    override fun format(value: Any?): String = value?.toString().orEmpty()
    override fun parse(text: String): Any? = text.trim()
}

/** Identity formatter (string passthrough). */
public object SKIdentityFormatter : SKFormatter {
    override fun format(value: Any?): String = value?.toString().orEmpty()
    override fun parse(text: String): Any? = text
}

/** Uppercase display; parse returns uppercase string. */
public object SKUppercaseFormatter : SKFormatter {
    override fun format(value: Any?): String = value?.toString().orEmpty().uppercase()
    override fun parse(text: String): Any? = text.uppercase()
}

/** Lowercase display; parse returns lowercase string. */
public object SKLowercaseFormatter : SKFormatter {
    override fun format(value: Any?): String = value?.toString().orEmpty().lowercase()
    override fun parse(text: String): Any? = text.lowercase()
}

/**
 * Applies formatters for fields.
 */
public interface SKFormatterEngine {
    public fun format(formatter: SKFormatter?, value: Any?): String

    public fun parse(formatter: SKFormatter?, text: String): Any?
}

/**
 * Default [SKFormatterEngine].
 *
 * **Internal implementation** — not intended for application use.
 */
@SKInternal
public class SKDefaultFormatterEngine : SKFormatterEngine {
    override fun format(formatter: SKFormatter?, value: Any?): String =
        formatter?.format(value) ?: value?.toString().orEmpty()

    override fun parse(formatter: SKFormatter?, text: String): Any? =
        formatter?.parse(text) ?: text
}
