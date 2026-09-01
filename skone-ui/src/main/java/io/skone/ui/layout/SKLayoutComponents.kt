@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.layout

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.theme.state.SKComponentState
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Divider orientation.
 */
public enum class SKDividerOrientation {
    Horizontal,
    Vertical,
}

/**
 * Lightweight decorative separator contract (Compose + XML).
 *
 * @see docs/WIDGETS_SKDIVIDER.md
 */
public class SKDividerComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Divider,
        behavior = SKBehaviorConfig.Passive,
    ),
    orientation: SKDividerOrientation = SKDividerOrientation.Horizontal,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val orientationRef = AtomicReference(orientation)

    public val orientation: SKDividerOrientation
        get() = orientationRef.get()

    public fun setOrientation(value: SKDividerOrientation) {
        orientationRef.set(value)
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKDivider"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            orientation: SKDividerOrientation = SKDividerOrientation.Horizontal,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Divider,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKDividerComponent = SKDividerComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            orientation = orientation,
        )
    }
}

/**
 * Foundational surface/container contract (Compose + XML).
 *
 * Optional click via [clickable]. Specialized Material card variants deferred.
 *
 * @see docs/WIDGETS_SKCARD.md
 */
public class SKCardComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Card,
        behavior = SKBehaviorConfig.Passive,
    ),
    clickable: Boolean = false,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val clickableRef = AtomicBoolean(clickable)

    public val clickable: Boolean
        get() = clickableRef.get()

    public val interactive: Boolean
        get() = config.enabled && clickable

    public fun setClickable(value: Boolean) {
        clickableRef.set(value)
        updateConfig(
            config.copy(
                behavior = config.behavior.copy(
                    enabled = config.enabled,
                    clickable = value,
                ),
            ),
        )
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value, clickable = clickable),
            ),
        )
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKCard"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            clickable: Boolean = false,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Card,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKCardComponent = SKCardComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled),
                appearance = appearance,
                behavior = if (clickable) {
                    SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true)
                } else {
                    SKBehaviorConfig.Passive.copy(enabled = enabled)
                },
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            clickable = clickable,
        )
    }
}
