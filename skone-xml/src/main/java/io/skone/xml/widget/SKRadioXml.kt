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
import io.skone.ui.selection.SKRadioButtonComponent
import io.skone.ui.selection.SKRadioGroupController
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML radio group — hosts [SKRadioButtonView] children and enforces single selection.
 *
 * @see docs/WIDGETS_SKRADIOGROUP.md
 */
public class SKRadioGroupView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private val controller = SKRadioGroupController.create()
        private var changeListener: ((String?) -> Unit)? = null
        private var groupEnabled: Boolean = true

        public val selectedValue: String?
            get() = controller.selected

        init {
            orientation = VERTICAL
            attrs?.let { applyAttributes(it) }
            controller.addListener { value ->
                syncChildren(value)
                changeListener?.invoke(value)
            }
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKRadioGroupView)
            try {
                groupEnabled = a.getBoolean(R.styleable.SKRadioGroupView_skEnabled, true)
                a.getString(R.styleable.SKRadioGroupView_skSelectedValue)?.let { controller.select(it) }
                a.getString(R.styleable.SKRadioGroupView_skTestTag)?.let { tag = it }
                a.getString(R.styleable.SKRadioGroupView_skContentDescription)?.let { contentDescription = it }
            } finally {
                a.recycle()
            }
        }

        public fun setSelectedValue(value: String?) {
            if (value == null) controller.clear() else controller.select(value)
        }

        public fun setGroupEnabled(value: Boolean) {
            groupEnabled = value
            forEachRadio { it.setControlEnabled(value && it.isControlEnabledInternal()) }
        }

        public fun setOnSelectedChangeListener(listener: ((String?) -> Unit)?) {
            changeListener = listener
        }

        internal fun requestSelect(value: String) {
            if (!groupEnabled) return
            controller.select(value)
        }

        internal fun isGroupEnabled(): Boolean = groupEnabled

        override fun onViewAdded(child: View?) {
            super.onViewAdded(child)
            if (child is SKRadioButtonView) {
                child.attachToGroup(this)
                syncChildren(controller.selected)
            }
        }

        private fun syncChildren(selected: String?) {
            forEachRadio { radio ->
                radio.setSelectedState(radio.value == selected)
            }
        }

        private fun forEachRadio(block: (SKRadioButtonView) -> Unit) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is SKRadioButtonView) block(child)
            }
        }
    }

/**
 * XML radio button — typically a child of [SKRadioGroupView].
 *
 * @see docs/WIDGETS_SKRADIOBUTTON.md
 */
public class SKRadioButtonView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skradio-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(role = "radio")
        private var radioValue: String = "option"
        private var labelText: String? = null
        private var selected: Boolean = false
        private var controlEnabled: Boolean = true
        private var group: SKRadioGroupView? = null
        private var clickListener: (() -> Unit)? = null
        private var cached: SKRadioButtonComponent? = null

        private val ringView = AppCompatTextView(context)
        private val labelView = AppCompatTextView(context)

        private val radio: SKRadioButtonComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKRadioButtonComponent.create(
                    id = componentId,
                    value = radioValue,
                    selected = selected,
                    label = labelText,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = radio

        public val value: String
            get() = radioValue

        public val isSelectedState: Boolean
            get() = selected

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            addView(ringView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            setOnClickListener {
                if (!effectiveEnabled()) return@setOnClickListener
                val g = group
                if (g != null) {
                    g.requestSelect(radioValue)
                } else {
                    setSelectedState(true)
                }
                radio.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKRadioButtonView)
            try {
                a.getString(R.styleable.SKRadioButtonView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKRadioButtonView_skValue)?.let { radioValue = it }
                labelText = a.getString(R.styleable.SKRadioButtonView_skLabel)
                selected = a.getBoolean(R.styleable.SKRadioButtonView_skSelected, false)
                controlEnabled = a.getBoolean(R.styleable.SKRadioButtonView_skEnabled, true)
                a.getString(R.styleable.SKRadioButtonView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKRadioButtonView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        internal fun attachToGroup(group: SKRadioGroupView) {
            this.group = group
        }

        internal fun isControlEnabledInternal(): Boolean = controlEnabled

        internal fun setSelectedState(value: Boolean) {
            selected = value
            radio.setSelected(value)
            render()
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { radio.detach() }
            this.runtime = runtime
            sync()
            radio.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { radio.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setRadioValue(value: String) {
            radioValue = value
            radio.setValue(value)
        }

        public fun setLabel(value: String?) {
            labelText = value
            radio.setLabel(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            radio.setEnabled(effectiveEnabled())
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.role == null) value.copy(role = "radio") else value
            sync()
            render()
        }

        public fun setOnSkClickListener(listener: (() -> Unit)?) {
            clickListener = listener
        }

        private fun effectiveEnabled(): Boolean =
            controlEnabled && (group?.isGroupEnabled() ?: true)

        private fun sync() {
            radio.setValue(radioValue)
            radio.setLabel(labelText)
            radio.setSelected(selected)
            radio.setEnabled(effectiveEnabled())
            radio.updateConfig(
                radio.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val spacing = theme.tokens.spacing.sm.toPx(this).toInt()
            val stroke = theme.tokens.spacing.xxs.toPx(this).toInt().coerceAtLeast(1)
            val enabled = effectiveEnabled()
            isEnabled = enabled
            isClickable = enabled
            alpha = if (enabled) 1f else 0.38f

            val size = look.iconSizePx.toInt()
            val lp = ringView.layoutParams as LayoutParams
            lp.width = size
            lp.height = size
            ringView.layoutParams = lp
            ringView.gravity = Gravity.CENTER
            ringView.text = if (selected) "●" else ""
            ringView.setTextColor(look.containerColor)
            ringView.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(theme.tokens.colors.color(SKColorRole.Surface).toArgb())
                setStroke(stroke, if (selected) look.containerColor else (look.outlineColor ?: look.containerColor))
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
                if (selected) "Selected" else "Not selected",
            ).joinToString(", ")
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "radio",
                    stateDescription = stateText,
                ),
                contentDescriptionFallback = labelText,
            )
            ViewCompat.setStateDescription(this, stateText)
            isSelected = selected
        }
    }
