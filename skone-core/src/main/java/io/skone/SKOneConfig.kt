package io.skone

import io.skone.ai.SKAIConfig
import io.skone.common.log.SKDefaultLogger
import io.skone.common.log.SKLogger
import io.skone.plugin.SKPlugin

/**
 * Configuration supplied to [SKOne.initialize].
 *
 * @property logger Logger implementation for the SDK process.
 * @property plugins Plugins registered during initialization.
 * @property ai Provider-agnostic AI configuration.
 */
public data class SKOneConfig(
    public val logger: SKLogger = SKDefaultLogger,
    public val plugins: List<SKPlugin> = emptyList(),
    public val ai: SKAIConfig = SKAIConfig.Disabled,
)
