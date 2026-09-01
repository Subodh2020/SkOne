package io.skone.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import io.skone.ui.chrome.SKTabItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKChromeComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tabs_selectAndDisabledSemantics() {
        var selected = "inbox"
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var current by remember { mutableStateOf("inbox") }
                SKTabs(
                    items = listOf(
                        SKTabItem("inbox", "Inbox"),
                        SKTabItem("drafts", "Drafts", enabled = false),
                        SKTabItem("sent", "Sent"),
                    ),
                    selectedId = current,
                    onSelect = {
                        current = it
                        selected = it
                    },
                    accessibility = SKAccessibilityConfig(testTag = "tabs"),
                )
            }
        }
        composeRule.onNodeWithTag("tabs_inbox")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithTag("tabs_sent").performClick()
        assertEquals("sent", selected)
        composeRule.onNodeWithTag("tabs_drafts").assertIsNotEnabled()
        composeRule.onNodeWithTag("tabs_drafts").performClick()
        assertEquals("sent", selected)
    }

    @Test
    fun badge_countDotAndHidden() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var visible by remember { mutableStateOf(true) }
                SKBadge(
                    text = "9+",
                    visible = visible,
                    accessibility = SKAccessibilityConfig(testTag = "badge"),
                )
                SKBadge(
                    dot = true,
                    accessibility = SKAccessibilityConfig(testTag = "dot"),
                )
                SKBadge(
                    text = "gone",
                    visible = false,
                    accessibility = SKAccessibilityConfig(testTag = "hidden"),
                )
            }
        }
        composeRule.onNodeWithTag("badge").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("9+").assertIsDisplayed()
        composeRule.onNodeWithTag("dot").assertIsDisplayed()
        composeRule.onNodeWithTag("hidden").assertDoesNotExist()
    }

    @Test
    fun avatar_initialsAndImageContent() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKAvatar(
                    initials = "sk",
                    accessibility = SKAccessibilityConfig(testTag = "avatar_initials"),
                )
                SKAvatar(
                    initials = "XX",
                    content = {
                        Box(Modifier.size(40.dp).background(Color.Cyan))
                    },
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Photo",
                        testTag = "avatar_image",
                    ),
                )
            }
        }
        composeRule.onNodeWithTag("avatar_initials").assertIsDisplayed()
        composeRule.onNodeWithText("SK").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Photo").assertIsDisplayed()
        assertFalse(
            composeRule.onNodeWithTag("avatar_initials")
                .fetchSemanticsNode()
                .config
                .contains(SemanticsProperties.Disabled),
        )
    }
}
