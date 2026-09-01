@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.ui.layout.SKDividerComponent
import io.skone.ui.layout.SKDividerOrientation
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * Decorative SKOne divider (XML).
 *
 * @see docs/WIDGETS_SKDIVIDER.md
 */
public class SKDividerView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skdivider-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Divider
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var orientation: SKDividerOrientation = SKDividerOrientation.Horizontal
        private var cached: SKDividerComponent? = null

        private val divider: SKDividerComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKDividerComponent.create(
                    id = componentId,
                    orientation = orientation,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent
            get() = divider

        init {
            importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKDividerView)
            try {
                a.getString(R.styleable.SKDividerView_skComponentId)?.let { componentId = it }
                orientation = when (a.getInt(R.styleable.SKDividerView_skOrientation, 0)) {
                    1 -> SKDividerOrientation.Vertical
                    else -> SKDividerOrientation.Horizontal
                }
                a.getString(R.styleable.SKDividerView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                    tag = it
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { divider.detach() }
            this.runtime = runtime
            sync()
            divider.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { divider.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setDividerOrientation(value: SKDividerOrientation) {
            orientation = value
            divider.setOrientation(value)
            requestLayout()
            render()
        }

        private fun sync() {
            divider.setOrientation(orientation)
            divider.updateConfig(
                divider.config.copy(appearance = appearance, accessibility = accessibilityConfig),
            )
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            setBackgroundColor(look.containerColor)
            accessibilityConfig.testTag?.let { tag = it }
            // Decorative — keep out of TalkBack unless host overrides importantForAccessibility.
            if (importantForAccessibility != IMPORTANT_FOR_ACCESSIBILITY_YES) {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val theme = SKThemeHelper.current()
            val thickness = theme.tokens.spacing.xxs.toPx(this).toInt().coerceAtLeast(1)
            when (orientation) {
                SKDividerOrientation.Horizontal -> {
                    val w = resolveSize(suggestedMinimumWidth.coerceAtLeast(thickness * 20), widthMeasureSpec)
                    setMeasuredDimension(w, resolveSize(thickness, heightMeasureSpec))
                }
                SKDividerOrientation.Vertical -> {
                    val h = resolveSize(suggestedMinimumHeight.coerceAtLeast(thickness * 20), heightMeasureSpec)
                    setMeasuredDimension(resolveSize(thickness, widthMeasureSpec), h)
                }
            }
        }
    }
