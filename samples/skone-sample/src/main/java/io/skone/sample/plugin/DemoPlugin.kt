package io.skone.sample.plugin

import io.skone.plugin.SKPlugin
import io.skone.plugin.SKPluginContext

/**
 * No-op demo plugin used by the sample app to exercise the plugin registry.
 */
class DemoPlugin : SKPlugin {
    override val id: String = "io.skone.sample.demo"
    override val version: String = "0.1.0"

    override fun onAttach(context: SKPluginContext) {
        context.logger.i(id, "DemoPlugin attached")
    }

    override fun onDetach() {
        // no-op
    }
}
