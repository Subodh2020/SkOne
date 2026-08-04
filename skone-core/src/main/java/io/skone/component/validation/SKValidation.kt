package io.skone.component.validation

import io.skone.common.error.SKError

/**
 * When validation should run for a component.
 */
public enum class SKValidationTrigger {
    OnChange,
    OnFocusLost,
    OnSubmit,
    Manual,
}

/**
 * A single validation failure.
 *
 * @property code Stable machine-readable code.
 * @property message Human-readable message (may be localized by the app).
 */
public data class SKValidationError(
    public val code: String,
    public val message: String,
) {
    /** Converts to an [SKError] for APIs that use [io.skone.common.result.SKResult]. */
    public fun toSKError(): SKError = SKError(code = code, message = message)
}

/**
 * Outcome of validating a value.
 */
public sealed interface SKValidationResult {
    /** Value passed all validators. */
    public data object Valid : SKValidationResult

    /** Value failed one or more validators. */
    public data class Invalid(
        public val errors: List<SKValidationError>,
    ) : SKValidationResult {
        public constructor(error: SKValidationError) : this(listOf(error))
    }
}

/**
 * Provider-agnostic validator for form-like values.
 *
 * Full formatter / mask frameworks arrive in a later milestone; this contract
 * is stable so widgets can accept validators early.
 */
public fun interface SKValidator<T> {
    /** Validates [value] and returns [SKValidationResult]. */
    public fun validate(value: T): SKValidationResult
}

/**
 * Validation configuration attached to a component instance.
 *
 * @property required Whether an empty value is invalid.
 * @property triggers When automatic validation runs.
 * @property supportingTextOnError Prefer showing validation messages as supporting text.
 */
public data class SKValidationConfig(
    public val required: Boolean = false,
    public val triggers: Set<SKValidationTrigger> = setOf(SKValidationTrigger.OnSubmit),
    public val supportingTextOnError: Boolean = true,
) {
    public companion object {
        /** No validation. */
        public val None: SKValidationConfig = SKValidationConfig(required = false, triggers = emptySet())

        /** Required field validated on submit. */
        public val Required: SKValidationConfig = SKValidationConfig(required = true)
    }
}

/**
 * Runs [validators] and returns the first failure set, or [SKValidationResult.Valid].
 */
public fun <T> validateAll(value: T, validators: List<SKValidator<T>>): SKValidationResult {
    val errors = mutableListOf<SKValidationError>()
    for (validator in validators) {
        when (val result = validator.validate(value)) {
            is SKValidationResult.Valid -> Unit
            is SKValidationResult.Invalid -> errors += result.errors
        }
    }
    return if (errors.isEmpty()) SKValidationResult.Valid else SKValidationResult.Invalid(errors)
}
