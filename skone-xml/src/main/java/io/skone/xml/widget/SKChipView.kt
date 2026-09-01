@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
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
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.selection.SKChipComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * Foundational SKOne chip (XML). Specialized chip variants deferred.
 *
 * @see docs/WIDGETS_SKCHIP.md
 */
public class SKChipView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skchip-${UUID.randomUUID()}"
        private var labelText: String = "Chip"
        private var selected: Boolean = false
        private var controlEnabled: Boolean = true
        private var leadingIcon: SKIconKey? = null
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(role = "button")
        private var clickListener: (() -> Unit)? = null
        private var cached: SKChipComponent? = null

        private val iconView = AppCompatTextView(context)
        private val labelView = AppCompatTextView(context)

        private val chip: SKChipComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKChipComponent.create(
                    id = componentId,
                    label = labelText,
                    selected = selected,
                    leadingIcon = leadingIcon,
                    enabled = controlEnabled,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = chip

        public val isSelectedState: Boolean
            get() = selected

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            addView(iconView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            iconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            setOnClickListener {
                if (!chip.interactive) return@setOnClickListener
                chip.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKChipView)
            try {
                a.getString(R.styleable.SKChipView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKChipView_skLabel)?.let { labelText = it }
                selected = a.getBoolean(R.styleable.SKChipView_skSelected, false)
                controlEnabled = a.getBoolean(R.styleable.SKChipView_skEnabled, true)
                a.getString(R.styleable.SKChipView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKChipView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { chip.detach() }
            this.runtime = runtime
            sync()
            chip.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { chip.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setSkLabel(value: String) {
            labelText = value
            chip.setLabel(value)
            render()
        }

        public fun setSelectedState(value: Boolean) {
            selected = value
            chip.setSelected(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            chip.setEnabled(value)
            render()
        }

        public fun setLeadingIcon(value: SKIconKey?) {
            leadingIcon = value
            chip.setLeadingIcon(value)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.role == null) value.copy(role = "button") else value
            sync()
            render()
        }

        public fun setOnSkClickListener(listener: (() -> Unit)?) {
            clickListener = listener
        }

        private fun sync() {
            chip.setLabel(labelText)
            chip.setSelected(selected)
            chip.setEnabled(controlEnabled)
            chip.setLeadingIcon(leadingIcon)
            val appearance = if (selected) SKAppearanceConfig.ChipSelected else SKAppearanceConfig.Chip
            chip.updateConfig(
                chip.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val appearance = if (selected) SKAppearanceConfig.ChipSelected else SKAppearanceConfig.Chip
            val look = appearance.resolve(theme, this)
            val spacing = theme.tokens.spacing.xs.toPx(this).toInt()
            isEnabled = controlEnabled
            isClickable = controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f
            background = look.toBackgroundDrawable(
                strokeWidthPx = if (look.outlineColor != null) theme.tokens.spacing.xxs.toPx(this) else 0f,
            )
            minimumHeight = look.heightPx.toInt()
            setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )

            val icon = leadingIcon
            if (icon == null) {
                iconView.visibility = View.GONE
            } else {
                iconView.visibility = View.VISIBLE
                val ref = runtime?.icons?.resolve(icon)
                iconView.text = ref?.vectorName?.take(1) ?: "•"
                iconView.setTextColor(look.contentColor)
                iconView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(SKTypographyRole.LabelSmall).size.value,
                )
                val explicitCd = icon.contentDescription?.takeIf { it.isNotBlank() }
                if (explicitCd != null) {
                    iconView.contentDescription = explicitCd
                    iconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    iconView.contentDescription = null
                    iconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
                }
                (iconView.layoutParams as LayoutParams).marginEnd = spacing
            }

            labelView.text = labelText
            labelView.setTypeface(Typeface.SANS_SERIF)
            labelView.setTextColor(look.contentColor)
            labelView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelLarge).size.value,
            )

            val stateText = listOfNotNull(
                accessibilityConfig.stateDescription?.takeIf { it.isNotBlank() },
                if (selected) "Selected" else null,
            ).takeIf { it.isNotEmpty() }?.joinToString(", ")
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "button",
                    stateDescription = stateText,
                ),
                contentDescriptionFallback = labelText,
            )
            ViewCompat.setStateDescription(this, stateText)
            isSelected = selected
        }
    }
