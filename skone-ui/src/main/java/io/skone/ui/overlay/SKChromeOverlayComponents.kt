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
 * Single menu option for [SKMenuComponent] / [SKDropdownMenuComponent].
 *
 * @see docs/WIDGETS_SKMENU.md
 */
public data class SKMenuItem(
    public val id: String,
    public val label: String,
    public val enabled: Boolean = true,
    public val leadingIcon: SKIconKey? = null,
)

/**
 * Reusable menu surface (list of actionable items). Host owns show/dismiss.
 *
 * @see docs/WIDGETS_SKMENU.md
 */
public class SKMenuComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Menu,
        behavior = SKBehaviorConfig.Passive,
    ),
    items: List<SKMenuItem>,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val itemsRef = AtomicReference(items)
    private val lastActionRef = AtomicReference<String?>(null)

    public val items: List<SKMenuItem> get() = itemsRef.get()
    public val lastActionId: String? get() = lastActionRef.get()

    public fun setItems(value: List<SKMenuItem>) { itemsRef.set(value) }

    public fun activate(itemId: String) {
        val item = itemsRef.get().firstOrNull { it.id == itemId } ?: return
        if (!item.enabled || !config.enabled) return
        lastActionRef.set(itemId)
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKMenu"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            items: List<SKMenuItem>,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Menu,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKMenuComponent = SKMenuComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            items = items,
        )
    }
}

/**
 * Host-controlled dropdown menu (expanded + items + dismiss).
 *
 * @see docs/WIDGETS_SKDROPDOWNMENU.md
 */
public class SKDropdownMenuComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.DropdownMenu,
        behavior = SKBehaviorConfig.Default,
    ),
    items: List<SKMenuItem>,
    expanded: Boolean = false,
    selectedId: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val itemsRef = AtomicReference(items)
    private val expandedRef = AtomicBoolean(expanded)
    private val selectedRef = AtomicReference(selectedId)
    private val enabledRef = AtomicBoolean(true)

    public val items: List<SKMenuItem> get() = itemsRef.get()
    public val expanded: Boolean get() = expandedRef.get()
    public val selectedId: String? get() = selectedRef.get()
    public val interactive: Boolean get() = config.enabled && enabledRef.get()

    public fun setItems(value: List<SKMenuItem>) { itemsRef.set(value) }
    public fun setExpanded(value: Boolean) { expandedRef.set(value) }
    public fun setSelectedId(value: String?) { selectedRef.set(value) }

    public fun setEnabled(value: Boolean) {
        enabledRef.set(value)
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun select(itemId: String) {
        if (!interactive) return
        val item = itemsRef.get().firstOrNull { it.id == itemId } ?: return
        if (!item.enabled) return
        selectedRef.set(itemId)
        expandedRef.set(false)
        super.performClick()
    }

    public fun dismiss() {
        expandedRef.set(false)
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKDropdownMenu"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            items: List<SKMenuItem>,
            expanded: Boolean = false,
            selectedId: String? = null,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.DropdownMenu,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKDropdownMenuComponent = SKDropdownMenuComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            items = items,
            expanded = expanded,
            selectedId = selectedId,
        ).also { it.setEnabled(enabled) }
    }
}

/**
 * Host-controlled tooltip message.
 *
 * @see docs/WIDGETS_SKTOOLTIP.md
 */
public class SKTooltipComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Tooltip,
        behavior = SKBehaviorConfig.Passive,
    ),
    message: String,
    visible: Boolean = false,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val messageRef = AtomicReference(message)
    private val visibleRef = AtomicBoolean(visible)

    public val message: String get() = messageRef.get()
    public val visible: Boolean get() = visibleRef.get()

    public fun setMessage(value: String) { messageRef.set(value) }
    public fun setVisible(value: Boolean) { visibleRef.set(value) }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKTooltip"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            message: String,
            visible: Boolean = false,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Tooltip,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKTooltipComponent = SKTooltipComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            message = message,
            visible = visible,
        ).also { it.setVisible(visible) }
    }
}

/**
 * Bottom application chrome bar.
 *
 * @see docs/WIDGETS_SKBOTTOMAPPBAR.md
 */
public class SKBottomAppBarComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.BottomAppBar,
        behavior = SKBehaviorConfig.Passive,
    ),
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    public companion object {
        public const val COMPONENT_TYPE: String = "SKBottomAppBar"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            appearance: SKAppearanceConfig = SKAppearanceConfig.BottomAppBar,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKBottomAppBarComponent = SKBottomAppBarComponent(
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
