@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
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
import io.skone.compose.theme.toDp
import io.skone.compose.theme.toTextStyle
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.button.SKButtonComponent
import java.util.UUID

/**
 * SKOne button widget (Compose).
 *
 * Visuals resolve through [appearance] + theme tokens only.
 * Default appearance is [SKAppearanceConfig.Button] (filled). Prefer
 * [SKAppearanceConfig.ButtonTonal], [SKAppearanceConfig.ButtonOutlined], or
 * [SKAppearanceConfig.ButtonText] for variants.
 *
 * ### Parameter order
 * modifier → enabled → loading → onClick → text → leadingIcon → appearance
 * → accessibility → analytics → ai
 *
 * @see docs/WIDGETS_SKBUTTON.md
 */
@Composable
public fun SKButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
    text: String,
    leadingIcon: SKIconKey? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Button,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skbutton-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        if (accessibility.role == null) accessibility.copy(role = "button") else accessibility
    }
    val component = remember(id) {
        SKButtonComponent.create(
            id = id,
            text = text,
            leadingIcon = leadingIcon,
            enabled = enabled,
            loading = loading,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }

    LaunchedEffect(text) { component.setText(text) }
    LaunchedEffect(leadingIcon) { component.setLeadingIcon(leadingIcon) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    LaunchedEffect(loading) { component.setLoading(loading) }
    LaunchedEffect(appearance, a11y, analytics, ai) {
        component.updateConfig(
            component.config.copy(
                appearance = appearance,
                accessibility = a11y,
                analytics = analytics,
                ai = ai,
            ),
        )
    }

    if (runtime != null) {
        SKComponentLifecycle(component = component, runtime = runtime)
    }

    val look = appearance.resolve()
    val interactive = enabled && !loading
    val alpha = if (enabled) 1f else DisabledAlpha
    val theme = skTheme
    val typeRole = appearance.typographyRole ?: SKTypographyRole.LabelLarge
    val labelStyle = theme.tokens.typography.scale(typeRole).toTextStyle()
        .copy(color = look.contentColor.copy(alpha = alpha))
    val interaction = remember { MutableInteractionSource() }
    val description = a11y.contentDescription ?: text
    val combinedState = listOfNotNull(
        a11y.stateDescription?.takeIf { it.isNotBlank() },
        if (loading) "Loading" else null,
    ).takeIf { it.isNotEmpty() }?.joinToString(", ")
    val outlineWidth = theme.tokens.spacing.xxs.toDp()

    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .defaultMinSize(minHeight = look.height)
            .heightIn(min = look.height)
            .background(look.containerColor, look.shape)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(outlineWidth, look.outlineColor, look.shape)
                } else {
                    Modifier
                },
            )
            .clickable(
                enabled = interactive,
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = {
                    component.performClick()
                    onClick()
                },
            )
            .semantics(mergeDescendants = true) {
                contentDescription = description
                a11y.testTag?.let { testTag = it }
                role = Role.Button
                if (!interactive) disabled()
                if (combinedState != null) stateDescription = combinedState
                applyOptionalAccessibility(
                    a11y.copy(
                        contentDescription = null,
                        testTag = null,
                        stateDescription = null,
                        role = null,
                    ),
                )
            }
            .padding(
                horizontal = look.horizontalPadding,
                vertical = look.verticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        when {
            loading -> {
                LoadingGlyph(size = look.iconSize, color = look.contentColor.copy(alpha = alpha))
                if (text.isNotBlank()) {
                    Spacer(modifier = Modifier.width(theme.tokens.spacing.xs.toDp()))
                }
            }
            leadingIcon != null -> {
                ButtonIconSlot(
                    key = leadingIcon,
                    size = look.iconSize,
                    contentColor = look.contentColor.copy(alpha = alpha),
                    runtime = runtime,
                )
                if (text.isNotBlank()) {
                    Spacer(modifier = Modifier.width(theme.tokens.spacing.xs.toDp()))
                }
            }
        }
        if (text.isNotBlank()) {
            BasicText(text = text, style = labelStyle)
        }
    }
}

@Composable
private fun ButtonIconSlot(
    key: SKIconKey,
    size: Dp,
    contentColor: Color,
    runtime: SKComponentRuntime?,
) {
    val ref = runtime?.icons?.resolve(key)
    val explicitCd = key.contentDescription?.takeIf { it.isNotBlank() }
    val iconModifier = if (explicitCd != null) {
        Modifier.size(size).semantics { contentDescription = explicitCd }
    } else {
        Modifier.size(size).clearAndSetSemantics { }
    }
    Box(modifier = iconModifier, contentAlignment = Alignment.Center) {
        BasicText(
            text = ref?.vectorName?.take(1) ?: "•",
            style = skTheme.tokens.typography.scale(SKTypographyRole.LabelSmall)
                .toTextStyle()
                .copy(color = contentColor),
        )
    }
}

@Composable
private fun LoadingGlyph(
    size: Dp,
    color: Color,
) {
    // Loading announcement is on the button node via stateDescription; glyph is decorative.
    Box(
        modifier = Modifier.size(size).clearAndSetSemantics { },
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = "…",
            style = skTheme.tokens.typography.scale(SKTypographyRole.LabelSmall)
                .toTextStyle()
                .copy(color = color),
        )
    }
}

private const val DisabledAlpha = 0.38f
