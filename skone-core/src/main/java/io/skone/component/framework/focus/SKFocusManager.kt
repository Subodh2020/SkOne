package io.skone.component.framework.focus

import io.skone.common.annotation.SKInternal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages focus ownership across components in a [io.skone.component.framework.SKComponentRuntime].
 */
public interface SKFocusManager {
    /** Currently focused component id, or `null`. */
    public val focusedId: StateFlow<String?>

    /** Requests focus for [componentId]; clears previous focus. */
    public fun requestFocus(componentId: String)

    /** Clears focus if [componentId] is focused, or clears any focus when `null`. */
    public fun clearFocus(componentId: String? = null)

    /** `true` when [componentId] currently has focus. */
    public fun isFocused(componentId: String): Boolean
}

/**
 * Default in-memory [SKFocusManager].
 *
 * **Internal implementation** — not intended for application use.
 */
@SKInternal
public class SKDefaultFocusManager : SKFocusManager {
    private val _focusedId = MutableStateFlow<String?>(null)
    override val focusedId: StateFlow<String?> = _focusedId.asStateFlow()

    override fun requestFocus(componentId: String) {
        _focusedId.value = componentId
    }

    override fun clearFocus(componentId: String?) {
        if (componentId == null || _focusedId.value == componentId) {
            _focusedId.value = null
        }
    }

    override fun isFocused(componentId: String): Boolean = _focusedId.value == componentId
}
