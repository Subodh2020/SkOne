@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
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
import io.skone.compose.theme.toTextStyle
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.overlay.SKSnackbarComponent
import java.util.UUID

/**
 * Transient feedback bar. Host controls visibility — no overlay manager.
 *
 * @see docs/WIDGETS_SKSNACKBAR.md
 */
@Composable
public fun SKSnackbar(
    modifier: Modifier = Modifier,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    visible: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Snackbar,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    if (!visible) return
    val id = componentId ?: remember { "sksnackbar-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        if (accessibility.liveRegion) accessibility else accessibility.copy(liveRegion = true)
    }
    val component = remember(id) {
        SKSnackbarComponent.create(
            id = id,
            message = message,
            actionLabel = actionLabel,
            visible = visible,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(message) { component.setMessage(message) }
    LaunchedEffect(actionLabel) { component.setActionLabel(actionLabel) }
    LaunchedEffect(visible) { component.setVisible(visible) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val description = a11y.contentDescription ?: message
    Row(
        modifier = modifier
            .skLayout(layout)
            .shadow(look.elevation ?: 0.dp, look.shape)
            .background(look.containerColor, look.shape)
            .semantics {
                contentDescription = description
                a11y.testTag?.let { testTag = it }
                liveRegion = LiveRegionMode.Polite
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, liveRegion = false),
                )
            }
            .padding(horizontal = look.horizontalPadding, vertical = look.verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(
            text = message,
            modifier = Modifier.weight(1f),
            style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyMedium)
                .toTextStyle()
                .copy(color = look.contentColor),
        )
        if (!actionLabel.isNullOrBlank() && onAction != null) {
            val interaction = remember { MutableInteractionSource() }
            BasicText(
                text = actionLabel,
                modifier = Modifier
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = {
                            component.performAction()
                            onAction()
                        },
                    )
                    .padding(start = theme.tokens.spacing.sm.toDp()),
                style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
        }
    }
}
