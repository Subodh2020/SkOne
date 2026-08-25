package io.skone.component.framework.validation

import io.skone.common.annotation.SKInternal
import io.skone.component.validation.SKValidationConfig
import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidator
import io.skone.component.validation.validateAll
import java.util.concurrent.ConcurrentHashMap

/**
 * Registers validators and validates component values.
 */
public interface SKValidationManager {
    /** Registers validators for [componentId], replacing any previous set. */
    public fun register(
        componentId: String,
        config: SKValidationConfig,
        validators: List<SKValidator<*>>,
    )

    /** Unregisters validators for [componentId]. */
    public fun unregister(componentId: String)

    /**
     * Validates [value] for [componentId].
     *
     * If the component is required and [value] is null/blank string, returns Invalid.
     */
    public fun <T> validate(componentId: String, value: T): SKValidationResult

    /** Validates all registered components using the last known values if stored; otherwise skips. */
    public fun validateAll(values: Map<String, Any?>): Map<String, SKValidationResult>
}

/**
 * Default [SKValidationManager] implementation.
 *
 * **Internal implementation** — not intended for application use.
 */
@SKInternal
public class SKDefaultValidationManager : SKValidationManager {
    private data class Entry(
        val config: SKValidationConfig,
        val validators: List<SKValidator<Any?>>,
    )

    private val entries = ConcurrentHashMap<String, Entry>()

    @Suppress("UNCHECKED_CAST")
    override fun register(
        componentId: String,
        config: SKValidationConfig,
        validators: List<SKValidator<*>>,
    ) {
        entries[componentId] = Entry(
            config = config,
            validators = validators as List<SKValidator<Any?>>,
        )
    }

    override fun unregister(componentId: String) {
        entries.remove(componentId)
    }

    override fun <T> validate(componentId: String, value: T): SKValidationResult {
        val entry = entries[componentId] ?: return SKValidationResult.Valid
        if (entry.config.required && isEmpty(value)) {
            return SKValidationResult.Invalid(
                SKValidationError(code = "skone.validation.required", message = "Required"),
            )
        }
        @Suppress("UNCHECKED_CAST")
        return validateAll(value as Any?, entry.validators)
    }

    override fun validateAll(values: Map<String, Any?>): Map<String, SKValidationResult> {
        return entries.keys.associateWith { id -> validate(id, values[id]) }
    }

    private fun isEmpty(value: Any?): Boolean = when (value) {
        null -> true
        is CharSequence -> value.isBlank()
        is Collection<*> -> value.isEmpty()
        else -> false
    }
}
