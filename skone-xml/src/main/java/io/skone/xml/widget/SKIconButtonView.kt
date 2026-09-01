@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.button.SKIconButtonComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toBackgroundDrawable
import java.util.UUID

/**
 * SKOne icon-only button (XML).
 *
 * Requires a meaningful description via accessibility contentDescription or
 * [SKIconKey.contentDescription]. Never announces the raw icon key alone.
 *
 * @see docs/WIDGETS_SKICONBUTTON.md
 */
public class SKIconButtonView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : AppCompatTextView(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skiconbutton-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.IconButton
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(role = "button")
        private var iconKey: SKIconKey = SKIconKey("skone.icon.placeholder")
        private var controlEnabled: Boolean = true
        private var clickListener: (() -> Unit)? = null
        private var cached: SKIconButtonComponent? = null

        private val iconButton: SKIconButtonComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKIconButtonComponent.create(
                    id = componentId,
                    icon = iconKey,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = iconButton

        init {
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            setOnClickListener {
                if (!iconButton.interactive) return@setOnClickListener
                iconButton.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKIconButtonView)
            try {
                a.getString(R.styleable.SKIconButtonView_skComponentId)?.let { componentId = it }
                val key = a.getString(R.styleable.SKIconButtonView_skIconKey) ?: "skone.icon.placeholder"
                val iconCd = a.getString(R.styleable.SKIconButtonView_skIconContentDescription)
                iconKey = SKIconKey(key, iconCd)
                controlEnabled = a.getBoolean(R.styleable.SKIconButtonView_skEnabled, true)
                a.getString(R.styleable.SKIconButtonView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKIconButtonView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { iconButton.detach() }
            this.runtime = runtime
            sync()
            iconButton.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { iconButton.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setIcon(value: SKIconKey) {
            iconKey = value
            iconButton.setIcon(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            iconButton.setEnabled(value)
            render()
        }

        public fun setAppearance(value: SKAppearanceConfig) {
            appearance = value
            sync()
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
            iconButton.setIcon(iconKey)
            iconButton.setEnabled(controlEnabled)
            iconButton.updateConfig(
                iconButton.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            isEnabled = controlEnabled
            isClickable = controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f
            background = look.toBackgroundDrawable()
            minimumWidth = look.heightPx.toInt()
            minimumHeight = look.heightPx.toInt()
            setPadding(
                (look.horizontalPaddingPx / 2).toInt(),
                (look.verticalPaddingPx / 2).toInt(),
                (look.horizontalPaddingPx / 2).toInt(),
                (look.verticalPaddingPx / 2).toInt(),
            )
            val ref = runtime?.icons?.resolve(iconKey)
            text = ref?.vectorName?.take(1) ?: "★"
            val scale = theme.tokens.typography.scale(SKTypographyRole.LabelLarge)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.size.value)
            setTextColor(look.contentColor)

            val description = accessibilityConfig.contentDescription
                ?: iconKey.contentDescription?.takeIf { it.isNotBlank() }
            // Never fall back to raw icon key — missing CD is an incomplete configuration.
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "button",
                    contentDescription = description,
                ),
                contentDescriptionFallback = description,
            )
        }
    }
