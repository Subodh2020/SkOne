package io.skone.theme

import io.skone.theme.defaults.SKLightColorTokens
import io.skone.theme.size.SKSize
import io.skone.theme.state.SKComponentState
import io.skone.theme.state.SKInteractionState
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SKThemeEngineTest {

    @Test
    fun `default light and dark themes expose distinct primary colors`() {
        assertTrue(SKThemes.Light.tokens.colors.primary != SKThemes.Dark.tokens.colors.primary)
        assertEquals(SKThemeMode.Light, SKThemes.Light.mode)
        assertEquals(SKThemeMode.Dark, SKThemes.Dark.mode)
    }

    @Test
    fun `builder overrides colors while preserving spacing defaults`() {
        val custom = SKLightColorTokens(primary = SKThemes.Dark.tokens.colors.primary)
        val theme = SKThemeBuilder.light()
            .name("custom")
            .colors(custom)
            .build()

        assertEquals("custom", theme.name)
        assertEquals(custom.primary, theme.tokens.colors.primary)
        assertEquals(SKThemes.Light.tokens.spacing.md, theme.tokens.spacing.md)
    }

    @Test
    fun `provider resolves system mode from flag`() {
        val provider = SKDefaultThemeProvider()
        assertEquals(SKThemes.Light.name, provider.theme(SKThemeMode.System, isSystemInDarkTheme = false).name)
        assertEquals(SKThemes.Dark.name, provider.theme(SKThemeMode.System, isSystemInDarkTheme = true).name)
    }

    @Test
    fun `size tokens scale consistently`() {
        val sizes = SKThemes.Light.sizes
        assertTrue(sizes.height(SKSize.Small).value < sizes.height(SKSize.Large).value)
        assertTrue(sizes.minTouchTarget.value >= 48f)
    }

    @Test
    fun `color role resolves against tokens`() {
        val primary = SKThemes.Light.tokens.colors.color(SKColorRole.Primary)
        assertEquals(SKThemes.Light.tokens.colors.primary, primary)
    }

    @Test
    fun `component state interaction priority`() {
        val pressed = SKComponentState(pressed = true, focused = true)
        assertEquals(SKInteractionState.Pressed, pressed.interactionState())

        val disabled = SKComponentState(enabled = false, pressed = true)
        assertEquals(SKInteractionState.Disabled, disabled.interactionState())
    }
}
