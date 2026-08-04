package io.skone.component.framework.event

import io.skone.component.framework.SKDisposable
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Typed events emitted by the component framework.
 */
public sealed interface SKComponentEvent {
    public val componentId: String
    public val componentType: String

    public data class Attached(
        override val componentId: String,
        override val componentType: String,
    ) : SKComponentEvent

    public data class Detached(
        override val componentId: String,
        override val componentType: String,
    ) : SKComponentEvent

    public data class Clicked(
        override val componentId: String,
        override val componentType: String,
    ) : SKComponentEvent

    public data class ValueChanged(
        override val componentId: String,
        override val componentType: String,
        public val fromUser: Boolean,
    ) : SKComponentEvent

    public data class SelectionChanged(
        override val componentId: String,
        override val componentType: String,
    ) : SKComponentEvent

    public data class FocusChanged(
        override val componentId: String,
        override val componentType: String,
        public val focused: Boolean,
    ) : SKComponentEvent

    public data class ValidationFinished(
        override val componentId: String,
        override val componentType: String,
        public val valid: Boolean,
    ) : SKComponentEvent

    public data class NavigationRequested(
        override val componentId: String,
        override val componentType: String,
        public val route: String,
    ) : SKComponentEvent

    public data class Custom(
        override val componentId: String,
        override val componentType: String,
        public val name: String,
        public val payload: Map<String, String> = emptyMap(),
    ) : SKComponentEvent
}

/**
 * Subscriber for [SKComponentEvent].
 */
public fun interface SKComponentEventListener {
    public fun onEvent(event: SKComponentEvent)
}

/**
 * Dispatches component events to subscribers.
 */
public interface SKEventDispatcher {
    public fun dispatch(event: SKComponentEvent)

    public fun subscribe(listener: SKComponentEventListener): SKDisposable

    public fun subscribe(
        componentId: String,
        listener: SKComponentEventListener,
    ): SKDisposable
}

/**
 * Default thread-safe [SKEventDispatcher].
 */
public class SKDefaultEventDispatcher : SKEventDispatcher {
    private data class Sub(
        val componentId: String?,
        val listener: SKComponentEventListener,
    )

    private val subs = CopyOnWriteArrayList<Sub>()

    override fun dispatch(event: SKComponentEvent) {
        subs.forEach { sub ->
            if (sub.componentId == null || sub.componentId == event.componentId) {
                sub.listener.onEvent(event)
            }
        }
    }

    override fun subscribe(listener: SKComponentEventListener): SKDisposable {
        val sub = Sub(null, listener)
        subs += sub
        return SKDisposable { subs.remove(sub) }
    }

    override fun subscribe(componentId: String, listener: SKComponentEventListener): SKDisposable {
        val sub = Sub(componentId, listener)
        subs += sub
        return SKDisposable { subs.remove(sub) }
    }
}
