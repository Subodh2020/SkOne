package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import io.skone.ui.overlay.SKSegmentItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKSheetSegmentComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bottomSheet_showActionsAndDismiss() {
        var visible = true
        var applied = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var open by remember { mutableStateOf(true) }
                SKBottomSheet(
                    visible = open,
                    onDismissRequest = {
                        open = false
                        visible = false
                    },
                    title = "Filters",
                    primaryActionLabel = "Apply",
                    onPrimaryAction = { applied++ },
                    secondaryActionLabel = "Reset",
                    onSecondaryAction = {},
                    secondaryEnabled = false,
                    accessibility = SKAccessibilityConfig(testTag = "sheet"),
                ) {
                    SKText(text = "Sheet body")
                }
            }
        }
        composeRule.onNodeWithTag("sheet").assertIsDisplayed()
        composeRule.onNodeWithText("Sheet body").assertIsDisplayed()
        composeRule.onNodeWithTag("sheet_secondary").assertIsNotEnabled()
        composeRule.onNodeWithTag("sheet_primary").performClick()
        assertEquals(1, applied)
        assertFalse(visible)
    }

    @Test
    fun segmented_selectionAndDisabled() {
        var selected = "all"
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var current by remember { mutableStateOf("all") }
                SKSegmentedButton(
                    items = listOf(
                        SKSegmentItem("all", "All"),
                        SKSegmentItem("unread", "Unread"),
                        SKSegmentItem("done", "Done", enabled = false),
                    ),
                    selectedId = current,
                    onSelect = {
                        current = it
                        selected = it
                    },
                    accessibility = SKAccessibilityConfig(testTag = "seg"),
                )
            }
        }
        composeRule.onNodeWithTag("seg_all")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithTag("seg_unread").performClick()
        assertEquals("unread", selected)
        composeRule.onNodeWithTag("seg_done").assertIsNotEnabled()
        composeRule.onNodeWithTag("seg_done").performClick()
        assertEquals("unread", selected)
        assertTrue(selected == "unread")
    }
}
