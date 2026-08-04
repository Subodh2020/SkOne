package io.skone.forms.field

import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.validation.SKValidationTrigger
import io.skone.forms.formatter.SKFormatter
import io.skone.forms.mask.SKInputMask
import io.skone.forms.validation.SKValidationRule

/**
 * Logical form field — not a UI widget.
 *
 * Future widgets (`SKTextField`, …) bind to a field id and forward value changes here.
 *
 * @property id Stable field identifier.
 * @property initialValue Value at registration / after reset.
 * @property rules Validation rules evaluated by [io.skone.forms.validation.SKValidationEngine].
 * @property formatter Optional display ↔ model formatter.
 * @property mask Optional input mask applied to raw user input.
 * @property triggers When automatic validation runs for this field.
 * @property ai Optional AI configuration for assistive features.
 * @property metadata Opaque app metadata.
 */
public data class SKFormField(
    public val id: String,
    public val initialValue: Any? = null,
    public val rules: List<SKValidationRule> = emptyList(),
    public val formatter: SKFormatter? = null,
    public val mask: SKInputMask? = null,
    public val triggers: Set<SKValidationTrigger> = setOf(
        SKValidationTrigger.OnFocusLost,
        SKValidationTrigger.OnSubmit,
    ),
    public val ai: SKAIComponentConfig? = null,
    public val metadata: Map<String, String> = emptyMap(),
) {
    init {
        require(id.isNotBlank()) { "Field id must not be blank" }
    }
}

/**
 * Mutable runtime snapshot for a registered field.
 */
public data class SKFieldState(
    public val field: SKFormField,
    public val value: Any? = field.initialValue,
    public val displayValue: String = "",
    public val touched: Boolean = false,
    public val focused: Boolean = false,
)
