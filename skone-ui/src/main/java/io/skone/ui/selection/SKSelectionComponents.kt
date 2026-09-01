@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.selection

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.state.SKComponentState
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

/**
 * Shared radio button contract (Compose + XML).
 *
 * Selection within a group is coordinated by [SKRadioGroupController] or host state.
 *
 * @see docs/WIDGETS_SKRADIOBUTTON.md
 */
public class SKRadioButtonComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Toggle,
        behavior = SKBehaviorConfig.Default,
    ),
    value: String,
    label: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val valueRef = AtomicReference(value)
    private val labelRef = AtomicReference(label)

    public val value: String
        get() = valueRef.get()

    public val label: String?
        get() = labelRef.get()

    public val selected: Boolean
        get() = config.state.selected

    public val interactive: Boolean
        get() = config.enabled

    public fun setLabel(value: String?) {
        labelRef.set(value)
    }

    public fun setValue(value: String) {
        valueRef.set(value)
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun setSelected(value: Boolean) {
        updateConfig(config.copy(state = config.state.copy(selected = value)))
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKRadioButton"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            value: String,
            selected: Boolean = false,
            label: String? = null,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKRadioButtonComponent = SKRadioButtonComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled, selected = selected),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            value = value,
            label = label,
        )
    }
}

/**
 * Minimal single-selection controller for radio groups.
 *
 * Not a generic selection framework — only coordinates one selected [String] value.
 *
 * @see docs/WIDGETS_SKRADIOGROUP.md
 */
public class SKRadioGroupController(
    initialSelected: String? = null,
) {
    private val selectedRef = AtomicReference(initialSelected)
    private val listeners = CopyOnWriteArrayList<(String?) -> Unit>()

    public val selected: String?
        get() = selectedRef.get()

    public fun select(value: String) {
        if (selectedRef.get() == value) return
        selectedRef.set(value)
        listeners.forEach { it(value) }
    }

    public fun clear() {
        if (selectedRef.get() == null) return
        selectedRef.set(null)
        listeners.forEach { it(null) }
    }

    public fun addListener(listener: (String?) -> Unit): () -> Unit {
        listeners += listener
        return { listeners.remove(listener) }
    }

    public companion object {
        @JvmStatic
        @JvmOverloads
        public fun create(initialSelected: String? = null): SKRadioGroupController =
            SKRadioGroupController(initialSelected)
    }
}

/**
 * Foundational selectable chip contract (Compose + XML).
 *
 * Specialized Material chip variants (filter/input/assist/suggestion) are deferred.
 *
 * @see docs/WIDGETS_SKCHIP.md
 */
public class SKChipComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Chip,
        behavior = SKBehaviorConfig.Default,
    ),
    label: String,
    leadingIcon: SKIconKey? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val labelRef = AtomicReference(label)
    private val leadingIconRef = AtomicReference(leadingIcon)

    public val label: String
        get() = labelRef.get()

    public val leadingIcon: SKIconKey?
        get() = leadingIconRef.get()

    public val selected: Boolean
        get() = config.state.selected

    public val interactive: Boolean
        get() = config.enabled

    public fun setLabel(value: String) {
        labelRef.set(value)
    }

    public fun setLeadingIcon(value: SKIconKey?) {
        leadingIconRef.set(value)
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun setSelected(value: Boolean) {
        updateConfig(config.copy(state = config.state.copy(selected = value)))
    }

    override fun performClick() {
        if (!interactive) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKChip"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            label: String,
            selected: Boolean = false,
            leadingIcon: SKIconKey? = null,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig? = null,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKChipComponent {
            val resolvedAppearance = appearance
                ?: if (selected) SKAppearanceConfig.ChipSelected else SKAppearanceConfig.Chip
            return SKChipComponent(
                id = id,
                config = SKComponentConfig(
                    state = SKComponentState(enabled = enabled, selected = selected),
                    appearance = resolvedAppearance,
                    behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                    accessibility = accessibility,
                    analytics = analytics,
                    ai = ai,
                ),
                label = label,
                leadingIcon = leadingIcon,
            )
        }
    }
}
