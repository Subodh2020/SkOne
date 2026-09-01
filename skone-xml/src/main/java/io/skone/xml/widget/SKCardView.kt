@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.ui.layout.SKCardComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * Foundational SKOne card / surface container (XML).
 *
 * Optional click via [setOnSkClickListener]. Specialized Material card variants deferred.
 *
 * @see docs/WIDGETS_SKCARD.md
 */
public class SKCardView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : FrameLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skcard-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Card
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var controlEnabled: Boolean = true
        private var clickableCard: Boolean = false
        private var clickListener: (() -> Unit)? = null
        private var cached: SKCardComponent? = null

        private val card: SKCardComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKCardComponent.create(
                    id = componentId,
                    clickable = clickableCard,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = card

        init {
            attrs?.let { applyAttributes(it) }
            setOnClickListener {
                if (!card.interactive) return@setOnClickListener
                card.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKCardView)
            try {
                a.getString(R.styleable.SKCardView_skComponentId)?.let { componentId = it }
                clickableCard = a.getBoolean(R.styleable.SKCardView_skClickable, false)
                controlEnabled = a.getBoolean(R.styleable.SKCardView_skEnabled, true)
                a.getString(R.styleable.SKCardView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKCardView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { card.detach() }
            this.runtime = runtime
            sync()
            card.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { card.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setCardClickable(value: Boolean) {
            clickableCard = value
            card.setClickable(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            card.setEnabled(value)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.role == null && clickableCard) {
                value.copy(role = "button")
            } else {
                value
            }
            sync()
            render()
        }

        public fun setOnSkClickListener(listener: (() -> Unit)?) {
            clickListener = listener
            if (listener != null && !clickableCard) {
                setCardClickable(true)
            }
        }

        private fun sync() {
            card.setClickable(clickableCard)
            card.setEnabled(controlEnabled)
            card.updateConfig(
                card.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            isEnabled = controlEnabled
            isClickable = clickableCard && controlEnabled
            isFocusable = clickableCard && controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f
            background = look.toBackgroundDrawable(
                strokeWidthPx = if (look.outlineColor != null) {
                    theme.tokens.spacing.xxs.toPx(this)
                } else {
                    0f
                },
            )
            elevation = look.elevationPx ?: 0f
            val padH = look.horizontalPaddingPx.toInt()
            val padV = look.verticalPaddingPx.toInt()
            setPadding(padH, padV, padH, padV)
            val a11y = if (accessibilityConfig.role == null && clickableCard) {
                accessibilityConfig.copy(role = "button")
            } else {
                accessibilityConfig
            }
            applySKAccessibilityConfig(a11y, contentDescriptionFallback = null)
            ViewCompat.setStateDescription(this, a11y.stateDescription)
        }
    }
