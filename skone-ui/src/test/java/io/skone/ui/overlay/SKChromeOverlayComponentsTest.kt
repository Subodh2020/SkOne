package io.skone.ui.overlay

import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.icon.SKIconKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKChromeOverlayComponentsTest {

    @Test
    fun menu_activatesEnabledOnly() {
        val menu = SKMenuComponent.create(
            id = "m1",
            items = listOf(
                SKMenuItem("edit", "Edit"),
                SKMenuItem("delete", "Delete", enabled = false),
                SKMenuItem("share", "Share", leadingIcon = SKIconKey("skone.icon.share")),
            ),
        )
        menu.activate("delete")
        assertNull(menu.lastActionId)
        menu.activate("edit")
        assertEquals("edit", menu.lastActionId)
    }

    @Test
    fun dropdown_selectDismissAndDisabled() {
        val dropdown = SKDropdownMenuComponent.create(
            id = "d1",
            items = listOf(
                SKMenuItem("a", "Alpha"),
                SKMenuItem("b", "Beta", enabled = false),
            ),
            expanded = true,
            selectedId = "a",
        )
        assertTrue(dropdown.expanded)
        dropdown.select("b")
        assertEquals("a", dropdown.selectedId)
        assertTrue(dropdown.expanded)
        dropdown.select("a")
        assertEquals("a", dropdown.selectedId)
        assertFalse(dropdown.expanded)
        dropdown.setExpanded(true)
        dropdown.setEnabled(false)
        dropdown.select("a")
        assertTrue(dropdown.expanded)
        dropdown.dismiss()
        assertFalse(dropdown.expanded)
    }

    @Test
    fun tooltip_visibilityAndMessage() {
        val tip = SKTooltipComponent.create(
            id = "t1",
            message = "Hint",
            visible = false,
            appearance = SKAppearanceConfig.Tooltip,
            accessibility = SKAccessibilityConfig(testTag = "tip"),
        )
        assertFalse(tip.visible)
        tip.setVisible(true)
        tip.setMessage("Updated")
        assertTrue(tip.visible)
        assertEquals("Updated", tip.message)
    }

    @Test
    fun bottomAppBar_creates() {
        val bar = SKBottomAppBarComponent.create(id = "b1")
        assertEquals("SKBottomAppBar", SKBottomAppBarComponent.COMPONENT_TYPE)
        assertEquals("b1", bar.id)
    }
}
