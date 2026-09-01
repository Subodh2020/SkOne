@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.compose.component.LocalSKComponentRuntime
import io.skone.compose.component.SKComponentLifecycle
import io.skone.compose.component.skLayout
import io.skone.compose.theme.resolve
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.compose.theme.toTextStyle
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.chrome.SKAvatarComponent
import io.skone.ui.chrome.SKBadgeComponent
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.chrome.SKTabRowComponent
import java.util.UUID

/**
 * Exclusive tab row. Alias surface name: SKTabs.
 *
 * Mirrors NavigationBar selection (`selectedId` / `onSelect`) — not a new selection framework.
 * No pager, swipe, or animated indicator framework.
 *
 * @see docs/WIDGETS_SKTABS.md
 */
@Composable
public fun SKTabRow(
    modifier: Modifier = Modifier,
    items: List<SKTabItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.TabRow,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "sktabrow-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKTabRowComponent.create(
            id = id,
            items = items,
            selectedId = selectedId,
            enabled = enabled,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(items) { component.setItems(items) }
    LaunchedEffect(selectedId) { component.setSelectedId(selectedId) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    Column(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .background(look.containerColor)
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it }
                accessibility.testTag?.let { testTag = it }
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val selected = item.id == selectedId
                val tabEnabled = enabled && item.enabled
                val interaction = remember(item.id) { MutableInteractionSource() }
                val color = if (selected) {
                    theme.tokens.colors.color(SKColorRole.Primary).toColor()
                } else {
                    look.contentColor
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = tabEnabled,
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Tab,
                            onClick = {
                                component.select(item.id)
                                onSelect(item.id)
                            },
                        )
                        .semantics {
                            contentDescription = item.label
                            role = Role.Tab
                            this.selected = selected
                            stateDescription = if (selected) "Selected" else "Not selected"
                            testTag = accessibility.testTag?.let { "${it}_${item.id}" } ?: "tab_${item.id}"
                            if (!tabEnabled) disabled()
                        }
                        .padding(vertical = theme.tokens.spacing.sm.toDp()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item.icon?.let { icon ->
                        val explicitCd = icon.contentDescription?.takeIf { it.isNotBlank() }
                        BasicText(
                            text = "•",
                            modifier = if (explicitCd != null) {
                                Modifier.semantics { contentDescription = explicitCd }
                            } else {
                                Modifier.clearAndSetSemantics { }
                            },
                            style = theme.tokens.typography.scale(SKTypographyRole.TitleSmall)
                                .toTextStyle()
                                .copy(color = color),
                        )
                    }
                    BasicText(
                        text = item.label,
                        style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelLarge)
                            .toTextStyle()
                            .copy(color = color),
                        maxLines = 1,
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = theme.tokens.spacing.xxs.toDp())
                            .height(2.dp)
                            .width(24.dp)
                            .background(
                                if (selected) theme.tokens.colors.color(SKColorRole.Primary).toColor()
                                else theme.tokens.colors.color(SKColorRole.Primary).toColor().copy(alpha = 0f),
                            ),
                    )
                }
            }
        }
        if (look.outlineColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(theme.tokens.spacing.xxs.toDp())
                    .background(look.outlineColor),
            )
        }
    }
}

/** Product alias for [SKTabRow]. */
@Composable
public fun SKTabs(
    modifier: Modifier = Modifier,
    items: List<SKTabItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.TabRow,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    SKTabRow(
        modifier = modifier,
        items = items,
        selectedId = selectedId,
        onSelect = onSelect,
        enabled = enabled,
        appearance = appearance,
        accessibility = accessibility,
        analytics = analytics,
        ai = ai,
        layout = layout,
        componentId = componentId,
        runtime = runtime,
    )
}

/**
 * Compact status / count badge. Dot-only mode is decorative by default.
 *
 * @see docs/WIDGETS_SKBADGE.md
 */
@Composable
public fun SKBadge(
    modifier: Modifier = Modifier,
    text: String = "",
    visible: Boolean = true,
    dot: Boolean = false,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Badge,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    if (!visible) return
    val id = componentId ?: remember { "skbadge-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKBadgeComponent.create(
            id = id,
            text = text,
            visible = visible,
            dot = dot,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(text) { component.setText(text) }
    LaunchedEffect(visible) { component.setVisible(visible) }
    LaunchedEffect(dot) { component.setDot(dot) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val label = accessibility.contentDescription?.takeIf { it.isNotBlank() }
        ?: if (dot) null else text.takeIf { it.isNotBlank() }
    val size = if (dot) {
        theme.tokens.spacing.sm.toDp()
    } else {
        look.height.coerceAtLeast(theme.tokens.spacing.md.toDp())
    }

    Box(
        modifier = modifier
            .skLayout(layout)
            .defaultMinSize(minWidth = size, minHeight = size)
            .clip(look.shape)
            .background(look.containerColor, look.shape)
            .then(
                if (label != null) {
                    Modifier.semantics {
                        contentDescription = label
                        accessibility.testTag?.let { testTag = it }
                        applyOptionalAccessibility(
                            accessibility.copy(contentDescription = null, testTag = null),
                        )
                    }
                } else {
                    Modifier.clearAndSetSemantics {
                        accessibility.testTag?.let { testTag = it }
                    }
                },
            )
            .padding(
                horizontal = if (dot) 0.dp else look.horizontalPadding / 2,
                vertical = if (dot) 0.dp else look.verticalPadding / 2,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (!dot && text.isNotBlank()) {
            BasicText(
                text = text,
                style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelSmall)
                    .toTextStyle()
                    .copy(color = look.contentColor, textAlign = TextAlign.Center),
                maxLines = 1,
            )
        }
    }
}

/**
 * Identity avatar. Host provides optional [content]; otherwise shows [initials].
 * No image-loading framework — hosts pass Compose content (Image/Icon/etc.).
 *
 * @see docs/WIDGETS_SKAVATAR.md
 */
@Composable
public fun SKAvatar(
    modifier: Modifier = Modifier,
    initials: String = "",
    content: (@Composable () -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Avatar,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skavatar-${UUID.randomUUID()}" }
    val hasImage = content != null
    val component = remember(id) {
        SKAvatarComponent.create(
            id = id,
            initials = initials,
            hasImage = hasImage,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(initials) { component.setInitials(initials) }
    LaunchedEffect(hasImage) { component.setHasImage(hasImage) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val description = accessibility.contentDescription ?: initials.takeIf { it.isNotBlank() }
    val size = look.height.coerceAtLeast(theme.tokens.spacing.xl.toDp() * 1.5f)

    Box(
        modifier = modifier
            .skLayout(layout)
            .size(size)
            .clip(look.shape)
            .background(look.containerColor, look.shape)
            .then(
                if (description != null) {
                    Modifier.semantics {
                        contentDescription = description
                        accessibility.testTag?.let { testTag = it }
                        applyOptionalAccessibility(
                            accessibility.copy(contentDescription = null, testTag = null),
                        )
                    }
                } else {
                    Modifier.clearAndSetSemantics {
                        accessibility.testTag?.let { testTag = it }
                    }
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (content != null) {
            content()
        } else {
            BasicText(
                text = initials.take(2).uppercase().ifBlank { "?" },
                style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleMedium)
                    .toTextStyle()
                    .copy(color = look.contentColor, textAlign = TextAlign.Center),
                maxLines = 1,
            )
        }
    }
}

private const val DisabledAlpha = 0.38f
