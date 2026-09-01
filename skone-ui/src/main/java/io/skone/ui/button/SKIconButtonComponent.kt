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
 * Icon-only button contract for Compose [io.skone.compose.widget.SKIconButton] and
 * XML [io.skone.xml.widget.SKIconButtonView].
 *
 * Unlike [SKButton], there is no text label — a meaningful accessibility
 * [SKAccessibilityConfig.contentDescription] (or non-blank [SKIconKey.contentDescription])
 * is required for production use.
 *
 * @see docs/WIDGETS_SKICONBUTTON.md
 */
public class SKIconButtonComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.IconButton,
        behavior = SKBehaviorConfig.Default,
    ),
    icon: SKIconKey,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val iconRef = AtomicReference(icon)

    public val icon: SKIconKey
        get() = iconRef.get()

    public val interactive: Boolean
        get() = config.enabled

    /** Resolved semantic description for TalkBack / tests. */
    public val semanticDescription: String?
        get() = config.accessibility.contentDescription
            ?: icon.contentDescription?.takeIf { it.isNotBlank() }

    public fun setIcon(value: SKIconKey) {
        iconRef.set(value)
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKIconButton"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            icon: SKIconKey,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.IconButton,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKIconButtonComponent = SKIconButtonComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            icon = icon,
        )
    }
}
