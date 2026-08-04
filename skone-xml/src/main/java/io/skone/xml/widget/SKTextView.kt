package io.skone.xml.widget

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.text.style.ForegroundColorSpan
import android.text.style.AbsoluteSizeSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.component.framework.SKComponentRuntime
import io.skone.theme.SKTheme
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.color
import io.skone.theme.tokens.scale
import io.skone.ui.text.SKAnnotatedText
import io.skone.ui.text.SKSpanStyle
import io.skone.ui.text.SKTextAlign
import io.skone.ui.text.SKTextComponent
import io.skone.ui.text.SKTextOverflow
import io.skone.xml.R
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.toArgb
import java.util.UUID

/**
 * SKOne text widget (XML / Views) — paired with Compose [io.skone.compose.widget.SKText].
 *
 * Visuals resolve through appearance + [SKThemeHelper] tokens only.
 * Call [bind] with a [SKComponentRuntime] from the host Activity/Fragment.
 *
 * @see docs/WIDGETS_SKTEXT.md
 */
public class SKTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var runtime: SKComponentRuntime? = null
    private var componentId: String = "sktext-${UUID.randomUUID()}"
    private var appearance: SKAppearanceConfig = SKAppearanceConfig.Text
    private var overflow: SKTextOverflow = SKTextOverflow.Clip
    private var textAlign: SKTextAlign = SKTextAlign.Start
    private var softWrapEnabled: Boolean = true
    private var annotated: SKAnnotatedText = SKAnnotatedText.plain("")
    private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None

    private val textComponent: SKTextComponent by lazy {
        SKTextComponent.create(
            id = componentId,
            annotated = annotated,
            appearance = appearance,
            overflow = overflow,
            maxLines = maxLines,
            softWrap = softWrapEnabled,
            textAlign = textAlign,
            accessibility = accessibilityConfig,
        )
    }

    /** Framework component for lifecycle / analytics / plugins. */
    public val component: SKComponent
        get() = textComponent

    init {
        textDirection = View.TEXT_DIRECTION_LOCALE
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        attrs?.let { applyAttributes(it) }
        render()
    }

    private fun applyAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SKTextView)
        try {
            a.getString(R.styleable.SKTextView_skComponentId)?.let { componentId = it }
            a.getString(R.styleable.SKTextView_skText)?.let {
                annotated = SKAnnotatedText.plain(it)
            }
            if (a.hasValue(R.styleable.SKTextView_skMaxLines)) {
                maxLines = a.getInt(R.styleable.SKTextView_skMaxLines, Int.MAX_VALUE)
            }
            softWrapEnabled = a.getBoolean(R.styleable.SKTextView_skSoftWrap, true)
            overflow = when (a.getInt(R.styleable.SKTextView_skOverflow, 0)) {
                1 -> SKTextOverflow.Ellipsis
                2 -> SKTextOverflow.Visible
                else -> SKTextOverflow.Clip
            }
            textAlign = when (a.getInt(R.styleable.SKTextView_skTextAlign, 0)) {
                1 -> SKTextAlign.Center
                2 -> SKTextAlign.End
                3 -> SKTextAlign.Justify
                else -> SKTextAlign.Start
            }
            if (a.hasValue(R.styleable.SKTextView_skTypographyRole)) {
                val role = typographyRoleFromAttr(a.getInt(R.styleable.SKTextView_skTypographyRole, 9))
                appearance = appearance.copy(typographyRole = role)
            }
            if (a.hasValue(R.styleable.SKTextView_skContentColorRole)) {
                val role = contentColorRoleFromAttr(a.getInt(R.styleable.SKTextView_skContentColorRole, 9))
                appearance = appearance.copy(contentColorRole = role)
            }
            a.getString(R.styleable.SKTextView_skContentDescription)?.let {
                accessibilityConfig = SKAccessibilityConfig(contentDescription = it)
                contentDescription = it
            }
        } finally {
            a.recycle()
        }
    }

    /**
     * Binds framework runtime (lifecycle, analytics, plugins).
     */
    public fun bind(runtime: SKComponentRuntime) {
        this.runtime?.let { textComponent.detach() }
        this.runtime = runtime
        syncComponent()
        textComponent.attach(runtime)
    }

    /** Unbinds runtime. */
    public fun unbind() {
        runtime?.let { textComponent.detach() }
        runtime = null
    }

    override fun onDetachedFromWindow() {
        unbind()
        super.onDetachedFromWindow()
    }

    /** Sets plain text and re-renders from tokens. */
    public fun setSkText(value: String) {
        annotated = SKAnnotatedText.plain(value)
        if (accessibilityConfig.contentDescription == null) {
            contentDescription = value
        }
        syncComponent()
        render()
    }

    /** Sets annotated text and re-renders. */
    public fun setSkAnnotated(value: SKAnnotatedText) {
        annotated = value
        syncComponent()
        render()
    }

    /** Overrides appearance (token roles only) and re-renders. */
    public fun setAppearance(value: SKAppearanceConfig) {
        appearance = value
        syncComponent()
        render()
    }

    private fun syncComponent() {
        textComponent.setAnnotated(annotated)
        textComponent.setOverflow(overflow)
        textComponent.setMaxLines(maxLines.coerceAtLeast(1))
        textComponent.setSoftWrap(softWrapEnabled)
        textComponent.setTextAlign(textAlign)
        textComponent.updateConfig(
            SKComponentConfig(
                appearance = appearance,
                accessibility = accessibilityConfig,
            ),
        )
    }

    private fun render() {
        val theme = SKThemeHelper.current()
        applyTheme(theme)
        text = annotated.toSpannable(theme)
        applyOverflow()
        applyAlign()
        if (!softWrapEnabled) {
            setSingleLine(true)
        }
        if (contentDescription.isNullOrBlank()) {
            contentDescription = accessibilityConfig.contentDescription ?: annotated.text
        }
    }

    private fun applyTheme(theme: SKTheme) {
        val role = appearance.typographyRole ?: SKTypographyRole.BodyLarge
        val scale = theme.tokens.typography.scale(role)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.size.value)
        setLineSpacing(0f, scale.lineHeight.value / scale.size.value)
        setTypeface(Typeface.create(Typeface.SANS_SERIF, typefaceStyle(scale.weight)))
        setTextColor(theme.tokens.colors.color(appearance.contentColorRole).toArgb())
    }

    private fun applyOverflow() {
        ellipsize = when (overflow) {
            SKTextOverflow.Ellipsis -> TextUtils.TruncateAt.END
            SKTextOverflow.Clip -> null
            SKTextOverflow.Visible -> null
        }
    }

    private fun applyAlign() {
        gravity = when (textAlign) {
            SKTextAlign.Start -> Gravity.START or Gravity.CENTER_VERTICAL
            SKTextAlign.Center -> Gravity.CENTER
            SKTextAlign.End -> Gravity.END or Gravity.CENTER_VERTICAL
            SKTextAlign.Justify -> Gravity.START or Gravity.CENTER_VERTICAL
        }
    }

    private fun typefaceStyle(weight: Int): Int = when {
        weight >= 700 -> Typeface.BOLD
        else -> Typeface.NORMAL
    }

    private fun SKAnnotatedText.toSpannable(theme: SKTheme): CharSequence {
        if (spans.isEmpty()) return text
        val builder = SpannableStringBuilder(text)
        spans.forEach { span ->
            val start = span.start.coerceIn(0, text.length)
            val end = span.end.coerceIn(start, text.length)
            span.styles.forEach { style ->
                when (style) {
                    SKSpanStyle.Bold -> builder.setSpan(
                        StyleSpan(Typeface.BOLD),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    SKSpanStyle.Italic -> builder.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    SKSpanStyle.Underline -> builder.setSpan(
                        UnderlineSpan(),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    is SKSpanStyle.ColorRole -> builder.setSpan(
                        ForegroundColorSpan(theme.tokens.colors.color(style.role).toArgb()),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    is SKSpanStyle.TypographyRole -> {
                        val scale = theme.tokens.typography.scale(style.role)
                        builder.setSpan(
                            AbsoluteSizeSpan(scale.size.value.toInt(), true),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                        )
                        if (scale.weight >= 700) {
                            builder.setSpan(
                                StyleSpan(Typeface.BOLD),
                                start,
                                end,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                            )
                        }
                    }
                }
            }
        }
        return builder
    }

    private fun typographyRoleFromAttr(value: Int): SKTypographyRole = when (value) {
        0 -> SKTypographyRole.DisplayLarge
        1 -> SKTypographyRole.DisplayMedium
        2 -> SKTypographyRole.DisplaySmall
        3 -> SKTypographyRole.HeadlineLarge
        4 -> SKTypographyRole.HeadlineMedium
        5 -> SKTypographyRole.HeadlineSmall
        6 -> SKTypographyRole.TitleLarge
        7 -> SKTypographyRole.TitleMedium
        8 -> SKTypographyRole.TitleSmall
        9 -> SKTypographyRole.BodyLarge
        10 -> SKTypographyRole.BodyMedium
        11 -> SKTypographyRole.BodySmall
        12 -> SKTypographyRole.LabelLarge
        13 -> SKTypographyRole.LabelMedium
        14 -> SKTypographyRole.LabelSmall
        else -> SKTypographyRole.BodyLarge
    }

    private fun contentColorRoleFromAttr(value: Int): SKColorRole = when (value) {
        0 -> SKColorRole.Primary
        1 -> SKColorRole.OnPrimary
        2 -> SKColorRole.Secondary
        3 -> SKColorRole.OnSecondary
        4 -> SKColorRole.Error
        5 -> SKColorRole.OnError
        6 -> SKColorRole.Background
        7 -> SKColorRole.OnBackground
        8 -> SKColorRole.Surface
        9 -> SKColorRole.OnSurface
        10 -> SKColorRole.OnSurfaceVariant
        else -> SKColorRole.OnSurface
    }
}
