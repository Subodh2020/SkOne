@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.IntOffset
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.compose.component.LocalSKComponentRuntime
import io.skone.compose.component.SKComponentLifecycle
import io.skone.compose.component.skLayout
import io.skone.compose.theme.resolve
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.ui.feedback.SKSliderComponent
import java.util.UUID
import kotlin.math.roundToInt

/**
 * SKOne slider (Compose). Continuous or stepped value within [valueRange].
 *
 * Deferred: range/dual-thumb/vertical/custom thumb.
 *
 * @see docs/WIDGETS_SKSLIDER.md
 */
@Composable
public fun SKSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Toggle,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skslider-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKSliderComponent.create(
            id = id,
            value = value,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(valueRange) { component.setValueRange(valueRange) }
    LaunchedEffect(steps) { component.setSteps(steps) }
    LaunchedEffect(value) { component.updateValue(value, fromUser = false) }
    LaunchedEffect(enabled) { component.setEnabled(enabled) }
    LaunchedEffect(appearance, accessibility, analytics, ai) {
        component.updateConfig(
            component.config.copy(
                appearance = appearance,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
        )
    }
    if (runtime != null) {
        SKComponentLifecycle(component, runtime)
    }

    val look = appearance.resolve()
    val theme = skTheme
    val trackHeight = theme.tokens.spacing.xxs.toDp() * 2
    val thumbSize = look.iconSize
    val alpha = if (enabled) 1f else DisabledAlpha
    val snapped = component.coerceAndSnap(value)
    val span = valueRange.endInclusive - valueRange.start
    val fraction = if (span <= 0f) 0f else ((snapped - valueRange.start) / span).coerceIn(0f, 1f)
    val valueText = formatSliderValue(snapped)
    val stateText = listOfNotNull(
        accessibility.stateDescription?.takeIf { it.isNotBlank() },
        valueText,
    ).joinToString(", ")

    BoxWithConstraints(
        modifier = modifier
            .skLayout(layout)
            .alpha(alpha)
            .height(thumbSize.coerceAtLeast(theme.tokens.spacing.xl.toDp()))
            .fillMaxWidth()
            .progressSemantics(
                value = snapped,
                valueRange = valueRange,
                steps = steps.coerceAtLeast(0),
            )
            .semantics(mergeDescendants = true) {
                accessibility.contentDescription?.let { contentDescription = it }
                accessibility.testTag?.let { testTag = it }
                stateDescription = stateText
                if (!enabled) disabled()
                applyOptionalAccessibility(
                    accessibility.copy(
                        contentDescription = null,
                        testTag = null,
                        stateDescription = null,
                        role = null,
                    ),
                )
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        val widthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val density = LocalDensity.current
        val thumbPx = with(density) { thumbSize.toPx() }
        val travel = (widthPx - thumbPx).coerceAtLeast(1f)

        fun emitFromX(x: Float) {
            val f = ((x - thumbPx / 2f) / travel).coerceIn(0f, 1f)
            val raw = valueRange.start + f * span
            val next = component.coerceAndSnap(raw)
            component.updateValue(next, fromUser = true)
            onValueChange(next)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .background(
                    color = look.outlineColor ?: look.contentColor.copy(alpha = 0.24f),
                    shape = look.shape,
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0.001f, 1f))
                .height(trackHeight)
                .background(look.containerColor, look.shape),
        )
        Box(
            modifier = Modifier
                .offset { IntOffset((fraction * travel).roundToInt(), 0) }
                .size(thumbSize)
                .background(look.containerColor, CircleShape),
        )
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(valueRange, steps) {
                        detectTapGestures { offset -> emitFromX(offset.x) }
                    }
                    .pointerInput(valueRange, steps) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            emitFromX(change.position.x)
                        }
                    },
            )
        }
    }
}

internal fun formatSliderValue(value: Float): String {
    return if (value == value.toLong().toFloat()) {
        value.toLong().toString()
    } else {
        String.format("%.2f", value)
    }
}

private const val DisabledAlpha = 0.38f
