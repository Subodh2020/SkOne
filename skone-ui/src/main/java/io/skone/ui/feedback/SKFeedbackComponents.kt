@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.feedback

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInputComponent
import io.skone.component.framework.base.SKBaseInteractiveComponent
import io.skone.theme.state.SKComponentState
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

/**
 * Shared slider contract (Compose + XML).
 *
 * Continuous or stepped [Float] values within [valueRange]. Range/dual-thumb/vertical deferred.
 *
 * @see docs/WIDGETS_SKSLIDER.md
 */
public class SKSliderComponent(
    id: String,
    initialValue: Float,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Toggle,
        behavior = SKBehaviorConfig.Default,
    ),
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
) : SKBaseInputComponent<Float>(
    id = id,
    componentType = COMPONENT_TYPE,
    initialValue = initialValue,
    config = config,
) {
    private val rangeRef = AtomicReference(valueRange)
    private val stepsRef = AtomicInteger(steps.coerceAtLeast(0))

    public val valueRange: ClosedFloatingPointRange<Float>
        get() = rangeRef.get()

    /** Discrete steps between min and max (0 = continuous). */
    public val steps: Int
        get() = stepsRef.get()

    public val interactive: Boolean
        get() = config.enabled

    public fun setEnabled(value: Boolean) {
        updateConfig(
            config.copy(
                state = config.state.copy(enabled = value),
                behavior = config.behavior.copy(enabled = value),
            ),
        )
    }

    public fun setValueRange(range: ClosedFloatingPointRange<Float>) {
        require(range.endInclusive >= range.start) { "valueRange end must be >= start" }
        rangeRef.set(range)
        setValue(coerceAndSnap(value), fromUser = false)
    }

    public fun setSteps(value: Int) {
        stepsRef.set(value.coerceAtLeast(0))
        setValue(coerceAndSnap(this.value), fromUser = false)
    }

    /** Updates value, clamping + snapping. */
    public fun updateValue(value: Float, fromUser: Boolean = true) {
        if (fromUser && !interactive) return
        setValue(coerceAndSnap(value), fromUser)
    }

    public fun coerceAndSnap(raw: Float): Float {
        val range = rangeRef.get()
        val clamped = raw.coerceIn(range.start, range.endInclusive)
        val stepCount = stepsRef.get()
        if (stepCount <= 0) return clamped
        val span = range.endInclusive - range.start
        if (span <= 0f) return range.start
        val stepSize = span / (stepCount + 1)
        val index = ((clamped - range.start) / stepSize).roundToInt().coerceIn(0, stepCount + 1)
        return range.start + index * stepSize
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKSlider"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            value: Float = 0f,
            valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
            steps: Int = 0,
            enabled: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKSliderComponent {
            require(valueRange.endInclusive >= valueRange.start) { "valueRange end must be >= start" }
            val component = SKSliderComponent(
                id = id,
                initialValue = value,
                config = SKComponentConfig(
                    state = SKComponentState(enabled = enabled),
                    appearance = appearance,
                    behavior = SKBehaviorConfig.Default.copy(enabled = enabled, clickable = true),
                    accessibility = accessibility,
                    analytics = analytics,
                    ai = ai,
                ),
                valueRange = valueRange,
                steps = steps,
            )
            component.setValue(component.coerceAndSnap(value), fromUser = false)
            return component
        }
    }
}

/**
 * Progress style for [SKProgressIndicatorComponent].
 */
public enum class SKProgressStyle {
    Linear,
    Circular,
}

/**
 * Shared progress indicator contract (Compose + XML).
 *
 * Determinate [progress] in `0f..1f`, or [indeterminate].
 *
 * @see docs/WIDGETS_SKPROGRESSINDICATOR.md
 */
public class SKProgressIndicatorComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Progress,
        behavior = SKBehaviorConfig.Passive,
    ),
    progress: Float = 0f,
    indeterminate: Boolean = false,
    style: SKProgressStyle = SKProgressStyle.Linear,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val progressRef = AtomicReference(progress.coerceIn(0f, 1f))
    private val indeterminateRef = AtomicBoolean(indeterminate)
    private val styleRef = AtomicReference(style)

    public val progress: Float
        get() = progressRef.get()

    public val indeterminate: Boolean
        get() = indeterminateRef.get()

    public val style: SKProgressStyle
        get() = styleRef.get()

    public fun setProgress(value: Float) {
        progressRef.set(value.coerceIn(0f, 1f))
    }

    public fun setIndeterminate(value: Boolean) {
        indeterminateRef.set(value)
    }

    public fun setStyle(value: SKProgressStyle) {
        styleRef.set(value)
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKProgressIndicator"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            progress: Float = 0f,
            indeterminate: Boolean = false,
            style: SKProgressStyle = SKProgressStyle.Linear,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Progress,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKProgressIndicatorComponent = SKProgressIndicatorComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            progress = progress,
            indeterminate = indeterminate,
            style = style,
        )
    }
}
