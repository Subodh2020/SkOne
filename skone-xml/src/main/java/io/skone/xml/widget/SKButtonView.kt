@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.button.SKButtonComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * SKOne button (XML / Views) — paired with Compose [io.skone.compose.widget.SKButton].
 *
 * Visuals resolve through appearance + [SKThemeHelper] tokens only.
 * Call [bind] with a [SKComponentRuntime] from the host Activity/Fragment.
 *
 * @see docs/WIDGETS_SKBUTTON.md
 */
public class SKButtonView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skbutton-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Button
        private var accessibilityConfig: SKAccessibilityConfig =
            SKAccessibilityConfig(role = "button")
        private var labelText: String = ""
        private var leadingIcon: SKIconKey? = null
        private var buttonEnabled: Boolean = true
        private var loading: Boolean = false
        private var clickListener: (() -> Unit)? = null

        private val leadingIconView = AppCompatTextView(context)
        private val loadingView = ProgressBar(context).apply {
            isIndeterminate = true
            visibility = View.GONE
        }
        private val labelView = AppCompatTextView(context)

        private var cachedComponent: SKButtonComponent? = null

        private val buttonComponent: SKButtonComponent
            get() {
                val existing = cachedComponent
                if (existing != null && existing.id == componentId) return existing
                return SKButtonComponent.create(
                    id = componentId,
                    text = labelText,
                    leadingIcon = leadingIcon,
                    enabled = buttonEnabled,
                    loading = loading,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cachedComponent = it }
            }

        /** Framework component for lifecycle / analytics / plugins. */
        public val component: SKComponent
            get() = buttonComponent

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            val iconParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            addView(leadingIconView, iconParams)
            addView(loadingView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            leadingIconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            loadingView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            setOnClickListener {
                if (!buttonComponent.interactive) return@setOnClickListener
                buttonComponent.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKButtonView)
            try {
                a.getString(R.styleable.SKButtonView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKButtonView_skText)?.let { labelText = it }
                buttonEnabled = a.getBoolean(R.styleable.SKButtonView_skEnabled, true)
                loading = a.getBoolean(R.styleable.SKButtonView_skLoading, false)
                appearance = when (a.getInt(R.styleable.SKButtonView_skButtonVariant, 0)) {
                    1 -> SKAppearanceConfig.ButtonTonal
                    2 -> SKAppearanceConfig.ButtonOutlined
                    3 -> SKAppearanceConfig.ButtonText
                    else -> SKAppearanceConfig.Button
                }
                a.getString(R.styleable.SKButtonView_skContentDescription)?.let { cd ->
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = cd)
                }
                a.getString(R.styleable.SKButtonView_skTestTag)?.let { tag ->
                    accessibilityConfig = accessibilityConfig.copy(testTag = tag)
                }
            } finally {
                a.recycle()
            }
        }

        /** Binds framework runtime (lifecycle, analytics, plugins). */
        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { buttonComponent.detach() }
            this.runtime = runtime
            syncComponent()
            buttonComponent.attach(runtime)
            render()
        }

        /** Unbinds runtime. */
        public fun unbind() {
            runtime?.let { buttonComponent.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setSkText(value: String) {
            labelText = value
            buttonComponent.setText(value)
            render()
        }

        public fun setLeadingIcon(value: SKIconKey?) {
            leadingIcon = value
            buttonComponent.setLeadingIcon(value)
            render()
        }

        public fun setButtonEnabled(value: Boolean) {
            buttonEnabled = value
            buttonComponent.setEnabled(value)
            render()
        }

        public fun setLoading(value: Boolean) {
            loading = value
            buttonComponent.setLoading(value)
            render()
        }

        public fun setAppearance(value: SKAppearanceConfig) {
            appearance = value
            syncComponent()
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig =
                if (value.role == null) value.copy(role = "button") else value
            syncComponent()
            render()
        }

        public fun setOnSkClickListener(listener: (() -> Unit)?) {
            clickListener = listener
        }

        private fun syncComponent() {
            buttonComponent.setText(labelText)
            buttonComponent.setLeadingIcon(leadingIcon)
            buttonComponent.setEnabled(buttonEnabled)
            buttonComponent.setLoading(loading)
            buttonComponent.updateConfig(
                buttonComponent.config.copy(
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val spacingPx = theme.tokens.spacing.xs.toPx(this).toInt()
            background = look.toBackgroundDrawable(
                strokeWidthPx = if (look.outlineColor != null) {
                    theme.tokens.spacing.xxs.toPx(this)
                } else {
                    0f
                },
            )
            look.elevationPx?.let { elevation = it }
            minimumHeight = look.heightPx.toInt()
            setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )
            isEnabled = buttonEnabled
            isClickable = buttonComponent.interactive
            alpha = if (buttonEnabled) 1f else DisabledAlpha

            val typeRole = appearance.typographyRole ?: SKTypographyRole.LabelLarge
            val scale = theme.tokens.typography.scale(typeRole)
            labelView.text = labelText
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.size.value)
            labelView.setTextColor(look.contentColor)
            labelView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
            labelView.visibility = if (labelText.isBlank()) View.GONE else View.VISIBLE

            if (loading) {
                loadingView.visibility = View.VISIBLE
                leadingIconView.visibility = View.GONE
                val lp = loadingView.layoutParams as LayoutParams
                lp.width = look.iconSizePx.toInt()
                lp.height = look.iconSizePx.toInt()
                lp.marginEnd = if (labelText.isNotBlank()) spacingPx else 0
                loadingView.layoutParams = lp
            } else {
                loadingView.visibility = View.GONE
                val icon = leadingIcon
                if (icon == null) {
                    leadingIconView.visibility = View.GONE
                } else {
                    leadingIconView.visibility = View.VISIBLE
                    val ref = runtime?.icons?.resolve(icon)
                    leadingIconView.text = ref?.vectorName?.take(1) ?: "•"
                    leadingIconView.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        theme.tokens.typography.scale(SKTypographyRole.LabelSmall).size.value,
                    )
                    leadingIconView.setTextColor(look.contentColor)
                    val explicitCd = icon.contentDescription?.takeIf { it.isNotBlank() }
                    if (explicitCd != null) {
                        leadingIconView.contentDescription = explicitCd
                        leadingIconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
                    } else {
                        leadingIconView.contentDescription = null
                        leadingIconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
                    }
                    val lp = leadingIconView.layoutParams as LayoutParams
                    lp.marginEnd = if (labelText.isNotBlank()) spacingPx else 0
                    leadingIconView.layoutParams = lp
                }
            }

            val combinedState = listOfNotNull(
                accessibilityConfig.stateDescription?.takeIf { it.isNotBlank() },
                if (loading) "Loading" else null,
            ).takeIf { it.isNotEmpty() }?.joinToString(", ")
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "button",
                    stateDescription = combinedState,
                ),
                contentDescriptionFallback = labelText,
            )
        }

        private companion object {
            const val DisabledAlpha = 0.38f
        }
    }
