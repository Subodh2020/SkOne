package io.skone.forms.validation

import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidator

/**
 * Declarative validation rule for form fields.
 *
 * Bridges to component-level [SKValidator] via [asValidator].
 */
public interface SKValidationRule {
    /** Stable rule id, e.g. `required`, `email`. */
    public val id: String

    /**
     * Validates [value] in optional [context] (sibling values, metadata).
     */
    public fun validate(value: Any?, context: SKValidationContext = SKValidationContext.Empty): SKValidationResult

    /** Adapts this rule to a typed [SKValidator]. */
    public fun <T> asValidator(): SKValidator<T> = SKValidator { value ->
        validate(value)
    }
}

/**
 * Context passed to rules (cross-field validation, locale, etc.).
 */
public data class SKValidationContext(
    public val values: Map<String, Any?> = emptyMap(),
    public val fieldId: String? = null,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        public val Empty: SKValidationContext = SKValidationContext()
    }
}

/**
 * Base for rules that only care about a single value.
 */
public abstract class SKSimpleValidationRule(
    override val id: String,
) : SKValidationRule {
    protected fun invalid(code: String, message: String): SKValidationResult =
        SKValidationResult.Invalid(SKValidationError(code = code, message = message))
}

/** Value must be non-null and non-blank (for CharSequence) / non-empty (for Collection). */
public class SKRequiredRule(
    private val message: String = "Required",
) : SKSimpleValidationRule(id = "required") {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult =
        if (isEmpty(value)) invalid("skone.forms.required", message) else SKValidationResult.Valid

    private fun isEmpty(value: Any?): Boolean = when (value) {
        null -> true
        is CharSequence -> value.isBlank()
        is Collection<*> -> value.isEmpty()
        else -> false
    }
}

/** Minimum string length. */
public class SKMinLengthRule(
    private val min: Int,
    private val message: String = "Must be at least $min characters",
) : SKSimpleValidationRule(id = "minLength") {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult {
        val text = value?.toString().orEmpty()
        if (text.isEmpty()) return SKValidationResult.Valid // combine with Required
        return if (text.length >= min) SKValidationResult.Valid else invalid("skone.forms.minLength", message)
    }
}

/** Maximum string length. */
public class SKMaxLengthRule(
    private val max: Int,
    private val message: String = "Must be at most $max characters",
) : SKSimpleValidationRule(id = "maxLength") {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult {
        val text = value?.toString().orEmpty()
        return if (text.length <= max) SKValidationResult.Valid else invalid("skone.forms.maxLength", message)
    }
}

/** Simple email shape check (not a full RFC parser). */
public class SKEmailRule(
    private val message: String = "Invalid email",
) : SKSimpleValidationRule(id = "email") {
    private val pattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult {
        val text = value?.toString().orEmpty()
        if (text.isEmpty()) return SKValidationResult.Valid
        return if (pattern.matches(text)) SKValidationResult.Valid else invalid("skone.forms.email", message)
    }
}

/** Regex match rule. */
public class SKPatternRule(
    private val pattern: Regex,
    private val message: String = "Invalid format",
    id: String = "pattern",
) : SKSimpleValidationRule(id = id) {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult {
        val text = value?.toString().orEmpty()
        if (text.isEmpty()) return SKValidationResult.Valid
        return if (pattern.matches(text)) SKValidationResult.Valid else invalid("skone.forms.pattern", message)
    }
}

/** Custom predicate rule. */
public class SKPredicateRule(
    id: String,
    private val message: String,
    private val predicate: (Any?, SKValidationContext) -> Boolean,
) : SKSimpleValidationRule(id = id) {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult =
        if (predicate(value, context)) SKValidationResult.Valid else invalid("skone.forms.$id", message)
}

/**
 * Composite rule: all children must pass.
 */
public class SKAllOfRule(
    private val rules: List<SKValidationRule>,
    override val id: String = "allOf",
) : SKValidationRule {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult {
        val errors = mutableListOf<SKValidationError>()
        rules.forEach { rule ->
            when (val result = rule.validate(value, context)) {
                is SKValidationResult.Valid -> Unit
                is SKValidationResult.Invalid -> errors += result.errors
            }
        }
        return if (errors.isEmpty()) SKValidationResult.Valid else SKValidationResult.Invalid(errors)
    }
}

/**
 * Composite rule: at least one child must pass (empty children ⇒ Valid).
 */
public class SKAnyOfRule(
    private val rules: List<SKValidationRule>,
    override val id: String = "anyOf",
) : SKValidationRule {
    override fun validate(value: Any?, context: SKValidationContext): SKValidationResult {
        if (rules.isEmpty()) return SKValidationResult.Valid
        val errors = mutableListOf<SKValidationError>()
        for (rule in rules) {
            when (val result = rule.validate(value, context)) {
                is SKValidationResult.Valid -> return SKValidationResult.Valid
                is SKValidationResult.Invalid -> errors += result.errors
            }
        }
        return SKValidationResult.Invalid(errors)
    }
}
