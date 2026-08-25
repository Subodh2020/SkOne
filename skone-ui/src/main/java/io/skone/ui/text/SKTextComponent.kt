@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.text

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import java.util.concurrent.atomic.AtomicReference

/**
 * Shared SKText contract used by Compose [io.skone.compose.widget.SKText] and
 * XML [io.skone.xml.widget.SKTextView].
 *
 * This class contains **no UI**. It owns content, overflow, alignment, and lifecycle.
 *
 * @see docs/WIDGETS_SKTEXT.md
 */
public class SKTextComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Text,
        behavior = SKBehaviorConfig.Passive,
    ),
    annotated: SKAnnotatedText = SKAnnotatedText.plain(""),
    overflow: SKTextOverflow = SKTextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
    textAlign: SKTextAlign = SKTextAlign.Start,
) : SKBaseInteractiveComponent(
    id = id,
    componentType = COMPONENT_TYPE,
    config = config,
) {
    private val annotatedRef = AtomicReference(annotated)
    private val overflowRef = AtomicReference(overflow)
    private val maxLinesRef = AtomicReference(maxLines)
    private val softWrapRef = AtomicReference(softWrap)
    private val textAlignRef = AtomicReference(textAlign)

    /** Current annotated content. */
    public val annotated: SKAnnotatedText
        get() = annotatedRef.get()

    /** Plain text convenience. */
    public val text: String
        get() = annotated.text

    public val overflow: SKTextOverflow
        get() = overflowRef.get()

    public val maxLines: Int
        get() = maxLinesRef.get()

    public val softWrap: Boolean
        get() = softWrapRef.get()

    public val textAlign: SKTextAlign
        get() = textAlignRef.get()

    /** Updates text content. */
    public fun setText(text: String) {
        annotatedRef.set(SKAnnotatedText.plain(text))
    }

    /** Updates annotated content. */
    public fun setAnnotated(value: SKAnnotatedText) {
        annotatedRef.set(value)
    }

    public fun setOverflow(value: SKTextOverflow) {
        overflowRef.set(value)
    }

    public fun setMaxLines(value: Int) {
        require(value >= 1) { "maxLines must be >= 1" }
        maxLinesRef.set(value)
    }

    public fun setSoftWrap(value: Boolean) {
        softWrapRef.set(value)
    }

    public fun setTextAlign(value: SKTextAlign) {
        textAlignRef.set(value)
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKText"

        /**
         * Factory aligning with API guideline parameter groups.
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            text: String = "",
            annotated: SKAnnotatedText? = null,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Text,
            overflow: SKTextOverflow = SKTextOverflow.Clip,
            maxLines: Int = Int.MAX_VALUE,
            softWrap: Boolean = true,
            textAlign: SKTextAlign = SKTextAlign.Start,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
            clickable: Boolean = false,
        ): SKTextComponent = SKTextComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = if (clickable) SKBehaviorConfig.Default else SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            annotated = annotated ?: SKAnnotatedText.plain(text),
            overflow = overflow,
            maxLines = maxLines,
            softWrap = softWrap,
            textAlign = textAlign,
        )
    }
}