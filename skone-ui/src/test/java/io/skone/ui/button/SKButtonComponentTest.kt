package io.skone.ui.button

import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.theme.tokens.SKColorRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKButtonComponentTest {

    @Test
    fun create_setsLabelAndFilledDefaults() {
        val button = SKButtonComponent.create(id = "b1", text = "Save")
        assertEquals("Save", button.text)
        assertEquals(SKAppearanceConfig.Button.containerColorRole, button.config.appearance.containerColorRole)
        assertTrue(button.config.behavior.clickable)
        assertTrue(button.interactive)
    }

    @Test
    fun disabled_isNotInteractive() {
        val button = SKButtonComponent.create(id = "b2", text = "Go", enabled = false)
        assertFalse(button.config.enabled)
        assertFalse(button.interactive)
    }

    @Test
    fun loading_blocksClicksWithoutClearingEnabled() {
        val button = SKButtonComponent.create(id = "b3", text = "Submit", enabled = true, loading = true)
        assertTrue(button.config.state.enabled)
        assertTrue(button.loading)
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

        button.setLoading(false)
        button.performClick()
        assertTrue(clicked)
    }

    @Test
    fun leadingIcon_andSetters() {
        val icon = SKIconKey("skone.icon.check", contentDescription = "Done")
        val button = SKButtonComponent.create(id = "b4", text = "OK", leadingIcon = icon)
        assertEquals(icon, button.leadingIcon)
        button.setText("Done")
        button.setLeadingIcon(null)
        assertEquals("Done", button.text)
        assertEquals(null, button.leadingIcon)
    }

    @Test
    fun variants_useDistinctTokenRoles() {
        assertEquals(SKColorRole.Primary, SKAppearanceConfig.Button.containerColorRole)
        assertEquals(SKColorRole.PrimaryContainer, SKAppearanceConfig.ButtonTonal.containerColorRole)
        assertEquals(SKColorRole.Outline, SKAppearanceConfig.ButtonOutlined.outlineColorRole)
        assertEquals(null, SKAppearanceConfig.ButtonText.outlineColorRole)
        assertEquals(SKColorRole.Primary, SKAppearanceConfig.ButtonText.contentColorRole)
    }
}
