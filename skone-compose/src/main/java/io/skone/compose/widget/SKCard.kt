@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
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
import io.skone.compose.theme.toDp
import io.skone.ui.layout.SKCardComponent
import java.util.UUID

/**
 * Foundational SKOne card / surface container (Compose).
 *
 * Optional [onClick] makes the card interactive (button role). Specialized Material card
 * variants are deferred.
 *
 * @see docs/WIDGETS_SKCARD.md
 */
@Composable
public fun SKCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Card,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    val clickable = onClick != null
    val id = componentId ?: remember { "skcard-${UUID.randomUUID()}" }
    val a11y = remember(accessibility, clickable) {
        when {
            accessibility.role != null -> accessibility
            clickable -> accessibility.copy(role = "button")
            else -> accessibility
        }
    }
    val component = remember(id) {
        SKCardComponent.create(
            id = id,
            clickable = clickable,
            enabled = enabled,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(clickable) { component.setClickable(clickable) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
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
        SKComponentLifecycle(component, runtime)
    }

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else DisabledAlpha
    val elevation = look.elevation ?: 0.dp
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .shadow(elevation = elevation, shape = look.shape, clip = false)
            .background(look.containerColor, look.shape)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(theme.tokens.spacing.xxs.toDp(), look.outlineColor, look.shape)
                } else {
                    Modifier
                },
            )
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
                } else {
                    Modifier
                },
            )
            .semantics(mergeDescendants = true) {
                a11y.contentDescription?.let { contentDescription = it }
                a11y.testTag?.let { testTag = it }
                if (clickable) role = Role.Button
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, role = null),
                )
            }
            .padding(
                horizontal = look.horizontalPadding,
                vertical = look.verticalPadding,
            ),
        content = content,
    )
}

private const val DisabledAlpha = 0.38f
