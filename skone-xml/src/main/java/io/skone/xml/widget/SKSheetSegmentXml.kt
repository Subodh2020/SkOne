@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import io.skone.ui.overlay.SKBottomSheetComponent
import io.skone.ui.overlay.SKSegmentItem
import io.skone.ui.overlay.SKSegmentedButtonComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx
import java.util.UUID

/**
 * XML bottom sheet via a bottom-gravity [Dialog]. Host owns show/dismiss.
 *
 * @see docs/WIDGETS_SKBOTTOMSHEET.md
 */
public class SKBottomSheetView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skbottomsheet-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.BottomSheet
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var titleText: String? = null
        private var primaryLabel: String? = null
        private var secondaryLabel: String? = null
        private var primaryEnabled: Boolean = true
        private var secondaryEnabled: Boolean = true
        private var primaryListener: (() -> Unit)? = null
        private var secondaryListener: (() -> Unit)? = null
        private var dismissListener: (() -> Unit)? = null
        private var sheetDialog: Dialog? = null
        private var cached: SKBottomSheetComponent? = null

        public val contentContainer: LinearLayout = LinearLayout(context).apply {
            orientation = VERTICAL
        }

        private val sheet: SKBottomSheetComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKBottomSheetComponent.create(
                    id = componentId,
                    title = titleText,
                    primaryActionLabel = primaryLabel,
                    secondaryActionLabel = secondaryLabel,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = sheet
        public val isShowing: Boolean get() = sheetDialog?.isShowing == true

        init {
            orientation = VERTICAL
            // Host helper; not shown inline — use show()/dismiss().
            visibility = GONE
            attrs?.let { applyAttributes(it) }
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKBottomSheetView)
            try {
                a.getString(R.styleable.SKBottomSheetView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKBottomSheetView_skTitle)?.let { titleText = it }
                a.getString(R.styleable.SKBottomSheetView_skPrimaryAction)?.let { primaryLabel = it }
                a.getString(R.styleable.SKBottomSheetView_skSecondaryAction)?.let { secondaryLabel = it }
                a.getString(R.styleable.SKBottomSheetView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKBottomSheetView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { sheet.detach() }
            this.runtime = runtime
            sheet.attach(runtime)
        }

        public fun unbind() {
            dismiss()
            runtime?.let { sheet.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setSheetTitle(value: String?) {
            titleText = value
            sheet.setTitle(value)
        }

        public fun setPrimaryAction(label: String?, enabled: Boolean = true, listener: (() -> Unit)?) {
            primaryLabel = label
            primaryEnabled = enabled
            primaryListener = listener
            sheet.setPrimaryActionLabel(label)
        }

        public fun setSecondaryAction(label: String?, enabled: Boolean = true, listener: (() -> Unit)?) {
            secondaryLabel = label
            secondaryEnabled = enabled
            secondaryListener = listener
            sheet.setSecondaryActionLabel(label)
        }

        public fun setOnDismissListener(listener: (() -> Unit)?) {
            dismissListener = listener
        }

        public fun setAccessibility(value: SKAccessibilityConfig) {
            accessibilityConfig = value
        }

        public fun setSheetContent(view: View?) {
            contentContainer.removeAllViews()
            if (view != null) {
                contentContainer.addView(
                    view,
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
                )
            }
        }

        public fun show() {
            if (sheetDialog?.isShowing == true) return
            sheet.setVisible(true)
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            val root = LinearLayout(context).apply {
                orientation = VERTICAL
                background = look.toBackgroundDrawable()
                elevation = look.elevationPx ?: 0f
                val padH = look.horizontalPaddingPx.toInt()
                val padV = look.verticalPaddingPx.toInt()
                setPadding(padH, padV, padH, padV)
                contentDescription = accessibilityConfig.contentDescription
                    ?: titleText
                    ?: "Bottom sheet"
                accessibilityConfig.testTag?.let { tag = it }
                if (!titleText.isNullOrBlank()) {
                    addView(
                        AppCompatTextView(context).apply {
                            text = titleText
                            setTextColor(look.contentColor)
                            setTextSize(
                                TypedValue.COMPLEX_UNIT_SP,
                                theme.tokens.typography.scale(
                                    appearance.typographyRole ?: SKTypographyRole.TitleMedium,
                                ).size.value,
                            )
                        },
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
                    )
                }
                if (contentContainer.parent is ViewGroup) {
                    (contentContainer.parent as ViewGroup).removeView(contentContainer)
                }
                addView(contentContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
                val actions = LinearLayout(context).apply {
                    orientation = HORIZONTAL
                    gravity = Gravity.END
                }
                if (!secondaryLabel.isNullOrBlank()) {
                    actions.addView(actionButton(secondaryLabel!!, secondaryEnabled, "secondary") {
                        if (!secondaryEnabled) return@actionButton
                        sheet.performSecondaryAction()
                        secondaryListener?.invoke()
                        dismiss()
                    })
                }
                if (!primaryLabel.isNullOrBlank()) {
                    actions.addView(actionButton(primaryLabel!!, primaryEnabled, "primary") {
                        if (!primaryEnabled) return@actionButton
                        sheet.performPrimaryAction()
                        primaryListener?.invoke()
                        dismiss()
                    })
                }
                if (actions.childCount > 0) {
                    addView(actions, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
                }
            }
            sheetDialog = Dialog(context).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(
                    root,
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                )
                window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                window?.setGravity(Gravity.BOTTOM)
                setCanceledOnTouchOutside(true)
                setOnCancelListener {
                    sheet.dismiss()
                    dismissListener?.invoke()
                }
                setOnDismissListener {
                    sheet.setVisible(false)
                }
                show()
            }
        }

        public fun dismiss() {
            sheetDialog?.dismiss()
            sheetDialog = null
            sheet.dismiss()
            dismissListener?.invoke()
        }

        private fun actionButton(
            label: String,
            enabled: Boolean,
            kind: String,
            onClick: () -> Unit,
        ): AppCompatTextView {
            val theme = SKThemeHelper.current()
            return AppCompatTextView(context).apply {
                text = label
                isEnabled = enabled
                alpha = if (enabled) 1f else 0.38f
                setTextColor(theme.tokens.colors.color(SKColorRole.Primary).toArgb())
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    theme.tokens.typography.scale(SKTypographyRole.LabelLarge).size.value,
                )
                val pad = theme.tokens.spacing.sm.toPx(this).toInt()
                setPadding(pad, pad, pad, pad)
                tag = accessibilityConfig.testTag?.let { "${it}_$kind" } ?: "sheet_$kind"
                contentDescription = label
                setOnClickListener { onClick() }
            }
        }
    }

/**
 * XML segmented button group.
 *
 * @see docs/WIDGETS_SKSEGMENTEDBUTTON.md
 */
public class SKSegmentedButtonView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sksegmented-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.SegmentedButton
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var items: List<SKSegmentItem> = emptyList()
        private var selectedId: String? = null
        private var controlEnabled: Boolean = true
        private var selectListener: ((String) -> Unit)? = null
        private var cached: SKSegmentedButtonComponent? = null

        private val segmented: SKSegmentedButtonComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKSegmentedButtonComponent.create(
                    id = componentId,
                    items = items,
                    selectedId = selectedId,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = segmented
        public val currentSelectedId: String? get() = selectedId

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            attrs?.let { applyAttributes(it) }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKSegmentedButtonView)
            try {
                a.getString(R.styleable.SKSegmentedButtonView_skComponentId)?.let { componentId = it }
                controlEnabled = a.getBoolean(R.styleable.SKSegmentedButtonView_skEnabled, true)
                a.getString(R.styleable.SKSegmentedButtonView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKSegmentedButtonView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { segmented.detach() }
            this.runtime = runtime
            segmented.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { segmented.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setSegmentItems(value: List<SKSegmentItem>) {
            require(value.size >= 2) { "SKSegmentedButtonView requires at least 2 segments" }
            items = value
            segmented.setItems(value)
            if (selectedId == null) selectedId = value.firstOrNull()?.id
            render()
        }

        public fun setSelectedSegmentId(value: String?) {
            selectedId = value
            segmented.setSelectedId(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            segmented.setEnabled(value)
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
            val inset = theme.tokens.spacing.xxs.toPx(this).toInt()
            setPadding(inset, inset, inset, inset)
            items.forEach { item ->
                val selected = item.id == selectedId
                val segmentEnabled = controlEnabled && item.enabled
                val bg = if (selected) {
                    theme.tokens.colors.color(SKColorRole.Primary).toArgb()
                } else {
                    android.graphics.Color.TRANSPARENT
                }
                val fg = if (selected) {
                    theme.tokens.colors.color(SKColorRole.OnPrimary).toArgb()
                } else {
                    look.contentColor
                }
                val itemView = AppCompatTextView(context).apply {
                    text = item.label
                    gravity = Gravity.CENTER
                    setTextColor(fg)
                    setBackgroundColor(bg)
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.LabelLarge).size.value,
                    )
                    val padH = theme.tokens.spacing.sm.toPx(this).toInt()
                    val padV = theme.tokens.spacing.sm.toPx(this).toInt()
                    setPadding(padH, padV, padH, padV)
                    isEnabled = segmentEnabled
                    alpha = if (item.enabled) 1f else 0.38f
                    tag = accessibilityConfig.testTag?.let { "${it}_${item.id}" } ?: "segment_${item.id}"
                    contentDescription = item.label
                    isSelected = selected
                    ViewCompat.setStateDescription(this, if (selected) "Selected" else "Not selected")
                    setOnClickListener {
                        if (!segmentEnabled) return@setOnClickListener
                        segmented.select(item.id)
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
