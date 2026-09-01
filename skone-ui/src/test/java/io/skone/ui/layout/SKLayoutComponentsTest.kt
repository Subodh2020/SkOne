package io.skone.ui.layout

import io.skone.component.appearance.SKAppearanceConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKLayoutComponentsTest {

    @Test
    fun divider_orientation() {
        val divider = SKDividerComponent.create(id = "d1", orientation = SKDividerOrientation.Vertical)
        assertEquals(SKDividerOrientation.Vertical, divider.orientation)
        assertEquals(SKAppearanceConfig.Divider.containerColorRole, divider.config.appearance.containerColorRole)
        divider.setOrientation(SKDividerOrientation.Horizontal)
        assertEquals(SKDividerOrientation.Horizontal, divider.orientation)
    }

    @Test
    fun card_clickableGatesPerformClick() {
        val card = SKCardComponent.create(id = "c1", clickable = false)
        assertFalse(card.interactive)
        card.performClick()
        card.setClickable(true)
        assertTrue(card.clickable)
        assertTrue(card.interactive)
        card.setEnabled(false)
        assertFalse(card.interactive)
    }
}
