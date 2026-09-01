@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.chrome

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
 * Single tab for [SKTabRowComponent].
 *
 * @see docs/WIDGETS_SKTABS.md
 */
public data class SKTabItem(
    public val id: String,
    public val label: String,
    public val icon: SKIconKey? = null,
    public val enabled: Boolean = true,
)

/**
 * Exclusive tab-selection row (NavigationBar-style items list — not a new selection framework).
 *
 * @see docs/WIDGETS_SKTABS.md
 */
public class SKTabRowComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.TabRow,
        behavior = SKBehaviorConfig.Default,
    ),
    items: List<SKTabItem>,
    selectedId: String?,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val itemsRef = AtomicReference(items)
    private val selectedRef = AtomicReference(selectedId)
    private val enabledRef = AtomicBoolean(true)

    public val items: List<SKTabItem> get() = itemsRef.get()
    public val selectedId: String? get() = selectedRef.get()
    public val interactive: Boolean get() = config.enabled && enabledRef.get()

    public fun setItems(value: List<SKTabItem>) { itemsRef.set(value) }
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
        val tab = itemsRef.get().firstOrNull { it.id == id } ?: return
        if (!tab.enabled) return
        setSelectedId(id)
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKTabRow"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            items: List<SKTabItem>,
            selectedId: String? = items.firstOrNull()?.id,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.TabRow,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKTabRowComponent = SKTabRowComponent(
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

/**
 * Compact status / count badge.
 *
 * @see docs/WIDGETS_SKBADGE.md
 */
public class SKBadgeComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Badge,
        behavior = SKBehaviorConfig.Passive,
    ),
    text: String = "",
    visible: Boolean = true,
    dot: Boolean = false,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val textRef = AtomicReference(text)
    private val visibleRef = AtomicBoolean(visible)
    private val dotRef = AtomicBoolean(dot)

    public val text: String get() = textRef.get()
    public val visible: Boolean get() = visibleRef.get()
    public val dot: Boolean get() = dotRef.get()

    /** Meaningful announcement when visible and not a silent decorative dot. */
    public val semanticLabel: String?
        get() {
            if (!visible) return null
            config.accessibility.contentDescription?.takeIf { it.isNotBlank() }?.let { return it }
            if (dot) return null
            return text.takeIf { it.isNotBlank() }
        }

    public fun setText(value: String) { textRef.set(value) }
    public fun setVisible(value: Boolean) { visibleRef.set(value) }
    public fun setDot(value: Boolean) { dotRef.set(value) }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKBadge"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            text: String = "",
            visible: Boolean = true,
            dot: Boolean = false,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Badge,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKBadgeComponent = SKBadgeComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            text = text,
            visible = visible,
            dot = dot,
        )
    }
}

/**
 * Identity / avatar primitive — host supplies image content; initials are the deterministic fallback.
 *
 * @see docs/WIDGETS_SKAVATAR.md
 */
public class SKAvatarComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Avatar,
        behavior = SKBehaviorConfig.Passive,
    ),
    initials: String = "",
    hasImage: Boolean = false,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val initialsRef = AtomicReference(initials)
    private val hasImageRef = AtomicBoolean(hasImage)

    public val initials: String get() = initialsRef.get()
    public val hasImage: Boolean get() = hasImageRef.get()

    public val semanticDescription: String?
        get() = config.accessibility.contentDescription
            ?: initials.takeIf { it.isNotBlank() }

    public fun setInitials(value: String) { initialsRef.set(value) }
    public fun setHasImage(value: Boolean) { hasImageRef.set(value) }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKAvatar"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            initials: String = "",
            hasImage: Boolean = false,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Avatar,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKAvatarComponent = SKAvatarComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            initials = initials,
            hasImage = hasImage,
        )
    }
}
