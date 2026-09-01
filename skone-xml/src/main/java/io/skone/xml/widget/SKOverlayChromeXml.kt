@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.overlay.SKBottomAppBarComponent
import io.skone.ui.overlay.SKDropdownMenuComponent
import io.skone.ui.overlay.SKMenuComponent
import io.skone.ui.overlay.SKMenuItem
import io.skone.ui.overlay.SKTooltipComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML menu surface (list of actionable items).
 *
 * @see docs/WIDGETS_SKMENU.md
 */
public class SKMenuView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skmenu-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Menu
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var items: List<SKMenuItem> = emptyList()
        private var clickListener: ((String) -> Unit)? = null
        private var cached: SKMenuComponent? = null

        private val menu: SKMenuComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKMenuComponent.create(
                    id = componentId,
                    items = items,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = menu

        init {
            orientation = VERTICAL
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKMenuView)
            try {
                a.getString(R.styleable.SKMenuView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKMenuView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKMenuView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { menu.detach() }
            this.runtime = runtime
            menu.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { menu.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setMenuItems(value: List<SKMenuItem>) {
            items = value
            menu.setItems(value)
            render()
        }

        public fun setOnItemClickListener(listener: ((String) -> Unit)?) {
            clickListener = listener
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
            val padV = theme.tokens.spacing.xs.toPx(this).toInt()
            setPadding(0, padV, 0, padV)
            items.forEach { item ->
                addView(createItemView(item, look.contentColor, theme, selected = false, tagPrefix = "menu"))
            }
            applySKAccessibilityConfig(
                accessibilityConfig,
                contentDescriptionFallback = "Menu",
            )
        }

        private fun createItemView(
            item: SKMenuItem,
            contentColor: Int,
            theme: io.skone.theme.SKTheme,
            selected: Boolean,
            tagPrefix: String,
        ): AppCompatTextView {
            val color = if (selected) {
                theme.tokens.colors.color(SKColorRole.Primary).toArgb()
            } else {
                contentColor
            }
            return AppCompatTextView(context).apply {
                text = item.label
                setTextColor(color)
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge).size.value,
                )
                val padH = theme.tokens.spacing.md.toPx(this).toInt()
                val padV = theme.tokens.spacing.sm.toPx(this).toInt()
                setPadding(padH, padV, padH, padV)
                alpha = if (item.enabled) 1f else 0.38f
                isEnabled = item.enabled
                isClickable = item.enabled
                isFocusable = item.enabled
                tag = accessibilityConfig.testTag?.let { "${it}_${item.id}" } ?: "${tagPrefix}_${item.id}"
                contentDescription = item.label
                isSelected = selected
                ViewCompat.setStateDescription(
                    this,
                    when {
                        !item.enabled -> "Disabled"
                        selected -> "Selected"
                        else -> null
                    },
                )
                setOnClickListener {
                    if (!item.enabled) return@setOnClickListener
                    menu.activate(item.id)
                    clickListener?.invoke(item.id)
                }
            }
        }
    }

/**
 * XML dropdown via [PopupWindow]. Host owns expand/dismiss.
 *
 * @see docs/WIDGETS_SKDROPDOWNMENU.md
 */
public class SKDropdownMenuView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skdropdown-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.DropdownMenu
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var items: List<SKMenuItem> = emptyList()
        private var selectedId: String? = null
        private var controlEnabled: Boolean = true
        private var isExpanded: Boolean = false
        private var selectListener: ((String) -> Unit)? = null
        private var dismissListener: (() -> Unit)? = null
        private var anchorView: View? = null
        private var popup: PopupWindow? = null
        private var cached: SKDropdownMenuComponent? = null

        private val dropdown: SKDropdownMenuComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKDropdownMenuComponent.create(
                    id = componentId,
                    items = items,
                    expanded = isExpanded,
                    selectedId = selectedId,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = dropdown
        public val expanded: Boolean get() = isExpanded
        public val currentSelectedId: String? get() = selectedId

        init {
            // Anchor helper view — not drawn; hosts typically call showAsDropDown on an anchor.
            visibility = GONE
            attrs?.let { applyAttributes(it) }
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKDropdownMenuView)
            try {
                a.getString(R.styleable.SKDropdownMenuView_skComponentId)?.let { componentId = it }
                controlEnabled = a.getBoolean(R.styleable.SKDropdownMenuView_skEnabled, true)
                a.getString(R.styleable.SKDropdownMenuView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKDropdownMenuView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { dropdown.detach() }
            this.runtime = runtime
            dropdown.attach(runtime)
        }

        public fun unbind() {
            dismiss()
            runtime?.let { dropdown.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setMenuItems(value: List<SKMenuItem>) {
            items = value
            dropdown.setItems(value)
        }

        public fun setSelectedItemId(value: String?) {
            selectedId = value
            dropdown.setSelectedId(value)
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            dropdown.setEnabled(value)
            if (!value) dismiss()
        }

        public fun setOnSelectListener(listener: ((String) -> Unit)?) {
            selectListener = listener
        }

        public fun setOnDismissListener(listener: (() -> Unit)?) {
            dismissListener = listener
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
        }

        public fun showAsDropDown(anchor: View) {
            if (!controlEnabled) return
            anchorView = anchor
            isExpanded = true
            dropdown.setExpanded(true)
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, anchor)
            val content = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = look.toBackgroundDrawable(
                    strokeWidthPx = if (look.outlineColor != null) theme.tokens.spacing.xxs.toPx(this) else 0f,
                )
                elevation = look.elevationPx ?: 0f
                val padV = theme.tokens.spacing.xs.toPx(this).toInt()
                setPadding(0, padV, 0, padV)
                minimumWidth = (160 * resources.displayMetrics.density).toInt()
                contentDescription = accessibilityConfig.contentDescription ?: "Dropdown menu"
                accessibilityConfig.testTag?.let { tag = it }
                items.forEach { item ->
                    addView(buildItem(item, look.contentColor, theme))
                }
            }
            popup?.dismiss()
            popup = PopupWindow(
                content,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true,
            ).apply {
                setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                isOutsideTouchable = true
                setOnDismissListener {
                    isExpanded = false
                    dropdown.dismiss()
                    dismissListener?.invoke()
                }
                showAsDropDown(anchor)
            }
        }

        public fun dismiss() {
            popup?.dismiss()
            popup = null
            isExpanded = false
            dropdown.dismiss()
        }

        private fun buildItem(
            item: SKMenuItem,
            contentColor: Int,
            theme: io.skone.theme.SKTheme,
        ): AppCompatTextView {
            val selected = item.id == selectedId
            val color = if (selected) {
                theme.tokens.colors.color(SKColorRole.Primary).toArgb()
            } else {
                contentColor
            }
            return AppCompatTextView(context).apply {
                text = item.label
                setTextColor(color)
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge).size.value,
                )
                val padH = theme.tokens.spacing.md.toPx(this).toInt()
                val padV = theme.tokens.spacing.sm.toPx(this).toInt()
                setPadding(padH, padV, padH, padV)
                alpha = if (item.enabled) 1f else 0.38f
                isEnabled = item.enabled
                tag = accessibilityConfig.testTag?.let { "${it}_${item.id}" } ?: "dropdown_${item.id}"
                contentDescription = item.label
                isSelected = selected
                ViewCompat.setStateDescription(
                    this,
                    when {
                        !item.enabled -> "Disabled"
                        selected -> "Selected"
                        else -> null
                    },
                )
                setOnClickListener {
                    if (!item.enabled) return@setOnClickListener
                    dropdown.select(item.id)
                    selectedId = item.id
                    selectListener?.invoke(item.id)
                    dismiss()
                }
            }
        }
    }

/**
 * XML tooltip — host-controlled visibility via a lightweight PopupWindow or inline TextView.
 *
 * @see docs/WIDGETS_SKTOOLTIP.md
 */
public class SKTooltipView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : AppCompatTextView(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sktooltip-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Tooltip
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var tooltipMessage: String = ""
        private var tooltipVisible: Boolean = false
        private var cached: SKTooltipComponent? = null

        private val tooltip: SKTooltipComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKTooltipComponent.create(
                    id = componentId,
                    message = tooltipMessage,
                    visible = tooltipVisible,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = tooltip

        init {
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKTooltipView)
            try {
                a.getString(R.styleable.SKTooltipView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKTooltipView_skMessage)?.let { tooltipMessage = it }
                tooltipVisible = a.getBoolean(R.styleable.SKTooltipView_skVisible, false)
                a.getString(R.styleable.SKTooltipView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKTooltipView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { tooltip.detach() }
            this.runtime = runtime
            tooltip.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { tooltip.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setMessage(value: String) {
            tooltipMessage = value
            tooltip.setMessage(value)
            render()
        }

        public fun setTooltipVisible(value: Boolean) {
            tooltipVisible = value
            tooltip.setVisible(value)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            visibility = if (tooltipVisible && tooltipMessage.isNotBlank()) View.VISIBLE else View.GONE
            text = tooltipMessage
            background = look.toBackgroundDrawable()
            elevation = look.elevationPx ?: 0f
            setTextColor(look.contentColor)
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodySmall).size.value,
            )
            setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )
            // Prefer host action CD; tooltip itself is not a live region.
            applySKAccessibilityConfig(
                accessibilityConfig,
                contentDescriptionFallback = tooltipMessage,
            )
        }
    }

/**
 * XML bottom app bar with leading / content / trailing containers.
 *
 * @see docs/WIDGETS_SKBOTTOMAPPBAR.md
 */
public class SKBottomAppBarView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skbottomappbar-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.BottomAppBar
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var cached: SKBottomAppBarComponent? = null

        public val leadingContainer: LinearLayout = LinearLayout(context).apply { orientation = HORIZONTAL }
        public val contentContainer: LinearLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        public val trailingContainer: LinearLayout = LinearLayout(context).apply { orientation = HORIZONTAL }
        public val fabContainer: LinearLayout = LinearLayout(context).apply { orientation = HORIZONTAL }

        private val bar: SKBottomAppBarComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKBottomAppBarComponent.create(
                    id = componentId,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = bar

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            attrs?.let { applyAttributes(it) }
            addView(leadingContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(contentContainer, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(trailingContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(fabContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKBottomAppBarView)
            try {
                a.getString(R.styleable.SKBottomAppBarView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKBottomAppBarView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKBottomAppBarView_skTestTag)?.let {
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

        public fun setLeading(view: View?) {
            leadingContainer.removeAllViews()
            if (view != null) leadingContainer.addView(view)
        }

        public fun setTrailing(view: View?) {
            trailingContainer.removeAllViews()
            if (view != null) trailingContainer.addView(view)
        }

        public fun setBarContent(view: View?) {
            contentContainer.removeAllViews()
            if (view != null) contentContainer.addView(view)
        }

        public fun setFloatingActionButton(view: View?) {
            fabContainer.removeAllViews()
            if (view != null) fabContainer.addView(view)
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            background = look.toBackgroundDrawable(
                strokeWidthPx = if (look.outlineColor != null) theme.tokens.spacing.xxs.toPx(this) else 0f,
            )
            elevation = look.elevationPx ?: 0f
            minimumHeight = look.heightPx.toInt().coerceAtLeast((56 * resources.displayMetrics.density).toInt())
            setPadding(look.horizontalPaddingPx.toInt(), 0, look.horizontalPaddingPx.toInt(), 0)
            applySKAccessibilityConfig(
                accessibilityConfig,
                contentDescriptionFallback = "Bottom app bar",
            )
        }
    }
