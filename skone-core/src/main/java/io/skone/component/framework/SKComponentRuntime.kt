package io.skone.component.framework

import io.skone.common.log.SKLogger
import io.skone.component.framework.analytics.SKAnalyticsHook
import io.skone.component.framework.animation.SKAnimationManager
import io.skone.component.framework.event.SKEventDispatcher
import io.skone.component.framework.focus.SKFocusManager
import io.skone.component.framework.icon.SKIconProvider
import io.skone.component.framework.plugin.SKComponentPlugin
import io.skone.component.framework.state.SKStateManager
import io.skone.component.framework.validation.SKValidationManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Shared runtime services available to attached [SKComponent] instances.
 *
 * Create via [SKComponentRuntime.create] or the component DSL.
 */
public class SKComponentRuntime internal constructor(
    public val focus: SKFocusManager,
    public val validation: SKValidationManager,
    public val state: SKStateManager,
    public val events: SKEventDispatcher,
    public val animation: SKAnimationManager,
    public val icons: SKIconProvider,
    public val analytics: SKAnalyticsHook,
    public val logger: SKLogger,
    private val plugins: CopyOnWriteArrayList<SKComponentPlugin>,
) {
    /** Snapshot of registered component plugins. */
    public fun plugins(): List<SKComponentPlugin> = plugins.toList()

    /** Registers a [SKComponentPlugin]. */
    public fun addPlugin(plugin: SKComponentPlugin) {
        plugins += plugin
        plugin.onRuntimeReady(this)
    }

    /** Removes a previously registered plugin. */
    public fun removePlugin(plugin: SKComponentPlugin) {
        if (plugins.remove(plugin)) {
            plugin.onRuntimeDisposed(this)
        }
    }

    internal fun notifyAttached(component: SKComponent) {
        plugins.forEach { it.onComponentAttached(this, component) }
    }

    internal fun notifyDetached(component: SKComponent) {
        plugins.forEach { it.onComponentDetached(this, component) }
    }

    public companion object {
        /**
         * Creates a runtime with default in-memory managers.
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            logger: SKLogger = io.skone.common.log.SKDefaultLogger,
            icons: SKIconProvider = io.skone.component.framework.icon.SKNoOpIconProvider,
            analytics: SKAnalyticsHook = io.skone.component.framework.analytics.SKNoOpAnalyticsHook,
            plugins: List<SKComponentPlugin> = emptyList(),
        ): SKComponentRuntime {
            val runtimePlugins = CopyOnWriteArrayList(plugins)
            return SKComponentRuntime(
                focus = io.skone.component.framework.focus.SKDefaultFocusManager(),
                validation = io.skone.component.framework.validation.SKDefaultValidationManager(),
                state = io.skone.component.framework.state.SKDefaultStateManager(),
                events = io.skone.component.framework.event.SKDefaultEventDispatcher(),
                animation = io.skone.component.framework.animation.SKDefaultAnimationManager(),
                icons = icons,
                analytics = analytics,
                logger = logger,
                plugins = runtimePlugins,
            ).also { rt ->
                runtimePlugins.forEach { it.onRuntimeReady(rt) }
            }
        }
    }
}
