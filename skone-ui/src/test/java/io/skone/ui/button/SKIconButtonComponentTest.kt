package io.skone.ui.button

import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.icon.SKIconKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKIconButtonComponentTest {

    @Test
    fun semanticDescription_prefersAccessibilityThenIconCd_neverRawKeyAlone() {
        val icon = SKIconKey("skone.icon.close")
        val missing = SKIconButtonComponent.create(id = "i1", icon = icon)
        assertNull(missing.semanticDescription)

        val fromIcon = SKIconButtonComponent.create(
            id = "i2",
            icon = SKIconKey("skone.icon.close", contentDescription = "Close"),
        )
        assertEquals("Close", fromIcon.semanticDescription)

        val fromA11y = SKIconButtonComponent.create(
            id = "i3",
            icon = icon,
            accessibility = SKAccessibilityConfig(contentDescription = "Dismiss dialog"),
        )
        assertEquals("Dismiss dialog", fromA11y.semanticDescription)
        assertFalse(fromA11y.semanticDescription == "skone.icon.close")
    }

    @Test
    fun disabled_notInteractive() {
        val button = SKIconButtonComponent.create(
            id = "i4",
            icon = SKIconKey("skone.icon.menu", contentDescription = "Menu"),
            enabled = false,
            appearance = SKAppearanceConfig.IconButton,
        )
        assertFalse(button.interactive)
        var clicked = false
        button.addInteractionListener(
            object : io.skone.component.framework.SKInteractionListener {
                override fun onClick(component: io.skone.component.framework.SKInteractiveComponent) {
                    clicked = true
                }
            },
        )
        button.performClick()
        assertFalse(clicked)
    }
}
