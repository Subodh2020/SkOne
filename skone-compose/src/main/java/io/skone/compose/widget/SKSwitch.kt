@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
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
import io.skone.ui.toggle.SKSwitchComponent
import java.util.UUID

/**
 * SKOne switch (Compose).
 *
 * @see docs/WIDGETS_SKSWITCH.md
 */
@Composable
public fun SKSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    label: String? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skswitch-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        if (accessibility.role == null) accessibility.copy(role = "switch") else accessibility
    }
    val component = remember(id) {
        SKSwitchComponent.create(
            id = id,
            checked = checked,
            label = label,
            enabled = enabled,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(checked) { component.setChecked(checked) }
    LaunchedEffect(label) { component.setLabel(label) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    LaunchedEffect(appearance, a11y, analytics, ai) {
        component.updateConfig(
            component.config.copy(appearance = appearance, accessibility = a11y, analytics = analytics, ai = ai),
        )
    }
    if (runtime != null) {
        SKComponentLifecycle(component, runtime)
    }

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    val trackColor = if (checked) {
        look.containerColor
    } else {
        theme.tokens.colors.color(SKColorRole.OutlineVariant).toColor()
    }
    val description = a11y.contentDescription ?: label
    val stateText = listOfNotNull(
        a11y.stateDescription?.takeIf { it.isNotBlank() },
        if (checked) "On" else "Off",
    ).joinToString(", ")
    val interaction = remember { MutableInteractionSource() }
    val trackWidth = look.iconSize * 2
    val trackHeight = look.iconSize
    val thumbSize = look.iconSize * 0.75f

    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                role = Role.Switch,
                onClick = {
                    val next = !checked
                    component.setChecked(next)
                    component.performClick()
                    onCheckedChange(next)
                },
            )
            .semantics(mergeDescendants = true) {
                description?.let { contentDescription = it }
                a11y.testTag?.let { testTag = it }
                role = Role.Switch
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
                stateDescription = stateText
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, stateDescription = null, role = null),
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(trackHeight)
                .background(trackColor, look.shape),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = theme.tokens.spacing.xxs.toDp())
                    .size(thumbSize)
                    .background(theme.tokens.colors.color(SKColorRole.Surface).toColor(), CircleShape),
            )
        }
        if (!label.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
            BasicText(
                text = label,
                style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge)
                    .toTextStyle()
                    .copy(color = theme.tokens.colors.color(SKColorRole.OnSurface).toColor()),
            )
        }
    }
}

private const val DisabledAlpha = 0.38f
