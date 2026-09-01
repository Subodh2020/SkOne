@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.ui.feedback.SKProgressIndicatorComponent
import io.skone.ui.feedback.SKProgressStyle
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * SKOne progress indicator (XML). Linear / circular, determinate / indeterminate.
 *
 * @see docs/WIDGETS_SKPROGRESSINDICATOR.md
 */
public class SKProgressIndicatorView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skprogress-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Progress
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var progressValue: Float = 0f
        private var indeterminate: Boolean = false
        private var style: SKProgressStyle = SKProgressStyle.Linear
        private var cached: SKProgressIndicatorComponent? = null
        private var sweep: Float = 0f
        private var animator: ValueAnimator? = null

        private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val progress: SKProgressIndicatorComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKProgressIndicatorComponent.create(
                    id = componentId,
                    progress = progressValue,
                    indeterminate = indeterminate,
                    style = style,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = progress

        public val progressFraction: Float
            get() = progressValue

        public val isIndeterminate: Boolean
            get() = indeterminate

        init {
            attrs?.let { applyAttributes(it) }
            render()
            updateAnimator()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKProgressIndicatorView)
            try {
                a.getString(R.styleable.SKProgressIndicatorView_skComponentId)?.let { componentId = it }
                progressValue = a.getFloat(R.styleable.SKProgressIndicatorView_skProgress, 0f).coerceIn(0f, 1f)
                indeterminate = a.getBoolean(R.styleable.SKProgressIndicatorView_skIndeterminate, false)
                style = when (a.getInt(R.styleable.SKProgressIndicatorView_skProgressStyle, 0)) {
                    1 -> SKProgressStyle.Circular
                    else -> SKProgressStyle.Linear
                }
                a.getString(R.styleable.SKProgressIndicatorView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKProgressIndicatorView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { progress.detach() }
            this.runtime = runtime
            sync()
            progress.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { progress.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            animator?.cancel()
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setProgressFraction(value: Float) {
            progressValue = value.coerceIn(0f, 1f)
            progress.setProgress(progressValue)
            render()
        }

        public fun setIndeterminateMode(value: Boolean) {
            indeterminate = value
            progress.setIndeterminate(value)
            updateAnimator()
            render()
        }

        public fun setProgressStyle(value: SKProgressStyle) {
            style = value
            progress.setStyle(value)
            requestLayout()
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            sync()
            render()
        }

        private fun sync() {
            progress.setProgress(progressValue)
            progress.setIndeterminate(indeterminate)
            progress.setStyle(style)
            progress.updateConfig(
                progress.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun updateAnimator() {
            animator?.cancel()
            if (!indeterminate) {
                animator = null
                return
            }
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1200L
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                addUpdateListener {
                    sweep = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            trackPaint.color = look.outlineColor ?: look.containerColor
            trackPaint.alpha = 61
            fillPaint.color = look.containerColor
            indicatorPaint.color = look.containerColor
            val stateText = when {
                accessibilityConfig.stateDescription?.isNotBlank() == true ->
                    accessibilityConfig.stateDescription
                indeterminate -> "In progress"
                else -> "${(progressValue * 100).toInt()} percent"
            }
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(stateDescription = stateText),
                contentDescriptionFallback = if (indeterminate) "Loading" else "Progress",
            )
            ViewCompat.setStateDescription(this, stateText)
            invalidate()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            when (style) {
                SKProgressStyle.Linear -> {
                    val h = theme.tokens.spacing.xs.toPx(this).toInt().coerceAtLeast(4)
                    val w = resolveSize(suggestedMinimumWidth.coerceAtLeast(h * 20), widthMeasureSpec)
                    setMeasuredDimension(w, resolveSize(h, heightMeasureSpec))
                }
                SKProgressStyle.Circular -> {
                    val d = (look.iconSizePx * 2).toInt()
                    val size = resolveSize(d, widthMeasureSpec)
                    setMeasuredDimension(size, resolveSize(d, heightMeasureSpec))
                }
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val theme = SKThemeHelper.current()
            when (style) {
                SKProgressStyle.Linear -> {
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), trackPaint)
                    if (indeterminate) {
                        val bar = width * 0.3f
                        val start = (width + bar) * sweep - bar
                        canvas.drawRect(start, 0f, start + bar, height.toFloat(), fillPaint)
                    } else {
                        canvas.drawRect(0f, 0f, width * progressValue, height.toFloat(), fillPaint)
                    }
                }
                SKProgressStyle.Circular -> {
                    val stroke = theme.tokens.spacing.xxs.toPx(this)
                    indicatorPaint.strokeWidth = stroke
                    trackPaint.style = Paint.Style.STROKE
                    trackPaint.strokeWidth = stroke
                    val inset = stroke / 2f
                    val oval = RectF(inset, inset, width - inset, height - inset)
                    canvas.drawArc(oval, 0f, 360f, false, trackPaint)
                    val angle = if (indeterminate) 270f * sweep.coerceIn(0.15f, 1f) else 360f * progressValue
                    val start = if (indeterminate) sweep * 360f else -90f
                    canvas.drawArc(oval, start, angle, false, indicatorPaint)
                    trackPaint.style = Paint.Style.FILL
                }
            }
        }
    }
