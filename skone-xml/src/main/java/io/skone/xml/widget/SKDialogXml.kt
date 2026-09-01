@file:OptIn(io.skone.common.annotation.SKInternal::class)

package io.skone.xml.widget

import android.app.Dialog
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.SKComponent
import io.skone.theme.tokens.SKTypographyRole
import io.skone.theme.tokens.scale
import io.skone.ui.overlay.SKAlertDialogComponent
import io.skone.ui.overlay.SKDialogComponent
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.theme.resolve
import io.skone.xml.theme.toBackgroundDrawable
import io.skone.xml.theme.toPx

/**
 * Programmatic XML dialog host. Not an inflate-only View — mirrors Compose [visible] + content.
 *
 * @see docs/WIDGETS_SKDIALOG.md
 */
public class SKDialogHost(
    private val context: Context,
) {
    private var dialog: Dialog? = null
    private var componentId: String = "skdialog-${System.currentTimeMillis()}"
    private var appearance: SKAppearanceConfig = SKAppearanceConfig.Dialog
    private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
    private var titleText: String? = null
    private var contentView: View? = null
    private var dismissListener: (() -> Unit)? = null
    private var cached: SKDialogComponent? = null

    private val component: SKDialogComponent
        get() {
            val existing = cached
            if (existing != null && existing.id == componentId) return existing
            return SKDialogComponent.create(
                id = componentId,
                title = titleText,
                appearance = appearance,
                accessibility = accessibilityConfig,
            ).also { cached = it }
        }

    public val skComponent: SKComponent get() = component

    public fun setTitle(value: String?): SKDialogHost = apply {
        titleText = value
        component.setTitle(value)
    }

    public fun setContentView(view: View): SKDialogHost = apply { contentView = view }

    public fun setOnDismissListener(listener: (() -> Unit)?): SKDialogHost = apply {
        dismissListener = listener
    }

    public fun setAccessibility(value: SKAccessibilityConfig): SKDialogHost = apply {
        accessibilityConfig = value
    }

    public fun show() {
        dismiss()
        component.setVisible(true)
        val theme = SKThemeHelper.current()
        val look = appearance.resolve(theme, View(context))
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = look.toBackgroundDrawable()
            elevation = look.elevationPx ?: 0f
            val padH = look.horizontalPaddingPx.toInt()
            val padV = look.verticalPaddingPx.toInt()
            setPadding(padH, padV, padH, padV)
            titleText?.takeIf { it.isNotBlank() }?.let { title ->
                addView(
                    AppCompatTextView(context).apply {
                        text = title
                        setTextColor(look.contentColor)
                        setTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            theme.tokens.typography.scale(SKTypographyRole.TitleLarge).size.value,
                        )
                    },
                )
            }
            contentView?.let { addView(it) }
            applySKAccessibilityConfig(accessibilityConfig, contentDescriptionFallback = titleText)
        }
        dialog = Dialog(context).apply {
            setContentView(ScrollView(context).apply { addView(root) })
            setOnDismissListener {
                component.setVisible(false)
                dismissListener?.invoke()
            }
            show()
        }
    }

    public fun dismiss() {
        component.dismiss()
        dialog?.dismiss()
        dialog = null
    }
}

/**
 * Programmatic alert dialog for XML (title, message, confirm/dismiss).
 *
 * @see docs/WIDGETS_SKALERTDIALOG.md
 */
public class SKAlertDialogHost(
    private val context: Context,
) {
    private var dialog: Dialog? = null
    private var componentId: String = "skalert-${System.currentTimeMillis()}"
    private var appearance: SKAppearanceConfig = SKAppearanceConfig.Dialog
    private var accessibilityConfig: SKAccessibilityConfig = SKAccessibilityConfig.None
    private var titleText: String = ""
    private var messageText: String = ""
    private var confirmText: String = "OK"
    private var dismissText: String? = "Cancel"
    private var confirmListener: (() -> Unit)? = null
    private var dismissListener: (() -> Unit)? = null
    private var cached: SKAlertDialogComponent? = null

    private val component: SKAlertDialogComponent
        get() {
            val existing = cached
            if (existing != null && existing.id == componentId) return existing
            return SKAlertDialogComponent.create(
                id = componentId,
                title = titleText,
                message = messageText,
                confirmLabel = confirmText,
                dismissLabel = dismissText,
                appearance = appearance,
                accessibility = accessibilityConfig,
            ).also { cached = it }
        }

    public val skComponent: SKComponent get() = component

    public fun setTitle(value: String): SKAlertDialogHost = apply {
        titleText = value
        component.setTitle(value)
    }

    public fun setMessage(value: String): SKAlertDialogHost = apply {
        messageText = value
        component.setMessage(value)
    }

    public fun setConfirmLabel(value: String): SKAlertDialogHost = apply {
        confirmText = value
        component.setConfirmLabel(value)
    }

    public fun setDismissLabel(value: String?): SKAlertDialogHost = apply {
        dismissText = value
        component.setDismissLabel(value)
    }

    public fun setOnConfirmListener(listener: (() -> Unit)?): SKAlertDialogHost = apply {
        confirmListener = listener
    }

    public fun setOnDismissListener(listener: (() -> Unit)?): SKAlertDialogHost = apply {
        dismissListener = listener
    }

    public fun setAccessibility(value: SKAccessibilityConfig): SKAlertDialogHost = apply {
        accessibilityConfig = value
    }

    public fun show() {
        dismiss()
        component.setVisible(true)
        val theme = SKThemeHelper.current()
        val look = appearance.resolve(theme, View(context))
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = look.toBackgroundDrawable()
            elevation = look.elevationPx ?: 0f
            val padH = look.horizontalPaddingPx.toInt()
            val padV = look.verticalPaddingPx.toInt()
            setPadding(padH, padV, padH, padV)
            addView(
                AppCompatTextView(context).apply {
                    text = titleText
                    setTextColor(look.contentColor)
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        theme.tokens.typography.scale(SKTypographyRole.TitleLarge).size.value,
                    )
                },
            )
            addView(
                AppCompatTextView(context).apply {
                    text = messageText
                    setTextColor(look.contentColor)
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        theme.tokens.typography.scale(SKTypographyRole.BodyMedium).size.value,
                    )
                    setPadding(0, theme.tokens.spacing.sm.toPx(this).toInt(), 0, 0)
                },
            )
            val actions = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
                setPadding(0, theme.tokens.spacing.md.toPx(this).toInt(), 0, 0)
            }
            dismissText?.takeIf { it.isNotBlank() }?.let { label ->
                actions.addView(
                    AppCompatTextView(context).apply {
                        text = label
                        setTextColor(look.contentColor)
                        setOnClickListener {
                            component.dismiss()
                            dismissListener?.invoke()
                            dialog?.dismiss()
                        }
                    },
                )
            }
            actions.addView(
                AppCompatTextView(context).apply {
                    text = confirmText
                    tag = accessibilityConfig.testTag?.let { "${it}_confirm" } ?: "sk_alert_confirm"
                    setTextColor(look.contentColor)
                    setPadding(theme.tokens.spacing.md.toPx(this).toInt(), 0, 0, 0)
                    setOnClickListener {
                        component.confirm()
                        confirmListener?.invoke()
                        dialog?.dismiss()
                    }
                },
            )
            addView(actions)
            applySKAccessibilityConfig(
                accessibilityConfig,
                contentDescriptionFallback = "$titleText. $messageText",
            )
        }
        dialog = Dialog(context).apply {
            setContentView(root)
            setOnDismissListener {
                component.setVisible(false)
                dismissListener?.invoke()
            }
            show()
        }
    }

    public fun dismiss() {
        component.dismiss()
        dialog?.dismiss()
        dialog = null
    }
}
