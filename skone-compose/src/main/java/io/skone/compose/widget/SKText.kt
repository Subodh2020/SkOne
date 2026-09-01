@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import io.skone.component.SKAnalyticsConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.compose.component.LocalSKComponentRuntime
import io.skone.compose.component.SKComponentLifecycle
import io.skone.compose.component.skLayout
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toColor
import io.skone.compose.theme.toTextStyle
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.text.SKAnnotatedText
import io.skone.ui.text.SKSpanStyle
import io.skone.ui.text.SKTextAlign
import io.skone.ui.text.SKTextComponent
import io.skone.ui.text.SKTextOverflow
import java.util.UUID

/**
 * SKOne text widget (Compose) — **reference implementation** for all future widgets.
 *
 * Visuals resolve through [appearance] + [io.skone.theme.SKTheme] tokens only.
 *
 * ### Parameter order
 * modifier → text/annotated → onClick → appearance → overflow/maxLines/softWrap/textAlign
 * → accessibility → analytics → ai
 *
 * @see docs/WIDGETS_SKTEXT.md
 */
@Composable
public fun SKText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Text,
    overflow: SKTextOverflow = SKTextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
    textAlign: SKTextAlign = SKTextAlign.Start,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    SKText(
        modifier = modifier,
        annotated = SKAnnotatedText.plain(text),
        onClick = onClick,
        appearance = appearance,
        overflow = overflow,
        maxLines = maxLines,
        softWrap = softWrap,
        textAlign = textAlign,
        accessibility = accessibility,
        analytics = analytics,
        ai = ai,
        layout = layout,
        componentId = componentId,
        runtime = runtime,
    )
}

/**
 * SKOne rich-text widget (Compose).
 */
@Composable
public fun SKText(
    modifier: Modifier = Modifier,
    annotated: SKAnnotatedText,
    onClick: (() -> Unit)? = null,
    appearance: SKAppearanceConfig = SKAppearanceConfig.Text,
    overflow: SKTextOverflow = SKTextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
    textAlign: SKTextAlign = SKTextAlign.Start,
    accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
    analytics: SKAnalyticsConfig? = null,
    ai: SKAIComponentConfig? = null,
    layout: SKLayoutSpec = SKLayoutSpec.Wrap,
    componentId: String? = null,
    runtime: SKComponentRuntime? = LocalSKComponentRuntime.current,
) {
    val id = componentId ?: remember { "sktext-${UUID.randomUUID()}" }
    val component = remember(id) {
        SKTextComponent.create(
            id = id,
            annotated = annotated,
            appearance = appearance,
            overflow = overflow,
            maxLines = maxLines,
            softWrap = softWrap,
            textAlign = textAlign,
            accessibility = accessibility,
            analytics = analytics,
            ai = ai,
            clickable = onClick != null,
        )
    }

    LaunchedEffect(annotated, appearance, overflow, maxLines, softWrap, textAlign, accessibility, analytics, ai) {
        component.setAnnotated(annotated)
        component.setOverflow(overflow)
        component.setMaxLines(maxLines)
        component.setSoftWrap(softWrap)
        component.setTextAlign(textAlign)
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
        SKComponentLifecycle(component = component, runtime = runtime)
    }

    val theme = skTheme
    val typographyRole = appearance.typographyRole ?: SKTypographyRole.BodyLarge
    val typeScale = theme.tokens.typography.scale(typographyRole)
    val contentColor = theme.tokens.colors.color(appearance.contentColorRole).toColor()
    val baseStyle = typeScale.toTextStyle().copy(color = contentColor)
    val annotatedString = remember(annotated, theme) {
        annotated.toAnnotatedString(theme) { role ->
            theme.tokens.typography.scale(role).toTextStyle()
        }
    }
    val composeAlign = textAlign.toCompose(LocalLayoutDirection.current)
    val composeOverflow = overflow.toCompose()

    val semanticsModifier = Modifier.semantics(mergeDescendants = accessibility.mergeDescendants) {
        val description = accessibility.contentDescription ?: annotated.text
        contentDescription = description
        accessibility.testTag?.let { testTag = it }
        if (accessibility.heading) {
            heading()
        }
        applyOptionalAccessibility(accessibility)
    }

    val clickModifier = if (onClick != null && component.config.enabled) {
        Modifier.clickable {
            component.performClick()
            onClick()
        }
    } else {
        Modifier
    }

    BasicText(
        text = annotatedString,
        modifier = modifier
            .then(semanticsModifier)
            .then(clickModifier)
            .skLayout(layout),
        style = baseStyle.merge(
            TextStyle(textAlign = composeAlign),
        ),
        overflow = composeOverflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
}

private fun SKTextOverflow.toCompose(): TextOverflow = when (this) {
    SKTextOverflow.Clip -> TextOverflow.Clip
    SKTextOverflow.Ellipsis -> TextOverflow.Ellipsis
    SKTextOverflow.Visible -> TextOverflow.Visible
}

private fun SKTextAlign.toCompose(direction: LayoutDirection): TextAlign = when (this) {
    SKTextAlign.Start -> TextAlign.Start
    SKTextAlign.Center -> TextAlign.Center
    SKTextAlign.End -> TextAlign.End
    SKTextAlign.Justify -> TextAlign.Justify
}

private fun SKAnnotatedText.toAnnotatedString(
    theme: io.skone.theme.SKTheme,
    typographyStyle: (SKTypographyRole) -> TextStyle,
): AnnotatedString {
    if (spans.isEmpty()) return AnnotatedString(text)
    return AnnotatedString.Builder(text).apply {
        spans.forEach { span ->
            val start = span.start.coerceIn(0, text.length)
            val end = span.end.coerceIn(start, text.length)
            span.styles.forEach { style ->
                when (style) {
                    SKSpanStyle.Bold -> addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start,
                        end,
                    )
                    SKSpanStyle.Italic -> addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic),
                        start,
                        end,
                    )
                    SKSpanStyle.Underline -> addStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                        start,
                        end,
                    )
                    is SKSpanStyle.ColorRole -> addStyle(
                        SpanStyle(color = theme.tokens.colors.color(style.role).toColor()),
                        start,
                        end,
                    )
                    is SKSpanStyle.TypographyRole -> {
                        val ts = typographyStyle(style.role)
                        addStyle(
                            SpanStyle(
                                fontWeight = ts.fontWeight,
                                fontSize = ts.fontSize,
                                fontFamily = ts.fontFamily,
                                letterSpacing = ts.letterSpacing,
                            ),
                            start,
                            end,
                        )
                    }
                }
            }
        }
    }.toAnnotatedString()
}
