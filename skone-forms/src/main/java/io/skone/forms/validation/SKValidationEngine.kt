package io.skone.forms.validation

import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.forms.field.SKFieldRegistry
import io.skone.forms.field.SKFormField

/**
 * Runs [SKValidationRule] sets for fields and entire forms.
 */
public interface SKValidationEngine {
    /** Validates a single [field] against [value]. */
    public fun validateField(
        field: SKFormField,
        value: Any?,
        context: SKValidationContext = SKValidationContext.Empty,
    ): SKValidationResult

    /** Validates all registered fields using current registry values. */
    public fun validateForm(registry: SKFieldRegistry): SKFormValidationResult
}

/**
 * Aggregated form validation outcome.
 *
 * @property overall [SKValidationResult.Valid] only when every field is valid.
 * @property fieldResults Per-field results.
 */
public data class SKFormValidationResult(
    public val overall: SKValidationResult,
    public val fieldResults: Map<String, SKValidationResult>,
) {
    public val isValid: Boolean get() = overall is SKValidationResult.Valid

    public companion object {
        public val Empty: SKFormValidationResult = SKFormValidationResult(
            overall = SKValidationResult.Valid,
            fieldResults = emptyMap(),
        )
    }
}

/**
 * Default [SKValidationEngine].
 */
public class SKDefaultValidationEngine : SKValidationEngine {
    override fun validateField(
        field: SKFormField,
        value: Any?,
        context: SKValidationContext,
    ): SKValidationResult {
        if (field.rules.isEmpty()) return SKValidationResult.Valid
        val errors = mutableListOf<SKValidationError>()
        val ctx = context.copy(fieldId = field.id)
        field.rules.forEach { rule ->
            when (val result = rule.validate(value, ctx)) {
                is SKValidationResult.Valid -> Unit
                is SKValidationResult.Invalid -> errors += result.errors
            }
        }
        return if (errors.isEmpty()) SKValidationResult.Valid else SKValidationResult.Invalid(errors)
    }

    override fun validateForm(registry: SKFieldRegistry): SKFormValidationResult {
        val values = registry.allStates().mapValues { it.value.value }
        val context = SKValidationContext(values = values)
        val fieldResults = registry.all().associate { field ->
            field.id to validateField(field, values[field.id], context)
        }
        val errors = fieldResults.values.filterIsInstance<SKValidationResult.Invalid>()
            .flatMap { it.errors }
        val overall = if (errors.isEmpty()) {
            SKValidationResult.Valid
        } else {
            SKValidationResult.Invalid(errors)
        }
        return SKFormValidationResult(overall = overall, fieldResults = fieldResults)
    }
}
