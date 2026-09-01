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
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
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
import io.skone.ui.selection.SKChipComponent
import java.util.UUID

/**
 * Foundational SKOne chip (Compose). Filter/input/assist/suggestion variants deferred.
 *
 * @see docs/WIDGETS_SKCHIP.md
 */
@Composable
public fun SKChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    enabled: Boolean = true,
    leadingIcon: SKIconKey? = null,
    appearance: SKAppearanceConfig? = null,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val resolvedAppearance = appearance
        ?: if (selected) SKAppearanceConfig.ChipSelected else SKAppearanceConfig.Chip
    val id = componentId ?: remember { "skchip-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        // Chips are selectable buttons — use button role; selection via selected property.
        if (accessibility.role == null) accessibility.copy(role = "button") else accessibility
    }
    val component = remember(id) {
        SKChipComponent.create(
            id = id,
            label = label,
            selected = selected,
            leadingIcon = leadingIcon,
            enabled = enabled,
            appearance = resolvedAppearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(label) { component.setLabel(label) }
    LaunchedEffect(leadingIcon) { component.setLeadingIcon(leadingIcon) }
    LaunchedEffect(selected) { component.setSelected(selected) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    LaunchedEffect(resolvedAppearance, a11y, analytics, ai) {
        component.updateConfig(
            component.config.copy(
                appearance = resolvedAppearance,
                accessibility = a11y,
                analytics = analytics,
                ai = ai,
            ),
        )
    }
    if (runtime != null) {
        SKComponentLifecycle(component, runtime)
    }

    val look = resolvedAppearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    val description = a11y.contentDescription ?: label
    val stateText = listOfNotNull(
        a11y.stateDescription?.takeIf { it.isNotBlank() },
        if (selected) "Selected" else null,
    ).takeIf { it.isNotEmpty() }?.joinToString(", ")
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .defaultMinSize(minHeight = look.height)
            .background(look.containerColor, look.shape)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(theme.tokens.spacing.xxs.toDp(), look.outlineColor, look.shape)
                } else {
                    Modifier
                },
            )
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
            .semantics(mergeDescendants = true) {
                contentDescription = description
                a11y.testTag?.let { testTag = it }
                role = Role.Button
                this.selected = selected
                if (stateText != null) stateDescription = stateText
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, stateDescription = null, role = null),
                )
            }
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (leadingIcon != null) {
            val explicitCd = leadingIcon.contentDescription?.takeIf { it.isNotBlank() }
            val ref = runtime?.icons?.resolve(leadingIcon)
            val iconMod = if (explicitCd != null) {
                Modifier.size(look.iconSize).semantics { contentDescription = explicitCd }
            } else {
                Modifier.size(look.iconSize).clearAndSetSemantics { }
            }
            Box(modifier = iconMod, contentAlignment = Alignment.Center) {
                BasicText(
                    text = ref?.vectorName?.take(1) ?: "•",
                    style = theme.tokens.typography.scale(SKTypographyRole.LabelSmall)
                        .toTextStyle()
                        .copy(color = look.contentColor),
                )
            }
            Spacer(modifier = Modifier.width(theme.tokens.spacing.xs.toDp()))
        }
        BasicText(
            text = label,
            style = theme.tokens.typography.scale(resolvedAppearance.typographyRole ?: SKTypographyRole.LabelLarge)
                .toTextStyle()
                .copy(color = look.contentColor),
        )
    }
}

private const val DisabledAlpha = 0.38f
