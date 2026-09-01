package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
class SKToggleComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun checkbox_togglesAndExposesState() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var checked by remember { mutableStateOf(false) }
                SKCheckbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    label = "Accept terms",
                    accessibility = SKAccessibilityConfig(testTag = "sk_checkbox"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_checkbox")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .performClick()
        composeRule.onNodeWithTag("sk_checkbox")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Checked"))
    }

    @Test
    fun switch_togglesOnOff() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var checked by remember { mutableStateOf(true) }
                SKSwitch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    label = "Notifications",
                    accessibility = SKAccessibilityConfig(testTag = "sk_switch"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_switch")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
            .performClick()
        composeRule.onNodeWithTag("sk_switch")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Off"))
    }

    @Test
    fun iconButton_requiresSemanticCdAndClicks() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKIconButton(
                    icon = SKIconKey("skone.icon.close", contentDescription = "Close"),
                    onClick = { clicks++ },
                    accessibility = SKAccessibilityConfig(testTag = "sk_icon_btn"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_icon_btn").assertIsDisplayed().performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
        composeRule.onNodeWithTag("sk_icon_btn")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Close")))
        assertTrue(true)
    }

    @Test
    fun disabledCheckbox_exposesDisabled() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKCheckbox(
                    checked = false,
                    onCheckedChange = {},
                    enabled = false,
                    label = "Locked",
                    accessibility = SKAccessibilityConfig(testTag = "sk_checkbox_disabled"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_checkbox_disabled")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
    }
}
