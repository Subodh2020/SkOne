package io.skone.compose.theme

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.compose.widget.SKText
import io.skone.theme.SKThemeMode
import io.skone.theme.SKThemeProvider
import io.skone.theme.SKThemes
import io.skone.theme.SKTheme as SKThemeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose theme default policy: [SKTheme] composable resolves Light/Dark without OptIn.
 */
@RunWith(AndroidJUnit4::class)
class SKThemeDefaultPolicyComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun skThemeComposableDefaultResolvesLightTokens() {
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                resolved = skTheme
                SKText(text = "light")
            }
        }
        composeRule.onNodeWithText("light").assertExists()
        composeRule.runOnIdle {
            assertEquals(SKThemes.Light.name, resolved.name)
            assertEquals(SKThemes.Light.tokens.colors.primary, resolved.tokens.colors.primary)
        }
    }

    @Test
    fun skThemeComposableDarkResolvesDarkTokens() {
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Dark) {
                resolved = skTheme
                SKText(text = "dark")
            }
        }
        composeRule.onNodeWithText("dark").assertExists()
        composeRule.runOnIdle {
            assertEquals(SKThemes.Dark.name, resolved.name)
            assertEquals(SKThemes.Dark.tokens.colors.primary, resolved.tokens.colors.primary)
            assertNotEquals(SKThemes.Light.tokens.colors.primary, resolved.tokens.colors.primary)
        }
    }

    @Test
    fun customProviderStillWorks() {
        val provider = object : SKThemeProvider {
            override fun theme(mode: SKThemeMode, isSystemInDarkTheme: Boolean): SKThemeModel =
                SKThemes.Light.copy(name = "compose.custom")
        }
        lateinit var resolved: SKThemeModel
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.System, provider = provider) {
                resolved = skTheme
                SKText(text = "custom")
            }
        }
        composeRule.onNodeWithText("custom").assertExists()
        composeRule.runOnIdle {
            assertEquals("compose.custom", resolved.name)
        }
    }
}
