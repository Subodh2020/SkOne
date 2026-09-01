@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.ui.feedback.SKSliderComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * SKOne slider (XML). Maps float [value] within [valueRange] with optional steps.
 *
 * @see docs/WIDGETS_SKSLIDER.md
 */
public class SKSliderView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skslider-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var valueRangeStart: Float = 0f
        private var valueRangeEnd: Float = 1f
        private var steps: Int = 0
        private var controlEnabled: Boolean = true
        private var changeListener: ((Float) -> Unit)? = null
        private var cached: SKSliderComponent? = null

        private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val slider: SKSliderComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKSliderComponent.create(
                    id = componentId,
                    value = currentValue,
                    valueRange = valueRangeStart..valueRangeEnd,
                    steps = steps,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        private var currentValue: Float = 0f

        public val component: SKComponent
            get() = slider

        public val value: Float
            get() = currentValue

        init {
            isClickable = true
            isFocusable = true
            attrs?.let { applyAttributes(it) }
            currentValue = slider.coerceAndSnap(currentValue)
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKSliderView)
            try {
                a.getString(R.styleable.SKSliderView_skComponentId)?.let { componentId = it }
                valueRangeStart = a.getFloat(R.styleable.SKSliderView_skValueMin, 0f)
                valueRangeEnd = a.getFloat(R.styleable.SKSliderView_skValueMax, 1f)
                currentValue = a.getFloat(R.styleable.SKSliderView_skValueFloat, valueRangeStart)
                steps = a.getInt(R.styleable.SKSliderView_skSteps, 0)
                controlEnabled = a.getBoolean(R.styleable.SKSliderView_skEnabled, true)
                a.getString(R.styleable.SKSliderView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKSliderView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { slider.detach() }
            this.runtime = runtime
            sync()
            slider.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { slider.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setSliderValue(value: Float) {
            currentValue = slider.coerceAndSnap(value)
            slider.updateValue(currentValue, fromUser = false)
            render()
        }

        public fun setValueRange(start: Float, endInclusive: Float) {
            valueRangeStart = start
            valueRangeEnd = endInclusive
            slider.setValueRange(start..endInclusive)
            currentValue = slider.value
            render()
        }

        public fun setSteps(value: Int) {
            steps = value
            slider.setSteps(value)
            currentValue = slider.value
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            slider.setEnabled(value)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            sync()
            render()
        }

        public fun setOnValueChangeListener(listener: ((Float) -> Unit)?) {
            changeListener = listener
        }

        private fun sync() {
            slider.setValueRange(valueRangeStart..valueRangeEnd)
            slider.setSteps(steps)
            slider.updateValue(currentValue, fromUser = false)
            slider.setEnabled(controlEnabled)
            slider.updateConfig(
                slider.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            isEnabled = controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f
            trackPaint.color = look.outlineColor ?: look.contentColor
            trackPaint.alpha = 61
            activePaint.color = look.containerColor
            thumbPaint.color = look.containerColor
            val stateText = listOfNotNull(
                accessibilityConfig.stateDescription?.takeIf { it.isNotBlank() },
                formatValue(currentValue),
            ).joinToString(", ")
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(stateDescription = stateText),
                contentDescriptionFallback = "Slider",
            )
            ViewCompat.setStateDescription(this, stateText)
            invalidate()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val desired = look.iconSizePx.toInt().coerceAtLeast(
                theme.tokens.spacing.xl.toPx(this).toInt(),
            )
            val w = resolveSize(suggestedMinimumWidth.coerceAtLeast(desired * 4), widthMeasureSpec)
            val h = resolveSize(desired, heightMeasureSpec)
            setMeasuredDimension(w, h)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val thumbR = look.iconSizePx / 2f
            val cy = height / 2f
            val left = thumbR
            val right = width - thumbR
            val trackH = theme.tokens.spacing.xxs.toPx(this) * 2f
            val span = valueRangeEnd - valueRangeStart
            val fraction = if (span <= 0f) 0f else ((currentValue - valueRangeStart) / span).coerceIn(0f, 1f)
            val thumbX = left + fraction * (right - left)
            canvas.drawRoundRect(left, cy - trackH / 2f, right, cy + trackH / 2f, trackH, trackH, trackPaint)
            canvas.drawRoundRect(left, cy - trackH / 2f, thumbX, cy + trackH / 2f, trackH, trackH, activePaint)
            canvas.drawCircle(thumbX, cy, thumbR, thumbPaint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (!controlEnabled) return false
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    val theme = SKThemeHelper.current()
                    val look = appearance.resolve(theme, this)
                    val thumbR = look.iconSizePx / 2f
                    val left = thumbR
                    val right = (width - thumbR).coerceAtLeast(left + 1f)
                    val f = ((event.x - left) / (right - left)).coerceIn(0f, 1f)
                    val raw = valueRangeStart + f * (valueRangeEnd - valueRangeStart)
                    val next = slider.coerceAndSnap(raw)
                    if (next != currentValue) {
                        currentValue = next
                        slider.updateValue(next, fromUser = true)
                        changeListener?.invoke(next)
                        render()
                    }
                    return true
                }
            }
            return super.onTouchEvent(event)
        }

        override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(info)
            info.className = "android.widget.SeekBar"
            info.rangeInfo = AccessibilityNodeInfo.RangeInfo.obtain(
                AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_FLOAT,
                valueRangeStart,
                valueRangeEnd,
                currentValue,
            )
        }

        private fun formatValue(value: Float): String =
            if (value == value.toLong().toFloat()) value.toLong().toString()
            else String.format("%.2f", value)
    }
