@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import io.skone.ui.search.SKEmptyStateComponent
import io.skone.ui.search.SKFabComponent
import io.skone.ui.search.SKSearchBarComponent
import java.util.UUID

/**
 * Search input. Host owns [query]; reuses BasicTextField + IME patterns (not a second field framework).
 *
 * @see docs/WIDGETS_SKSEARCHBAR.md
 */
@Composable
public fun SKSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.SearchBar,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "sksearchbar-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        accessibility.copy(
            contentDescription = accessibility.contentDescription ?: "Search",
        )
    }
    val component = remember(id) {
        SKSearchBarComponent.create(
            id = id,
            query = query,
            placeholder = placeholder,
            enabled = enabled,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(query) { component.setQuery(query) }
    LaunchedEffect(placeholder) { component.setPlaceholder(placeholder) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    val focusRequester = remember { FocusRequester() }
    val clearTag = a11y.testTag?.let { "${it}_clear" } ?: "sk_search_clear"

    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .defaultMinSize(minHeight = look.height)
            .background(look.containerColor, look.shape)
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(look.iconSize)
                .clearAndSetSemantics { },
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = "⌕",
                style = theme.tokens.typography.scale(SKTypographyRole.TitleMedium)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
        }
        Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                BasicText(
                    text = placeholder,
                    style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge)
                        .toTextStyle()
                        .copy(color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()),
                )
            }
            BasicTextField(
                value = query,
                onValueChange = {
                    component.setQuery(it)
                    onQueryChange(it)
                },
                enabled = enabled,
                singleLine = true,
                textStyle = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge)
                    .toTextStyle()
                    .copy(color = look.contentColor),
                cursorBrush = SolidColor(look.contentColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch?.invoke(query)
                    },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focused ->
                        component.onFocusChanged(focused.isFocused)
                    }
                    .semantics {
                        contentDescription = a11y.contentDescription ?: "Search"
                        a11y.testTag?.let { testTag = it }
                        if (!enabled) disabled()
                        applyOptionalAccessibility(
                            a11y.copy(contentDescription = null, testTag = null),
                        )
                    },
            )
        }
        if (query.isNotEmpty() && enabled) {
            val clearInteraction = remember { MutableInteractionSource() }
            BasicText(
                text = "✕",
                modifier = Modifier
                    .clickable(
                        interactionSource = clearInteraction,
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            component.clear()
                            onQueryChange("")
                            onClear?.invoke()
                        },
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = "Clear search"
                        testTag = clearTag
                    }
                    .padding(start = theme.tokens.spacing.xs.toDp()),
                style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
        }
    }
}

/**
 * Empty / zero-results content block.
 *
 * @see docs/WIDGETS_SKEMPTYSTATE.md
 */
@Composable
public fun SKEmptyState(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    icon: SKIconKey? = null,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.EmptyState,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skemptystate-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKEmptyStateComponent.create(
            id = id,
            title = title,
            description = description,
            icon = icon,
            primaryActionLabel = primaryActionLabel,
            secondaryActionLabel = secondaryActionLabel,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(title) { component.setTitle(title) }
    LaunchedEffect(description) { component.setDescription(description) }
    LaunchedEffect(icon) { component.setIcon(icon) }
    LaunchedEffect(primaryActionLabel) { component.setPrimaryActionLabel(primaryActionLabel) }
    LaunchedEffect(secondaryActionLabel) { component.setSecondaryActionLabel(secondaryActionLabel) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val descriptionText = accessibility.contentDescription
        ?: listOfNotNull(title, description).joinToString(". ")

    Column(
        modifier = modifier
            .skLayout(layout)
            .padding(look.horizontalPadding, look.verticalPadding)
            .semantics(mergeDescendants = true) {
                contentDescription = descriptionText
                accessibility.testTag?.let { testTag = it }
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(theme.tokens.spacing.sm.toDp()),
    ) {
        if (icon != null) {
            val explicitCd = icon.contentDescription?.takeIf { it.isNotBlank() }
            val iconMod = if (explicitCd != null) {
                Modifier.size(look.iconSize * 2).semantics { contentDescription = explicitCd }
            } else {
                Modifier.size(look.iconSize * 2).clearAndSetSemantics { }
            }
            Box(modifier = iconMod, contentAlignment = Alignment.Center) {
                BasicText(
                    text = "◇",
                    style = theme.tokens.typography.scale(SKTypographyRole.HeadlineMedium)
                        .toTextStyle()
                        .copy(color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()),
                )
            }
        }
        BasicText(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleMedium)
                .toTextStyle()
                .copy(color = look.contentColor, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth(),
        )
        if (!description.isNullOrBlank()) {
            BasicText(
                text = description,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = theme.tokens.typography.scale(SKTypographyRole.BodyMedium)
                    .toTextStyle()
                    .copy(
                        color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor(),
                        textAlign = TextAlign.Center,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (!primaryActionLabel.isNullOrBlank() && onPrimaryAction != null) {
            val interaction = remember { MutableInteractionSource() }
            BasicText(
                text = primaryActionLabel,
                modifier = Modifier
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            component.performPrimaryAction()
                            onPrimaryAction()
                        },
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = primaryActionLabel
                        testTag = accessibility.testTag?.let { "${it}_primary" } ?: "sk_empty_primary"
                    },
                style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                    .toTextStyle()
                    .copy(color = theme.tokens.colors.color(SKColorRole.Primary).toColor()),
            )
        }
        if (!secondaryActionLabel.isNullOrBlank() && onSecondaryAction != null) {
            val interaction = remember { MutableInteractionSource() }
            BasicText(
                text = secondaryActionLabel,
                modifier = Modifier
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            component.performSecondaryAction()
                            onSecondaryAction()
                        },
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = secondaryActionLabel
                        testTag = accessibility.testTag?.let { "${it}_secondary" } ?: "sk_empty_secondary"
                    },
                style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                    .toTextStyle()
                    .copy(color = theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()),
            )
        }
    }
}

/**
 * Floating action button. Requires explicit accessibility description (never raw icon key).
 *
 * @see docs/WIDGETS_SKFAB.md
 */
@Composable
public fun SKFab(
    modifier: Modifier = Modifier,
    icon: SKIconKey,
    onClick: () -> Unit,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Fab,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skfab-${UUID.randomUUID()}" }
    val semanticDescription = accessibility.contentDescription
        ?: icon.contentDescription?.takeIf { it.isNotBlank() }
    require(!semanticDescription.isNullOrBlank()) {
        "SKFab requires accessibility.contentDescription or SKIconKey.contentDescription"
    }
    val a11y = remember(accessibility, semanticDescription) {
        val withCd = if (accessibility.contentDescription.isNullOrBlank()) {
            accessibility.copy(contentDescription = semanticDescription)
        } else {
            accessibility
        }
        if (withCd.role == null) withCd.copy(role = "button") else withCd
    }
    val component = remember(id) {
        SKFabComponent.create(
            id = id,
            icon = icon,
            enabled = enabled,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(icon) { component.setIcon(icon) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    LaunchedEffect(a11y, appearance, analytics, ai) {
        component.updateConfig(
            component.config.copy(
                appearance = appearance,
                accessibility = a11y,
                analytics = analytics,
                ai = ai,
            ),
        )
    }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    val interaction = remember { MutableInteractionSource() }
    val size = look.height.coerceAtLeast(theme.tokens.spacing.xl.toDp() * 2)

    Box(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .size(size)
            .background(look.containerColor, look.shape)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = {
                    component.performClick()
                    onClick()
                },
            )
            .semantics {
                contentDescription = semanticDescription
                a11y.testTag?.let { testTag = it }
                role = Role.Button
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, role = null),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = "+",
            style = theme.tokens.typography.scale(SKTypographyRole.HeadlineSmall)
                .toTextStyle()
                .copy(color = look.contentColor),
        )
    }
}

private const val DisabledAlpha = 0.38f
