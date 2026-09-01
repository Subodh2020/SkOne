@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
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
import io.skone.ui.layout.SKListItemComponent
import io.skone.ui.layout.SKScaffoldComponent
import io.skone.ui.layout.SKSectionHeaderComponent
import java.util.UUID

/**
 * Practical list row. Leading icon is decorative unless [SKIconKey.contentDescription] is set.
 *
 * @see docs/WIDGETS_SKLISTITEM.md
 */
@Composable
public fun SKListItem(
    modifier: Modifier = Modifier,
    headline: String,
    supportingText: String? = null,
    leadingIcon: SKIconKey? = null,
    trailingText: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.ListItem,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val clickable = onClick != null
    val id = componentId ?: remember { "sklistitem-${UUID.randomUUID()}" }
    val a11y = remember(accessibility, clickable) {
        when {
            accessibility.role != null -> accessibility
            clickable -> accessibility.copy(role = "button")
            else -> accessibility
        }
    }
    val component = remember(id) {
        SKListItemComponent.create(
            id = id,
            headline = headline,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingText = trailingText,
            selected = selected,
            enabled = enabled,
            clickable = clickable,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(headline) { component.setHeadline(headline) }
    LaunchedEffect(supportingText) { component.setSupportingText(supportingText) }
    LaunchedEffect(leadingIcon) { component.setLeadingIcon(leadingIcon) }
    LaunchedEffect(trailingText) { component.setTrailingText(trailingText) }
    LaunchedEffect(selected) { component.setSelected(selected) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    LaunchedEffect(clickable) { component.setClickable(clickable) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    val description = a11y.contentDescription ?: buildString {
        append(headline)
        if (!supportingText.isNullOrBlank()) append(", ").append(supportingText)
        if (!trailingText.isNullOrBlank()) append(", ").append(trailingText)
    }
    val stateText = listOfNotNull(
        a11y.stateDescription?.takeIf { it.isNotBlank() },
        if (selected) "Selected" else null,
    ).takeIf { it.isNotEmpty() }?.joinToString(", ")
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .heightIn(min = look.height)
            .background(if (selected) look.containerColor.copy(alpha = 0.12f) else look.containerColor)
            .then(
                if (clickable) {
                    Modifier.clickable(
                        enabled = enabled,
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            component.performClick()
                            onClick?.invoke()
                        },
                    )
                } else Modifier,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = description
                a11y.testTag?.let { testTag = it }
                if (clickable) role = Role.Button
                this.selected = selected
                if (stateText != null) stateDescription = stateText
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, stateDescription = null, role = null),
                )
            }
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            val explicitCd = leadingIcon.contentDescription?.takeIf { it.isNotBlank() }
            val iconMod = if (explicitCd != null) {
                Modifier.size(look.iconSize).semantics { contentDescription = explicitCd }
            } else {
                Modifier.size(look.iconSize).clearAndSetSemantics { }
            }
            Box(modifier = iconMod, contentAlignment = Alignment.Center) {
                BasicText(
                    text = "•",
                    style = theme.tokens.typography.scale(SKTypographyRole.TitleMedium)
                        .toTextStyle()
                        .copy(color = look.contentColor),
                )
            }
            Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
        }
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = headline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
            if (!supportingText.isNullOrBlank()) {
                BasicText(
                    text = supportingText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = theme.tokens.typography.scale(SKTypographyRole.BodyMedium)
                        .toTextStyle()
                        .copy(color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()),
                )
            }
        }
        if (!trailingText.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
            BasicText(
                text = trailingText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                    .toTextStyle()
                    .copy(color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()),
            )
        }
    }
}

/**
 * Lightweight section header with optional supporting text and action.
 *
 * @see docs/WIDGETS_SKSECTIONHEADER.md
 */
@Composable
public fun SKSectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    supportingText: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.SectionHeader,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "sksectionheader-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        if (accessibility.heading) accessibility else accessibility.copy(heading = true)
    }
    val component = remember(id) {
        SKSectionHeaderComponent.create(
            id = id,
            title = title,
            supportingText = supportingText,
            actionLabel = actionLabel,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(title) { component.setTitle(title) }
    LaunchedEffect(supportingText) { component.setSupportingText(supportingText) }
    LaunchedEffect(actionLabel) { component.setActionLabel(actionLabel) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    Row(
        modifier = modifier
            .skLayout(layout)
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding)
            .semantics(mergeDescendants = true) {
                contentDescription = a11y.contentDescription ?: title
                a11y.testTag?.let { testTag = it }
                heading()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, heading = false),
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleSmall)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
            if (!supportingText.isNullOrBlank()) {
                BasicText(
                    text = supportingText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = theme.tokens.typography.scale(SKTypographyRole.BodySmall)
                        .toTextStyle()
                        .copy(color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()),
                )
            }
        }
        if (!actionLabel.isNullOrBlank() && onAction != null) {
            val interaction = remember { MutableInteractionSource() }
            BasicText(
                text = actionLabel,
                modifier = Modifier
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            component.performAction()
                            onAction()
                        },
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = actionLabel
                    },
                style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                    .toTextStyle()
                    .copy(color = theme.tokens.colors.color(SKColorRole.Primary).toColor()),
            )
        }
    }
}

/**
 * Lightweight screen shell: topBar / content / bottomBar (+ optional snackbar / FAB overlays).
 *
 * Applies [skSafeDrawingPadding] so bars clear system insets under edge-to-edge hosts.
 * Optional [floatingActionButton] is placed bottom-end over content — no scroll-aware / speed-dial behavior.
 * No navigation / overlay / snackbar manager.
 *
 * @see docs/WIDGETS_SKSCAFFOLD.md
 */
@Composable
public fun SKScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbar: @Composable BoxScope.() -> Unit = {},
    floatingActionButton: @Composable BoxScope.() -> Unit = {},
    appearance: SKAppearanceConfig = SKAppearanceConfig.Scaffold,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    contentSafeDrawing: Boolean = true,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    val id = componentId ?: remember { "skscaffold-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKScaffoldComponent.create(
            id = id,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(look.containerColor)
            .then(if (contentSafeDrawing) Modifier.skSafeDrawingPadding() else Modifier)
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it }
                accessibility.testTag?.let { testTag = it }
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            },
    ) {
        topBar()
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxSize(), content = content)
            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                snackbar()
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                floatingActionButton()
            }
        }
        bottomBar()
    }
}

private const val DisabledAlpha = 0.38f
