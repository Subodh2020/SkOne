@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
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
import io.skone.ui.feedback.SKProgressIndicatorComponent
import io.skone.ui.feedback.SKProgressStyle
import java.util.UUID

/**
 * SKOne progress indicator (Compose). Linear and circular; determinate or indeterminate.
 *
 * @see docs/WIDGETS_SKPROGRESSINDICATOR.md
 */
@Composable
public fun SKProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    indeterminate: Boolean = false,
    style: SKProgressStyle = SKProgressStyle.Linear,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Progress,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skprogress-${UUID.randomUUID()}" }
    val clamped = progress.coerceIn(0f, 1f)
    val component = remember(id) {
        SKProgressIndicatorComponent.create(
            id = id,
            progress = clamped,
            indeterminate = indeterminate,
            style = style,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(clamped) { component.setProgress(clamped) }
    LaunchedEffect(indeterminate) { component.setIndeterminate(indeterminate) }
    LaunchedEffect(style) { component.setStyle(style) }
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
    val trackColor = look.outlineColor ?: look.containerColor.copy(alpha = 0.24f)
    val indicatorColor = look.containerColor
    val percent = (clamped * 100).toInt()
    val stateText = when {
        accessibility.stateDescription?.isNotBlank() == true -> accessibility.stateDescription
        indeterminate -> "In progress"
        else -> "$percent percent"
    }
    val semanticsMod = Modifier
        .then(
            if (!indeterminate) {
                Modifier.progressSemantics(clamped, 0f..1f, 0)
            } else {
                Modifier
            },
        )
        .semantics {
            accessibility.contentDescription?.let { contentDescription = it }
                ?: run { contentDescription = if (indeterminate) "Loading" else "Progress" }
            accessibility.testTag?.let { testTag = it }
            stateDescription = stateText.orEmpty()
            applyOptionalAccessibility(
                accessibility.copy(
                    contentDescription = null,
                    testTag = null,
                    stateDescription = null,
                    role = null,
                ),
            )
        }

    when (style) {
        SKProgressStyle.Linear -> {
            val height = theme.tokens.spacing.xs.toDp()
            val sweep by rememberIndeterminateSweep(indeterminate)
            Canvas(
                modifier = modifier
                    .skLayout(layout)
                    .fillMaxWidth()
                    .height(height)
                    .then(semanticsMod),
            ) {
                drawRect(trackColor, size = size)
                if (indeterminate) {
                    val barWidth = size.width * 0.3f
                    val start = (size.width + barWidth) * sweep - barWidth
                    drawRect(
                        color = indicatorColor,
                        topLeft = Offset(start, 0f),
                        size = Size(barWidth, size.height),
                    )
                } else {
                    drawRect(
                        color = indicatorColor,
                        size = Size(size.width * clamped, size.height),
                    )
                }
            }
        }
        SKProgressStyle.Circular -> {
            val diameter = look.iconSize * 2
            val stroke = theme.tokens.spacing.xxs.toDp()
            val sweep by rememberIndeterminateSweep(indeterminate)
            Canvas(
                modifier = modifier
                    .skLayout(if (layout == SKLayoutSpec.FillWidth) SKLayoutSpec.Wrap else layout)
                    .size(diameter)
                    .then(semanticsMod),
            ) {
                val strokePx = stroke.toPx()
                val arcSize = Size(size.minDimension - strokePx, size.minDimension - strokePx)
                val topLeft = Offset(strokePx / 2f, strokePx / 2f)
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
                val angle = if (indeterminate) 270f * sweep.coerceIn(0.15f, 1f) else 360f * clamped
                val start = if (indeterminate) sweep * 360f else -90f
                drawArc(
                    color = indicatorColor,
                    startAngle = start,
                    sweepAngle = angle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
            }
        }
    }
}

@Composable
private fun rememberIndeterminateSweep(active: Boolean): androidx.compose.runtime.State<Float> {
    val transition = rememberInfiniteTransition(label = "sk-progress")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (active) 1200 else 1, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sweep",
    )
}
