@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
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
import io.skone.ui.overlay.SKBottomAppBarComponent
import io.skone.ui.overlay.SKDropdownMenuComponent
import io.skone.ui.overlay.SKMenuComponent
import io.skone.ui.overlay.SKMenuItem
import io.skone.ui.overlay.SKTooltipComponent
import java.util.UUID

/**
 * Menu surface — list of actionable items. Host owns visibility / placement.
 *
 * @see docs/WIDGETS_SKMENU.md
 */
@Composable
public fun SKMenu(
    modifier: Modifier = Modifier,
    items: List<SKMenuItem>,
    onItemClick: (String) -> Unit,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Menu,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skmenu-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKMenuComponent.create(
            id = id,
            items = items,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(items) { component.setItems(items) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    Column(
        modifier = modifier
            .skLayout(layout)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .background(look.containerColor, look.shape)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(theme.tokens.spacing.xxs.toDp(), look.outlineColor, look.shape)
                } else Modifier,
            )
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it } ?: run {
                    contentDescription = "Menu"
                }
                accessibility.testTag?.let { testTag = it }
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            }
            .padding(vertical = theme.tokens.spacing.xs.toDp())
            .defaultMinSize(minWidth = 160.dp),
    ) {
        items.forEach { item ->
            MenuItemRow(
                item = item,
                contentColor = look.contentColor,
                typographyRole = appearance.typographyRole ?: SKTypographyRole.BodyLarge,
                testTag = accessibility.testTag?.let { "${it}_${item.id}" } ?: "menu_${item.id}",
                selected = false,
                onClick = {
                    component.activate(item.id)
                    onItemClick(item.id)
                },
            )
        }
    }
}

/**
 * Dropdown menu popup. Host owns [expanded]; dismiss via [onDismissRequest].
 * Uses Compose [Popup] — not a generic overlay manager.
 *
 * @see docs/WIDGETS_SKDROPDOWNMENU.md
 */
@Composable
public fun SKDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<SKMenuItem>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedId: String? = null,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.DropdownMenu,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    if (!expanded || !enabled) return
    val id = componentId ?: remember { "skdropdown-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKDropdownMenuComponent.create(
            id = id,
            items = items,
            expanded = expanded,
            selectedId = selectedId,
            enabled = enabled,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(items) { component.setItems(items) }
    LaunchedEffect(expanded) { component.setExpanded(expanded) }
    LaunchedEffect(selectedId) { component.setSelectedId(selectedId) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val outlineWidth = theme.tokens.spacing.xxs.toDp()
    Popup(
        onDismissRequest = {
            component.dismiss()
            onDismissRequest()
        },
        properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Column(
            modifier = modifier
                .shadow(look.elevation ?: 0.dp, look.shape)
                .background(look.containerColor, look.shape)
                .then(
                    if (look.outlineColor != null) {
                        Modifier.border(outlineWidth, look.outlineColor, look.shape)
                    } else Modifier,
                )
                .semantics {
                    accessibility.contentDescription?.let { contentDescription = it } ?: run {
                        contentDescription = "Dropdown menu"
                    }
                    accessibility.testTag?.let { testTag = it }
                    role = Role.DropdownList
                    applyOptionalAccessibility(
                        accessibility.copy(contentDescription = null, testTag = null, role = null),
                    )
                }
                .padding(vertical = theme.tokens.spacing.xs.toDp())
                .defaultMinSize(minWidth = 160.dp),
        ) {
            items.forEach { item ->
                MenuItemRow(
                    item = item,
                    contentColor = look.contentColor,
                    typographyRole = appearance.typographyRole ?: SKTypographyRole.BodyLarge,
                    testTag = accessibility.testTag?.let { "${it}_${item.id}" } ?: "dropdown_${item.id}",
                    selected = item.id == selectedId,
                    onClick = {
                        component.select(item.id)
                        onItemClick(item.id)
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    item: SKMenuItem,
    contentColor: androidx.compose.ui.graphics.Color,
    typographyRole: SKTypographyRole,
    testTag: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val theme = skTheme
    val interaction = remember(item.id) { MutableInteractionSource() }
    val alpha = if (item.enabled) 1f else DisabledAlpha
    val color = if (selected) {
        theme.tokens.colors.color(SKColorRole.Primary).toColor()
    } else {
        contentColor
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable(
                enabled = item.enabled,
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = item.label
                role = Role.Button
                this.selected = selected
                if (!item.enabled) {
                    stateDescription = "Disabled"
                    disabled()
                } else if (selected) {
                    stateDescription = "Selected"
                }
                this.testTag = testTag
            }
            .padding(
                horizontal = theme.tokens.spacing.md.toDp(),
                vertical = theme.tokens.spacing.sm.toDp(),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.leadingIcon?.let { icon ->
            val explicitCd = icon.contentDescription?.takeIf { it.isNotBlank() }
            BasicText(
                text = "•",
                modifier = if (explicitCd != null) {
                    Modifier.semantics { contentDescription = explicitCd }
                } else {
                    Modifier.clearAndSetSemantics { }
                }.padding(end = theme.tokens.spacing.sm.toDp()),
                style = theme.tokens.typography.scale(SKTypographyRole.BodyLarge)
                    .toTextStyle()
                    .copy(color = color),
            )
        }
        BasicText(
            text = item.label,
            style = theme.tokens.typography.scale(typographyRole)
                .toTextStyle()
                .copy(color = color),
            maxLines = 1,
        )
    }
}

/**
 * Host-controlled tooltip. Prefer attaching meaningful info to the host action;
 * this surface is for short explicit hints without a tooltip manager.
 *
 * @see docs/WIDGETS_SKTOOLTIP.md
 */
@Composable
public fun SKTooltip(
    message: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Tooltip,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    if (!visible || message.isBlank()) return
    val id = componentId ?: remember { "sktooltip-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKTooltipComponent.create(
            id = id,
            message = message,
            visible = visible,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(message) { component.setMessage(message) }
    LaunchedEffect(visible) { component.setVisible(visible) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    // Avoid live-region noise; hosts should prefer action CD when possible.
    BasicText(
        text = message,
        modifier = modifier
            .skLayout(layout)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .background(look.containerColor, look.shape)
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding)
            .semantics {
                contentDescription = accessibility.contentDescription ?: message
                accessibility.testTag?.let { testTag = it }
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            },
        style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodySmall)
            .toTextStyle()
            .copy(color = look.contentColor),
        maxLines = 3,
    )
}

/**
 * Bottom application chrome. Slot-based leading / content / trailing.
 * Optional [floatingActionButton] is a simple layout slot — no scroll-hide / docking animation.
 * Prefer Scaffold FAB when FAB must float over content.
 *
 * @see docs/WIDGETS_SKBOTTOMAPPBAR.md
 */
@Composable
public fun SKBottomAppBar(
    modifier: Modifier = Modifier,
    leading: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable BoxScope.() -> Unit = {},
    appearance: SKAppearanceConfig = SKAppearanceConfig.BottomAppBar,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skbottomappbar-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKBottomAppBarComponent.create(
            id = id,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    Box(
        modifier = modifier
            .skLayout(layout)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .background(look.containerColor)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(theme.tokens.spacing.xxs.toDp(), look.outlineColor)
                } else Modifier,
            )
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it } ?: run {
                    contentDescription = "Bottom app bar"
                }
                accessibility.testTag?.let { testTag = it }
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            }
            .fillMaxWidth()
            .height(look.height.coerceAtLeast(56.dp))
            .padding(horizontal = look.horizontalPadding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp()),
        ) {
            leading()
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
            trailing()
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = theme.tokens.spacing.sm.toDp())) {
            floatingActionButton()
        }
    }
}

private const val DisabledAlpha = 0.38f
