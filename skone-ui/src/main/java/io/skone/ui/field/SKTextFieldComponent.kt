@file:OptIn(
    io.skone.common.annotation.SKExperimental::class,
    io.skone.common.annotation.SKInternal::class,
)

package io.skone.ui.field

import io.skone.common.annotation.SKInternal
import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInputComponent
import io.skone.component.framework.icon.SKIconKey
import io.skone.component.validation.SKValidationConfig
import io.skone.component.validation.SKValidationResult
import io.skone.component.validation.SKValidationTrigger
import io.skone.forms.SKFormController
import io.skone.forms.field.SKFormField
import io.skone.forms.formatter.SKFormatter
import io.skone.forms.mask.SKInputMask
import io.skone.forms.validation.SKRequiredRule
import io.skone.forms.validation.SKValidationRule
import io.skone.theme.state.SKComponentState
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Shared SKTextField contract (no UI).
 *
 * Compose [io.skone.compose.widget.SKTextField] and XML [io.skone.xml.widget.SKTextFieldView]
 * wrap this component and optionally auto-register with [SKFormController].
 *
 * @see docs/WIDGETS_SKTEXTFIELD.md
 */
public class SKTextFieldComponent(
    id: String,
    initialValue: String = "",
    config: SKComponentConfig = defaultConfig(),
    label: String? = null,
    hint: String? = null,
    supportingText: String? = null,
    leadingIcon: SKIconKey? = null,
    trailingIcon: SKIconKey? = null,
    visualState: SKFieldVisualState = SKFieldVisualState.None,
    imeAction: SKImeAction = SKImeAction.Default,
    keyboardType: SKKeyboardType = SKKeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    formatter: SKFormatter? = null,
    mask: SKInputMask? = null,
    rules: List<SKValidationRule> = emptyList(),
) : SKBaseInputComponent<String>(
    id = id,
    componentType = COMPONENT_TYPE,
    initialValue = initialValue,
    config = config,
) {
    private val labelRef = AtomicReference(label)
    private val hintRef = AtomicReference(hint)
    private val supportingRef = AtomicReference(supportingText)
    private val leadingRef = AtomicReference(leadingIcon)
    private val trailingRef = AtomicReference(trailingIcon)
    private val visualRef = AtomicReference(visualState)
    private val imeRef = AtomicReference(imeAction)
    private val keyboardRef = AtomicReference(keyboardType)
    private val singleLineRef = AtomicBoolean(singleLine)
    private val maxLinesRef = AtomicReference(maxLines)
    private val formatterRef = AtomicReference(formatter)
    private val maskRef = AtomicReference(mask)
    private val rulesRef = AtomicReference(rules)
    private val formRegistered = AtomicBoolean(false)

    public val label: String? get() = labelRef.get()
    public val hint: String? get() = hintRef.get()
    public val fieldSupportingText: String? get() = supportingRef.get()
    public val leadingIcon: SKIconKey? get() = leadingRef.get()
    public val trailingIcon: SKIconKey? get() = trailingRef.get()
    public val visualState: SKFieldVisualState get() = visualRef.get()
    public val imeAction: SKImeAction get() = imeRef.get()
    public val keyboardType: SKKeyboardType get() = keyboardRef.get()
    public val singleLine: Boolean get() = singleLineRef.get()
    public val maxLines: Int get() = maxLinesRef.get()
    public val formatter: SKFormatter? get() = formatterRef.get()
    public val mask: SKInputMask? get() = maskRef.get()
    public val rules: List<SKValidationRule> get() = rulesRef.get()

    public fun setLabel(value: String?) { labelRef.set(value) }
    public fun setHint(value: String?) { hintRef.set(value) }
    public fun setSupportingText(value: String?) { supportingRef.set(value) }
    public fun setLeadingIcon(value: SKIconKey?) { leadingRef.set(value) }
    public fun setTrailingIcon(value: SKIconKey?) { trailingRef.set(value) }
    public fun setVisualState(value: SKFieldVisualState) {
        visualRef.set(value)
        publishState(config.state.copy(error = value == SKFieldVisualState.Error))
    }
    public fun setImeAction(value: SKImeAction) { imeRef.set(value) }
    public fun setKeyboardType(value: SKKeyboardType) { keyboardRef.set(value) }
    public fun setSingleLine(value: Boolean) { singleLineRef.set(value) }
    public fun setMaxLines(value: Int) {
        require(value >= 1)
        maxLinesRef.set(value)
    }
    public fun setFormatter(value: SKFormatter?) { formatterRef.set(value) }
    public fun setMask(value: SKInputMask?) { maskRef.set(value) }
    public fun setRules(value: List<SKValidationRule>) { rulesRef.set(value) }

    /**
     * Builds the [SKFormField] descriptor used for auto-registration.
     *
     * Bridge plumbing for Compose/XML hosts; not intended for application use.
     */
    @SKInternal
    public fun toFormField(): SKFormField {
        val requiredRules = if (config.required && rules.none { it.id == "required" }) {
            listOf(SKRequiredRule()) + rules
        } else {
            rules
        }
        return SKFormField(
            id = id,
            initialValue = value,
            rules = requiredRules,
            formatter = formatter,
            mask = mask,
            triggers = config.validation.triggers.ifEmpty {
                setOf(SKValidationTrigger.OnFocusLost, SKValidationTrigger.OnSubmit)
            },
            ai = config.ai,
        )
    }

    /**
     * Registers with [form] once. Safe to call multiple times.
     *
     * Bridge plumbing for Compose/XML hosts; not intended for application use.
     */
    @SKInternal
    public fun ensureRegistered(form: SKFormController) {
        if (formRegistered.compareAndSet(false, true)) {
            if (form.registry.get(id) == null) {
                form.register(toFormField())
            }
        }
    }

    /**
     * Unregisters from [form] if previously registered by this component.
     *
     * Bridge plumbing for Compose/XML hosts; not intended for application use.
     */
    @SKInternal
    public fun ensureUnregistered(form: SKFormController) {
        if (formRegistered.compareAndSet(true, false)) {
            form.unregister(id)
        }
    }

    /**
     * Applies a validation result to visual state and supporting text preference.
     */
    public fun applyValidationResult(result: SKValidationResult) {
        when (result) {
            is SKValidationResult.Valid -> {
                setVisualState(SKFieldVisualState.Success)
            }
            is SKValidationResult.Invalid -> {
                setVisualState(SKFieldVisualState.Error)
                if (config.validation.supportingTextOnError) {
                    setSupportingText(result.errors.firstOrNull()?.message)
                }
            }
        }
    }

    /**
     * Resolved appearance considering error/success overrides.
     */
    public fun resolvedAppearance(): SKAppearanceConfig = when (visualState) {
        SKFieldVisualState.Error -> SKAppearanceConfig.TextFieldError
        SKFieldVisualState.Success -> SKAppearanceConfig.TextFieldSuccess
        SKFieldVisualState.None -> config.appearance
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKTextField"

        /**
         * Shared [SKComponentConfig] factory for SKTextField bridges.
         *
         * Bridge plumbing; prefer [create] / widget parameters for application code.
         */
        @SKInternal
        public fun defaultConfig(
            required: Boolean = false,
            readOnly: Boolean = false,
            enabled: Boolean = true,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
            appearance: SKAppearanceConfig = SKAppearanceConfig.TextField,
        ): SKComponentConfig = SKComponentConfig(
            state = SKComponentState(
                enabled = enabled,
                readOnly = readOnly,
            ),
            appearance = appearance,
            behavior = SKBehaviorConfig(
                enabled = enabled,
                clickable = false,
                focusable = true,
            ),
            validation = if (required) SKValidationConfig.Required else SKValidationConfig.None,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            initialValue: String = "",
            label: String? = null,
            hint: String? = null,
            supportingText: String? = null,
            leadingIcon: SKIconKey? = null,
            trailingIcon: SKIconKey? = null,
            required: Boolean = false,
            readOnly: Boolean = false,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.TextField,
            formatter: SKFormatter? = null,
            mask: SKInputMask? = null,
            rules: List<SKValidationRule> = emptyList(),
            imeAction: SKImeAction = SKImeAction.Default,
            keyboardType: SKKeyboardType = SKKeyboardType.Text,
            singleLine: Boolean = true,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKTextFieldComponent = SKTextFieldComponent(
            id = id,
            initialValue = initialValue,
            config = defaultConfig(
                required = required,
                readOnly = readOnly,
                enabled = enabled,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
                appearance = appearance,
            ),
            label = label,
            hint = hint,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            imeAction = imeAction,
            keyboardType = keyboardType,
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
            formatter = formatter,
            mask = mask,
            rules = rules,
        )
    }
}
