@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.button

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.state.SKComponentState
import java.util.concurrent.atomic.AtomicReference

/**
 * Shared SKButton contract used by Compose [io.skone.compose.widget.SKButton] and
 * XML [io.skone.xml.widget.SKButtonView].
 *
 * Contains **no UI**. Owns label, optional leading icon, enabled/loading state, and click lifecycle.
 *
 * @see docs/WIDGETS_SKBUTTON.md
 */
public class SKButtonComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Button,
        behavior = SKBehaviorConfig.Default,
    ),
    text: String = "",
    leadingIcon: SKIconKey? = null,
) : SKBaseInteractiveComponent(
    id = id,
    componentType = COMPONENT_TYPE,
    config = config,
) {
    private val textRef = AtomicReference(text)
    private val leadingIconRef = AtomicReference(leadingIcon)

    /** Button label. */
    public val text: String
        get() = textRef.get()

    /** Optional leading icon key (decorative unless [SKIconKey.contentDescription] is set). */
    public val leadingIcon: SKIconKey?
        get() = leadingIconRef.get()

    /** Whether the button is currently loading. */
    public val loading: Boolean
        get() = config.state.loading

    /** Whether the button accepts clicks (`enabled` ∧ !`loading`). */
    public val interactive: Boolean
        get() = config.enabled && !loading

    public fun setText(value: String) {
        textRef.set(value)
    }

    public fun setLeadingIcon(value: SKIconKey?) {
        leadingIconRef.set(value)
    }

    /**
     * Updates enabled flag on [SKComponentState] while preserving other state fields.
     */
    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    /**
     * Updates loading flag. Loading buttons do not accept clicks.
     */
    public fun setLoading(value: Boolean) {
        updateConfig(config.copy(state = config.state.copy(loading = value)))
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKButton"

        /**
         * Factory aligning with API guideline parameter groups.
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            text: String = "",
            leadingIcon: SKIconKey? = null,
            enabled: Boolean = true,
            loading: Boolean = false,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Button,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKButtonComponent = SKButtonComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled, loading = loading),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}
