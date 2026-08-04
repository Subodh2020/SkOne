package io.skone.component.framework.base

import io.skone.SKOne
import io.skone.ai.SKAIRequest
import io.skone.ai.SKAIResponse
import io.skone.common.result.SKResult
import io.skone.component.SKComponentConfig
import io.skone.component.framework.SKAIComponent
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.SKDisposable
import io.skone.component.framework.SKInputComponent
import io.skone.component.framework.SKInteractionListener
import io.skone.component.framework.SKInteractiveComponent
import io.skone.component.framework.SKNavigationComponent
import io.skone.component.framework.SKNavigationDestination
import io.skone.component.framework.SKNavigationListener
import io.skone.component.framework.SKSelectableComponent
import io.skone.component.framework.SKSelectionListener
import io.skone.component.framework.SKValueListener
import io.skone.component.framework.event.SKComponentEvent
import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidator
import io.skone.theme.state.SKComponentState
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Abstract base implementing [SKComponent] lifecycle and runtime wiring.
 *
 * UI bases (Compose/XML) and future widgets extend this — it contains **no UI**.
 */
public abstract class SKBaseComponent(
    id: String,
    componentType: String,
    config: SKComponentConfig = SKComponentConfig.Default,
) : SKComponent {

    override val id: String = id
    override val componentType: String = componentType

    private val configRef = AtomicReference(config)
    private val attached = AtomicBoolean(false)
    private val runtimeRef = AtomicReference<SKComponentRuntime?>(null)

    override val config: SKComponentConfig
        get() = configRef.get()

    override val isAttached: Boolean
        get() = attached.get()

    /** Currently bound runtime, or `null`. */
    protected val runtime: SKComponentRuntime?
        get() = runtimeRef.get()

    override fun updateConfig(config: SKComponentConfig) {
        configRef.set(config)
        runtime?.state?.setState(id, config.state)
        onConfigChanged(config)
    }

    override fun attach(runtime: SKComponentRuntime) {
        if (attached.getAndSet(true)) {
            detach()
            attached.set(true)
        }
        runtimeRef.set(runtime)
        runtime.state.setState(id, config.state)
        runtime.events.dispatch(SKComponentEvent.Attached(id, componentType))
        runtime.analytics.trackEvent(
            SKComponentEvent.Attached(id, componentType),
            config.analytics,
        )
        runtime.notifyAttached(this)
        onAttached(runtime)
    }

    override fun detach() {
        if (!attached.getAndSet(false)) return
        val rt = runtimeRef.getAndSet(null) ?: return
        onDetached(rt)
        rt.events.dispatch(SKComponentEvent.Detached(id, componentType))
        rt.analytics.trackEvent(
            SKComponentEvent.Detached(id, componentType),
            config.analytics,
        )
        rt.notifyDetached(this)
        rt.focus.clearFocus(id)
        rt.state.clear(id)
        rt.validation.unregister(id)
    }

    /** Syncs [SKComponentState] into the state manager and config. */
    protected fun publishState(state: SKComponentState) {
        runtime?.state?.setState(id, state)
        configRef.updateAndGet { it.copy(state = state) }
    }

    protected open fun onAttached(runtime: SKComponentRuntime) {}
    protected open fun onDetached(runtime: SKComponentRuntime) {}
    protected open fun onConfigChanged(config: SKComponentConfig) {}

    protected fun dispatch(event: SKComponentEvent) {
        val rt = runtime ?: return
        rt.events.dispatch(event)
        rt.analytics.trackEvent(event, config.analytics)
        rt.plugins().forEach { it.onComponentEvent(rt, event) }
    }
}

/**
 * Base for interactive components.
 */
public abstract class SKBaseInteractiveComponent(
    id: String,
    componentType: String,
    config: SKComponentConfig = SKComponentConfig.Default,
) : SKBaseComponent(id, componentType, config), SKInteractiveComponent {

    private val listeners = CopyOnWriteArrayList<SKInteractionListener>()

    override fun performClick() {
        if (!config.enabled) return
        listeners.forEach { it.onClick(this) }
        dispatch(SKComponentEvent.Clicked(id, componentType))
    }

    override fun performLongClick(): Boolean {
        if (!config.enabled || !config.behavior.longPressEnabled) return false
        val consumed = listeners.any { it.onLongClick(this) }
        return consumed
    }

    override fun onFocusChanged(focused: Boolean) {
        publishState(config.state.copy(focused = focused))
        listeners.forEach { it.onFocusChanged(this, focused) }
        dispatch(SKComponentEvent.FocusChanged(id, componentType, focused))
        if (focused) {
            runtime?.focus?.requestFocus(id)
        } else {
            runtime?.focus?.clearFocus(id)
        }
    }

    override fun addInteractionListener(listener: SKInteractionListener): SKDisposable {
        listeners += listener
        return SKDisposable { listeners.remove(listener) }
    }
}

/**
 * Base for input components.
 */
public abstract class SKBaseInputComponent<T>(
    id: String,
    componentType: String,
    initialValue: T,
    config: SKComponentConfig = SKComponentConfig.Default,
    private val validators: List<SKValidator<T>> = emptyList(),
) : SKBaseInteractiveComponent(id, componentType, config), SKInputComponent<T> {

    private val valueRef = AtomicReference(initialValue)
    private val valueListeners = CopyOnWriteArrayList<SKValueListener<T>>()

    override val value: T
        get() = valueRef.get()

    override fun onAttached(runtime: SKComponentRuntime) {
        super.onAttached(runtime)
        @Suppress("UNCHECKED_CAST")
        runtime.validation.register(
            componentId = id,
            config = config.validation,
            validators = validators as List<SKValidator<*>>,
        )
    }

    override fun setValue(value: T, fromUser: Boolean) {
        valueRef.set(value)
        valueListeners.forEach { it.onValueChanged(this, value, fromUser) }
        dispatch(SKComponentEvent.ValueChanged(id, componentType, fromUser))
        if (fromUser &&
            io.skone.component.validation.SKValidationTrigger.OnChange in config.validation.triggers
        ) {
            validate()
        }
    }

    override fun validate(): SKValidationResult {
        val result = runtime?.validation?.validate(id, value) ?: SKValidationResult.Valid
        val error = result is SKValidationResult.Invalid
        publishState(config.state.copy(error = error))
        dispatch(SKComponentEvent.ValidationFinished(id, componentType, valid = !error))
        return result
    }

    override fun clearValidation() {
        publishState(config.state.copy(error = false))
    }

    override fun addValueListener(listener: SKValueListener<T>): SKDisposable {
        valueListeners += listener
        return SKDisposable { valueListeners.remove(listener) }
    }
}

/**
 * Base for selectable components.
 */
public abstract class SKBaseSelectableComponent<T>(
    id: String,
    componentType: String,
    config: SKComponentConfig = SKComponentConfig.Default,
    override val multiSelect: Boolean = false,
    initialSelection: Set<T> = emptySet(),
) : SKBaseInteractiveComponent(id, componentType, config), SKSelectableComponent<T> {

    private val selectionRef = AtomicReference(initialSelection)
    private val listeners = CopyOnWriteArrayList<SKSelectionListener<T>>()

    override val selection: Set<T>
        get() = selectionRef.get()

    override val selected: T?
        get() = selectionRef.get().firstOrNull()

    override fun select(option: T) {
        val next = if (multiSelect) selectionRef.get() + option else setOf(option)
        selectionRef.set(next)
        publishState(config.state.copy(selected = next.isNotEmpty()))
        notifySelection()
    }

    override fun deselect(option: T) {
        selectionRef.updateAndGet { it - option }
        publishState(config.state.copy(selected = selectionRef.get().isNotEmpty()))
        notifySelection()
    }

    override fun clearSelection() {
        selectionRef.set(emptySet())
        publishState(config.state.copy(selected = false))
        notifySelection()
    }

    private fun notifySelection() {
        listeners.forEach { it.onSelectionChanged(this, selected, selection) }
        dispatch(SKComponentEvent.SelectionChanged(id, componentType))
    }

    override fun addSelectionListener(listener: SKSelectionListener<T>): SKDisposable {
        listeners += listener
        return SKDisposable { listeners.remove(listener) }
    }
}

/**
 * Base for navigation components.
 */
public abstract class SKBaseNavigationComponent(
    id: String,
    componentType: String,
    config: SKComponentConfig = SKComponentConfig.Default,
    override val destination: SKNavigationDestination? = null,
) : SKBaseInteractiveComponent(id, componentType, config), SKNavigationComponent {

    private val listeners = CopyOnWriteArrayList<SKNavigationListener>()

    override fun navigate() {
        val dest = destination ?: return
        if (!config.enabled) return
        listeners.forEach { it.onNavigate(this, dest) }
        dispatch(SKComponentEvent.NavigationRequested(id, componentType, dest.route))
    }

    override fun performClick() {
        super.performClick()
        navigate()
    }

    override fun addNavigationListener(listener: SKNavigationListener): SKDisposable {
        listeners += listener
        return SKDisposable { listeners.remove(listener) }
    }
}

/**
 * Mixin-style base that adds [SKAIComponent] on top of [SKBaseComponent].
 *
 * Widgets that need AI can extend this or implement [SKAIComponent] separately.
 */
public abstract class SKBaseAIComponent(
    id: String,
    componentType: String,
    config: SKComponentConfig = SKComponentConfig.Default,
) : SKBaseComponent(id, componentType, config), SKAIComponent {

    /**
     * Builds the prompt for AI. Override in widgets to include current value/context.
     */
    protected open fun buildPrompt(promptOverride: String?): String =
        promptOverride
            ?: config.ai?.promptTemplate
            ?: ""

    override suspend fun runAI(promptOverride: String?): SKResult<SKAIResponse> {
        val aiConfig = config.ai
        if (aiConfig == null || !aiConfig.enabled) {
            return SKResult.failure(
                code = io.skone.common.error.SKError.CODE_AI_UNAVAILABLE,
                message = "AI is not configured for component '$id'",
            )
        }
        val request = SKAIRequest(
            prompt = buildPrompt(promptOverride),
            capabilities = aiConfig.capabilities,
            metadata = aiConfig.metadata + mapOf(
                "componentId" to id,
                "componentType" to componentType,
            ),
        )
        return SKOne.aiComplete(request, providerId = aiConfig.providerId)
    }
}
