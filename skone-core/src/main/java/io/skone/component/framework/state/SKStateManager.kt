package io.skone.component.framework.state

import io.skone.theme.state.SKComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Observes and updates [SKComponentState] per component id.
 */
public interface SKStateManager {
    /** Returns the current state for [componentId], or [SKComponentState.Default]. */
    public fun getState(componentId: String): SKComponentState

    /** Replaces state for [componentId]. */
    public fun setState(componentId: String, state: SKComponentState)

    /** Updates state via [transform]. */
    public fun updateState(componentId: String, transform: (SKComponentState) -> SKComponentState)

    /** Observes state changes for [componentId]. */
    public fun observe(componentId: String): StateFlow<SKComponentState>

    /** Clears tracked state for [componentId]. */
    public fun clear(componentId: String)
}

/**
 * Default in-memory [SKStateManager].
 */
public class SKDefaultStateManager : SKStateManager {
    private val states = ConcurrentHashMap<String, MutableStateFlow<SKComponentState>>()

    private fun flow(componentId: String): MutableStateFlow<SKComponentState> =
        states.getOrPut(componentId) { MutableStateFlow(SKComponentState.Default) }

    override fun getState(componentId: String): SKComponentState = flow(componentId).value

    override fun setState(componentId: String, state: SKComponentState) {
        flow(componentId).value = state
    }

    override fun updateState(componentId: String, transform: (SKComponentState) -> SKComponentState) {
        val f = flow(componentId)
        f.value = transform(f.value)
    }

    override fun observe(componentId: String): StateFlow<SKComponentState> = flow(componentId).asStateFlow()

    override fun clear(componentId: String) {
        states.remove(componentId)
    }
}
