package io.skone.forms.mask

import io.skone.common.annotation.SKInternal

/**
 * Input mask applied as the user types.
 *
 * @property pattern Mask pattern using:
 * - `#` = digit
 * - `A` = letter
 * - `*` = alphanumeric
 * - other characters = literals
 */
public data class SKInputMask(
    public val pattern: String,
    public val placeholder: Char = '_',
)

/**
 * Result of applying a mask to raw input.
 *
 * @property display Masked display string.
 * @property raw Extracted raw (unmasked) characters.
 * @property complete `true` when the mask is fully filled.
 */
public data class SKMaskedValue(
    public val display: String,
    public val raw: String,
    public val complete: Boolean,
)

/**
 * Applies [SKInputMask] patterns to raw user input.
 */
public interface SKInputMaskEngine {
    public fun apply(mask: SKInputMask, rawInput: String): SKMaskedValue

    public fun strip(mask: SKInputMask, display: String): String
}

/**
 * Default [SKInputMaskEngine].
 *
 * **Internal implementation** — not intended for application use.
 */
@SKInternal
public class SKDefaultInputMaskEngine : SKInputMaskEngine {
    override fun apply(mask: SKInputMask, rawInput: String): SKMaskedValue {
        val rawChars = rawInput.filter { !it.isWhitespace() }.toMutableList()
        val out = StringBuilder()
        var rawIndex = 0
        var slots = 0
        var filled = 0

        for (token in mask.pattern) {
            when (token) {
                '#', 'A', '*' -> {
                    slots++
                    val next = rawChars.getOrNull(rawIndex)
                    if (next != null && matches(token, next)) {
                        out.append(next)
                        rawIndex++
                        filled++
                    } else {
                        out.append(mask.placeholder)
                    }
                }
                else -> out.append(token)
            }
        }

        val raw = rawChars.take(rawIndex).joinToString("")

        return SKMaskedValue(
            display = out.toString(),
            raw = raw,
            complete = slots > 0 && filled == slots,
        )
    }

    override fun strip(mask: SKInputMask, display: String): String {
        val result = StringBuilder()
        var di = 0
        for (token in mask.pattern) {
            if (di >= display.length) break
            when (token) {
                '#', 'A', '*' -> {
                    val ch = display[di]
                    if (ch != mask.placeholder && matches(token, ch)) {
                        result.append(ch)
                    }
                    di++
                }
                else -> {
                    if (display[di] == token) di++
                }
            }
        }
        // Also keep any extra trailing input digits beyond pattern (for progressive typing)
        return result.toString()
    }

    private fun matches(token: Char, ch: Char): Boolean = when (token) {
        '#' -> ch.isDigit()
        'A' -> ch.isLetter()
        '*' -> ch.isLetterOrDigit()
        else -> false
    }
}

/**
 * Common mask presets (patterns only — not widgets).
 */
public object SKInputMasks {
    /** US phone: (###) ###-#### */
    public val UsPhone: SKInputMask = SKInputMask(pattern = "(###) ###-####")

    /** Date: ##/##/#### */
    public val DateMdY: SKInputMask = SKInputMask(pattern = "##/##/####")

    /** Credit-card-like groups: #### #### #### #### */
    public val Card16: SKInputMask = SKInputMask(pattern = "#### #### #### ####")
}
