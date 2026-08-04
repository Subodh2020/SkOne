package io.skone.component.framework

import io.skone.component.SKComponentConfig

/**
 * Root contract for every SKOne UI component (Compose or XML).
 *
 * This is **not** a widget. Concrete widgets implement or wrap this contract
 * and resolve visuals exclusively through [config] + theme tokens.
 *
 * @see docs/COMPONENT_FRAMEWORK.md
 */
public interface SKComponent {
    /** Stable instance id within a [SKComponentRuntime] scope. */
    public val id: String

    /** Logical type name for analytics / tooling, e.g. `SKButton`. */
    public val componentType: String

    /** Current aggregate configuration. */
    public val config: SKComponentConfig

    /** Replaces configuration; implementations should notify [SKStateManager] as needed. */
    public fun updateConfig(config: SKComponentConfig)

    /** Binds this component to a runtime (managers, events, plugins). */
    public fun attach(runtime: SKComponentRuntime)

    /** Unbinds from the runtime and releases resources. */
    public fun detach()

    /** `true` after a successful [attach] and before [detach]. */
    public val isAttached: Boolean
}

/**
 * Interactive component contract (click, long-click, focus).
 */
public interface SKInteractiveComponent : SKComponent {
    /** Primary click / tap. No-op when disabled. */
    public fun performClick()

    /** Long-press. Returns `true` if consumed. */
    public fun performLongClick(): Boolean

    /** Notifies focus change; typically driven by [SKFocusManager]. */
    public fun onFocusChanged(focused: Boolean)

    /** Registers an interaction listener. Returns an unsubscribe handle. */
    public fun addInteractionListener(listener: SKInteractionListener): SKDisposable
}

/**
 * Listener for interactive component events.
 */
public interface SKInteractionListener {
    public fun onClick(component: SKInteractiveComponent) {}
    public fun onLongClick(component: SKInteractiveComponent): Boolean = false
    public fun onFocusChanged(component: SKInteractiveComponent, focused: Boolean) {}
}

/**
 * Value-holding input component (text fields, pickers, …).
 *
 * @param T Value type.
 */
public interface SKInputComponent<T> : SKInteractiveComponent {
    /** Current value. */
    public val value: T

    /**
     * Updates [value].
     *
     * @param fromUser `true` when the change originated from user input.
     */
    public fun setValue(value: T, fromUser: Boolean = true)

    /** Runs registered validators for this component. */
    public fun validate(): io.skone.component.validation.SKValidationResult

    /** Clears validation error state without changing the value. */
    public fun clearValidation()

    public fun addValueListener(listener: SKValueListener<T>): SKDisposable
}

/**
 * Listener for input value changes.
 */
public fun interface SKValueListener<T> {
    public fun onValueChanged(component: SKInputComponent<T>, value: T, fromUser: Boolean)
}

/**
 * Selection component (radio, chips, lists, …).
 *
 * @param T Option type.
 */
public interface SKSelectableComponent<T> : SKInteractiveComponent {
    /** Currently selected option for single-select; `null` if none. */
    public val selected: T?

    /** Selected set for multi-select; empty when none. */
    public val selection: Set<T>

    /** Whether multiple selection is allowed. */
    public val multiSelect: Boolean

    public fun select(option: T)

    public fun deselect(option: T)

    public fun clearSelection()

    public fun addSelectionListener(listener: SKSelectionListener<T>): SKDisposable
}

/**
 * Listener for selection changes.
 */
public fun interface SKSelectionListener<T> {
    public fun onSelectionChanged(
        component: SKSelectableComponent<T>,
        selected: T?,
        selection: Set<T>,
    )
}

/**
 * Navigation-oriented component (tabs, nav items, links, …).
 */
public interface SKNavigationComponent : SKInteractiveComponent {
    /** Destination descriptor; `null` when not navigable. */
    public val destination: SKNavigationDestination?

    /** Requests navigation to [destination]. */
    public fun navigate()

    public fun addNavigationListener(listener: SKNavigationListener): SKDisposable
}

/**
 * Opaque navigation target. Apps / navigation modules interpret [route].
 *
 * @property route Stable route key.
 * @property arguments Optional string arguments.
 */
public data class SKNavigationDestination(
    public val route: String,
    public val arguments: Map<String, String> = emptyMap(),
)

/**
 * Listener for navigation requests.
 */
public fun interface SKNavigationListener {
    public fun onNavigate(component: SKNavigationComponent, destination: SKNavigationDestination)
}

/**
 * Component that can invoke AI through [io.skone.SKOne] using [io.skone.component.ai.SKAIComponentConfig].
 */
public interface SKAIComponent : SKComponent {
    /**
     * Runs AI for this component's current context.
     *
     * Implementations build an [io.skone.ai.SKAIRequest] from config + value.
     */
    public suspend fun runAI(
        promptOverride: String? = null,
    ): io.skone.common.result.SKResult<io.skone.ai.SKAIResponse>
}

/**
 * Handle that cancels a subscription or listener registration.
 */
public fun interface SKDisposable {
    public fun dispose()

    public companion object {
        /** No-op disposable. */
        public val NoOp: SKDisposable = SKDisposable { }
    }
}
