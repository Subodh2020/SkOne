package io.skone.compose.widget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKSelectionComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun radioGroup_selectsSingleOption() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var selected by remember { mutableStateOf<String?>("a") }
                SKRadioGroup(
                    selectedValue = selected,
                    onSelectedChange = { selected = it },
                    accessibility = SKAccessibilityConfig(testTag = "sk_radio_group"),
                ) {
                    SKRadioButton(
                        value = "a",
                        label = "Alpha",
                        accessibility = SKAccessibilityConfig(testTag = "radio_a"),
                    )
                    SKRadioButton(
                        value = "b",
                        label = "Beta",
                        accessibility = SKAccessibilityConfig(testTag = "radio_b"),
                    )
                }
            }
        }
        composeRule.onNodeWithTag("radio_a")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithTag("radio_b").performClick()
        composeRule.onNodeWithTag("radio_b")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithTag("radio_a")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
    }

    @Test
    fun chip_togglesSelectedSemantics() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var selected by remember { mutableStateOf(false) }
                SKChip(
                    label = "Kotlin",
                    selected = selected,
                    onClick = { selected = !selected },
                    leadingIcon = SKIconKey("skone.icon.tag"),
                    accessibility = SKAccessibilityConfig(testTag = "sk_chip"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_chip")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
            .performClick()
        composeRule.onNodeWithTag("sk_chip")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Selected"))
    }

    @Test
    fun radio_disabled() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKRadioButton(
                    value = "x",
                    selected = false,
                    enabled = false,
                    label = "Locked",
                    accessibility = SKAccessibilityConfig(testTag = "radio_disabled"),
                )
            }
        }
        composeRule.onNodeWithTag("radio_disabled")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
    }
}
