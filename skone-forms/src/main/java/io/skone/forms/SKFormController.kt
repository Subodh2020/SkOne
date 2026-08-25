@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.forms

import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidationTrigger
import io.skone.forms.ai.SKDefaultFormAIHooks
import io.skone.forms.ai.SKFormAIHooks
import io.skone.forms.error.SKDefaultFormErrorManager
import io.skone.forms.error.SKFormErrorManager
import io.skone.forms.field.SKDefaultFieldRegistry
import io.skone.forms.field.SKFieldRegistry
import io.skone.forms.field.SKFormField
import io.skone.forms.focus.SKDefaultFocusChain
import io.skone.forms.focus.SKFocusChain
import io.skone.forms.formatter.SKDefaultFormatterEngine
import io.skone.forms.formatter.SKFormatterEngine
import io.skone.forms.mask.SKDefaultInputMaskEngine
import io.skone.forms.mask.SKInputMaskEngine
import io.skone.forms.mask.SKMaskedValue
import io.skone.forms.state.SKFormLifecycle
import io.skone.forms.state.SKFormState
import io.skone.forms.validation.SKDefaultValidationEngine
import io.skone.forms.validation.SKFormValidationResult
import io.skone.forms.validation.SKValidationContext
import io.skone.forms.validation.SKValidationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Central orchestrator for SKOne forms.
 *
 * Future input widgets register fields and forward user input here.
 * This class contains **no UI**.
 *
 * @see docs/FORM_FRAMEWORK.md
 */
public class SKFormController internal constructor(
    public val registry: SKFieldRegistry,
    public val validation: SKValidationEngine,
    public val formatters: SKFormatterEngine,
    public val masks: SKInputMaskEngine,
    public val focus: SKFocusChain,
    public val errors: SKFormErrorManager,
    public val ai: SKFormAIHooks,
) {
    private val _state = MutableStateFlow(SKFormState.Initial)
    public val state: StateFlow<SKFormState> = _state.asStateFlow()

    private var disposed: Boolean = false

    /** Registers a logical field and appends it to the focus chain. */
    public fun register(field: SKFormField) {
        ensureActive()
        registry.register(field)
        focus.append(field.id)
        publishReadyOrDirty()
    }

    /** Unregisters a field. */
    public fun unregister(fieldId: String) {
        ensureActive()
        registry.unregister(fieldId)
        focus.remove(fieldId)
        errors.clearField(fieldId)
        publishReadyOrDirty()
    }

    /**
     * Updates a field from raw user input.
     *
     * Applies mask (if any), then formatter parse, then optional OnChange validation.
     */
    public fun updateRawInput(fieldId: String, rawInput: String) {
        ensureActive()
        val field = registry.get(fieldId) ?: return
        val masked: SKMaskedValue? = field.mask?.let { masks.apply(it, rawInput) }
        val textForParse = masked?.raw ?: rawInput
        val parsed = formatters.parse(field.formatter, textForParse)
        val display = masked?.display ?: formatters.format(field.formatter, parsed)

        registry.updateState(fieldId) { current ->
            current.copy(
                value = parsed,
                displayValue = display,
                touched = true,
            )
        }
        markDirty()

        if (SKValidationTrigger.OnChange in field.triggers) {
            validateField(fieldId)
        }
    }

    /** Sets a model value directly (programmatic / autofill). */
    public fun updateValue(fieldId: String, value: Any?) {
        ensureActive()
        val field = registry.get(fieldId) ?: return
        val display = formatters.format(field.formatter, value)
        registry.updateState(fieldId) {
            it.copy(value = value, displayValue = display, touched = true)
        }
        markDirty()
        if (SKValidationTrigger.OnChange in field.triggers) {
            validateField(fieldId)
        }
    }

    /** Marks focus; updates focus chain and optional OnFocusLost on previous field. */
    public fun requestFocus(fieldId: String) {
        ensureActive()
        val previous = focus.focusedId.value
        if (previous != null && previous != fieldId) {
            registry.updateState(previous) { it.copy(focused = false) }
            val prevField = registry.get(previous)
            if (prevField != null && SKValidationTrigger.OnFocusLost in prevField.triggers) {
                validateField(previous)
            }
        }
        focus.requestFocus(fieldId)
        registry.updateState(fieldId) { it.copy(focused = true) }
    }

    public fun clearFocus() {
        ensureActive()
        val previous = focus.focusedId.value
        if (previous != null) {
            registry.updateState(previous) { it.copy(focused = false) }
            val prevField = registry.get(previous)
            if (prevField != null && SKValidationTrigger.OnFocusLost in prevField.triggers) {
                validateField(previous)
            }
        }
        focus.clearFocus()
    }

    /** Validates a single field and updates the error manager. */
    public fun validateField(fieldId: String): SKValidationResult {
        ensureActive()
        val field = registry.get(fieldId) ?: return SKValidationResult.Valid
        val value = registry.state(fieldId)?.value
        val context = SKValidationContext(
            values = registry.allStates().mapValues { it.value.value },
            fieldId = fieldId,
        )
        setLifecycle(SKFormLifecycle.Validating)
        val result = validation.validateField(field, value, context)
        errors.setFieldErrors(fieldId, result)
        refreshValidity()
        return result
    }

    /** Validates the entire form. */
    public fun validate(): SKFormValidationResult {
        ensureActive()
        setLifecycle(SKFormLifecycle.Validating)
        val result = validation.validateForm(registry)
        errors.setFormErrors(result)
        refreshValidity(result)
        return result
    }

    /**
     * Validates then transitions to [SKFormLifecycle.Submitting] / [SKFormLifecycle.Submitted]
     * or [SKFormLifecycle.Invalid].
     *
     * @return validation result; callers perform side effects on success.
     */
    public fun submit(): SKFormValidationResult {
        ensureActive()
        val result = validate()
        if (!result.isValid) {
            setLifecycle(SKFormLifecycle.Invalid)
            return result
        }
        _state.update {
            it.copy(
                lifecycle = SKFormLifecycle.Submitting,
                submitting = true,
                isValid = true,
            )
        }
        // Synchronous submit completion — async submit hooks can wrap this later.
        _state.update {
            it.copy(
                lifecycle = SKFormLifecycle.Submitted,
                submitting = false,
                isDirty = false,
            )
        }
        return result
    }

    /** Resets all fields to initial values and clears errors. */
    public fun reset() {
        ensureActive()
        registry.all().forEach { field ->
            registry.updateState(field.id) {
                it.copy(
                    value = field.initialValue,
                    displayValue = formatters.format(field.formatter, field.initialValue),
                    touched = false,
                    focused = false,
                )
            }
        }
        errors.clearAll()
        focus.clearFocus()
        _state.value = SKFormState(
            lifecycle = SKFormLifecycle.Ready,
            isDirty = false,
            isValid = null,
            submitting = false,
            fieldCount = registry.all().size,
            errorCount = 0,
        )
    }

    /** Current values map. */
    public fun values(): Map<String, Any?> =
        registry.allStates().mapValues { it.value.value }

    /** Disposes the controller. */
    public fun dispose() {
        if (disposed) return
        disposed = true
        registry.clear()
        errors.clearAll()
        focus.clearFocus()
        _state.value = _state.value.copy(lifecycle = SKFormLifecycle.Disposed)
    }

    private fun markDirty() {
        _state.update {
            it.copy(
                lifecycle = if (it.lifecycle == SKFormLifecycle.Submitted) {
                    SKFormLifecycle.Dirty
                } else {
                    SKFormLifecycle.Dirty
                },
                isDirty = true,
                fieldCount = registry.all().size,
            )
        }
    }

    private fun publishReadyOrDirty() {
        val dirty = registry.allStates().any { (id, state) ->
            val field = registry.get(id) ?: return@any false
            state.value != field.initialValue
        }
        _state.update {
            it.copy(
                lifecycle = when {
                    dirty -> SKFormLifecycle.Dirty
                    it.lifecycle == SKFormLifecycle.Created || it.lifecycle == SKFormLifecycle.Ready ->
                        SKFormLifecycle.Ready
                    else -> it.lifecycle
                },
                isDirty = dirty,
                fieldCount = registry.all().size,
                errorCount = errors.errors.value.size,
            )
        }
    }

    private fun refreshValidity(result: SKFormValidationResult? = null) {
        val valid = result?.isValid ?: !errors.hasErrors()
        _state.update {
            it.copy(
                lifecycle = if (valid) {
                    if (it.isDirty) SKFormLifecycle.Dirty else SKFormLifecycle.Ready
                } else {
                    SKFormLifecycle.Invalid
                },
                isValid = valid,
                errorCount = errors.errors.value.size,
                fieldCount = registry.all().size,
            )
        }
    }

    private fun setLifecycle(lifecycle: SKFormLifecycle) {
        _state.update { it.copy(lifecycle = lifecycle) }
    }

    private fun ensureActive() {
        check(!disposed && _state.value.lifecycle != SKFormLifecycle.Disposed) {
            "SKFormController is disposed"
        }
    }

    public companion object {
        /** Creates a controller with default engines. */
        @JvmStatic
        @JvmOverloads
        public fun create(
            registry: SKFieldRegistry = SKDefaultFieldRegistry(),
            validation: SKValidationEngine = SKDefaultValidationEngine(),
            formatters: SKFormatterEngine = SKDefaultFormatterEngine(),
            masks: SKInputMaskEngine = SKDefaultInputMaskEngine(),
            focus: SKFocusChain = SKDefaultFocusChain(),
            errors: SKFormErrorManager = SKDefaultFormErrorManager(),
            ai: SKFormAIHooks = SKDefaultFormAIHooks(),
        ): SKFormController = SKFormController(
            registry = registry,
            validation = validation,
            formatters = formatters,
            masks = masks,
            focus = focus,
            errors = errors,
            ai = ai,
        ).also {
            it._state.value = SKFormState(lifecycle = SKFormLifecycle.Ready)
        }
    }
}
