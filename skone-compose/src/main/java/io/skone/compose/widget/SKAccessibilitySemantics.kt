package io.skone.compose.widget

import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import io.skone.component.accessibility.SKAccessibilityConfig

/**
 * Shared Compose mappings for [SKAccessibilityConfig] fields that both
 * [SKText] and [SKTextField] consume.
 */
internal fun String.toComposeRole(): Role? = when (lowercase()) {
    "button" -> Role.Button
    "checkbox" -> Role.Checkbox
    "switch" -> Role.Switch
    "radiobutton", "radio" -> Role.RadioButton
    "tab" -> Role.Tab
    "image" -> Role.Image
    "dropdownlist", "dropdown" -> Role.DropdownList
    else -> null
}

/**
 * Applies optional role / stateDescription / liveRegion / traversalIndex onto a
 * semantics receiver. Does not set contentDescription, testTag, or heading.
 */
internal fun SemanticsPropertyReceiver.applyOptionalAccessibility(config: SKAccessibilityConfig) {
    config.stateDescription?.takeIf { it.isNotBlank() }?.let { stateDescription = it }
    config.role?.toComposeRole()?.let { role = it }
    if (config.liveRegion) {
        liveRegion = LiveRegionMode.Polite
    }
    config.traversalIndex?.let { traversalIndex = it }
}
