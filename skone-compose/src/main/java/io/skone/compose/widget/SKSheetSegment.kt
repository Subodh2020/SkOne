@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import io.skone.ui.overlay.SKBottomSheetComponent
import io.skone.ui.overlay.SKSegmentItem
import io.skone.ui.overlay.SKSegmentedButtonComponent
import java.util.UUID

/**
 * Lean host-controlled bottom sheet. Uses [Dialog] anchored visually to the bottom —
 * not a sheet manager, gesture framework, or Material ModalBottomSheet wrapper.
 *
 * @see docs/WIDGETS_SKBOTTOMSHEET.md
 */
@Composable
public fun SKBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    primaryEnabled: Boolean = true,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryEnabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.BottomSheet,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    val id = componentId ?: remember { "skbottomsheet-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKBottomSheetComponent.create(
            id = id,
            title = title,
            primaryActionLabel = primaryActionLabel,
            secondaryActionLabel = secondaryActionLabel,
            visible = visible,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(title) { component.setTitle(title) }
    LaunchedEffect(primaryActionLabel) { component.setPrimaryActionLabel(primaryActionLabel) }
    LaunchedEffect(secondaryActionLabel) { component.setSecondaryActionLabel(secondaryActionLabel) }
    LaunchedEffect(visible) { component.setVisible(visible) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    Dialog(
        onDismissRequest = {
            component.dismiss()
            onDismissRequest()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        component.dismiss()
                        onDismissRequest()
                    },
                ),
        ) {
            Column(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .shadow(look.elevation ?: 0.dp, look.shape)
                    .clip(look.shape)
                    .background(look.containerColor, look.shape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* consume scrim clicks */ },
                    )
                    .semantics {
                        accessibility.contentDescription?.let { contentDescription = it }
                            ?: title?.let { contentDescription = it }
                            ?: run { contentDescription = "Bottom sheet" }
                        accessibility.testTag?.let { testTag = it }
                        applyOptionalAccessibility(
                            accessibility.copy(contentDescription = null, testTag = null),
                        )
                    }
                    .padding(look.horizontalPadding, look.verticalPadding),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            theme.tokens.colors.color(SKColorRole.OutlineVariant).toColor(),
                            look.shape,
                        )
                        .clearAndSetSemantics { },
                )
                Spacer(modifier = Modifier.height(theme.tokens.spacing.sm.toDp()))
                if (!title.isNullOrBlank()) {
                    BasicText(
                        text = title,
                        style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleMedium)
                            .toTextStyle()
                            .copy(color = look.contentColor),
                    )
                    Spacer(modifier = Modifier.height(theme.tokens.spacing.sm.toDp()))
                }
                content()
                if (!primaryActionLabel.isNullOrBlank() || !secondaryActionLabel.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(theme.tokens.spacing.md.toDp()))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!secondaryActionLabel.isNullOrBlank() && onSecondaryAction != null) {
                            SheetAction(
                                label = secondaryActionLabel,
                                enabled = secondaryEnabled,
                                testTag = accessibility.testTag?.let { "${it}_secondary" } ?: "sheet_secondary",
                                onClick = {
                                    if (secondaryEnabled) {
                                        component.performSecondaryAction()
                                        onSecondaryAction()
                                        onDismissRequest()
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.width(theme.tokens.spacing.sm.toDp()))
                        }
                        if (!primaryActionLabel.isNullOrBlank() && onPrimaryAction != null) {
                            SheetAction(
                                label = primaryActionLabel,
                                enabled = primaryEnabled,
                                emphasized = true,
                                testTag = accessibility.testTag?.let { "${it}_primary" } ?: "sheet_primary",
                                onClick = {
                                    if (primaryEnabled) {
                                        component.performPrimaryAction()
                                        onPrimaryAction()
                                        onDismissRequest()
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetAction(
    label: String,
    enabled: Boolean,
    testTag: String,
    emphasized: Boolean = false,
    onClick: () -> Unit,
) {
    val theme = skTheme
    val interaction = remember { MutableInteractionSource() }
    val color = if (emphasized) {
        theme.tokens.colors.color(SKColorRole.Primary).toColor()
    } else {
        theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toColor()
    }
    BasicText(
        text = label,
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.38f)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = label
                role = Role.Button
                this.testTag = testTag
                if (!enabled) disabled()
            },
        style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
            .toTextStyle()
            .copy(color = color),
    )
}

/**
 * Exclusive segmented control. NavigationBar/TabRow-style selection — not a new selection framework.
 *
 * @see docs/WIDGETS_SKSEGMENTEDBUTTON.md
 */
@Composable
public fun SKSegmentedButton(
    modifier: Modifier = Modifier,
    items: List<SKSegmentItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.SegmentedButton,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    require(items.size >= 2) { "SKSegmentedButton requires at least 2 segments" }
    val id = componentId ?: remember { "sksegmented-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKSegmentedButtonComponent.create(
            id = id,
            items = items,
            selectedId = selectedId,
            enabled = enabled,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(items) { component.setItems(items) }
    LaunchedEffect(selectedId) { component.setSelectedId(selectedId) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    if (runtime != null) SKComponentLifecycle(component, runtime)

    val look = appearance.resolve()
    val theme = skTheme
    val alpha = if (enabled) 1f else 0.38f
    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .clip(look.shape)
            .background(look.containerColor, look.shape)
            .then(
                if (look.outlineColor != null) {
                    Modifier.border(theme.tokens.spacing.xxs.toDp(), look.outlineColor, look.shape)
                } else Modifier,
            )
            .semantics {
                accessibility.contentDescription?.let { contentDescription = it }
                accessibility.testTag?.let { testTag = it }
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    accessibility.copy(contentDescription = null, testTag = null),
                )
            }
            .padding(theme.tokens.spacing.xxs.toDp()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = item.id == selectedId
            val segmentEnabled = enabled && item.enabled
            val interaction = remember(item.id) { MutableInteractionSource() }
            val bg = if (selected) {
                theme.tokens.colors.color(SKColorRole.Primary).toColor()
            } else {
                Color.Transparent
            }
            val fg = if (selected) {
                theme.tokens.colors.color(SKColorRole.OnPrimary).toColor()
            } else {
                look.contentColor
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(look.shape)
                    .background(bg, look.shape)
                    .clickable(
                        enabled = segmentEnabled,
                        interactionSource = interaction,
                        indication = null,
                        role = Role.RadioButton,
                        onClick = {
                            component.select(item.id)
                            onSelect(item.id)
                        },
                    )
                    .semantics {
                        contentDescription = item.label
                        role = Role.RadioButton
                        this.selected = selected
                        stateDescription = if (selected) "Selected" else "Not selected"
                        testTag = accessibility.testTag?.let { "${it}_${item.id}" } ?: "segment_${item.id}"
                        if (!segmentEnabled) disabled()
                    }
                    .padding(
                        horizontal = theme.tokens.spacing.sm.toDp(),
                        vertical = theme.tokens.spacing.sm.toDp(),
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item.leadingIcon?.let { icon ->
                    val explicitCd = icon.contentDescription?.takeIf { it.isNotBlank() }
                    BasicText(
                        text = "•",
                        modifier = Modifier
                            .then(
                                if (explicitCd != null) {
                                    Modifier.semantics { contentDescription = explicitCd }
                                } else {
                                    Modifier.clearAndSetSemantics { }
                                },
                            )
                            .padding(end = theme.tokens.spacing.xs.toDp()),
                        style = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
                            .toTextStyle()
                            .copy(color = fg),
                    )
                }
                BasicText(
                    text = item.label,
                    style = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelLarge)
                        .toTextStyle()
                        .copy(color = fg),
                    maxLines = 1,
                )
            }
        }
    }
}
