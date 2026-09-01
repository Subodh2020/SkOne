@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.toggle

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.theme.state.SKComponentState
import java.util.concurrent.atomic.AtomicReference

/**
 * Shared checkbox contract for Compose [io.skone.compose.widget.SKCheckbox] and
 * XML [io.skone.xml.widget.SKCheckboxView].
 *
 * Binary checked state only — indeterminate is deferred.
 *
 * @see docs/WIDGETS_SKCHECKBOX.md
 */
public class SKCheckboxComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Toggle,
        behavior = SKBehaviorConfig.Default,
        state = SKComponentState(checked = false),
    ),
    label: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val labelRef = AtomicReference(label)

    public val label: String?
        get() = labelRef.get()

    public val checked: Boolean
        get() = config.state.checked == true

    public val interactive: Boolean
        get() = config.enabled

    public fun setLabel(value: String?) {
        labelRef.set(value)
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun setChecked(value: Boolean) {
        updateConfig(config.copy(state = config.state.copy(checked = value)))
    }

    public fun toggle() {
        if (!interactive) return
        setChecked(!checked)
        super.performClick()
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKCheckbox"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            checked: Boolean = false,
            label: String? = null,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKCheckboxComponent = SKCheckboxComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled, checked = checked),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            label = label,
        )
    }
}

/**
 * Shared switch contract for Compose [io.skone.compose.widget.SKSwitch] and
 * XML [io.skone.xml.widget.SKSwitchView].
 *
 * @see docs/WIDGETS_SKSWITCH.md
 */
public class SKSwitchComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Toggle,
        behavior = SKBehaviorConfig.Default,
        state = SKComponentState(checked = false),
    ),
    label: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val labelRef = AtomicReference(label)

    public val label: String?
        get() = labelRef.get()

    public val checked: Boolean
        get() = config.state.checked == true

    public val interactive: Boolean
        get() = config.enabled

    public fun setLabel(value: String?) {
        labelRef.set(value)
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun setChecked(value: Boolean) {
        updateConfig(config.copy(state = config.state.copy(checked = value)))
    }

    public fun toggle() {
        if (!interactive) return
        setChecked(!checked)
        super.performClick()
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKSwitch"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            checked: Boolean = false,
            label: String? = null,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKSwitchComponent = SKSwitchComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled, checked = checked),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            label = label,
        )
    }
}
