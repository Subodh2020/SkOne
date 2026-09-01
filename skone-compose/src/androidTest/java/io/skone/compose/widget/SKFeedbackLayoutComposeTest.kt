package io.skone.compose.widget

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.compose.theme.SKTheme
import io.skone.theme.SKThemeMode
import io.skone.ui.feedback.SKProgressStyle
import io.skone.ui.layout.SKDividerOrientation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SKFeedbackLayoutComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun slider_exposesProgressSemanticsAndDisabled() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var value by remember { mutableFloatStateOf(0.25f) }
                SKSlider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = 0f..1f,
                    enabled = false,
                    accessibility = SKAccessibilityConfig(testTag = "sk_slider", contentDescription = "Volume"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_slider")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Disabled))
    }

    @Test
    fun progress_determinateSemantics() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKProgressIndicator(
                    progress = 0.5f,
                    indeterminate = false,
                    style = SKProgressStyle.Linear,
                    accessibility = SKAccessibilityConfig(testTag = "sk_progress"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_progress")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "50 percent"))
    }

    @Test
    fun divider_hasTestTagWithoutContentDescription() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                SKDivider(
                    orientation = SKDividerOrientation.Horizontal,
                    accessibility = SKAccessibilityConfig(testTag = "sk_divider"),
                )
            }
        }
        composeRule.onNodeWithTag("sk_divider").assertExists()
    }

    @Test
    fun card_clickableExposesButtonRole() {
        composeRule.setContent {
            SKTheme(mode = SKThemeMode.Light) {
                var clicks by remember { mutableStateOf(0) }
                SKCard(
                    onClick = { clicks++ },
                    accessibility = SKAccessibilityConfig(testTag = "sk_card", contentDescription = "Profile card"),
                ) {
                    SKText(text = "Hello")
                }
            }
        }
        composeRule.onNodeWithTag("sk_card")
            .assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, androidx.compose.ui.semantics.Role.Button))
            .performClick()
    }
}
