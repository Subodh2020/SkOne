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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
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
import io.skone.ui.selection.SKRadioButtonComponent
import java.util.UUID

internal data class SKRadioGroupScope(
    val selectedValue: String?,
    val onSelectedChange: (String) -> Unit,
    val groupEnabled: Boolean,
)

internal val LocalSKRadioGroup = compositionLocalOf<SKRadioGroupScope?> { null }

/**
 * Groups [SKRadioButton] children so only one value is selected.
 *
 * @see docs/WIDGETS_SKRADIOGROUP.md
 */
@Composable
public fun SKRadioGroup(
    modifier: Modifier = Modifier,
    selectedValue: String?,
    onSelectedChange: (String) -> Unit,
    enabled: Boolean = true,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.semantics(mergeDescendants = false) {
            accessibility.testTag?.let { testTag = it }
            accessibility.contentDescription?.let { contentDescription = it }
            if (!enabled) disabled()
        },
        verticalArrangement = Arrangement.spacedBy(skTheme.tokens.spacing.xs.toDp()),
        content = {
            CompositionLocalProvider(
                LocalSKRadioGroup provides SKRadioGroupScope(selectedValue, onSelectedChange, enabled),
            ) {
                content()
            }
        },
    )
}

/**
 * SKOne radio button (Compose). Prefer placing inside [SKRadioGroup].
 *
 * @see docs/WIDGETS_SKRADIOBUTTON.md
 */
@Composable
public fun SKRadioButton(
    modifier: Modifier = Modifier,
    value: String,
    selected: Boolean? = null,
    onClick: (() -> Unit)? = null,
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
    val group = LocalSKRadioGroup.current
    val isSelected = selected ?: (group?.selectedValue == value)
    val isEnabled = enabled && (group?.groupEnabled ?: true)
    val id = componentId ?: remember { "skradio-${UUID.randomUUID()}" }
    val a11y = remember(accessibility) {
        if (accessibility.role == null) accessibility.copy(role = "radio") else accessibility
    }
    val component = remember(id) {
        SKRadioButtonComponent.create(
            id = id,
            value = value,
            selected = isSelected,
            label = label,
            enabled = isEnabled,
            appearance = appearance,
            accessibility = a11y,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(isSelected) { component.setSelected(isSelected) }
    LaunchedEffect(label) { component.setLabel(label) }
    LaunchedEffect(isEnabled) { component.setEnabled(isEnabled) }
    LaunchedEffect(value) { component.setValue(value) }
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
    val alpha = if (isEnabled) 1f else DisabledAlpha
    val description = a11y.contentDescription ?: label
    val stateText = listOfNotNull(
        a11y.stateDescription?.takeIf { it.isNotBlank() },
        if (isSelected) "Selected" else "Not selected",
    ).joinToString(", ")
    val interaction = remember { MutableInteractionSource() }
    val ringSize = look.iconSize

    Row(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .clickable(
                enabled = isEnabled,
                interactionSource = interaction,
                indication = null,
                role = Role.RadioButton,
                onClick = {
                    if (!isSelected) {
                        group?.onSelectedChange?.invoke(value)
                    }
                    component.setSelected(true)
                    component.performClick()
                    onClick?.invoke()
                },
            )
            .semantics(mergeDescendants = true) {
                description?.let { contentDescription = it }
                a11y.testTag?.let { testTag = it }
                role = Role.RadioButton
                this.selected = isSelected
                stateDescription = stateText
                if (!isEnabled) disabled()
                applyOptionalAccessibility(
                    a11y.copy(contentDescription = null, testTag = null, stateDescription = null, role = null),
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(ringSize)
                .border(
                    width = theme.tokens.spacing.xxs.toDp(),
                    color = if (isSelected) look.containerColor else (look.outlineColor ?: look.contentColor),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(ringSize * 0.5f)
                        .background(look.containerColor, CircleShape),
                )
            }
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
