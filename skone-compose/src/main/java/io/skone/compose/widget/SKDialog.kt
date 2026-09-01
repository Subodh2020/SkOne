@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.compose.component.LocalSKComponentRuntime
import io.skone.compose.component.SKComponentLifecycle
import io.skone.compose.theme.resolve
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.compose.theme.toTextStyle
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.overlay.SKAlertDialogComponent
import io.skone.ui.overlay.SKDialogComponent
import java.util.UUID

/**
 * Generic modal dialog. Host controls [visible]; dismiss via [onDismissRequest].
 *
 * @see docs/WIDGETS_SKDIALOG.md
 */
@Composable
public fun SKDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Dialog,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    val id = componentId ?: remember { "skdialog-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKDialogComponent.create(
            id = id,
            title = title,
            visible = visible,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(title) { component.setTitle(title) }
    LaunchedEffect(visible) { component.setVisible(visible) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    Dialog(
        onDismissRequest = {
            component.dismiss()
            onDismissRequest()
        },
        properties = properties,
    ) {
        Column(
            modifier = modifier
                .shadow(look.elevation ?: 0.dp, look.shape)
                .background(look.containerColor, look.shape)
                .semantics {
                    accessibility.contentDescription?.let { contentDescription = it }
                        ?: title?.let { contentDescription = it }
                    accessibility.testTag?.let { testTag = it }
                    applyOptionalAccessibility(
                        accessibility.copy(contentDescription = null, testTag = null),
                    )
                }
                .padding(look.horizontalPadding, look.verticalPadding),
        ) {
            if (!title.isNullOrBlank()) {
                BasicText(
                    text = title,
                    style = theme.tokens.typography.scale(SKTypographyRole.TitleLarge)
                        .toTextStyle()
                        .copy(color = look.contentColor),
                )
                Spacer(modifier = Modifier.height(theme.tokens.spacing.sm.toDp()))
            }
            content()
        }
    }
}

/**
 * Alert dialog with title, message, confirm and optional dismiss actions.
 *
 * @see docs/WIDGETS_SKALERTDIALOG.md
 */
@Composable
public fun SKAlertDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmLabel: String = "OK",
    dismissLabel: String? = "Cancel",
    modifier: Modifier = Modifier,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Dialog,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    if (!visible) return
    val id = componentId ?: remember { "skalertdialog-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKAlertDialogComponent.create(
            id = id,
            title = title,
            message = message,
            confirmLabel = confirmLabel,
            dismissLabel = dismissLabel,
            visible = visible,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(title) { component.setTitle(title) }
    LaunchedEffect(message) { component.setMessage(message) }
    LaunchedEffect(confirmLabel) { component.setConfirmLabel(confirmLabel) }
    LaunchedEffect(dismissLabel) { component.setDismissLabel(dismissLabel) }
    LaunchedEffect(visible) { component.setVisible(visible) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val paneDescription = accessibility.contentDescription ?: "$title. $message"
    Dialog(
        onDismissRequest = {
            component.dismiss()
            onDismissRequest()
        },
    ) {
        Column(
            modifier = modifier
                .shadow(look.elevation ?: 0.dp, look.shape)
                .background(look.containerColor, look.shape)
                .semantics {
                    contentDescription = paneDescription
                    accessibility.testTag?.let { testTag = it }
                    applyOptionalAccessibility(
                        accessibility.copy(contentDescription = null, testTag = null),
                    )
                }
                .padding(look.horizontalPadding, look.verticalPadding),
        ) {
            BasicText(
                text = title,
                style = theme.tokens.typography.scale(SKTypographyRole.TitleLarge)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
            Spacer(modifier = Modifier.height(theme.tokens.spacing.sm.toDp()))
            BasicText(
                text = message,
                style = theme.tokens.typography.scale(SKTypographyRole.BodyMedium)
                    .toTextStyle()
                    .copy(color = look.contentColor),
            )
            Spacer(modifier = Modifier.height(theme.tokens.spacing.md.toDp()))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!dismissLabel.isNullOrBlank()) {
                    val dismissInteraction = remember { MutableInteractionSource() }
                    BasicText(
                        text = dismissLabel,
                        modifier = Modifier.clickable(
                            interactionSource = dismissInteraction,
                            indication = null,
                            onClick = {
                                component.dismiss()
                                onDismissRequest()
                            },
                        ),
                        style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                            .toTextStyle()
                            .copy(color = look.contentColor),
                    )
                    Spacer(modifier = Modifier.width(theme.tokens.spacing.md.toDp()))
                }
                val confirmInteraction = remember { MutableInteractionSource() }
                BasicText(
                    text = confirmLabel,
                    modifier = Modifier
                        .semantics { testTag = accessibility.testTag?.let { "${it}_confirm" } ?: "sk_alert_confirm" }
                        .clickable(
                            interactionSource = confirmInteraction,
                            indication = null,
                            onClick = {
                                component.confirm()
                                onConfirm()
                            },
                        ),
                    style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                        .toTextStyle()
                        .copy(color = look.contentColor),
                )
            }
        }
    }
}
