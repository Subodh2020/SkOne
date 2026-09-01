@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.toggle.SKCheckboxComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * SKOne checkbox (XML). Binary checked only — indeterminate deferred.
 *
 * @see docs/WIDGETS_SKCHECKBOX.md
 */
public class SKCheckboxView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skcheckbox-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(role = "checkbox")
        private var labelText: String? = null
        private var checked: Boolean = false
        private var controlEnabled: Boolean = true
        private var changeListener: ((Boolean) -> Unit)? = null

        private val boxView = AppCompatTextView(context)
        private val labelView = AppCompatTextView(context)
        private var cached: SKCheckboxComponent? = null

        private val checkbox: SKCheckboxComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKCheckboxComponent.create(
                    id = componentId,
                    checked = checked,
                    label = labelText,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = checkbox

        public val isChecked: Boolean
            get() = checked

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            addView(boxView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            setOnClickListener {
                if (!checkbox.interactive) return@setOnClickListener
                val next = !checked
                setChecked(next)
                checkbox.performClick()
                changeListener?.invoke(next)
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKCheckboxView)
            try {
                a.getString(R.styleable.SKCheckboxView_skComponentId)?.let { componentId = it }
                labelText = a.getString(R.styleable.SKCheckboxView_skLabel)
                checked = a.getBoolean(R.styleable.SKCheckboxView_skChecked, false)
                controlEnabled = a.getBoolean(R.styleable.SKCheckboxView_skEnabled, true)
                a.getString(R.styleable.SKCheckboxView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKCheckboxView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { checkbox.detach() }
            this.runtime = runtime
            sync()
            checkbox.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { checkbox.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setLabel(value: String?) {
            labelText = value
            checkbox.setLabel(value)
            render()
        }

        public fun setChecked(value: Boolean) {
            checked = value
            checkbox.setChecked(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            checkbox.setEnabled(value)
            render()
        }

        public fun setAppearance(value: SKAppearanceConfig) {
            appearance = value
            sync()
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.role == null) value.copy(role = "checkbox") else value
            sync()
            render()
        }

        public fun setOnCheckedChangeListener(listener: ((Boolean) -> Unit)?) {
            changeListener = listener
        }

        private fun sync() {
            checkbox.setLabel(labelText)
            checkbox.setChecked(checked)
            checkbox.setEnabled(controlEnabled)
            checkbox.updateConfig(
                checkbox.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val spacing = theme.tokens.spacing.sm.toPx(this).toInt()
            val stroke = theme.tokens.spacing.xxs.toPx(this).toInt().coerceAtLeast(1)
            isEnabled = controlEnabled
            isClickable = controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f

            val boxSize = look.iconSizePx.toInt()
            val lp = boxView.layoutParams as LayoutParams
            lp.width = boxSize
            lp.height = boxSize
            boxView.layoutParams = lp
            boxView.gravity = Gravity.CENTER
            boxView.text = if (checked) "✓" else ""
            boxView.setTextColor(look.contentColor)
            boxView.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = look.cornerRadiusPx
                setColor(
                    if (checked) look.containerColor
                    else theme.tokens.colors.color(SKColorRole.Surface).toArgb(),
                )
                setStroke(stroke, look.outlineColor ?: look.containerColor)
            }

            val showLabel = !labelText.isNullOrBlank()
            labelView.visibility = if (showLabel) View.VISIBLE else View.GONE
            if (showLabel) {
                labelView.text = labelText
                val scale = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge)
                labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.size.value)
                labelView.setTypeface(Typeface.SANS_SERIF)
                labelView.setTextColor(theme.tokens.colors.color(SKColorRole.OnSurface).toArgb())
                (labelView.layoutParams as LayoutParams).marginStart = spacing
            }

            val stateText = listOfNotNull(
                accessibilityConfig.stateDescription?.takeIf { it.isNotBlank() },
                if (checked) "Checked" else "Unchecked",
            ).joinToString(", ")
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "checkbox",
                    stateDescription = stateText,
                ),
                contentDescriptionFallback = labelText,
            )
            ViewCompat.setStateDescription(this, stateText)
        }
    }
