@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import io.skone.ui.layout.SKListItemComponent
import io.skone.ui.layout.SKScaffoldComponent
import io.skone.ui.layout.SKSectionHeaderComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML list row.
 *
 * @see docs/WIDGETS_SKLISTITEM.md
 */
public class SKListItemView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sklistitem-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.ListItem
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var headlineText: String = ""
        private var supporting: String? = null
        private var trailing: String? = null
        private var leadingIcon: SKIconKey? = null
        private var selected: Boolean = false
        private var controlEnabled: Boolean = true
        private var clickableRow: Boolean = false
        private var clickListener: (() -> Unit)? = null
        private var cached: SKListItemComponent? = null

        private val leadingView = AppCompatTextView(context)
        private val headlineView = AppCompatTextView(context)
        private val supportingView = AppCompatTextView(context)
        private val trailingView = AppCompatTextView(context)
        private val textColumn = LinearLayout(context).apply { orientation = VERTICAL }

        private val item: SKListItemComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKListItemComponent.create(
                    id = componentId,
                    headline = headlineText,
                    supportingText = supporting,
                    leadingIcon = leadingIcon,
                    trailingText = trailing,
                    selected = selected,
                    enabled = controlEnabled,
                    clickable = clickableRow,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = item
        public val isSelectedState: Boolean get() = selected

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            attrs?.let { applyAttributes(it) }
            textColumn.addView(headlineView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            textColumn.addView(supportingView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(leadingView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(textColumn, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(trailingView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            setOnClickListener {
                if (!item.interactive) return@setOnClickListener
                item.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKListItemView)
            try {
                a.getString(R.styleable.SKListItemView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKListItemView_skHeadline)?.let { headlineText = it }
                a.getString(R.styleable.SKListItemView_skSupportingText)?.let { supporting = it }
                a.getString(R.styleable.SKListItemView_skTrailingText)?.let { trailing = it }
                selected = a.getBoolean(R.styleable.SKListItemView_skSelected, false)
                controlEnabled = a.getBoolean(R.styleable.SKListItemView_skEnabled, true)
                clickableRow = a.getBoolean(R.styleable.SKListItemView_skClickable, false)
                a.getString(R.styleable.SKListItemView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKListItemView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { item.detach() }
            this.runtime = runtime
            item.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { item.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setHeadline(value: String) { headlineText = value; item.setHeadline(value); render() }
        public fun setSupportingText(value: String?) { supporting = value; item.setSupportingText(value); render() }
        public fun setTrailingText(value: String?) { trailing = value; item.setTrailingText(value); render() }
        public fun setLeadingIcon(value: SKIconKey?) { leadingIcon = value; item.setLeadingIcon(value); render() }
        public fun setSelectedState(value: Boolean) { selected = value; item.setSelected(value); render() }
        public fun setControlEnabled(value: Boolean) { controlEnabled = value; item.setEnabled(value); render() }
        public fun setRowClickable(value: Boolean) { clickableRow = value; item.setClickable(value); render() }
        public fun setOnSkClickListener(listener: (() -> Unit)?) {
            clickListener = listener
            if (listener != null && !clickableRow) setRowClickable(true)
        }
        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.role == null && clickableRow) value.copy(role = "button") else value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            isEnabled = controlEnabled
            isClickable = clickableRow && controlEnabled
            isFocusable = clickableRow && controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f
            minimumHeight = look.heightPx.toInt()
            setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )
            setBackgroundColor(look.containerColor)
            if (leadingIcon == null) {
                leadingView.visibility = View.GONE
            } else {
                leadingView.visibility = View.VISIBLE
                leadingView.text = "•"
                leadingView.setTextColor(look.contentColor)
                val explicit = leadingIcon?.contentDescription?.takeIf { it.isNotBlank() }
                if (explicit != null) {
                    leadingView.contentDescription = explicit
                    leadingView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    leadingView.contentDescription = null
                    leadingView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
                }
                (leadingView.layoutParams as LayoutParams).marginEnd =
                    theme.tokens.spacing.sm.toPx(this).toInt()
            }
            headlineView.text = headlineText
            headlineView.maxLines = 1
            headlineView.ellipsize = android.text.TextUtils.TruncateAt.END
            headlineView.setTextColor(look.contentColor)
            headlineView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.BodyLarge).size.value,
            )
            if (supporting.isNullOrBlank()) {
                supportingView.visibility = View.GONE
            } else {
                supportingView.visibility = View.VISIBLE
                supportingView.text = supporting
                supportingView.maxLines = 2
                supportingView.ellipsize = android.text.TextUtils.TruncateAt.END
                supportingView.setTextColor(theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb())
                supportingView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(SKTypographyRole.BodyMedium).size.value,
                )
            }
            if (trailing.isNullOrBlank()) {
                trailingView.visibility = View.GONE
            } else {
                trailingView.visibility = View.VISIBLE
                trailingView.text = trailing
                trailingView.maxLines = 1
                trailingView.ellipsize = android.text.TextUtils.TruncateAt.END
                trailingView.setTextColor(theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb())
            }
            val description = accessibilityConfig.contentDescription ?: buildString {
                append(headlineText)
                if (!supporting.isNullOrBlank()) append(", ").append(supporting)
                if (!trailing.isNullOrBlank()) append(", ").append(trailing)
            }
            val stateText = listOfNotNull(
                accessibilityConfig.stateDescription?.takeIf { it.isNotBlank() },
                if (selected) "Selected" else null,
            ).takeIf { it.isNotEmpty() }?.joinToString(", ")
            applySKAccessibilityConfig(
                accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: if (clickableRow) "button" else null,
                    stateDescription = stateText,
                ),
                contentDescriptionFallback = description,
            )
            ViewCompat.setStateDescription(this, stateText)
            isSelected = selected
        }
    }

/**
 * XML section header.
 *
 * @see docs/WIDGETS_SKSECTIONHEADER.md
 */
public class SKSectionHeaderView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sksectionheader-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.SectionHeader
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(heading = true)
        private var titleText: String = ""
        private var supporting: String? = null
        private var actionText: String? = null
        private var actionListener: (() -> Unit)? = null
        private var cached: SKSectionHeaderComponent? = null

        private val titleView = AppCompatTextView(context)
        private val supportingView = AppCompatTextView(context)
        private val actionView = AppCompatTextView(context)
        private val textColumn = LinearLayout(context).apply { orientation = VERTICAL }

        private val header: SKSectionHeaderComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKSectionHeaderComponent.create(
                    id = componentId,
                    title = titleText,
                    supportingText = supporting,
                    actionLabel = actionText,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = header

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            attrs?.let { applyAttributes(it) }
            textColumn.addView(titleView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            textColumn.addView(supportingView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(textColumn, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(actionView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            actionView.setOnClickListener {
                header.performAction()
                actionListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKSectionHeaderView)
            try {
                a.getString(R.styleable.SKSectionHeaderView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKSectionHeaderView_skTitle)?.let { titleText = it }
                a.getString(R.styleable.SKSectionHeaderView_skSupportingText)?.let { supporting = it }
                a.getString(R.styleable.SKSectionHeaderView_skActionLabel)?.let { actionText = it }
                a.getString(R.styleable.SKSectionHeaderView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKSectionHeaderView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { header.detach() }
            this.runtime = runtime
            header.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { header.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setHeaderTitle(value: String) { titleText = value; header.setTitle(value); render() }
        public fun setSupportingText(value: String?) { supporting = value; header.setSupportingText(value); render() }
        public fun setActionLabel(value: String?) { actionText = value; header.setActionLabel(value); render() }
        public fun setOnActionListener(listener: (() -> Unit)?) { actionListener = listener }
        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = if (value.heading) value else value.copy(heading = true)
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )
            titleView.text = titleText
            titleView.maxLines = 1
            titleView.ellipsize = android.text.TextUtils.TruncateAt.END
            titleView.setTextColor(look.contentColor)
            titleView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleSmall).size.value,
            )
            if (supporting.isNullOrBlank()) {
                supportingView.visibility = View.GONE
            } else {
                supportingView.visibility = View.VISIBLE
                supportingView.text = supporting
                supportingView.maxLines = 2
                supportingView.ellipsize = android.text.TextUtils.TruncateAt.END
                supportingView.setTextColor(theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb())
            }
            if (actionText.isNullOrBlank()) {
                actionView.visibility = View.GONE
            } else {
                actionView.visibility = View.VISIBLE
                actionView.text = actionText
                actionView.setTextColor(theme.tokens.colors.color(SKColorRole.Primary).toArgb())
            }
            applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = titleText)
        }
    }

/**
 * XML scaffold shell: top / content / bottom (+ optional snackbar overlay).
 *
 * Applies system-bar padding by default for edge-to-edge hosts.
 *
 * @see docs/WIDGETS_SKSCAFFOLD.md
 */
public class SKScaffoldView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skscaffold-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Scaffold
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var applySafeDrawing: Boolean = true
        private var cached: SKScaffoldComponent? = null

        public val topBarContainer: FrameLayout = FrameLayout(context)
        public val contentContainer: FrameLayout = FrameLayout(context)
        public val snackbarContainer: FrameLayout = FrameLayout(context)
        public val fabContainer: FrameLayout = FrameLayout(context)
        public val bottomBarContainer: FrameLayout = FrameLayout(context)
        private val contentStack = FrameLayout(context)

        private val scaffold: SKScaffoldComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKScaffoldComponent.create(
                    id = componentId,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = scaffold

        init {
            orientation = VERTICAL
            attrs?.let { applyAttributes(it) }
            contentStack.addView(
                contentContainer,
                FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
            )
            contentStack.addView(
                snackbarContainer,
                FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM,
                ),
            )
            contentStack.addView(
                fabContainer,
                FrameLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM or Gravity.END,
                ).also { lp ->
                    val pad = (16 * resources.displayMetrics.density).toInt()
                    lp.setMargins(pad, pad, pad, pad)
                },
            )
            addView(topBarContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(contentStack, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
            addView(bottomBarContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            if (applySafeDrawing) skApplySystemBarPadding()
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKScaffoldView)
            try {
                a.getString(R.styleable.SKScaffoldView_skComponentId)?.let { componentId = it }
                applySafeDrawing = a.getBoolean(R.styleable.SKScaffoldView_skSafeDrawing, true)
                a.getString(R.styleable.SKScaffoldView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKScaffoldView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { scaffold.detach() }
            this.runtime = runtime
            scaffold.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { scaffold.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setTopBar(view: View?) {
            topBarContainer.removeAllViews()
            if (view != null) topBarContainer.addView(view, matchWrap())
        }

        public fun setBottomBar(view: View?) {
            bottomBarContainer.removeAllViews()
            if (view != null) bottomBarContainer.addView(view, matchWrap())
        }

        public fun setContent(view: View?) {
            contentContainer.removeAllViews()
            if (view != null) {
                contentContainer.addView(
                    view,
                    FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
                )
            }
        }

        public fun setSnackbar(view: View?) {
            snackbarContainer.removeAllViews()
            if (view != null) snackbarContainer.addView(view, matchWrap())
        }

        public fun setFloatingActionButton(view: View?) {
            fabContainer.removeAllViews()
            if (view != null) {
                fabContainer.addView(
                    view,
                    FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT),
                )
            }
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        private fun matchWrap(): ViewGroup.LayoutParams =
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            setBackgroundColor(look.containerColor)
            applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = null)
        }
    }
