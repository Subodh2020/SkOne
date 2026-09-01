@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
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
import io.skone.ui.toggle.SKSwitchComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * SKOne switch (XML).
 *
 * @see docs/WIDGETS_SKSWITCH.md
 */
public class SKSwitchView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skswitch-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(role = "switch")
        private var labelText: String? = null
        private var checked: Boolean = false
        private var controlEnabled: Boolean = true
        private var changeListener: ((Boolean) -> Unit)? = null

        private val track = FrameLayout(context)
        private val thumb = View(context)
        private val labelView = AppCompatTextView(context)
        private var cached: SKSwitchComponent? = null

        private val switchComponent: SKSwitchComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKSwitchComponent.create(
                    id = componentId,
                    checked = checked,
                    label = labelText,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = switchComponent

        public val isChecked: Boolean
            get() = checked

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            track.addView(thumb)
            addView(track, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            setOnClickListener {
                if (!switchComponent.interactive) return@setOnClickListener
                val next = !checked
                setChecked(next)
                switchComponent.performClick()
                changeListener?.invoke(next)
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKSwitchView)
            try {
                a.getString(R.styleable.SKSwitchView_skComponentId)?.let { componentId = it }
                labelText = a.getString(R.styleable.SKSwitchView_skLabel)
                checked = a.getBoolean(R.styleable.SKSwitchView_skChecked, false)
                controlEnabled = a.getBoolean(R.styleable.SKSwitchView_skEnabled, true)
                a.getString(R.styleable.SKSwitchView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKSwitchView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { switchComponent.detach() }
            this.runtime = runtime
            sync()
            switchComponent.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { switchComponent.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setLabel(value: String?) {
            labelText = value
            switchComponent.setLabel(value)
            render()
        }

        public fun setChecked(value: Boolean) {
            checked = value
            switchComponent.setChecked(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            switchComponent.setEnabled(value)
            render()
        }

        public fun setAppearance(value: SKAppearanceConfig) {
            appearance = value
            sync()
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.role == null) value.copy(role = "switch") else value
            sync()
            render()
        }

        public fun setOnCheckedChangeListener(listener: ((Boolean) -> Unit)?) {
            changeListener = listener
        }

        private fun sync() {
            switchComponent.setLabel(labelText)
            switchComponent.setChecked(checked)
            switchComponent.setEnabled(controlEnabled)
            switchComponent.updateConfig(
                switchComponent.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val spacing = theme.tokens.spacing.sm.toPx(this).toInt()
            val pad = theme.tokens.spacing.xxs.toPx(this).toInt()
            isEnabled = controlEnabled
            isClickable = controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f

            val trackH = look.iconSizePx.toInt()
            val trackW = trackH * 2
            val thumbSize = (look.iconSizePx * 0.75f).toInt()
            track.layoutParams = LayoutParams(trackW, trackH)
            track.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = look.cornerRadiusPx.coerceAtLeast(trackH / 2f)
                setColor(
                    if (checked) look.containerColor
                    else theme.tokens.colors.color(SKColorRole.OutlineVariant).toArgb(),
                )
            }
            val thumbLp = FrameLayout.LayoutParams(thumbSize, thumbSize)
            thumbLp.gravity = if (checked) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
            thumbLp.marginStart = pad
            thumbLp.marginEnd = pad
            thumb.layoutParams = thumbLp
            thumb.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(theme.tokens.colors.color(SKColorRole.Surface).toArgb())
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
                if (checked) "On" else "Off",
            ).joinToString(", ")
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "switch",
                    stateDescription = stateText,
                ),
                contentDescriptionFallback = labelText,
            )
            ViewCompat.setStateDescription(this, stateText)
        }
    }
