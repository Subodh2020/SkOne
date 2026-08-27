@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.component.validation.SKValidationResult
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
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * SKOne text field (XML / Views) — paired with Compose [io.skone.compose.widget.SKTextField].
 *
 * Visuals resolve through appearance + [SKThemeHelper] tokens only.
 * Call [bind] with an optional [SKFormController] to auto-register.
 *
 * @see docs/WIDGETS_SKTEXTFIELD.md
 */
public class SKTextFieldView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var form: SKFormController? = null
        private var fieldId: String = "sktextfield-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.TextField
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var labelText: String? = null
        private var hintText: String? = null
        private var supporting: String? = null
        private var required: Boolean = false
        private var readOnly: Boolean = false
        private var fieldEnabled: Boolean = true
        private var singleLine: Boolean = true
        private var maxLinesValue: Int = 1
        private var imeAction: SKImeAction = SKImeAction.Default
        private var keyboardType: SKKeyboardType = SKKeyboardType.Text
        private var formatter: SKFormatter? = null
        private var mask: SKInputMask? = null
        private var rules: List<SKValidationRule> = emptyList()
        private var leadingIcon: SKIconKey? = null
        private var trailingIcon: SKIconKey? = null
        private var suppressWatcher: Boolean = false

        private val labelView = AppCompatTextView(context)
        private val editText: AppCompatEditText = AppCompatEditText(context)
        private val supportingView = AppCompatTextView(context)

        private var cachedComponent: SKTextFieldComponent? = null

        private val fieldComponent: SKTextFieldComponent
            get() {
                val existing = cachedComponent
                if (existing != null && existing.id == fieldId) return existing
                if (existing != null && existing.isAttached) {
                    existing.detach()
                }
                return SKTextFieldComponent.create(
                    id = fieldId,
                    initialValue = editText.text?.toString().orEmpty(),
                    label = labelText,
                    hint = hintText,
                    supportingText = supporting,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    required = required,
                    readOnly = readOnly,
                    enabled = fieldEnabled,
                    appearance = appearance,
                    formatter = formatter,
                    mask = mask,
                    rules = rules,
                    imeAction = imeAction,
                    keyboardType = keyboardType,
                    singleLine = singleLine,
                    accessibility = accessibilityConfig,
                ).also { cachedComponent = it }
            }

        /** Framework component for lifecycle / analytics / plugins. */
        public val component: SKComponent
            get() = fieldComponent

        /** Underlying editable for advanced hosts. */
        public val input: EditText
            get() = editText

        init {
            orientation = VERTICAL
            attrs?.let { applyAttributes(it) }
            addView(labelView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(editText, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(supportingView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            editText.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) = Unit

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) = Unit

                    override fun afterTextChanged(s: Editable?) {
                        if (suppressWatcher || !fieldEnabled || readOnly) return
                        val raw = s?.toString().orEmpty()
                        val controller = form
                        if (controller != null) {
                            controller.updateRawInput(fieldId, raw)
                            val display = controller.registry.state(fieldId)?.displayValue ?: raw
                            if (display != raw) {
                                suppressWatcher = true
                                editText.setText(display)
                                editText.setSelection(display.length)
                                suppressWatcher = false
                            }
                        } else {
                            fieldComponent.setValue(raw, fromUser = true)
                        }
                        valueListener?.invoke(editText.text?.toString().orEmpty())
                    }
                },
            )
            editText.setOnFocusChangeListener { _, hasFocus ->
                fieldComponent.onFocusChanged(hasFocus)
                if (hasFocus) {
                    form?.requestFocus(fieldId)
                } else {
                    refreshValidationVisuals()
                }
                applyChrome()
            }
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == imeAction.toEditorAction() || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    when (imeAction) {
                        SKImeAction.Next -> form?.focus?.focusNext(fieldId)
                        SKImeAction.Previous -> form?.focus?.focusPrevious(fieldId)
                        SKImeAction.Done -> form?.clearFocus()
                        else -> Unit
                    }
                    imeListener?.invoke(imeAction)
                    true
                } else {
                    false
                }
            }
            render()
        }

        private var valueListener: ((String) -> Unit)? = null
        private var imeListener: ((SKImeAction) -> Unit)? = null

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKTextFieldView)
            try {
                a.getString(R.styleable.SKTextFieldView_skFieldId)?.let { fieldId = it }
                labelText = a.getString(R.styleable.SKTextFieldView_skLabel)
                hintText = a.getString(R.styleable.SKTextFieldView_skHint)
                supporting = a.getString(R.styleable.SKTextFieldView_skSupportingText)
                a.getString(R.styleable.SKTextFieldView_skValue)?.let {
                    suppressWatcher = true
                    editText.setText(it)
                    suppressWatcher = false
                }
                required = a.getBoolean(R.styleable.SKTextFieldView_skRequired, false)
                readOnly = a.getBoolean(R.styleable.SKTextFieldView_skReadOnly, false)
                singleLine = a.getBoolean(R.styleable.SKTextFieldView_skSingleLine, true)
                if (a.hasValue(R.styleable.SKTextFieldView_skMaxLines)) {
                    maxLinesValue = a.getInt(R.styleable.SKTextFieldView_skMaxLines, 1).coerceAtLeast(1)
                } else {
                    maxLinesValue = if (singleLine) 1 else Int.MAX_VALUE
                }
                imeAction =
                    when (a.getInt(R.styleable.SKTextFieldView_skImeAction, 0)) {
                        1 -> SKImeAction.Done
                        2 -> SKImeAction.Go
                        3 -> SKImeAction.Next
                        4 -> SKImeAction.Previous
                        5 -> SKImeAction.Search
                        6 -> SKImeAction.Send
                        7 -> SKImeAction.None
                        else -> SKImeAction.Default
                    }
                keyboardType =
                    when (a.getInt(R.styleable.SKTextFieldView_skKeyboardType, 0)) {
                        1 -> SKKeyboardType.Ascii
                        2 -> SKKeyboardType.Number
                        3 -> SKKeyboardType.Phone
                        4 -> SKKeyboardType.Email
                        5 -> SKKeyboardType.Password
                        6 -> SKKeyboardType.Uri
                        else -> SKKeyboardType.Text
                    }
                a.getString(R.styleable.SKTextFieldView_skContentDescription)?.let {
                    accessibilityConfig = SKAccessibilityConfig(contentDescription = it)
                }
            } finally {
                a.recycle()
            }
        }

        /**
         * Binds framework runtime and optionally a form controller (auto-register).
         */
        @JvmOverloads
        public fun bind(
            runtime: SKComponentRuntime,
            form: SKFormController? = null,
        ) {
            this.runtime?.let { fieldComponent.detach() }
            this.form?.let { fieldComponent.ensureUnregistered(it) }
            this.runtime = runtime
            this.form = form
            syncComponent()
            fieldComponent.attach(runtime)
            form?.let { fieldComponent.ensureRegistered(it) }
            render()
        }

        /** Unbinds runtime and form registration. */
        public fun unbind() {
            form?.let { fieldComponent.ensureUnregistered(it) }
            form = null
            runtime?.let { fieldComponent.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setFieldId(id: String) {
            fieldId = id
        }

        public fun setSkValue(value: String) {
            suppressWatcher = true
            editText.setText(value)
            suppressWatcher = false
            form?.updateValue(fieldId, value) ?: fieldComponent.setValue(value, fromUser = false)
        }

        public fun getSkValue(): String = editText.text?.toString().orEmpty()

        public fun setLabel(value: String?) {
            labelText = value
            fieldComponent.setLabel(value)
            render()
        }

        public fun setHint(value: String?) {
            hintText = value
            fieldComponent.setHint(value)
            render()
        }

        public fun setSupportingText(value: String?) {
            supporting = value
            fieldComponent.setSupportingText(value)
            render()
        }

        public fun setRequired(value: Boolean) {
            required = value
            syncComponent()
            render()
        }

        public fun setReadOnly(value: Boolean) {
            readOnly = value
            syncComponent()
            render()
        }

        public fun setFieldEnabled(value: Boolean) {
            fieldEnabled = value
            syncComponent()
            render()
        }

        public fun setAppearance(value: SKAppearanceConfig) {
            appearance = value
            syncComponent()
            render()
        }

        public fun setFormatter(value: SKFormatter?) {
            formatter = value
            fieldComponent.setFormatter(value)
        }

        public fun setMask(value: SKInputMask?) {
            mask = value
            fieldComponent.setMask(value)
        }

        public fun setRules(value: List<SKValidationRule>) {
            rules = value
            fieldComponent.setRules(value)
        }

        public fun setLeadingIcon(value: SKIconKey?) {
            leadingIcon = value
            fieldComponent.setLeadingIcon(value)
        }

        public fun setTrailingIcon(value: SKIconKey?) {
            trailingIcon = value
            fieldComponent.setTrailingIcon(value)
        }

        public fun setImeAction(value: SKImeAction) {
            imeAction = value
            fieldComponent.setImeAction(value)
            applyIme()
        }

        public fun setKeyboardType(value: SKKeyboardType) {
            keyboardType = value
            fieldComponent.setKeyboardType(value)
            applyKeyboard()
        }

        public fun setOnValueChangeListener(listener: ((String) -> Unit)?) {
            valueListener = listener
        }

        public fun setOnImeActionListener(listener: ((SKImeAction) -> Unit)?) {
            imeListener = listener
        }

        public fun setVisualState(state: SKFieldVisualState) {
            fieldComponent.setVisualState(state)
            render()
        }

        public fun applyValidationResult(result: SKValidationResult) {
            fieldComponent.applyValidationResult(result)
            render()
        }

        private fun syncComponent() {
            fieldComponent.setLabel(labelText)
            fieldComponent.setHint(hintText)
            fieldComponent.setSupportingText(supporting)
            fieldComponent.setLeadingIcon(leadingIcon)
            fieldComponent.setTrailingIcon(trailingIcon)
            fieldComponent.setFormatter(formatter)
            fieldComponent.setMask(mask)
            fieldComponent.setRules(rules)
            fieldComponent.setImeAction(imeAction)
            fieldComponent.setKeyboardType(keyboardType)
            fieldComponent.setSingleLine(singleLine)
            fieldComponent.setMaxLines(maxLinesValue)
            fieldComponent.updateConfig(
                SKTextFieldComponent.defaultConfig(
                    required = required,
                    readOnly = readOnly,
                    enabled = fieldEnabled,
                    accessibility = accessibilityConfig,
                    appearance = appearance,
                ),
            )
        }

        private fun refreshValidationVisuals() {
            val controller = form ?: return
            val errs = controller.errors.errorsFor(fieldId)
            if (errs.isNotEmpty()) {
                fieldComponent.applyValidationResult(SKValidationResult.Invalid(errs))
            } else if (fieldComponent.visualState == SKFieldVisualState.Error) {
                fieldComponent.setVisualState(SKFieldVisualState.None)
            }
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val spacingPx = theme.tokens.spacing.xs.toPx(this).toInt()

            val showLabel = !labelText.isNullOrBlank()
            labelView.visibility = if (showLabel) View.VISIBLE else View.GONE
            if (showLabel) {
                labelView.text = if (required) "$labelText *" else labelText
                val labelScale = theme.tokens.typography.scale(SKTypographyRole.LabelMedium)
                labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, labelScale.size.value)
                labelView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
                val labelColor =
                    if (fieldComponent.visualState == SKFieldVisualState.Error) {
                        SKColorRole.Error
                    } else {
                        SKColorRole.OnSurfaceVariant
                    }
                labelView.setTextColor(theme.tokens.colors.color(labelColor).toArgb())
                (labelView.layoutParams as LayoutParams).bottomMargin = spacingPx
            }

            applyChrome()
            applyKeyboard()
            applyIme()
            editText.hint = hintText
            editText.isEnabled = fieldEnabled
            editText.isFocusable = fieldEnabled && !readOnly
            editText.isFocusableInTouchMode = fieldEnabled && !readOnly
            editText.isCursorVisible = fieldEnabled && !readOnly
            if (singleLine) {
                editText.setSingleLine(true)
                editText.maxLines = 1
            } else {
                editText.setSingleLine(false)
                editText.maxLines = maxLinesValue
            }

            val supportText = fieldComponent.fieldSupportingText ?: supporting
            val showSupport = !supportText.isNullOrBlank()
            supportingView.visibility = if (showSupport) View.VISIBLE else View.GONE
            if (showSupport) {
                supportingView.text = supportText
                val supportScale = theme.tokens.typography.scale(SKTypographyRole.BodySmall)
                supportingView.setTextSize(TypedValue.COMPLEX_UNIT_SP, supportScale.size.value)
                val supportColor =
                    when (fieldComponent.visualState) {
                        SKFieldVisualState.Error -> SKColorRole.Error
                        SKFieldVisualState.Success -> SKColorRole.Primary
                        SKFieldVisualState.None -> SKColorRole.OnSurfaceVariant
                    }
                supportingView.setTextColor(theme.tokens.colors.color(supportColor).toArgb())
                (supportingView.layoutParams as LayoutParams).topMargin = spacingPx
            }

            val description =
                accessibilityConfig.contentDescription
                    ?: labelText
                    ?: hintText
                    ?: "Text field"
            contentDescription = description
            editText.contentDescription = description
            importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        }

        private fun applyChrome() {
            val theme = SKThemeHelper.current()
            val resolved = fieldComponent.resolvedAppearance()
            val look = resolved.resolve(theme, this)
            val stroke =
                if (editText.hasFocus()) {
                    theme.tokens.spacing.xs.toPx(this)
                } else {
                    theme.tokens.spacing.xxs.toPx(this)
                }
            editText.background = look.toBackgroundDrawable(strokeWidthPx = stroke)
            editText.setTextColor(look.contentColor)
            editText.setHintTextColor(theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb())
            val body = theme.tokens.typography.scale(resolved.typographyRole ?: SKTypographyRole.BodyLarge)
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, body.size.value)
            editText.minHeight = look.heightPx.toInt()
            editText.setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )
            editText.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            look.elevationPx?.let { editText.elevation = it }
        }

        private fun applyKeyboard() {
            var type =
                when (keyboardType) {
                    SKKeyboardType.Text -> InputType.TYPE_CLASS_TEXT
                    SKKeyboardType.Ascii -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    SKKeyboardType.Number -> InputType.TYPE_CLASS_NUMBER
                    SKKeyboardType.Phone -> InputType.TYPE_CLASS_PHONE
                    SKKeyboardType.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    SKKeyboardType.Password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    SKKeyboardType.Uri -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
                }
            if (singleLine && keyboardType == SKKeyboardType.Text) {
                type = type or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            }
            editText.inputType = type
        }

        private fun applyIme() {
            editText.imeOptions = imeAction.toEditorAction()
        }

        private fun SKImeAction.toEditorAction(): Int =
            when (this) {
                SKImeAction.Default -> EditorInfo.IME_ACTION_UNSPECIFIED
                SKImeAction.Done -> EditorInfo.IME_ACTION_DONE
                SKImeAction.Go -> EditorInfo.IME_ACTION_GO
                SKImeAction.Next -> EditorInfo.IME_ACTION_NEXT
                SKImeAction.Previous -> EditorInfo.IME_ACTION_PREVIOUS
                SKImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
                SKImeAction.Send -> EditorInfo.IME_ACTION_SEND
                SKImeAction.None -> EditorInfo.IME_ACTION_NONE
            }
    }
