@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
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
import io.skone.ui.chrome.SKAvatarComponent
import io.skone.ui.chrome.SKBadgeComponent
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.chrome.SKTabRowComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML tab row with exclusive selection (NavigationBar-style items list).
 *
 * @see docs/WIDGETS_SKTABS.md
 */
public class SKTabRowView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sktabrow-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.TabRow
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var items: List<SKTabItem> = emptyList()
        private var selectedId: String? = null
        private var controlEnabled: Boolean = true
        private var selectListener: ((String) -> Unit)? = null
        private var cached: SKTabRowComponent? = null

        private val tabRow: SKTabRowComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKTabRowComponent.create(
                    id = componentId,
                    items = items,
                    selectedId = selectedId,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = tabRow
        public val currentSelectedId: String? get() = selectedId

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKTabRowView)
            try {
                a.getString(R.styleable.SKTabRowView_skComponentId)?.let { componentId = it }
                controlEnabled = a.getBoolean(R.styleable.SKTabRowView_skEnabled, true)
                a.getString(R.styleable.SKTabRowView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKTabRowView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { tabRow.detach() }
            this.runtime = runtime
            tabRow.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { tabRow.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setTabItems(value: List<SKTabItem>) {
            items = value
            tabRow.setItems(value)
            if (selectedId == null) selectedId = value.firstOrNull()?.id
            render()
        }

        public fun setSelectedTabId(value: String?) {
            selectedId = value
            tabRow.setSelectedId(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            tabRow.setEnabled(value)
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
            alpha = if (controlEnabled) 1f else 0.38f
            isEnabled = controlEnabled
            items.forEach { item ->
                val selected = item.id == selectedId
                val tabEnabled = controlEnabled && item.enabled
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
                        theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelLarge).size.value,
                    )
                    typeface = Typeface.create(Typeface.SANS_SERIF, if (selected) Typeface.BOLD else Typeface.NORMAL)
                    tag = accessibilityConfig.testTag?.let { "${it}_${item.id}" } ?: "tab_${item.id}"
                    contentDescription = item.label
                    isSelected = selected
                    ViewCompat.setStateDescription(this, if (selected) "Selected" else "Not selected")
                    isEnabled = tabEnabled
                    setOnClickListener {
                        if (!tabEnabled) return@setOnClickListener
                        tabRow.select(item.id)
                        selectedId = item.id
                        selectListener?.invoke(item.id)
                        render()
                    }
                }
                addView(itemView, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            }
            // Container itself is not a second announcement source for each tab.
            importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            accessibilityConfig.testTag?.let { tag = it }
        }
    }

/**
 * Compact status / count badge (XML).
 *
 * @see docs/WIDGETS_SKBADGE.md
 */
public class SKBadgeView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : AppCompatTextView(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skbadge-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Badge
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var badgeText: String = ""
        private var badgeVisible: Boolean = true
        private var badgeDot: Boolean = false
        private var cached: SKBadgeComponent? = null

        private val badge: SKBadgeComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKBadgeComponent.create(
                    id = componentId,
                    text = badgeText,
                    visible = badgeVisible,
                    dot = badgeDot,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = badge

        init {
            gravity = Gravity.CENTER
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKBadgeView)
            try {
                a.getString(R.styleable.SKBadgeView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKBadgeView_skBadgeText)?.let { badgeText = it }
                badgeVisible = a.getBoolean(R.styleable.SKBadgeView_skVisible, true)
                badgeDot = a.getBoolean(R.styleable.SKBadgeView_skDot, false)
                a.getString(R.styleable.SKBadgeView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKBadgeView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { badge.detach() }
            this.runtime = runtime
            badge.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { badge.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setBadgeText(value: String) {
            badgeText = value
            badge.setText(value)
            render()
        }

        public fun setBadgeVisible(value: Boolean) {
            badgeVisible = value
            badge.setVisible(value)
            render()
        }

        public fun setDot(value: Boolean) {
            badgeDot = value
            badge.setDot(value)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            visibility = if (badgeVisible) View.VISIBLE else View.GONE
            background = look.toBackgroundDrawable()
            if (badgeDot) {
                text = ""
                val size = theme.tokens.spacing.sm.toPx(this).toInt()
                minimumWidth = size
                minimumHeight = size
            } else {
                text = badgeText
                setTextColor(look.contentColor)
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelSmall).size.value,
                )
                val padH = (look.horizontalPaddingPx / 2).toInt()
                val padV = (look.verticalPaddingPx / 2).toInt()
                setPadding(padH, padV, padH, padV)
                minimumHeight = look.heightPx.toInt()
            }
            val label = accessibilityConfig.contentDescription?.takeIf { it.isNotBlank() }
                ?: if (badgeDot) null else badgeText.takeIf { it.isNotBlank() }
            if (label != null) {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
                applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = label)
            } else {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
                contentDescription = null
                accessibilityConfig.testTag?.let { tag = it }
            }
        }
    }

/**
 * Identity avatar (XML). Host drawable via [setImage]; otherwise shows initials.
 *
 * @see docs/WIDGETS_SKAVATAR.md
 */
public class SKAvatarView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : FrameLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skavatar-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Avatar
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var initialsText: String = ""
        private var imageDrawable: Drawable? = null
        private var cached: SKAvatarComponent? = null

        private val initialsView = AppCompatTextView(context)
        private val imageView = ImageView(context)

        private val avatar: SKAvatarComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKAvatarComponent.create(
                    id = componentId,
                    initials = initialsText,
                    hasImage = imageDrawable != null,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = avatar

        init {
            attrs?.let { applyAttributes(it) }
            initialsView.gravity = Gravity.CENTER
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            addView(initialsView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            addView(imageView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKAvatarView)
            try {
                a.getString(R.styleable.SKAvatarView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKAvatarView_skInitials)?.let { initialsText = it }
                a.getString(R.styleable.SKAvatarView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKAvatarView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { avatar.detach() }
            this.runtime = runtime
            avatar.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { avatar.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setInitials(value: String) {
            initialsText = value
            avatar.setInitials(value)
            render()
        }

        public fun setImage(drawable: Drawable?) {
            imageDrawable = drawable
            avatar.setHasImage(drawable != null)
            render()
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
            render()
        }

        public fun setAppearance(value: SKAppearanceConfig) {
            appearance = value
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val size = look.heightPx.toInt().coerceAtLeast((theme.tokens.spacing.xl.toPx(this) * 1.5f).toInt())
            layoutParams = (layoutParams ?: LayoutParams(size, size)).also {
                it.width = size
                it.height = size
            }
            minimumWidth = size
            minimumHeight = size
            background = look.toBackgroundDrawable()
            clipToOutline = true
            if (imageDrawable != null) {
                imageView.setImageDrawable(imageDrawable)
                imageView.visibility = View.VISIBLE
                initialsView.visibility = View.GONE
            } else {
                imageView.visibility = View.GONE
                initialsView.visibility = View.VISIBLE
                initialsView.text = initialsText.take(2).uppercase().ifBlank { "?" }
                initialsView.setTextColor(look.contentColor)
                initialsView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleMedium).size.value,
                )
            }
            val description = accessibilityConfig.contentDescription
                ?: initialsText.takeIf { it.isNotBlank() }
            if (description != null) {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
                applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = description)
            } else {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
                contentDescription = null
                accessibilityConfig.testTag?.let { tag = it }
            }
            imageView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            initialsView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }
