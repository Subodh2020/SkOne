package io.skone.forms.field

import java.util.concurrent.ConcurrentHashMap

/**
 * Registry of [SKFormField] descriptors and their [SKFieldState].
 */
public interface SKFieldRegistry {
    public fun register(field: SKFormField)

    public fun unregister(fieldId: String)

    public fun get(fieldId: String): SKFormField?

    public fun state(fieldId: String): SKFieldState?

    public fun all(): List<SKFormField>

    public fun allStates(): Map<String, SKFieldState>

    public fun updateState(fieldId: String, transform: (SKFieldState) -> SKFieldState)

    public fun clear()
}

/**
 * Thread-safe in-memory [SKFieldRegistry].
 */
public class SKDefaultFieldRegistry : SKFieldRegistry {
    private val fields = ConcurrentHashMap<String, SKFormField>()
    private val states = ConcurrentHashMap<String, SKFieldState>()

    override fun register(field: SKFormField) {
        fields[field.id] = field
        states[field.id] = SKFieldState(
            field = field,
            value = field.initialValue,
            displayValue = field.formatter?.format(field.initialValue).orEmpty()
                .ifEmpty { field.initialValue?.toString().orEmpty() },
        )
    }

    override fun unregister(fieldId: String) {
        fields.remove(fieldId)
        states.remove(fieldId)
    }

    override fun get(fieldId: String): SKFormField? = fields[fieldId]

    override fun state(fieldId: String): SKFieldState? = states[fieldId]

    override fun all(): List<SKFormField> = fields.values.toList()

    override fun allStates(): Map<String, SKFieldState> = states.toMap()

    override fun updateState(fieldId: String, transform: (SKFieldState) -> SKFieldState) {
        val current = states[fieldId] ?: return
        states[fieldId] = transform(current)
    }

    override fun clear() {
        fields.clear()
        states.clear()
    }
}
