package io.skone.compose.widget

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.component.ProvideSKComponentRuntime
import io.skone.compose.component.rememberSKComponentRuntime
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI + accessibility tests for [SKText].
 */
@RunWith(AndroidJUnit4::class)
class SKTextComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun displaysText() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                val runtime = rememberSKComponentRuntime()
                ProvideSKComponentRuntime(runtime) {
                    SKText(text = "Accessible greeting")
                }
            }
        }
        composeRule.onNodeWithText("Accessible greeting").assertIsDisplayed()
    }

    @Test
    fun appliesContentDescriptionAndTestTag() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKText(
                    text = "Visible",
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Greeting label",
                        testTag = "sktext_greeting",
                    ),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Greeting label").assertIsDisplayed()
        composeRule.onNodeWithTag("sktext_greeting").assertIsDisplayed()
    }
}
