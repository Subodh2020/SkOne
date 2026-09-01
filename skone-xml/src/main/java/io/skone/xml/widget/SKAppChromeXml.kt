@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.search.SKEmptyStateComponent
import io.skone.ui.search.SKFabComponent
import io.skone.ui.search.SKSearchBarComponent
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toArgb
import io.skone.xml.theme.toBackgroundDrawable
import java.util.UUID

/**
 * Search input (XML). Host-owned query — reuses EditText + IME Search (not a second field framework).
 *
 * @see docs/WIDGETS_SKSEARCHBAR.md
 */
public class SKSearchBarView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "sksearchbar-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.SearchBar
        private var accessibilityConfig: SKAccessibilityConfig =
            SKAccessibilityConfig(contentDescription = "Search")
        private var placeholderText: String = "Search"
        private var controlEnabled: Boolean = true
        private var suppressWatcher: Boolean = false
        private var queryListener: ((String) -> Unit)? = null
        private var searchListener: ((String) -> Unit)? = null
        private var clearListener: (() -> Unit)? = null
        private var cached: SKSearchBarComponent? = null

        private val leadingIcon = AppCompatTextView(context)
        private val editText = AppCompatEditText(context)
        private val clearButton = AppCompatTextView(context)

        private val searchBar: SKSearchBarComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKSearchBarComponent.create(
                    id = componentId,
                    query = editText.text?.toString().orEmpty(),
                    placeholder = placeholderText,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = searchBar
        public val input: AppCompatEditText get() = editText

        init {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isFocusable = false
            attrs?.let { applyAttributes(it) }
            leadingIcon.text = "⌕"
            leadingIcon.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            clearButton.text = "✕"
            clearButton.contentDescription = "Clear search"
            clearButton.isClickable = true
            clearButton.isFocusable = true
            clearButton.setOnClickListener {
                if (!searchBar.interactive) return@setOnClickListener
                searchBar.clear()
                setQuery("")
                queryListener?.invoke("")
                clearListener?.invoke()
            }
            editText.imeOptions = EditorInfo.IME_ACTION_SEARCH
            editText.isSingleLine = true
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchListener?.invoke(editText.text?.toString().orEmpty())
                    true
                } else {
                    false
                }
            }
            editText.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                    override fun afterTextChanged(s: Editable?) {
                        if (suppressWatcher) return
                        val q = s?.toString().orEmpty()
                        searchBar.setQuery(q)
                        updateClearVisibility(q)
                        queryListener?.invoke(q)
                    }
                },
            )
            editText.setOnFocusChangeListener { _, hasFocus ->
                searchBar.onFocusChanged(hasFocus)
            }
            addView(leadingIcon, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(editText, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            addView(clearButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKSearchBarView)
            try {
                a.getString(R.styleable.SKSearchBarView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKSearchBarView_skQuery)?.let { editText.setText(it) }
                a.getString(R.styleable.SKSearchBarView_skPlaceholder)?.let { placeholderText = it }
                controlEnabled = a.getBoolean(R.styleable.SKSearchBarView_skEnabled, true)
                a.getString(R.styleable.SKSearchBarView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKSearchBarView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { searchBar.detach() }
            this.runtime = runtime
            searchBar.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { searchBar.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setQuery(value: String) {
            if (editText.text?.toString() == value) {
                updateClearVisibility(value)
                return
            }
            suppressWatcher = true
            editText.setText(value)
            editText.setSelection(value.length.coerceAtMost(editText.text?.length ?: 0))
            suppressWatcher = false
            searchBar.setQuery(value)
            updateClearVisibility(value)
        }

        public fun setPlaceholder(value: String) {
            placeholderText = value
            searchBar.setPlaceholder(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            searchBar.setEnabled(value)
            render()
        }

        public fun setOnQueryChangeListener(listener: ((String) -> Unit)?) {
            queryListener = listener
        }

        public fun setOnSearchListener(listener: ((String) -> Unit)?) {
            searchListener = listener
        }

        public fun setOnClearListener(listener: (() -> Unit)?) {
            clearListener = listener
        }

        public fun setAccessibility(config: SKAccessibilityConfig) {
            accessibilityConfig = config
            render()
        }

        private fun updateClearVisibility(query: String) {
            clearButton.visibility = if (query.isNotEmpty() && controlEnabled) View.VISIBLE else View.GONE
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            background = look.toBackgroundDrawable()
            minimumHeight = look.heightPx.toInt()
            setPadding(
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
                look.horizontalPaddingPx.toInt(),
                look.verticalPaddingPx.toInt(),
            )
            editText.isEnabled = controlEnabled
            editText.isFocusable = controlEnabled
            editText.isFocusableInTouchMode = controlEnabled
            editText.hint = placeholderText
            editText.setHintTextColor(theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb())
            editText.setTextColor(look.contentColor)
            val typeRole = appearance.typographyRole ?: SKTypographyRole.BodyLarge
            val scale = theme.tokens.typography.scale(typeRole)
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.size.value)
            editText.background = null
            leadingIcon.setTextColor(look.contentColor)
            clearButton.setTextColor(look.contentColor)
            clearButton.tag = accessibilityConfig.testTag?.let { "${it}_clear" } ?: "sk_search_clear"
            alpha = if (controlEnabled) 1f else 0.38f
            updateClearVisibility(editText.text?.toString().orEmpty())
            editText.applySKAccessibilityConfig(
                accessibilityConfig,
                contentDescriptionFallback = "Search",
            )
            importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }

/**
 * Empty / zero-results content (XML).
 *
 * @see docs/WIDGETS_SKEMPTYSTATE.md
 */
public class SKEmptyStateView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skemptystate-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.EmptyState
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
        private var titleText: String = ""
        private var descriptionText: String? = null
        private var iconKey: SKIconKey? = null
        private var primaryLabel: String? = null
        private var secondaryLabel: String? = null
        private var primaryListener: (() -> Unit)? = null
        private var secondaryListener: (() -> Unit)? = null
        private var cached: SKEmptyStateComponent? = null

        private val iconView = AppCompatTextView(context)
        private val titleView = AppCompatTextView(context)
        private val descriptionView = AppCompatTextView(context)
        private val primaryButton = AppCompatTextView(context)
        private val secondaryButton = AppCompatTextView(context)

        private val emptyState: SKEmptyStateComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKEmptyStateComponent.create(
                    id = componentId,
                    title = titleText,
                    description = descriptionText,
                    icon = iconKey,
                    primaryActionLabel = primaryLabel,
                    secondaryActionLabel = secondaryLabel,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = emptyState

        init {
            orientation = VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            attrs?.let { applyAttributes(it) }
            iconView.text = "◇"
            iconView.gravity = Gravity.CENTER
            primaryButton.isClickable = true
            primaryButton.isFocusable = true
            primaryButton.setOnClickListener {
                emptyState.performPrimaryAction()
                primaryListener?.invoke()
            }
            secondaryButton.isClickable = true
            secondaryButton.isFocusable = true
            secondaryButton.setOnClickListener {
                emptyState.performSecondaryAction()
                secondaryListener?.invoke()
            }
            addView(iconView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(titleView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(descriptionView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            addView(primaryButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            addView(secondaryButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKEmptyStateView)
            try {
                a.getString(R.styleable.SKEmptyStateView_skComponentId)?.let { componentId = it }
                a.getString(R.styleable.SKEmptyStateView_skTitle)?.let { titleText = it }
                a.getString(R.styleable.SKEmptyStateView_skDescription)?.let { descriptionText = it }
                a.getString(R.styleable.SKEmptyStateView_skIconKey)?.let { iconKey = SKIconKey(it) }
                a.getString(R.styleable.SKEmptyStateView_skPrimaryAction)?.let { primaryLabel = it }
                a.getString(R.styleable.SKEmptyStateView_skSecondaryAction)?.let { secondaryLabel = it }
                a.getString(R.styleable.SKEmptyStateView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKEmptyStateView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { emptyState.detach() }
            this.runtime = runtime
            emptyState.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { emptyState.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setTitle(value: String) {
            titleText = value
            emptyState.setTitle(value)
            render()
        }

        public fun setDescription(value: String?) {
            descriptionText = value
            emptyState.setDescription(value)
            render()
        }

        public fun setIcon(value: SKIconKey?) {
            iconKey = value
            emptyState.setIcon(value)
            render()
        }

        public fun setPrimaryAction(label: String?, listener: (() -> Unit)?) {
            primaryLabel = label
            primaryListener = listener
            emptyState.setPrimaryActionLabel(label)
            render()
        }

        public fun setSecondaryAction(label: String?, listener: (() -> Unit)?) {
            secondaryLabel = label
            secondaryListener = listener
            emptyState.setSecondaryActionLabel(label)
            render()
        }

        public fun setAccessibility(config: SKAccessibilityConfig) {
            accessibilityConfig = config
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
            titleView.gravity = Gravity.CENTER
            titleView.setTextColor(look.contentColor)
            val titleScale = theme.tokens.typography.scale(appearance.typographyRole ?: SKTypographyRole.TitleMedium)
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleScale.size.value)
            titleView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
            descriptionView.text = descriptionText
            descriptionView.visibility = if (descriptionText.isNullOrBlank()) View.GONE else View.VISIBLE
            descriptionView.gravity = Gravity.CENTER
            descriptionView.setTextColor(theme.tokens.colors.color(SKColorRole.OnSurfaceVariant).toArgb())
            val bodyScale = theme.tokens.typography.scale(SKTypographyRole.BodyMedium)
            descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, bodyScale.size.value)
            if (iconKey != null) {
                iconView.visibility = View.VISIBLE
                val cd = iconKey?.contentDescription
                if (cd.isNullOrBlank()) {
                    iconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
                    iconView.contentDescription = null
                } else {
                    iconView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
                    iconView.contentDescription = cd
                }
            } else {
                iconView.visibility = View.GONE
            }
            primaryButton.text = primaryLabel
            primaryButton.visibility =
                if (primaryLabel.isNullOrBlank() || primaryListener == null) View.GONE else View.VISIBLE
            primaryButton.contentDescription = primaryLabel
            primaryButton.tag = accessibilityConfig.testTag?.let { "${it}_primary" } ?: "sk_empty_primary"
            secondaryButton.text = secondaryLabel
            secondaryButton.visibility =
                if (secondaryLabel.isNullOrBlank() || secondaryListener == null) View.GONE else View.VISIBLE
            secondaryButton.contentDescription = secondaryLabel
            secondaryButton.tag = accessibilityConfig.testTag?.let { "${it}_secondary" } ?: "sk_empty_secondary"
            val merged = listOfNotNull(titleText, descriptionText).joinToString(". ")
            applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = merged)
        }
    }

/**
 * Floating action button (XML). Requires accessible content description (a11y or icon CD).
 *
 * @see docs/WIDGETS_SKFAB.md
 */
public class SKFabView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : AppCompatTextView(context, attrs, defStyleAttr) {
        private var runtime: SKComponentRuntime? = null
        private var componentId: String = "skfab-${UUID.randomUUID()}"
        private var appearance: SKAppearanceConfig = SKAppearanceConfig.Fab
        private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig(role = "button")
        private var iconKey: SKIconKey = SKIconKey("skone.icon.add")
        private var controlEnabled: Boolean = true
        private var clickListener: (() -> Unit)? = null
        private var cached: SKFabComponent? = null

        private val fab: SKFabComponent
            get() {
                val existing = cached
                if (existing != null && existing.id == componentId) return existing
                return SKFabComponent.create(
                    id = componentId,
                    icon = iconKey,
                    enabled = controlEnabled,
                    appearance = appearance,
                    accessibility = accessibilityConfig,
                ).also { cached = it }
            }

        public val component: SKComponent get() = fab

        init {
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            text = "+"
            attrs?.let { applyAttributes(it) }
            setOnClickListener {
                if (!fab.interactive) return@setOnClickListener
                fab.performClick()
                clickListener?.invoke()
            }
            render()
        }

        private fun applyAttributes(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SKFabView)
            try {
                a.getString(R.styleable.SKFabView_skComponentId)?.let { componentId = it }
                val key = a.getString(R.styleable.SKFabView_skIconKey) ?: "skone.icon.add"
                val iconCd = a.getString(R.styleable.SKFabView_skIconContentDescription)
                iconKey = SKIconKey(key, iconCd)
                controlEnabled = a.getBoolean(R.styleable.SKFabView_skEnabled, true)
                a.getString(R.styleable.SKFabView_skContentDescription)?.let {
                    accessibilityConfig = accessibilityConfig.copy(contentDescription = it)
                }
                a.getString(R.styleable.SKFabView_skTestTag)?.let {
                    accessibilityConfig = accessibilityConfig.copy(testTag = it)
                }
            } finally {
                a.recycle()
            }
        }

        public fun bind(runtime: SKComponentRuntime) {
            this.runtime?.let { fab.detach() }
            this.runtime = runtime
            fab.attach(runtime)
            render()
        }

        public fun unbind() {
            runtime?.let { fab.detach() }
            runtime = null
        }

        override fun onDetachedFromWindow() {
            unbind()
            super.onDetachedFromWindow()
        }

        public fun setIcon(value: SKIconKey) {
            iconKey = value
            fab.setIcon(value)
            render()
        }

        public fun setControlEnabled(value: Boolean) {
            controlEnabled = value
            fab.setEnabled(value)
            render()
        }

        public fun setOnFabClickListener(listener: (() -> Unit)?) {
            clickListener = listener
        }

        public fun setAccessibility(config: SKAccessibilityConfig) {
            accessibilityConfig = if (config.role == null) config.copy(role = "button") else config
            render()
        }

        private fun render() {
            val theme = SKThemeHelper.current()
            val look = appearance.resolve(theme, this)
            isEnabled = controlEnabled
            isClickable = controlEnabled
            alpha = if (controlEnabled) 1f else 0.38f
            background = look.toBackgroundDrawable()
            look.elevationPx?.let { elevation = it }
            minimumWidth = look.heightPx.toInt()
            minimumHeight = look.heightPx.toInt()
            setPadding(
                (look.horizontalPaddingPx / 2).toInt(),
                (look.verticalPaddingPx / 2).toInt(),
                (look.horizontalPaddingPx / 2).toInt(),
                (look.verticalPaddingPx / 2).toInt(),
            )
            setTextColor(look.contentColor)
            val scale = theme.tokens.typography.scale(SKTypographyRole.HeadlineSmall)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.size.value)
            val description = accessibilityConfig.contentDescription
                ?: iconKey.contentDescription?.takeIf { it.isNotBlank() }
            applySKAccessibilityConfig(
                config = accessibilityConfig.copy(
                    role = accessibilityConfig.role ?: "button",
                    contentDescription = description,
                ),
                contentDescriptionFallback = description,
            )
        }
    }
