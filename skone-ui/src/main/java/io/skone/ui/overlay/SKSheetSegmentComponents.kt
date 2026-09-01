@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.overlay

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
 * Host-controlled bottom sheet contract. No sheet manager / overlay framework.
 *
 * @see docs/WIDGETS_SKBOTTOMSHEET.md
 */
public class SKBottomSheetComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.BottomSheet,
        behavior = SKBehaviorConfig.Passive,
    ),
    title: String? = null,
    primaryActionLabel: String? = null,
    secondaryActionLabel: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val titleRef = AtomicReference(title)
    private val primaryRef = AtomicReference(primaryActionLabel)
    private val secondaryRef = AtomicReference(secondaryActionLabel)
    private val visibleRef = AtomicBoolean(true)

    public val title: String? get() = titleRef.get()
    public val primaryActionLabel: String? get() = primaryRef.get()
    public val secondaryActionLabel: String? get() = secondaryRef.get()
    public val visible: Boolean get() = visibleRef.get()

    public fun setTitle(value: String?) { titleRef.set(value) }
    public fun setPrimaryActionLabel(value: String?) { primaryRef.set(value) }
    public fun setSecondaryActionLabel(value: String?) { secondaryRef.set(value) }
    public fun setVisible(value: Boolean) { visibleRef.set(value) }

    public fun dismiss() {
        setVisible(false)
    }

    public fun performPrimaryAction() {
        if (primaryActionLabel.isNullOrBlank() || !config.enabled) return
        setVisible(false)
        super.performClick()
    }

    public fun performSecondaryAction() {
        if (secondaryActionLabel.isNullOrBlank() || !config.enabled) return
        setVisible(false)
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKBottomSheet"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String? = null,
            primaryActionLabel: String? = null,
            secondaryActionLabel: String? = null,
            visible: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.BottomSheet,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKBottomSheetComponent = SKBottomSheetComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            title = title,
            primaryActionLabel = primaryActionLabel,
            secondaryActionLabel = secondaryActionLabel,
        ).also { it.setVisible(visible) }
    }
}

/**
 * Single segment for [SKSegmentedButtonComponent].
 *
 * @see docs/WIDGETS_SKSEGMENTEDBUTTON.md
 */
public data class SKSegmentItem(
    public val id: String,
    public val label: String,
    public val enabled: Boolean = true,
    public val leadingIcon: SKIconKey? = null,
)

/**
 * Exclusive segmented selection (TabRow/NavigationBar-style items list).
 *
 * @see docs/WIDGETS_SKSEGMENTEDBUTTON.md
 */
public class SKSegmentedButtonComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.SegmentedButton,
        behavior = SKBehaviorConfig.Default,
    ),
    items: List<SKSegmentItem>,
    selectedId: String?,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val itemsRef = AtomicReference(items)
    private val selectedRef = AtomicReference(selectedId)
    private val enabledRef = AtomicBoolean(true)

    public val items: List<SKSegmentItem> get() = itemsRef.get()
    public val selectedId: String? get() = selectedRef.get()
    public val interactive: Boolean get() = config.enabled && enabledRef.get()

    public fun setItems(value: List<SKSegmentItem>) { itemsRef.set(value) }
    public fun setSelectedId(value: String?) {
        selectedRef.set(value)
        updateConfig(config.copy(state = config.state.copy(selected = value != null)))
    }

    public fun setEnabled(value: Boolean) {
        enabledRef.set(value)
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun select(id: String) {
        if (!interactive) return
        val item = itemsRef.get().firstOrNull { it.id == id } ?: return
        if (!item.enabled) return
        setSelectedId(id)
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKSegmentedButton"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            items: List<SKSegmentItem>,
            selectedId: String? = items.firstOrNull()?.id,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.SegmentedButton,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKSegmentedButtonComponent = SKSegmentedButtonComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled, selected = selectedId != null),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            items = items,
            selectedId = selectedId,
        ).also { it.setEnabled(enabled) }
    }
}
