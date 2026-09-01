@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.layout

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.state.SKComponentState
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * List row contract — headline with optional supporting/leading/trailing content.
 *
 * @see docs/WIDGETS_SKLISTITEM.md
 */
public class SKListItemComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.ListItem,
        behavior = SKBehaviorConfig.Passive,
    ),
    headline: String,
    supportingText: String? = null,
    leadingIcon: SKIconKey? = null,
    trailingText: String? = null,
    clickable: Boolean = false,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val headlineRef = AtomicReference(headline)
    private val supportingRef = AtomicReference(supportingText)
    private val leadingRef = AtomicReference(leadingIcon)
    private val trailingRef = AtomicReference(trailingText)
    private val clickableRef = AtomicBoolean(clickable)

    public val headline: String get() = headlineRef.get()
    public val supportingText: String? get() = supportingRef.get()
    public val leadingIcon: SKIconKey? get() = leadingRef.get()
    public val trailingText: String? get() = trailingRef.get()
    public val clickable: Boolean get() = clickableRef.get()
    public val selected: Boolean get() = config.state.selected
    public val interactive: Boolean get() = config.enabled && clickable

    public fun setHeadline(value: String) { headlineRef.set(value) }
    public fun setSupportingText(value: String?) { supportingRef.set(value) }
    public fun setLeadingIcon(value: SKIconKey?) { leadingRef.set(value) }
    public fun setTrailingText(value: String?) { trailingRef.set(value) }

    public fun setClickable(value: Boolean) {
        clickableRef.set(value)
        updateConfig(config.copy(behavior = config.behavior.copy(enabled = config.enabled, clickable = value)))
    }

    public fun setSelected(value: Boolean) {
        updateConfig(config.copy(state = config.state.copy(selected = value)))
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
        public const val COMPONENT_TYPE: String = "SKListItem"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            headline: String,
            supportingText: String? = null,
            leadingIcon: SKIconKey? = null,
            trailingText: String? = null,
            selected: Boolean = false,
            enabled: Boolean = true,
            clickable: Boolean = false,
            appearance: SKAppearanceConfig = SKAppearanceConfig.ListItem,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKListItemComponent = SKListItemComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled, selected = selected),
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
            headline = headline,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingText = trailingText,
            clickable = clickable,
        )
    }
}

/**
 * Lightweight section title contract.
 *
 * @see docs/WIDGETS_SKSECTIONHEADER.md
 */
public class SKSectionHeaderComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.SectionHeader,
        behavior = SKBehaviorConfig.Passive,
    ),
    title: String,
    supportingText: String? = null,
    actionLabel: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val titleRef = AtomicReference(title)
    private val supportingRef = AtomicReference(supportingText)
    private val actionRef = AtomicReference(actionLabel)

    public val title: String get() = titleRef.get()
    public val supportingText: String? get() = supportingRef.get()
    public val actionLabel: String? get() = actionRef.get()

    public fun setTitle(value: String) { titleRef.set(value) }
    public fun setSupportingText(value: String?) { supportingRef.set(value) }
    public fun setActionLabel(value: String?) { actionRef.set(value) }

    public fun performAction() {
        if (actionLabel.isNullOrBlank() || !config.enabled) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKSectionHeader"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String,
            supportingText: String? = null,
            actionLabel: String? = null,
            appearance: SKAppearanceConfig = SKAppearanceConfig.SectionHeader,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKSectionHeaderComponent = SKSectionHeaderComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            title = title,
            supportingText = supportingText,
            actionLabel = actionLabel,
        )
    }
}

/**
 * Lightweight screen-shell contract. Host supplies top/content/bottom; no navigation framework.
 *
 * @see docs/WIDGETS_SKSCAFFOLD.md
 */
public class SKScaffoldComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Scaffold,
        behavior = SKBehaviorConfig.Passive,
    ),
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    public companion object {
        public const val COMPONENT_TYPE: String = "SKScaffold"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Scaffold,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKScaffoldComponent = SKScaffoldComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
        )
    }
}
