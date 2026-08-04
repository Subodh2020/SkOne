package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.component.validation.SKValidationResult
import io.skone.compose.component.LocalSKComponentRuntime
import io.skone.compose.component.SKComponentLifecycle
import io.skone.compose.component.skLayout
import io.skone.compose.forms.LocalSKFormController
import io.skone.compose.theme.resolve
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toDp
import io.skone.compose.theme.toTextStyle
import io.skone.forms.SKFormController
import io.skone.forms.formatter.SKFormatter
import io.skone.forms.mask.SKInputMask
import io.skone.forms.validation.SKValidationRule
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.field.SKFieldVisualState
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.ui.field.SKTextFieldComponent
import java.util.UUID

/**
 * SKOne text field (Compose) — **flagship input** and reference for future fields.
 *
 * Auto-registers with [LocalSKFormController] when present.
 *
 * @see docs/WIDGETS_SKTEXTFIELD.md
 */
@Composable
public fun SKTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    fieldId: String? = null,
    onImeAction: ((SKImeAction) -> Unit)? = null,
    label: String? = null,
    hint: String? = null,
    supportingText: String? = null,
    leadingIcon: SKIconKey? = null,
    trailingIcon: SKIconKey? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.TextField,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    imeAction: SKImeAction = SKImeAction.Default,
    keyboardType: SKKeyboardType = SKKeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    formatter: SKFormatter? = null,
    mask: SKInputMask? = null,
    rules: List<SKValidationRule> = emptyList(),
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    form: SKFormController? = LocalSKFormController.current,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = fieldId ?: remember { "sktextfield-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKTextFieldComponent.create(
            id = id,
            initialValue = value,
            label = label,
            hint = hint,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            required = required,
            readOnly = readOnly,
            enabled = enabled,
            appearance = appearance,
            formatter = formatter,
            mask = mask,
            rules = rules,
            imeAction = imeAction,
            keyboardType = keyboardType,
            singleLine = singleLine,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }

    LaunchedEffect(
        label, hint, supportingText, leadingIcon, trailingIcon, required, readOnly, enabled,
        appearance, formatter, mask, rules, imeAction, keyboardType, singleLine, maxLines,
        accessibility, analytics, ai,
    ) {
        component.setLabel(label)
        component.setHint(hint)
        component.setSupportingText(supportingText)
        component.setLeadingIcon(leadingIcon)
        component.setTrailingIcon(trailingIcon)
        component.setFormatter(formatter)
        component.setMask(mask)
        component.setRules(rules)
        component.setImeAction(imeAction)
        component.setKeyboardType(keyboardType)
        component.setSingleLine(singleLine)
        component.setMaxLines(maxLines)
        component.updateConfig(
            SKTextFieldComponent.defaultConfig(
                required = required,
                readOnly = readOnly,
                enabled = enabled,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
                appearance = appearance,
            ),
        )
    }

    if (runtime != null) {
        SKComponentLifecycle(component = component, runtime = runtime)
    }

    var visualState by remember { mutableStateOf(component.visualState) }
    var supportOverride by remember { mutableStateOf(component.fieldSupportingText) }

    if (form != null) {
        DisposableEffect(form, id) {
            component.ensureRegistered(form)
            onDispose { component.ensureUnregistered(form) }
        }
        LaunchedEffect(value) {
            val current = form.registry.state(id)?.displayValue
            if (current != value) {
                form.updateValue(id, value)
            }
        }
        val formErrors by form.errors.errors.collectAsState()
        LaunchedEffect(formErrors[id]) {
            val fieldErrors = formErrors[id]
            if (!fieldErrors.isNullOrEmpty()) {
                component.applyValidationResult(SKValidationResult.Invalid(fieldErrors))
            } else if (component.visualState == SKFieldVisualState.Error) {
                component.setVisualState(SKFieldVisualState.None)
            }
            visualState = component.visualState
            supportOverride = component.fieldSupportingText
        }
    }

    val theme = skTheme
    val resolvedAppearance = when (visualState) {
        SKFieldVisualState.Error -> SKAppearanceConfig.TextFieldError
        SKFieldVisualState.Success -> SKAppearanceConfig.TextFieldSuccess
        SKFieldVisualState.None -> component.resolvedAppearance()
    }
    val look = resolvedAppearance.resolve(theme)
    val bodyStyle = theme.tokens.typography.scale(
        resolvedAppearance.typographyRole ?: SKTypographyRole.BodyLarge,
    ).toTextStyle().copy(color = look.contentColor)

    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val outlineColor = look.outlineColor ?: theme.tokens.colors.outline.toColor()
    val borderWidth = if (focused) {
        theme.tokens.spacing.xs.toDp()
    } else {
        theme.tokens.spacing.xxs.toDp()
    }

    var internalText by remember { mutableStateOf(value) }
    LaunchedEffect(value) { internalText = value }

    fun handleIme() {
        onImeAction?.invoke(imeAction)
        when (imeAction) {
            SKImeAction.Next -> form?.focus?.focusNext(id)
            SKImeAction.Previous -> form?.focus?.focusPrevious(id)
            SKImeAction.Done -> form?.clearFocus()
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = accessibility.contentDescription
                    ?: label
                    ?: hint
                    ?: "Text field"
                accessibility.testTag?.let { testTag = it }
                if (visualState == SKFieldVisualState.Error) {
                    error(supportOverride ?: supportingText ?: "Invalid")
                }
            }
            .skLayout(layout),
    ) {
        if (label != null) {
            SKText(
                text = if (required) "$label *" else label,
                appearance = SKAppearanceConfig.Text.copy(
                    typographyRole = SKTypographyRole.LabelMedium,
                    contentColorRole = if (visualState == SKFieldVisualState.Error) {
                        SKColorRole.Error
                    } else {
                        SKColorRole.OnSurfaceVariant
                    },
                ),
            )
            Spacer(modifier = Modifier.height(theme.tokens.spacing.xs.toDp()))
        }

        BasicTextField(
            value = internalText,
            onValueChange = { raw ->
                if (!enabled || readOnly) return@BasicTextField
                if (form != null) {
                    form.updateRawInput(id, raw)
                    val display = form.registry.state(id)?.displayValue ?: raw
                    internalText = display
                    onValueChange(display)
                } else {
                    internalText = raw
                    component.setValue(raw, fromUser = true)
                    onValueChange(raw)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    component.onFocusChanged(state.isFocused)
                    if (state.isFocused) {
                        form?.requestFocus(id)
                    }
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = bodyStyle,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = if (keyboardType == SKKeyboardType.Password) {
                PasswordVisualTransformation()
            } else {
                visualTransformation
            },
            cursorBrush = SolidColor(theme.tokens.colors.primary.toColor()),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType.toCompose(),
                imeAction = imeAction.toCompose(),
            ),
            keyboardActions = KeyboardActions(
                onDone = { handleIme() },
                onGo = { handleIme() },
                onNext = { handleIme() },
                onPrevious = { handleIme() },
                onSearch = { handleIme() },
                onSend = { handleIme() },
            ),
            interactionSource = interaction,
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = look.height)
                        .background(look.containerColor, look.shape)
                        .border(borderWidth, outlineColor, look.shape)
                        .padding(
                            horizontal = look.horizontalPadding,
                            vertical = look.verticalPadding,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        IconSlot(key = leadingIcon, size = look.iconSize, runtime = runtime)
                        Spacer(modifier = Modifier.width(theme.tokens.spacing.xs.toDp()))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (internalText.isEmpty() && hint != null) {
                            BasicText(
                                text = hint,
                                style = bodyStyle.copy(
                                    color = theme.tokens.colors.onSurfaceVariant.toColor(),
                                ),
                            )
                        }
                        inner()
                    }
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(theme.tokens.spacing.xs.toDp()))
                        IconSlot(key = trailingIcon, size = look.iconSize, runtime = runtime)
                    }
                }
            },
        )

        val support = supportOverride ?: supportingText
        if (support != null) {
            Spacer(modifier = Modifier.height(theme.tokens.spacing.xs.toDp()))
            SKText(
                text = support,
                appearance = SKAppearanceConfig.Text.copy(
                    typographyRole = SKTypographyRole.BodySmall,
                    contentColorRole = when (visualState) {
                        SKFieldVisualState.Error -> SKColorRole.Error
                        SKFieldVisualState.Success -> SKColorRole.Primary
                        SKFieldVisualState.None -> SKColorRole.OnSurfaceVariant
                    },
                ),
            )
        }
    }
}

@Composable
private fun IconSlot(
    key: SKIconKey,
    size: Dp,
    runtime: SKComponentRuntime?,
) {
    val ref = runtime?.icons?.resolve(key)
    Box(
        modifier = Modifier
            .size(size)
            .semantics { contentDescription = key.contentDescription ?: key.key },
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = ref?.vectorName?.take(1) ?: "•",
            style = skTheme.tokens.typography.scale(SKTypographyRole.LabelSmall)
                .toTextStyle()
                .copy(color = skTheme.tokens.colors.onSurfaceVariant.toColor()),
        )
    }
}

private fun SKImeAction.toCompose(): ImeAction = when (this) {
    SKImeAction.Default -> ImeAction.Default
    SKImeAction.Done -> ImeAction.Done
    SKImeAction.Go -> ImeAction.Go
    SKImeAction.Next -> ImeAction.Next
    SKImeAction.Previous -> ImeAction.Previous
    SKImeAction.Search -> ImeAction.Search
    SKImeAction.Send -> ImeAction.Send
    SKImeAction.None -> ImeAction.None
}

private fun SKKeyboardType.toCompose(): KeyboardType = when (this) {
    SKKeyboardType.Text -> KeyboardType.Text
    SKKeyboardType.Ascii -> KeyboardType.Ascii
    SKKeyboardType.Number -> KeyboardType.Number
    SKKeyboardType.Phone -> KeyboardType.Phone
    SKKeyboardType.Email -> KeyboardType.Email
    SKKeyboardType.Password -> KeyboardType.Password
    SKKeyboardType.Uri -> KeyboardType.Uri
}
