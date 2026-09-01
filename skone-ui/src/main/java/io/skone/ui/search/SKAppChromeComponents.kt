@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.search

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
 * Search input contract. Host-owned query string — not a second text-input framework.
 *
 * @see docs/WIDGETS_SKSEARCHBAR.md
 */
public class SKSearchBarComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.SearchBar,
        behavior = SKBehaviorConfig.Default,
    ),
    query: String = "",
    placeholder: String = "Search",
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val queryRef = AtomicReference(query)
    private val placeholderRef = AtomicReference(placeholder)
    private val clearVisibleRef = AtomicBoolean(query.isNotEmpty())

    public val query: String get() = queryRef.get()
    public val placeholder: String get() = placeholderRef.get()
    public val interactive: Boolean get() = config.enabled
    public val clearVisible: Boolean get() = clearVisibleRef.get()

    public fun setQuery(value: String) {
        queryRef.set(value)
        clearVisibleRef.set(value.isNotEmpty())
    }

    public fun setPlaceholder(value: String) {
        placeholderRef.set(value)
    }

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun clear() {
        if (!interactive) return
        setQuery("")
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKSearchBar"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            query: String = "",
            placeholder: String = "Search",
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.SearchBar,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKSearchBarComponent = SKSearchBarComponent(
            id = id,
            config = SKComponentConfig(
                state = SKComponentState(enabled = enabled),
                appearance = appearance,
                behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            query = query,
            placeholder = placeholder,
        )
    }
}

/**
 * Empty / zero-results content state.
 *
 * @see docs/WIDGETS_SKEMPTYSTATE.md
 */
public class SKEmptyStateComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.EmptyState,
        behavior = SKBehaviorConfig.Passive,
    ),
    title: String,
    description: String? = null,
    icon: SKIconKey? = null,
    primaryActionLabel: String? = null,
    secondaryActionLabel: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val titleRef = AtomicReference(title)
    private val descriptionRef = AtomicReference(description)
    private val iconRef = AtomicReference(icon)
    private val primaryRef = AtomicReference(primaryActionLabel)
    private val secondaryRef = AtomicReference(secondaryActionLabel)

    public val title: String get() = titleRef.get()
    public val description: String? get() = descriptionRef.get()
    public val icon: SKIconKey? get() = iconRef.get()
    public val primaryActionLabel: String? get() = primaryRef.get()
    public val secondaryActionLabel: String? get() = secondaryRef.get()

    public fun setTitle(value: String) { titleRef.set(value) }
    public fun setDescription(value: String?) { descriptionRef.set(value) }
    public fun setIcon(value: SKIconKey?) { iconRef.set(value) }
    public fun setPrimaryActionLabel(value: String?) { primaryRef.set(value) }
    public fun setSecondaryActionLabel(value: String?) { secondaryRef.set(value) }

    public fun performPrimaryAction() {
        if (primaryActionLabel.isNullOrBlank() || !config.enabled) return
        super.performClick()
    }

    public fun performSecondaryAction() {
        if (secondaryActionLabel.isNullOrBlank() || !config.enabled) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKEmptyState"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String,
            description: String? = null,
            icon: SKIconKey? = null,
            primaryActionLabel: String? = null,
            secondaryActionLabel: String? = null,
            appearance: SKAppearanceConfig = SKAppearanceConfig.EmptyState,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKEmptyStateComponent = SKEmptyStateComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            title = title,
            description = description,
            icon = icon,
            primaryActionLabel = primaryActionLabel,
            secondaryActionLabel = secondaryActionLabel,
        )
    }
}

/**
 * Floating action button contract. Requires an accessible content description.
 *
 * @see docs/WIDGETS_SKFAB.md
 */
public class SKFabComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Fab,
        behavior = SKBehaviorConfig.Default,
    ),
    icon: SKIconKey,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val iconRef = AtomicReference(icon)

    public val icon: SKIconKey get() = iconRef.get()
    public val interactive: Boolean get() = config.enabled

    public val semanticDescription: String?
        get() = config.accessibility.contentDescription
            ?: icon.contentDescription?.takeIf { it.isNotBlank() }

    public fun setIcon(value: SKIconKey) { iconRef.set(value) }

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
        public const val COMPONENT_TYPE: String = "SKFab"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            icon: SKIconKey,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Fab,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKFabComponent = SKFabComponent(
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
