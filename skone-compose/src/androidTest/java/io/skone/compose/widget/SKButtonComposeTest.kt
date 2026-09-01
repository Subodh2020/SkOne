package io.skone.compose.widget

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.component.framework.icon.SKIconKey
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKButtonComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun displaysLabelAndInvokesClick() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKButton(
                    text = "Continue",
                    onClick = { clicks++ },
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Continue action",
                        testTag = "skbutton_continue",
                    ),
                )
            }
        }
        composeRule.onNodeWithTag("skbutton_continue")
            .assertIsDisplayed()
            .assert(hasClickAction())
            .performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
        composeRule.onNodeWithText("Continue").assertIsDisplayed()
    }

    @Test
    fun disabledExposesDisabledSemantics() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKButton(
                    text = "Save",
                    enabled = false,
                    onClick = {},
                    accessibility = SKAccessibilityConfig(testTag = "skbutton_disabled"),
                )
            }
        }
        composeRule.onNodeWithTag("skbutton_disabled")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
    }

    @Test
    fun loadingBlocksClickAndExposesStateDescription() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKButton(
                    text = "Submit",
                    loading = true,
                    onClick = { clicks++ },
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Submit form",
                        testTag = "skbutton_loading",
                    ),
                )
            }
        }
        composeRule.onNodeWithTag("skbutton_loading")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Loading"))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
        composeRule.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun decorativeLeadingIconDoesNotExposeRawKey() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKButton(
                    text = "Mail",
                    leadingIcon = SKIconKey("skone.icon.mail"),
                    onClick = {},
                    accessibility = SKAccessibilityConfig(
                        contentDescription = "Mail action",
                        testTag = "skbutton_mail",
                    ),
                )
            }
        }
        composeRule.onNodeWithTag("skbutton_mail").assertIsDisplayed()
        composeRule
            .onAllNodes(hasContentDescription("skone.icon.mail"), useUnmergedTree = true)
            .assertCountEquals(0)
    }

    @Test
    fun outlinedVariantStillClickable() {
        var clicks = 0
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKButton(
                    text = "Outline",
                    appearance = SKAppearanceConfig.ButtonOutlined,
                    onClick = { clicks++ },
                    accessibility = SKAccessibilityConfig(testTag = "skbutton_outlined"),
                )
            }
        }
        composeRule.onNodeWithTag("skbutton_outlined").performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun defaultRoleIsButton() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKButton(
                    text = "OK",
                    onClick = {},
                    accessibility = SKAccessibilityConfig(testTag = "skbutton_role"),
                )
            }
        }
        composeRule.onNodeWithTag("skbutton_role")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Role))
    }
}
