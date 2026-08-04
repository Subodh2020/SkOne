package io.skone.plugin

/**
 * Extension point for SKOne capabilities (camera, AI, analytics, …).
 *
 * Plugins are registered explicitly via [SKPluginRegistry] or
 * [io.skone.SKOneConfig.plugins]. No classpath scanning is performed.
 *
 * @see docs/adr/0002-plugin-architecture.md
 */
public interface SKPlugin {
    /** Stable unique plugin identifier, e.g. `io.skone.plugin.demo`. */
    public val id: String

    /** Semantic version of this plugin implementation. */
    public val version: String

    /**
     * Called when the plugin is attached to the SDK.
     *
     * @param context Access to logger and typed dependencies.
     */
    public fun onAttach(context: SKPluginContext)

    /**
     * Called when the plugin is detached / unregistered.
     * Release resources here.
     */
    public fun onDetach()
}
