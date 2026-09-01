@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
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
import io.skone.ui.layout.SKDividerComponent
import io.skone.ui.layout.SKDividerOrientation
import java.util.UUID

/**
 * Decorative SKOne divider (Compose). Horizontal or vertical hairline.
 *
 * @see docs/WIDGETS_SKDIVIDER.md
 */
@Composable
public fun SKDivider(
    modifier: Modifier = Modifier,
    orientation: SKDividerOrientation = SKDividerOrientation.Horizontal,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Divider,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.FillWidth,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "skdivider-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKDividerComponent.create(
            id = id,
            orientation = orientation,
            appearance = appearance,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
        )
    }
    LaunchedEffect(orientation) { component.setOrientation(orientation) }
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
    val thickness = skTheme.tokens.spacing.xxs.toDp()
    val decorative = Modifier.clearAndSetSemantics {
        accessibility.testTag?.let { testTag = it }
    }
    when (orientation) {
        SKDividerOrientation.Horizontal -> Box(
            modifier = modifier
                .skLayout(layout)
                .fillMaxWidth()
                .height(thickness)
                .background(look.containerColor)
                .then(decorative),
        )
        SKDividerOrientation.Vertical -> Box(
            modifier = modifier
                .skLayout(if (layout == SKLayoutSpec.FillWidth) SKLayoutSpec.Wrap else layout)
                .fillMaxHeight()
                .width(thickness)
                .background(look.containerColor)
                .then(decorative),
        )
    }
}
