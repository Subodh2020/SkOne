package io.skone.forms.error

import io.skone.component.validation.SKValidationError
import io.skone.component.validation.SKValidationResult
import io.skone.forms.validation.SKFormValidationResult
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tracks field-level and form-level validation errors.
 */
public interface SKFormErrorManager {
    public val errors: StateFlow<Map<String, List<SKValidationError>>>

    public fun setFieldErrors(fieldId: String, result: SKValidationResult)

    public fun setFormErrors(result: SKFormValidationResult)

    public fun clearField(fieldId: String)

    public fun clearAll()

    public fun errorsFor(fieldId: String): List<SKValidationError>

    public fun hasErrors(): Boolean
}

/**
 * Default in-memory [SKFormErrorManager].
 */
public class SKDefaultFormErrorManager : SKFormErrorManager {
    private val _errors = MutableStateFlow<Map<String, List<SKValidationError>>>(emptyMap())
    override val errors: StateFlow<Map<String, List<SKValidationError>>> = _errors.asStateFlow()

    private val store = ConcurrentHashMap<String, List<SKValidationError>>()

    override fun setFieldErrors(fieldId: String, result: SKValidationResult) {
        when (result) {
            is SKValidationResult.Valid -> store.remove(fieldId)
            is SKValidationResult.Invalid -> store[fieldId] = result.errors
        }
        publish()
    }

    override fun setFormErrors(result: SKFormValidationResult) {
        store.clear()
        result.fieldResults.forEach { (id, fieldResult) ->
            if (fieldResult is SKValidationResult.Invalid) {
                store[id] = fieldResult.errors
            }
        }
        publish()
    }

    override fun clearField(fieldId: String) {
        store.remove(fieldId)
        publish()
    }

    override fun clearAll() {
        store.clear()
        publish()
    }

    override fun errorsFor(fieldId: String): List<SKValidationError> = store[fieldId].orEmpty()

    override fun hasErrors(): Boolean = store.isNotEmpty()

    private fun publish() {
        _errors.value = store.toMap()
    }
}
