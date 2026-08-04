package io.skone.component.framework.dsl

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.component.validation.SKValidationConfig
import io.skone.theme.state.SKComponentState

/**
 * Fluent builder for [SKComponentConfig] (+ optional layout metadata).
 *
 * This DSL does **not** create widgets — only configuration used by future widgets.
 */
public class SKComponentBuilder {
    public var state: SKComponentState = SKComponentState.Default
    public var appearance: SKAppearanceConfig = SKAppearanceConfig.Primary
    public var behavior: SKBehaviorConfig = SKBehaviorConfig.Default
    public var validation: SKValidationConfig = SKValidationConfig.None
    public var accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None
    public var analytics: SKAnalyticsConfig? = null
    public var ai: SKAIComponentConfig? = null
    public var supportingText: String? = null
    public var layout: SKLayoutSpec = SKLayoutSpec.Wrap

    public fun state(block: SKComponentState.() -> SKComponentState) {
        state = state.block()
    }

    public fun appearance(block: SKAppearanceConfig.() -> SKAppearanceConfig) {
        appearance = appearance.block()
    }

    public fun behavior(block: SKBehaviorConfig.() -> SKBehaviorConfig) {
        behavior = behavior.block()
    }

    public fun validation(block: SKValidationConfig.() -> SKValidationConfig) {
        validation = validation.block()
    }

    public fun accessibility(block: SKAccessibilityConfig.() -> SKAccessibilityConfig) {
        accessibility = accessibility.block()
    }

    public fun buildConfig(): SKComponentConfig = SKComponentConfig(
        state = state,
        appearance = appearance,
        behavior = behavior,
        validation = validation,
        accessibility = accessibility,
        analytics = analytics,
        ai = ai,
        supportingText = supportingText,
    )

    public fun build(): SKComponentSpec = SKComponentSpec(
        config = buildConfig(),
        layout = layout,
    )
}

/**
 * Result of the component DSL: config + layout, still not a widget.
 */
public data class SKComponentSpec(
    public val config: SKComponentConfig,
    public val layout: SKLayoutSpec = SKLayoutSpec.Wrap,
)

/**
 * Builds an [SKComponentSpec] using a fluent DSL.
 *
 * ### Example
 * ```kotlin
 * val spec = skComponent {
 *     appearance = SKAppearanceConfig.Primary.copy(size = SKSize.Large)
 *     validation = SKValidationConfig.Required
 *     accessibility = SKAccessibilityConfig(contentDescription = "Submit")
 *     layout = SKLayoutSpec.FillWidth
 * }
 * ```
 */
public fun skComponent(block: SKComponentBuilder.() -> Unit): SKComponentSpec =
    SKComponentBuilder().apply(block).build()

/**
 * Builds only an [SKComponentConfig].
 */
public fun skComponentConfig(block: SKComponentBuilder.() -> Unit): SKComponentConfig =
    SKComponentBuilder().apply(block).buildConfig()
