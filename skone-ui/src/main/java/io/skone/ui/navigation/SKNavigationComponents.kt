@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.navigation

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
 * Top app bar contract — title plus optional navigation/action icons.
 *
 * @see docs/WIDGETS_SKTOPAPPBAR.md
 */
public class SKTopAppBarComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.TopAppBar,
        behavior = SKBehaviorConfig.Passive,
    ),
    title: String,
    navigationIcon: SKIconKey? = null,
    actionIcon: SKIconKey? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val titleRef = AtomicReference(title)
    private val navRef = AtomicReference(navigationIcon)
    private val actionRef = AtomicReference(actionIcon)

    public val title: String get() = titleRef.get()
    public val navigationIcon: SKIconKey? get() = navRef.get()
    public val actionIcon: SKIconKey? get() = actionRef.get()

    public fun setTitle(value: String) { titleRef.set(value) }
    public fun setNavigationIcon(value: SKIconKey?) { navRef.set(value) }
    public fun setActionIcon(value: SKIconKey?) { actionRef.set(value) }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKTopAppBar"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String,
            navigationIcon: SKIconKey? = null,
            actionIcon: SKIconKey? = null,
            appearance: SKAppearanceConfig = SKAppearanceConfig.TopAppBar,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKTopAppBarComponent = SKTopAppBarComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            title = title,
            navigationIcon = navigationIcon,
            actionIcon = actionIcon,
        )
    }
}

/**
 * Single navigation destination for [SKNavigationBarComponent].
 */
public data class SKNavigationItem(
    public val id: String,
    public val label: String,
    public val icon: SKIconKey? = null,
)

/**
 * Bottom / primary navigation bar with exclusive selection.
 *
 * @see docs/WIDGETS_SKNAVIGATIONBAR.md
 */
public class SKNavigationBarComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.NavigationBar,
        behavior = SKBehaviorConfig.Default,
    ),
    items: List<SKNavigationItem>,
    selectedId: String?,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val itemsRef = AtomicReference(items)
    private val selectedRef = AtomicReference(selectedId)
    private val enabledRef = AtomicBoolean(true)

    public val items: List<SKNavigationItem> get() = itemsRef.get()
    public val selectedId: String? get() = selectedRef.get()
    public val interactive: Boolean get() = config.enabled && enabledRef.get()

    public fun setItems(value: List<SKNavigationItem>) { itemsRef.set(value) }
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
        setSelectedId(id)
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKNavigationBar"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            items: List<SKNavigationItem>,
            selectedId: String? = items.firstOrNull()?.id,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.NavigationBar,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKNavigationBarComponent = SKNavigationBarComponent(
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
