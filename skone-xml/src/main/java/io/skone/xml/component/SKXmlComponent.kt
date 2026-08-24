package io.skone.xml.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.base.SKBaseComponent
import io.skone.component.framework.dsl.SKComponentSpec
import io.skone.component.framework.layout.SKLayoutMode
import io.skone.component.framework.layout.SKLayoutSpec
import io.skone.xml.theme.toPx

/**
 * Base [FrameLayout] for future SKOne XML widgets.
 *
 * Wires a framework [SKComponent] to Android View lifecycle.
 * This class renders **no** production UI chrome — subclasses add content.
 *
 * @param context Android context.
 * @param attrs Optional attribute set.
 * @param defStyleAttr Default style attribute.
 */
public abstract class SKXmlComponent
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : FrameLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null

        /** Framework component owned by this view. Provided by subclasses. */
        protected abstract val component: SKComponent

        /** Optional layout spec applied when attached. */
        protected open val layoutSpec: SKLayoutSpec
            get() = SKLayoutSpec.Wrap

        /**
         * Binds this view to a [SKComponentRuntime].
         * Typically called from the host Activity/Fragment after inflation.
         */
        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { component.detach() }
            this.runtime = runtime
            component.attach(runtime)
            applyLayoutSpec(layoutSpec)
            onBind(runtime)
        }

        /** Unbinds from the runtime. */
        public fun unbind() {
            runtime?.let {
                onUnbind(it)
                component.detach()
            }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        /** Applies [SKLayoutSpec] to this view's [LayoutParams] where possible. */
        protected fun applyLayoutSpec(spec: SKLayoutSpec) {
            val lp =
                layoutParams ?: LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                )
            lp.width =
                when (spec.width.mode) {
                    SKLayoutMode.Wrap -> LayoutParams.WRAP_CONTENT
                    SKLayoutMode.Fill -> LayoutParams.MATCH_PARENT
                    SKLayoutMode.Exact -> (spec.width.exact?.toPx(this) ?: 0f).toInt()
                }
            lp.height =
                when (spec.height.mode) {
                    SKLayoutMode.Wrap -> LayoutParams.WRAP_CONTENT
                    SKLayoutMode.Fill -> LayoutParams.MATCH_PARENT
                    SKLayoutMode.Exact -> (spec.height.exact?.toPx(this) ?: 0f).toInt()
                }
            val start = spec.padding.start.toPx(this).toInt()
            val top = spec.padding.top.toPx(this).toInt()
            val end = spec.padding.end.toPx(this).toInt()
            val bottom = spec.padding.bottom.toPx(this).toInt()
            setPaddingRelative(start, top, end, bottom)
            layoutParams = lp
        }

        protected open fun onBind(runtime: SKComponentRuntime) {}

        protected open fun onUnbind(runtime: SKComponentRuntime) {}
    }

/**
 * Convenience host that wraps a pre-built [SKBaseComponent] and [SKComponentSpec].
 *
 * Still not a production widget — useful for tests and custom View composition.
 */
public open class SKXmlComponentHost
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        private val hosted: SKBaseComponent,
        private val spec: SKComponentSpec = SKComponentSpec(hosted.config),
    ) : SKXmlComponent(context, attrs, defStyleAttr) {
        override val component: SKComponent
            get() = hosted

        override val layoutSpec: SKLayoutSpec
            get() = spec.layout

        init {
            hosted.updateConfig(spec.config)
        }
    }
