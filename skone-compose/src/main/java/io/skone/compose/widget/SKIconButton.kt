@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.skone.compose.theme.toTextStyle
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.button.SKIconButtonComponent
import java.util.UUID

/**
 * SKOne icon-only button (Compose).
 *
 * Requires a meaningful description via [accessibility].contentDescription or
 * [SKIconKey.contentDescription]. Never announces the raw icon key.
 *
 * @see docs/WIDGETS_SKICONBUTTON.md
 */
@Composable
public fun SKIconButton(
    modifier: Modifier = Modifier,
    icon: SKIconKey,
    onClick: () -> Unit,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.IconButton,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skiconbutton-${UUID.randomUUID()}" }
    val semanticDescription = accessibility.contentDescription
        ?: icon.contentDescription?.takeIf { it.isNotBlank() }
    require(!semanticDescription.isNullOrBlank()) {
        "SKIconButton requires accessibility.contentDescription or SKIconKey.contentDescription"
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
        SKIconButtonComponent.create(
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
    val interaction = remember { MutableInteractionSource() }
    val ref = runtime?.icons?.resolve(icon)

    Box(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .defaultMinSize(minWidth = look.height, minHeight = look.height)
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
            }
            .padding(look.horizontalPadding / 2),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = ref?.vectorName?.take(1) ?: "★",
            style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                .toTextStyle()
                .copy(color = look.contentColor.copy(alpha = alpha)),
            modifier = Modifier.size(look.iconSize),
        )
    }
}

private const val DisabledAlpha = 0.38f
