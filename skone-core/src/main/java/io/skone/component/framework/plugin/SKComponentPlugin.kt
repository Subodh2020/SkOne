package io.skone.component.framework.plugin

import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.event.SKComponentEvent

/**
 * Plugin hooks for the component framework lifecycle.
 *
 * Distinct from [io.skone.plugin.SKPlugin] (SDK-level plugins). These observe
 * component attach/detach/events inside a [SKComponentRuntime].
 */
public interface SKComponentPlugin {
    /** Called when the plugin is added to a runtime. */
    public fun onRuntimeReady(runtime: SKComponentRuntime) {}

    /** Called when the plugin is removed or the runtime is disposed. */
    public fun onRuntimeDisposed(runtime: SKComponentRuntime) {}

    /** Called after a component attaches. */
    public fun onComponentAttached(runtime: SKComponentRuntime, component: SKComponent) {}

    /** Called after a component detaches. */
    public fun onComponentDetached(runtime: SKComponentRuntime, component: SKComponent) {}

    /** Called for every dispatched component event (optional observation). */
    public fun onComponentEvent(runtime: SKComponentRuntime, event: SKComponentEvent) {}
}
