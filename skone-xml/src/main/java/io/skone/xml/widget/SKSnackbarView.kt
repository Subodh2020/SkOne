@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.overlay.SKSnackbarComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML snackbar bar. Host controls visibility via [setSnackbarVisible].
 *
 * @see docs/WIDGETS_SKSNACKBAR.md
 */
public class SKSnackbarView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sksnackbar-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Snackbar
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(liveRegion = true)
        private var messageText: String = ""
        private var actionText: String? = null
        private var actionListener: (() -> Unit)? = null
        private var cached: SKSnackbarComponent? = null

        private val messageView = AppCompatTextView(context)
        private val actionView = AppCompatTextView(context)

        private val snackbar: SKSnackbarComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKSnackbarComponent.create(
                    id = componentId,
                    message = messageText,
                    actionLabel = actionText,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = snackbar

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            attrs?.let { applyAttributes(it) }
            addView(messageView, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(actionView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            actionView.setOnClickListener {
                snackbar.performAction()
                actionListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKSnackbarView)
            try {
                a.getString(R.styleable.SKSnackbarView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKSnackbarView_skMessage)?.let { messageText = it }
                a.getString(R.styleable.SKSnackbarView_skActionLabel)?.let { actionText = it }
                a.getString(R.styleable.SKSnackbarView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKSnackbarView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { snackbar.detach() }
            this.runtime = runtime
            snackbar.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { snackbar.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setMessage(value: String) {
            messageText = value
            snackbar.setMessage(value)
            render()
        }

        public fun setActionLabel(value: String?) {
            actionText = value
            snackbar.setActionLabel(value)
            render()
        }

        public fun setOnActionListener(listener: (() -> Unit)?) {
            actionListener = listener
        }

        public fun setSnackbarVisible(value: Boolean) {
            snackbar.setVisible(value)
            visibility = if (value) View.VISIBLE else View.GONE
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value.copy(liveRegion = value.liveRegion || true)
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            background = look.toBackgroundDrawable()
            elevation = look.elevationPx ?: 0f
            val padH = look.horizontalPaddingPx.toInt()
            val padV = look.verticalPaddingPx.toInt()
            setPadding(padH, padV, padH, padV)
            messageView.text = messageText
            messageView.setTextColor(look.contentColor)
            val body = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyMedium)
            messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, body.size.value)
            val showAction = !actionText.isNullOrBlank()
            actionView.visibility = if (showAction) View.VISIBLE else View.GONE
            if (showAction) {
                actionView.text = actionText
                actionView.setTextColor(look.contentColor)
                actionView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(SKTypographyRole.LabelLarge).size.value,
                )
                (actionView.layoutParams as LayoutParams).marginStart =
                    theme.tokens.spacing.sm.toPx(this).toInt()
            }
            snackbar.setMessage(messageText)
            snackbar.setActionLabel(actionText)
            applySKAccessibilityConfig(
                accessibilityConfig,
                contentDescriptionFallback = messageText,
            )
            ViewCompat.setStateDescription(this, accessibilityConfig.stateDescription)
        }
    }
