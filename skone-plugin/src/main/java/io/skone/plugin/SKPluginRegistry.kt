package io.skone.plugin

import io.skone.common.log.SKLog
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry of attached [SKPlugin] instances.
 *
 * Implementations must be thread-safe.
 */
public interface SKPluginRegistry {
    /**
     * Registers [plugin] and invokes [SKPlugin.onAttach].
     *
     * If a plugin with the same [SKPlugin.id] is already registered, it is
     * detached first and replaced (idempotent replace with warning).
     */
    public fun register(plugin: SKPlugin)

    /**
     * Unregisters the plugin with [pluginId] and invokes [SKPlugin.onDetach].
     *
     * No-op if the id is unknown.
     */
    public fun unregister(pluginId: String)

    /** Returns the plugin for [pluginId], or `null`. */
    public fun get(pluginId: String): SKPlugin?

    /** Snapshot of all registered plugins. */
    public fun all(): List<SKPlugin>
}

/**
 * Thread-safe in-memory [SKPluginRegistry] with no reflection.
 */
public class SKInMemoryPluginRegistry(
    private val contextFactory: () -> SKPluginContext,
) : SKPluginRegistry {

    private val plugins = ConcurrentHashMap<String, SKPlugin>()

    override fun register(plugin: SKPlugin) {
        require(plugin.id.isNotBlank()) { "Plugin id must not be blank" }
        require(plugin.version.isNotBlank()) { "Plugin version must not be blank" }

        val existing = plugins.put(plugin.id, plugin)
        if (existing != null) {
            SKLog.w(TAG, "Replacing plugin '${plugin.id}' (${existing.version} -> ${plugin.version})")
            runCatching { existing.onDetach() }
                .onFailure { SKLog.e(TAG, "Error detaching plugin '${existing.id}'", it) }
        }

        plugin.onAttach(contextFactory())
        SKLog.i(TAG, "Registered plugin '${plugin.id}' v${plugin.version}")
    }

    override fun unregister(pluginId: String) {
        val removed = plugins.remove(pluginId) ?: return
        runCatching { removed.onDetach() }
            .onFailure { SKLog.e(TAG, "Error detaching plugin '$pluginId'", it) }
        SKLog.i(TAG, "Unregistered plugin '$pluginId'")
    }

    override fun get(pluginId: String): SKPlugin? = plugins[pluginId]

    override fun all(): List<SKPlugin> = plugins.values.toList()

    private companion object {
        const val TAG = "SKPluginRegistry"
    }
}
