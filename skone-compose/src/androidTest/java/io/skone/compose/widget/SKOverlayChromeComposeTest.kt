package io.skone.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import io.skone.ui.overlay.SKMenuItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKOverlayChromeComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun menu_itemClickAndDisabled() {
        var clicked = ""
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKMenu(
                    items = listOf(
                        SKMenuItem("edit", "Edit"),
                        SKMenuItem("delete", "Delete", enabled = false),
                    ),
                    onItemClick = { clicked = it },
                    accessibility = SKAccessibilityConfig(testTag = "menu"),
                )
            }
        }
        composeRule.onNodeWithTag("menu_edit").performClick()
        assertEquals("edit", clicked)
        composeRule.onNodeWithTag("menu_delete").assertIsNotEnabled()
        composeRule.onNodeWithTag("menu_delete").performClick()
        assertEquals("edit", clicked)
    }

    @Test
    fun dropdown_openSelectAndDismiss() {
        var selected = ""
        var expanded = true
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var open by remember { mutableStateOf(true) }
                Box(Modifier.fillMaxSize()) {
                    SKDropdownMenu(
                        expanded = open,
                        onDismissRequest = {
                            open = false
                            expanded = false
                        },
                        items = listOf(
                            SKMenuItem("alpha", "Alpha"),
                            SKMenuItem("beta", "Beta", enabled = false),
                        ),
                        selectedId = "alpha",
                        onItemClick = { selected = it },
                        accessibility = SKAccessibilityConfig(testTag = "dd"),
                    )
                }
            }
        }
        composeRule.onNodeWithTag("dd_alpha")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .performClick()
        assertEquals("alpha", selected)
        assertFalse(expanded)
    }

    @Test
    fun tooltip_showsMessageWhenVisible() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKTooltip(
                    message = "Archive thread",
                    visible = true,
                    accessibility = SKAccessibilityConfig(testTag = "tip"),
                )
                SKTooltip(
                    message = "Hidden",
                    visible = false,
                    accessibility = SKAccessibilityConfig(testTag = "hidden_tip"),
                )
            }
        }
        composeRule.onNodeWithTag("tip").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Archive thread").assertIsDisplayed()
        composeRule.onNodeWithTag("hidden_tip").assertDoesNotExist()
    }

    @Test
    fun bottomAppBar_exposesSemanticsAndSlots() {
        var action = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKBottomAppBar(
                    leading = {
                        SKIconButton(
                            icon = io.skone.component.framework.icon.SKIconKey(
                                "skone.icon.menu",
                                contentDescription = "Menu",
                            ),
                            onClick = { action++ },
                            accessibility = SKAccessibilityConfig(testTag = "bab_leading"),
                        )
                    },
                    content = { SKText(text = "Ready") },
                    trailing = {
                        SKButton(text = "Save", onClick = { action += 10 })
                    },
                    accessibility = SKAccessibilityConfig(testTag = "bab"),
                )
            }
        }
        composeRule.onNodeWithTag("bab").assertIsDisplayed()
        composeRule.onNodeWithText("Ready").assertIsDisplayed()
        composeRule.onNodeWithTag("bab_leading").performClick()
        assertEquals(1, action)
        assertTrue(action >= 1)
    }
}
