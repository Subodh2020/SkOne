package io.skone.xml.widget

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import androidx.core.view.ViewCompat
import io.skone.component.accessibility.SKAccessibilityConfig

/**
 * Applies [SKAccessibilityConfig] fields that map onto Android View accessibility APIs.
 *
 * [SKAccessibilityConfig.testTag] is stored as [View.setTag] for Espresso / Robolectric
 * (`withTagValue`) — there is no Compose-style semantics testTag on Views.
 */
internal fun View.applySKAccessibilityConfig(
    config: SKAccessibilityConfig,
    contentDescriptionFallback: String?,
    errorText: CharSequence? = null,
) {
    val description = config.contentDescription ?: contentDescriptionFallback
    if (!description.isNullOrBlank()) {
        contentDescription = description
    }

    config.testTag?.takeIf { it.isNotBlank() }?.let { tag = it }

    ViewCompat.setStateDescription(this, config.stateDescription)
    ViewCompat.setAccessibilityHeading(this, config.heading)

    if (config.traversalIndex != null) {
        // View traversal APIs are pairwise (before/after); float index alone has no 1:1 View mapping.
        // Persist for hosts that read config from the component; do not invent fake traversal edges.
    }

    if (errorText != null) {
        accessibilityDelegate =
            object : View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfo,
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.text?.let { /* keep */ }
                    info.isContentInvalid = true
                    @Suppress("DEPRECATION")
                    info.error = errorText
                }
            }
    } else if (this is EditText) {
        // Clear a previous error delegate when leaving error state.
        accessibilityDelegate = null
    }
}
