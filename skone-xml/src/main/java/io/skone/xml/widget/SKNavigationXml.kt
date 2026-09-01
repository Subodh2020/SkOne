@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.navigation.SKNavigationBarComponent
import io.skone.ui.navigation.SKNavigationItem
import io.skone.ui.navigation.SKTopAppBarComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML top app bar.
 *
 * @see docs/WIDGETS_SKTOPAPPBAR.md
 */
public class SKTopAppBarView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sktopappbar-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.TopAppBar
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var titleText: String = ""
        private var navigationIcon: SKIconKey? = null
        private var actionIcon: SKIconKey? = null
        private var navListener: (() -> Unit)? = null
        private var actionListener: (() -> Unit)? = null
        private var cached: SKTopAppBarComponent? = null

        private val navView = AppCompatTextView(context)
        private val titleView = AppCompatTextView(context)
        private val actionView = AppCompatTextView(context)

        private val bar: SKTopAppBarComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKTopAppBarComponent.create(
                    id = componentId,
                    title = titleText,
                    navigationIcon = navigationIcon,
                    actionIcon = actionIcon,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = bar

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            attrs?.let { applyAttributes(it) }
            addView(navView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(titleView, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(actionView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            navView.setOnClickListener { navListener?.invoke() }
            actionView.setOnClickListener { actionListener?.invoke() }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKTopAppBarView)
            try {
                a.getString(R.styleable.SKTopAppBarView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKTopAppBarView_skTitle)?.let { titleText = it }
                a.getString(R.styleable.SKTopAppBarView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKTopAppBarView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { bar.detach() }
            this.runtime = runtime
            bar.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { bar.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setBarTitle(value: String) {
            titleText = value
            bar.setTitle(value)
            render()
        }

        public fun setNavigationIcon(value: SKIconKey?, onClick: (() -> Unit)?) {
            require(value == null || !value.contentDescription.isNullOrBlank()) {
                "SKTopAppBarView navigationIcon requires an explicit contentDescription"
            }
            navigationIcon = value
            navListener = onClick
            bar.setNavigationIcon(value)
            render()
        }

        public fun setActionIcon(value: SKIconKey?, onClick: (() -> Unit)?) {
            require(value == null || !value.contentDescription.isNullOrBlank()) {
                "SKTopAppBarView actionIcon requires an explicit contentDescription"
            }
            actionIcon = value
            actionListener = onClick
            bar.setActionIcon(value)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            background = look.toBackgroundDrawable()
            elevation = look.elevationPx ?: 0f
            minimumHeight = look.heightPx.toInt()
            val padH = look.horizontalPaddingPx.toInt()
            val padV = look.verticalPaddingPx.toInt()
            setPadding(padH, padV, padH, padV)
            titleView.text = titleText
            titleView.setTextColor(look.contentColor)
            titleView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleLarge).size.value,
            )
            if (navigationIcon != null) {
                navView.visibility = View.VISIBLE
                navView.text = "☰"
                navView.contentDescription = navigationIcon?.contentDescription
                navView.setTextColor(look.contentColor)
            } else {
                navView.visibility = View.GONE
            }
            if (actionIcon != null) {
                actionView.visibility = View.VISIBLE
                actionView.text = "⋮"
                actionView.contentDescription = actionIcon?.contentDescription
                actionView.setTextColor(look.contentColor)
            } else {
                actionView.visibility = View.GONE
            }
            applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = titleText)
        }
    }

/**
 * XML navigation bar with exclusive selection.
 *
 * @see docs/WIDGETS_SKNAVIGATIONBAR.md
 */
public class SKNavigationBarView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sknavbar-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.NavigationBar
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var items: List<SKNavigationItem> = emptyList()
        private var selectedId: String? = null
        private var controlEnabled: Boolean = true
        private var selectListener: ((String) -> Unit)? = null
        private var cached: SKNavigationBarComponent? = null

        private val bar: SKNavigationBarComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKNavigationBarComponent.create(
                    id = componentId,
                    items = items,
                    selectedId = selectedId,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = bar
        public val currentSelectedId: String? get() = selectedId

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKNavigationBarView)
            try {
                a.getString(R.styleable.SKNavigationBarView_skComponentId)?.let { componentId = it }
                controlEnabled = a.getBoolean(R.styleable.SKNavigationBarView_skEnabled, true)
                a.getString(R.styleable.SKNavigationBarView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKNavigationBarView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { bar.detach() }
            this.runtime = runtime
            bar.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { bar.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setNavigationItems(value: List<SKNavigationItem>) {
            items = value
            bar.setItems(value)
            if (selectedId == null) selectedId = value.firstOrNull()?.id
            render()
        }

        public fun setSelectedItemId(value: String?) {
            selectedId = value
            bar.setSelectedId(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            bar.setEnabled(value)
            render()
        }

        public fun setOnSelectListener(listener: ((String) -> Unit)?) {
            selectListener = listener
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            removeAllViews()
            background = look.toBackgroundDrawable(
                strokeWidthPx = if (look.outlineColor != null) theme.tokens.spacing.xxs.toPx(this) else 0f,
            )
            elevation = look.elevationPx ?: 0f
            alpha = if (controlEnabled) 1f else 0.38f
            isEnabled = controlEnabled
            items.forEach { item ->
                val selected = item.id == selectedId
                val color = if (selected) {
                    theme.tokens.colors.color(SKColorRole.Primary).toArgb()
                } else {
                    look.contentColor
                }
                val itemView = AppCompatTextView(context).apply {
                    text = item.label
                    gravity = Gravity.CENTER
                    setTextColor(color)
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelMedium).size.value,
                    )
                    tag = accessibilityConfig.testTag?.let { "${it}_${item.id}" } ?: "nav_${item.id}"
                    contentDescription = item.label
                    isSelected = selected
                    ViewCompat.setStateDescription(this, if (selected) "Selected" else "Not selected")
                    isEnabled = controlEnabled
                    setOnClickListener {
                        if (!controlEnabled) return@setOnClickListener
                        bar.select(item.id)
                        selectedId = item.id
                        selectListener?.invoke(item.id)
                        render()
                    }
                }
                addView(itemView, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            }
            applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = null)
        }
    }
