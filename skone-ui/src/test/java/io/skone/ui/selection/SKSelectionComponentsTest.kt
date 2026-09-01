package io.skone.ui.selection

import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.icon.SKIconKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKSelectionComponentsTest {

    @Test
    fun radioGroup_selectsOneValue() {
        val group = SKRadioGroupController.create()
        assertNull(group.selected)
        group.select("a")
        assertEquals("a", group.selected)
        group.select("b")
        assertEquals("b", group.selected)
        group.clear()
        assertNull(group.selected)
    }

    @Test
    fun radioButton_selectedAndDisabled() {
        val radio = SKRadioButtonComponent.create(id = "r1", value = "x", label = "Option X", selected = true)
        assertTrue(radio.selected)
        radio.setEnabled(false)
        assertFalse(radio.interactive)
        radio.performClick()
    }

    @Test
    fun chip_usesSelectedAppearanceByDefault() {
        val chip = SKChipComponent.create(id = "c1", label = "Filter", selected = true)
        assertTrue(chip.selected)
        assertEquals(
            SKAppearanceConfig.ChipSelected.containerColorRole,
            chip.config.appearance.containerColorRole,
        )
        chip.setSelected(false)
        assertFalse(chip.selected)
        chip.setLeadingIcon(SKIconKey("skone.icon.tag", contentDescription = "Tag"))
        assertEquals("Tag", chip.leadingIcon?.contentDescription)
    }
}
