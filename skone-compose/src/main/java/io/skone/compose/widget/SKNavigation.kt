@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
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
import io.skone.ui.navigation.SKNavigationBarComponent
import io.skone.ui.navigation.SKNavigationItem
import io.skone.ui.navigation.SKTopAppBarComponent
import java.util.UUID

/**
 * Top app bar with title and optional navigation/action icons.
 *
 * Icons require explicit contentDescription (never raw key).
 *
 * @see docs/WIDGETS_SKTOPAPPBAR.md
 */
@Composable
public fun SKTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: SKIconKey? = null,
    onNavigationClick: (() -> Unit)? = null,
    actionIcon: SKIconKey? = null,
    onActionClick: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.TopAppBar,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    require(navigationIcon == null || !navigationIcon.contentDescription.isNullOrBlank()) {
        "SKTopAppBar navigationIcon requires an explicit contentDescription"
    }
    require(actionIcon == null || !actionIcon.contentDescription.isNullOrBlank()) {
        "SKTopAppBar actionIcon requires an explicit contentDescription"
    }
    val id = componentId ?: remember { "sktopappbar-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKTopAppBarComponent.create(
            id = id,
            title = title,
            navigationIcon = navigationIcon,
            actionIcon = actionIcon,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(title) { component.setTitle(title) }
    LaunchedEffect(navigationIcon) { component.setNavigationIcon(navigationIcon) }
    LaunchedEffect(actionIcon) { component.setActionIcon(actionIcon) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    Row(
        modifier = modifier
            .skLayout(layout)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .background(look.containerColor)
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it } ?: run {
                    contentDescription = title
                }
                accessibility.testTag?.let { testTag = it }
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            }
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding)
            .height(look.height),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (navigationIcon != null && onNavigationClick != null) {
            AppBarIconButton(
                icon = navigationIcon,
                onClick = onNavigationClick,
                contentColor = look.contentColor,
                testTag = accessibility.testTag?.let { "${it}_nav" },
            )
            Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
        }
        BasicText(
            text = title,
            modifier = Modifier.weight(1f),
            style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleLarge)
                .toTextStyle()
                .copy(color = look.contentColor),
            maxLines = 1,
        )
        if (actionIcon != null && onActionClick != null) {
            Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
            AppBarIconButton(
                icon = actionIcon,
                onClick = onActionClick,
                contentColor = look.contentColor,
                testTag = accessibility.testTag?.let { "${it}_action" },
            )
        }
    }
}

@Composable
private fun AppBarIconButton(
    icon: SKIconKey,
    onClick: () -> Unit,
    contentColor: androidx.compose.ui.graphics.Color,
    testTag: String?,
) {
    val theme = skTheme
    val interaction = remember { MutableInteractionSource() }
    val cd = icon.contentDescription.orEmpty()
    BasicText(
        text = "☰",
        modifier = Modifier
            .size(theme.tokens.spacing.xl.toDp())
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = cd
                role = Role.Button
                testTag?.let { this.testTag = it }
            },
        style = theme.tokens.typography.scale(SKTypographyRole.TitleMedium)
            .toTextStyle()
            .copy(color = contentColor),
    )
}

/**
 * Primary navigation bar with exclusive item selection.
 *
 * @see docs/WIDGETS_SKNAVIGATIONBAR.md
 */
@Composable
public fun SKNavigationBar(
    modifier: Modifier = Modifier,
    items: List<SKNavigationItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.NavigationBar,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "sknavbar-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKNavigationBarComponent.create(
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
    val alpha = if (enabled) 1f else 0.38f
    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .background(look.containerColor)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(theme.tokens.spacing.xxs.toDp(), look.outlineColor)
                } else Modifier,
            )
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it }
                accessibility.testTag?.let { testTag = it }
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            }
            .padding(vertical = theme.tokens.spacing.xs.toDp())
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = item.id == selectedId
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
                        enabled = enabled,
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
                        testTag = accessibility.testTag?.let { "${it}_${item.id}" } ?: "nav_${item.id}"
                        if (!enabled) disabled()
                    }
                    .padding(vertical = theme.tokens.spacing.xs.toDp()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BasicText(
                    text = item.icon?.let { "•" } ?: "•",
                    style = theme.tokens.typography.scale(SKTypographyRole.TitleMedium)
                        .toTextStyle()
                        .copy(color = color),
                )
                BasicText(
                    text = item.label,
                    style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelMedium)
                        .toTextStyle()
                        .copy(color = color),
                    maxLines = 1,
                )
            }
        }
    }
}
