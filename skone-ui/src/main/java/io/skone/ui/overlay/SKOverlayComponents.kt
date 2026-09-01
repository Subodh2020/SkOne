@file:OptIn(io.skone.common.annotation.SKExperimental::class)

package io.skone.ui.overlay

import io.skone.component.SKAnalyticsConfig
import io.skone.component.SKComponentConfig
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.ai.SKAIComponentConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.behavior.SKBehaviorConfig
import io.skone.component.framework.base.SKBaseInteractiveComponent
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Snackbar message contract (Compose + XML). Host controls visibility; no overlay manager.
 *
 * @see docs/WIDGETS_SKSNACKBAR.md
 */
public class SKSnackbarComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Snackbar,
        behavior = SKBehaviorConfig.Passive,
    ),
    message: String,
    actionLabel: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val messageRef = AtomicReference(message)
    private val actionLabelRef = AtomicReference(actionLabel)
    private val visibleRef = AtomicBoolean(true)

    public val message: String get() = messageRef.get()
    public val actionLabel: String? get() = actionLabelRef.get()
    public val visible: Boolean get() = visibleRef.get()

    public fun setMessage(value: String) { messageRef.set(value) }
    public fun setActionLabel(value: String?) { actionLabelRef.set(value) }
    public fun setVisible(value: Boolean) { visibleRef.set(value) }

    public fun performAction() {
        if (!config.enabled || actionLabel.isNullOrBlank()) return
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKSnackbar"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            message: String,
            actionLabel: String? = null,
            visible: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Snackbar,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKSnackbarComponent = SKSnackbarComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            message = message,
            actionLabel = actionLabel,
        ).also { it.setVisible(visible) }
    }
}

/**
 * Generic dialog contract. Host owns show/dismiss; content is surface-specific.
 *
 * @see docs/WIDGETS_SKDIALOG.md
 */
public class SKDialogComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Dialog,
        behavior = SKBehaviorConfig.Passive,
    ),
    title: String? = null,
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val titleRef = AtomicReference(title)
    private val visibleRef = AtomicBoolean(true)

    public val title: String? get() = titleRef.get()
    public val visible: Boolean get() = visibleRef.get()

    public fun setTitle(value: String?) { titleRef.set(value) }
    public fun setVisible(value: Boolean) { visibleRef.set(value) }

    public fun dismiss() {
        setVisible(false)
        super.performClick()
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKDialog"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String? = null,
            visible: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Dialog,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKDialogComponent = SKDialogComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            title = title,
        ).also { it.setVisible(visible) }
    }
}

/**
 * Alert dialog with title, message, and confirm/dismiss actions.
 *
 * @see docs/WIDGETS_SKALERTDIALOG.md
 */
public class SKAlertDialogComponent(
    id: String,
    config: SKComponentConfig = SKComponentConfig(
        appearance = SKAppearanceConfig.Dialog,
        behavior = SKBehaviorConfig.Passive,
    ),
    title: String,
    message: String,
    confirmLabel: String = "OK",
    dismissLabel: String? = "Cancel",
) : SKBaseInteractiveComponent(id, COMPONENT_TYPE, config) {
    private val titleRef = AtomicReference(title)
    private val messageRef = AtomicReference(message)
    private val confirmRef = AtomicReference(confirmLabel)
    private val dismissRef = AtomicReference(dismissLabel)
    private val visibleRef = AtomicBoolean(true)

    public val title: String get() = titleRef.get()
    public val message: String get() = messageRef.get()
    public val confirmLabel: String get() = confirmRef.get()
    public val dismissLabel: String? get() = dismissRef.get()
    public val visible: Boolean get() = visibleRef.get()

    public fun setTitle(value: String) { titleRef.set(value) }
    public fun setMessage(value: String) { messageRef.set(value) }
    public fun setConfirmLabel(value: String) { confirmRef.set(value) }
    public fun setDismissLabel(value: String?) { dismissRef.set(value) }
    public fun setVisible(value: Boolean) { visibleRef.set(value) }

    public fun confirm() {
        setVisible(false)
        super.performClick()
    }

    public fun dismiss() {
        setVisible(false)
    }

    public companion object {
        public const val COMPONENT_TYPE: String = "SKAlertDialog"

        @JvmStatic
        @JvmOverloads
        public fun create(
            id: String,
            title: String,
            message: String,
            confirmLabel: String = "OK",
            dismissLabel: String? = "Cancel",
            visible: Boolean = true,
            appearance: SKAppearanceConfig = SKAppearanceConfig.Dialog,
            accessibility: SKAccessibilityConfig = SKAccessibilityConfig.None,
            analytics: SKAnalyticsConfig? = null,
            ai: SKAIComponentConfig? = null,
        ): SKAlertDialogComponent = SKAlertDialogComponent(
            id = id,
            config = SKComponentConfig(
                appearance = appearance,
                behavior = SKBehaviorConfig.Passive,
                accessibility = accessibility,
                analytics = analytics,
                ai = ai,
            ),
            title = title,
            message = message,
            confirmLabel = confirmLabel,
            dismissLabel = dismissLabel,
        ).also { it.setVisible(visible) }
    }
}
