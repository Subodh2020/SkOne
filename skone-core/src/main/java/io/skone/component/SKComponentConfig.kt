package io.skone.component

import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.validation.SKValidationConfig
import io.skone.theme.state.SKComponentState

/**
 * Aggregate configuration contract for future SKOne UI components.
 *
 * This is **not** a widget. It is the stable parameter bundle every widget will accept.
 * Public widget parameter order (see [docs/SDK_API_GUIDELINES.md](docs/SDK_API_GUIDELINES.md)):
 *
 * 1. modifier / layout
 * 2. state / value
 * 3. callbacks
 * 4. content
 * 5. appearance
 * 6. behavior
 * 7. validation
 * 8. accessibility
 * 9. analytics
 * 10. AI configuration
 *
 * @property state Interaction / visual state flags.
 * @property appearance Token-driven look (size, shape, color roles, type, elevation).
 * @property behavior Interaction behavior.
 * @property validation Validation policy (validators supplied separately by widgets).
 * @property accessibility Accessibility semantics.
 * @property analytics Optional analytics hook configuration.
 * @property ai Optional AI attachment.
 * @property supportingText Helper text shown below the component when applicable.
 */
public data class SKComponentConfig(
    public val state: SKComponentState = SKComponentState.Default,
    public val appearance: SKAppearanceConfig = SKAppearanceConfig.Primary,
    public val behavior: SKBehaviorConfig = SKBehaviorConfig.Default,
    public val validation: SKValidationConfig = SKValidationConfig.None,
    public val accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    public val analytics: SKAnalyticsConfig? = null,
    public val ai: SKAIComponentConfig? = null,
    public val supportingText: String? = null,
) {
    /** Whether the component is enabled (state ∧ behavior). */
    public val enabled: Boolean get() = state.enabled && behavior.enabled

    /** Whether the component is in an error visual state. */
    public val isError: Boolean get() = state.error

    /** Whether the component is read-only. */
    public val readOnly: Boolean get() = state.readOnly

    /** Whether a value is required. */
    public val required: Boolean get() = validation.required

    public companion object {
        /** Neutral defaults. */
        public val Default: SKComponentConfig = SKComponentConfig()
    }
}

/**
 * Analytics hook configuration (provider-agnostic).
 *
 * @property componentName Logical component name for events.
 * @property properties Extra event properties.
 * @property enabled Whether analytics emission is enabled for this instance.
 */
public data class SKAnalyticsConfig(
    public val componentName: String,
    public val properties: Map<String, String> = emptyMap(),
    public val enabled: Boolean = true,
)
