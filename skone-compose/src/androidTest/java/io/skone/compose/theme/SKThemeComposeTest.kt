package io.skone.compose.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.compose.widget.SKText
import io.skone.theme.SKThemeBuilder
import io.skone.theme.SKThemeMode
import io.skone.theme.SKThemeProvider
import io.skone.theme.SKThemes
import io.skone.theme.SKTheme as SKThemeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose contracts for [SKTheme] / [skTheme].
 */
@RunWith(AndroidJUnit4::class)
class SKThemeComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun lightModeResolvesLightTokens() {
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                resolved = skTheme
                SKText(text = "Light theme")
            }
        }
        composeRule.onNodeWithText("Light theme").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(SKThemeMode.Light, resolved.mode)
            assertEquals(SKThemes.Light.name, resolved.name)
            assertEquals(SKThemes.Light.tokens.colors.primary, resolved.tokens.colors.primary)
        }
    }

    @Test
    fun darkModeResolvesDarkTokensDistinctFromLight() {
        assertNotEquals(
            "Fixture: Light and Dark primary tokens must differ",
            SKThemes.Light.tokens.colors.primary,
            SKThemes.Dark.tokens.colors.primary,
        )

        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Dark) {
                resolved = skTheme
                SKText(text = "Dark theme")
            }
        }
        composeRule.onNodeWithText("Dark theme").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(SKThemeMode.Dark, resolved.mode)
            assertEquals(SKThemes.Dark.name, resolved.name)
            assertEquals(SKThemes.Dark.tokens.colors.primary, resolved.tokens.colors.primary)
            assertNotEquals(
                SKThemes.Light.tokens.colors.primary,
                resolved.tokens.colors.primary,
            )
        }
    }

    @Test
    fun omittedSKThemeFallsBackToLight() {
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            // No SKTheme { } wrapper — LocalSKTheme default is SKThemes.Light.
            resolved = skTheme
            SKText(text = "Default theme")
        }
        composeRule.onNodeWithText("Default theme").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(SKThemes.Light.name, resolved.name)
            assertEquals(SKThemeMode.Light, resolved.mode)
            assertEquals(SKThemes.Light.tokens.colors.primary, resolved.tokens.colors.primary)
        }
    }

    @Test
    fun explicitThemeOverrideIsConsumed() {
        val custom = SKThemeBuilder.light()
            .name("skone.test.custom")
            .build()
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            // theme= ignores mode/provider — public override path.
            SKTheme(mode = SKThemeMode.Dark, theme = custom) {
                resolved = skTheme
                SKText(text = "Custom theme")
            }
        }
        composeRule.onNodeWithText("Custom theme").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals("skone.test.custom", resolved.name)
            assertEquals(custom.tokens.colors.primary, resolved.tokens.colors.primary)
        }
    }

    @Test
    fun customThemeProviderIsConsumed() {
        val provider = object : SKThemeProvider {
            override fun theme(mode: SKThemeMode, isSystemInDarkTheme: Boolean): SKThemeModel =
                SKThemes.Light.copy(name = "skone.test.provider")
        }
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Dark, provider = provider) {
                resolved = skTheme
                SKText(text = "Provider theme")
            }
        }
        composeRule.onNodeWithText("Provider theme").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals("skone.test.provider", resolved.name)
            assertTrue(resolved.mode == SKThemeMode.Light)
        }
    }
}
