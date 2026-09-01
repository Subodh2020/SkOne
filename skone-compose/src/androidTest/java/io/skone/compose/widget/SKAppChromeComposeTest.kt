package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKAppChromeComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun searchBar_queryClearAndIme() {
        var searched = ""
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var query by remember { mutableStateOf("") }
                SKSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { searched = it },
                    onClear = { query = "" },
                    accessibility = SKAccessibilityConfig(testTag = "sk_search"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_search").assertIsDisplayed()
        composeRule.onNodeWithTag("sk_search").performTextInput("widgets")
        composeRule.onNodeWithTag("sk_search_clear").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("widgets").assertDoesNotExist()
        composeRule.onNodeWithTag("sk_search").performTextInput("fab")
        composeRule.onNodeWithTag("sk_search").performImeAction()
        assertEquals("fab", searched)
    }

    @Test
    fun searchBar_disabled() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKSearchBar(
                    query = "locked",
                    onQueryChange = {},
                    enabled = false,
                    accessibility = SKAccessibilityConfig(testTag = "sk_search_off"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_search_off")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
    }

    @Test
    fun emptyState_actionsAndA11y() {
        var primary = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKEmptyState(
                    title = "No matches",
                    description = "Try a different search",
                    icon = SKIconKey("skone.icon.empty"),
                    primaryActionLabel = "Clear",
                    onPrimaryAction = { primary++ },
                    secondaryActionLabel = "Help",
                    onSecondaryAction = {},
                    accessibility = SKAccessibilityConfig(testTag = "sk_empty"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_empty").assertIsDisplayed()
        composeRule.onNodeWithTag("sk_empty_primary").performClick()
        assertEquals(1, primary)
        composeRule.onNodeWithText("No matches").assertIsDisplayed()
    }

    @Test
    fun fab_clickRequiresCdAndRespectsDisabled() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var enabled by remember { mutableStateOf(true) }
                SKFab(
                    icon = SKIconKey("skone.icon.add", contentDescription = "Add"),
                    onClick = {
                        clicks++
                        enabled = false
                    },
                    enabled = enabled,
                    accessibility = SKAccessibilityConfig(testTag = "sk_fab"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_fab").assertIsEnabled().performClick()
        assertEquals(1, clicks)
        composeRule.onNodeWithContentDescription("Add").assertIsNotEnabled()
    }

    @Test
    fun fab_missingContentDescription_throws() {
        var threw = false
        try {
            composeRule.setContent {
                SKTheme(mode = SKThemeMode.Light) {
                    SKFab(
                        icon = SKIconKey("skone.icon.add"),
                        onClick = {},
                    )
                }
            }
        } catch (e: IllegalArgumentException) {
            threw = true
        } catch (e: Throwable) {
            threw = e.cause is IllegalArgumentException ||
                e.message?.contains("SKFab requires") == true
        }
        assertTrue(threw)
    }
}
