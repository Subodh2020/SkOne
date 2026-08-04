package io.skone.component.ai

import io.skone.ai.SKAICapability

/**
 * When component-level AI features may run.
 */
public enum class SKAITrigger {
    Manual,
    OnFocus,
    OnBlur,
    OnIdle,
    OnSubmit,
}

/**
 * AI configuration attached to a future component instance.
 *
 * Providers are resolved via [io.skone.SKOne]; this config only declares intent.
 * Do not embed vendor SDKs here.
 *
 * @property enabled Whether AI features are enabled for this component.
 * @property providerId Optional provider override; `null` uses SKOne default.
 * @property capabilities Requested capabilities (grammar, suggestions, voice, …).
 * @property triggers When AI may auto-run.
 * @property promptTemplate Optional prompt template; `{value}` replaced by component value.
 * @property metadata Opaque options for providers / analytics.
 */
public data class SKAIComponentConfig(
    public val enabled: Boolean = true,
    public val providerId: String? = null,
    public val capabilities: Set<SKAICapability> = emptySet(),
    public val triggers: Set<SKAITrigger> = setOf(SKAITrigger.Manual),
    public val promptTemplate: String? = null,
    public val metadata: Map<String, String> = emptyMap(),
) {
    public companion object {
        /** AI disabled for the component. */
        public val Disabled: SKAIComponentConfig = SKAIComponentConfig(enabled = false)

        /** Grammar assistance on blur. */
        public val GrammarOnBlur: SKAIComponentConfig = SKAIComponentConfig(
            capabilities = setOf(SKAICapability.Grammar),
            triggers = setOf(SKAITrigger.OnBlur, SKAITrigger.Manual),
        )
    }
}
