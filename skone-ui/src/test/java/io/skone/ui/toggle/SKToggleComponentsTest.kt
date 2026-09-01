package io.skone.ui.toggle

import io.skone.component.appearance.SKAppearanceConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKToggleComponentsTest {

    @Test
    fun checkbox_togglesCheckedAndBlocksWhenDisabled() {
        val box = SKCheckboxComponent.create(id = "c1", checked = false, label = "Accept")
        assertFalse(box.checked)
        box.toggle()
        assertTrue(box.checked)
        box.setEnabled(false)
        assertFalse(box.interactive)
        box.toggle()
        assertTrue(box.checked) // unchanged while disabled
    }

    @Test
    fun switch_setCheckedAndAppearanceDefault() {
        val sw = SKSwitchComponent.create(id = "s1", checked = true, label = "Dark")
        assertTrue(sw.checked)
        assertEquals(SKAppearanceConfig.Toggle.containerColorRole, sw.config.appearance.containerColorRole)
        sw.setChecked(false)
        assertFalse(sw.checked)
    }
}
