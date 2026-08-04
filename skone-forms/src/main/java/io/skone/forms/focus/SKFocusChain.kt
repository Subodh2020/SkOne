package io.skone.forms.focus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Manages keyboard / programmatic focus order across form fields.
 */
public interface SKFocusChain {
    /** Ordered field ids. */
    public val order: List<String>

    /** Currently focused field id. */
    public val focusedId: StateFlow<String?>

    public fun setOrder(fieldIds: List<String>)

    public fun append(fieldId: String)

    public fun remove(fieldId: String)

    public fun requestFocus(fieldId: String)

    public fun clearFocus()

    /** Moves focus to the next field after [fieldId]; returns the new id or `null`. */
    public fun focusNext(fieldId: String): String?

    /** Moves focus to the previous field before [fieldId]; returns the new id or `null`. */
    public fun focusPrevious(fieldId: String): String?

    public fun first(): String?

    public fun last(): String?
}

/**
 * Default [SKFocusChain].
 */
public class SKDefaultFocusChain : SKFocusChain {
    private val _order = CopyOnWriteArrayList<String>()
    private val _focusedId = MutableStateFlow<String?>(null)

    override val order: List<String>
        get() = _order.toList()

    override val focusedId: StateFlow<String?> = _focusedId.asStateFlow()

    override fun setOrder(fieldIds: List<String>) {
        _order.clear()
        _order.addAll(fieldIds.distinct())
    }

    override fun append(fieldId: String) {
        if (!_order.contains(fieldId)) _order += fieldId
    }

    override fun remove(fieldId: String) {
        _order.remove(fieldId)
        if (_focusedId.value == fieldId) _focusedId.value = null
    }

    override fun requestFocus(fieldId: String) {
        _focusedId.value = fieldId
    }

    override fun clearFocus() {
        _focusedId.value = null
    }

    override fun focusNext(fieldId: String): String? {
        val index = _order.indexOf(fieldId)
        if (index < 0 || index >= _order.lastIndex) {
            _focusedId.value = null
            return null
        }
        val next = _order[index + 1]
        _focusedId.value = next
        return next
    }

    override fun focusPrevious(fieldId: String): String? {
        val index = _order.indexOf(fieldId)
        if (index <= 0) {
            _focusedId.value = null
            return null
        }
        val prev = _order[index - 1]
        _focusedId.value = prev
        return prev
    }

    override fun first(): String? = _order.firstOrNull()

    override fun last(): String? = _order.lastOrNull()
}
